package com.instituto.api;

import com.instituto.api.dto.*;
import com.instituto.api.entity.*;
import com.instituto.api.repository.*;
import com.instituto.api.security.JwtService;
import com.instituto.api.service.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import java.net.URI;
import java.net.http.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import static org.assertj.core.api.Assertions.*;

/** Solo se habilita explícitamente contra la base aislada, nunca contra IER. */
@EnabledIfEnvironmentVariable(named = "IER_RUN_DB_TESTS", matches = "true")
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.jpa.hibernate.ddl-auto=validate", "spring.jpa.open-in-view=false",
        "app.jwt.secret=clave-exclusiva-de-pruebas-no-utilizar-en-produccion",
        "spring.mail.username=", "spring.mail.password=" })
class FlujosIntegracionTest {
    @DynamicPropertySource
    static void baseAislada(DynamicPropertyRegistry registry) {
        String url = System.getenv("IER_TEST_DB_URL");
        if (!"jdbc:postgresql://127.0.0.1:55432/ier_test".equals(url)) {
            throw new IllegalStateException("Las pruebas solo aceptan el PostgreSQL aislado 127.0.0.1:55432/ier_test");
        }
        registry.add("spring.datasource.url", () -> url);
        registry.add("spring.datasource.username", () -> "ier_test");
        registry.add("spring.datasource.password", () -> System.getenv("IER_TEST_DB_PASSWORD"));
    }

    @LocalServerPort int port;
    @Autowired AuthService auth;
    @Autowired ReservaService reservas;
    @Autowired LaboratorioService laboratorios;
    @Autowired InvestigadorService investigadores;
    @Autowired JwtService jwt;
    @Autowired UsuarioRepository usuarios;
    @Autowired RolRepository roles;
    @Autowired InvestigadorRepository perfiles;
    @Autowired LaboratorioRepository labs;
    @Autowired ReservaLaboratorioRepository reservaRepo;
    @Autowired ContactoRepository contactos;
    @Autowired org.springframework.jdbc.core.JdbcTemplate jdbc;
    @Autowired ProyectoRepository proyectos;
    @Autowired PlatformTransactionManager tx;

    Usuario admin;
    Investigador perfil;
    Laboratorio lab;
    String bearer;

    @BeforeEach void preparar() {
        Rol rolAdmin = roles.findByNombreIgnoreCase("ADMIN").orElseGet(() -> rol("ADMIN"));
        Rol rolInv = roles.findByNombreIgnoreCase("INVESTIGADOR").orElseGet(() -> rol("INVESTIGADOR"));
        admin = usuario(rolAdmin);
        Usuario cuenta = usuario(rolInv);
        perfil = new Investigador(); perfil.setNombre("Prueba"); perfil.setUsuario(cuenta);
        perfil = perfiles.save(perfil);
        lab = new Laboratorio(); lab.setNombre("Laboratorio de prueba"); lab = labs.save(lab);
        bearer = jwt.generarToken(admin, null);
    }

    @AfterEach void limpiarContexto() { SecurityContextHolder.clearContext(); }

    @Test void enlaceValidoEsPublicoYActivacionEsDeUnSoloUso() throws Exception {
        var invitacion = auth.prepararInvitacion(new InvitacionRequest("Prueba", correo()), false);
        assertThat(invitacion.venceEn()).isBetween(LocalDateTime.now().plusHours(47), LocalDateTime.now().plusHours(49));
        assertThat(get("/api/auth/invitaciones/" + invitacion.token(), null).statusCode()).isEqualTo(200);
        assertThat(get("/api/auth/invitaciones/" + invitacion.token(), "JWT-invalido").statusCode()).isEqualTo(401);
        auth.configurarCuenta(new ConfigurarCuentaRequest(invitacion.token(), "password-prueba-123"));
        assertThat(get("/api/auth/invitaciones/" + invitacion.token(), null).statusCode()).isEqualTo(400);
    }

    @Test void reenvioInvalidaElEnlaceAnteriorSinDuplicarCuenta() {
        String email = correo();
        var request = new InvitacionRequest("Prueba", email);
        var primera = auth.prepararInvitacion(request, false);
        long antes = usuarios.count();
        assertThatThrownBy(() -> auth.prepararInvitacion(request, false)).isInstanceOf(IllegalStateException.class);
        var segunda = auth.prepararInvitacion(request, true);
        assertThat(usuarios.count()).isEqualTo(antes);
        assertThatThrownBy(() -> auth.validarToken(primera.token())).isInstanceOf(IllegalArgumentException.class);
        assertThat(auth.validarToken(segunda.token()).getUsername()).isEqualTo(email);
    }

    @Test void loginDeInvitadoAceptaMayusculasEnCorreo() {
        String correo = correo();
        var invitacion = auth.prepararInvitacion(new InvitacionRequest("Prueba", correo), false);
        auth.configurarCuenta(new ConfigurarCuentaRequest(invitacion.token(), "password-prueba-123"));
        var respuesta = auth.login(new LoginRequest(correo.toUpperCase(Locale.ROOT), "password-prueba-123"));
        assertThat(respuesta.username()).isEqualTo(correo);
        assertThat(respuesta.token()).isNotBlank();
    }

    @Test void rechazaInvitacionExpirada() {
        var invitacion = auth.prepararInvitacion(new InvitacionRequest("Prueba", correo()), false);
        Usuario pendiente = usuarios.findByTokenInvitacion(invitacion.token()).orElseThrow();
        pendiente.setFechaExpiracionToken(LocalDateTime.now().minusMinutes(1)); usuarios.save(pendiente);
        assertThatThrownBy(() -> auth.validarToken(invitacion.token())).hasMessageContaining("expirado");
    }

    @Test void crearConIdNoSobrescribeContactoNiReserva() throws Exception {
        Contacto contacto = new Contacto(); contacto.setNombre("Original"); contacto.setEmail(correo()); contacto.setMensaje("Original");
        contacto = contactos.save(contacto);
        var respuesta = post("/api/contacto", "{\"id\":" + contacto.getId() + ",\"nombre\":\"Otro\",\"email\":\"a@example.org\",\"mensaje\":\"Otro\"}", null);
        assertThat(respuesta.statusCode()).isEqualTo(400);
        assertThat(contactos.findById(contacto.getId()).orElseThrow().getMensaje()).isEqualTo("Original");
        assertThat(post("/api/reservas", "{\"id\":1,\"fecha\":\"" + LocalDate.now().plusDays(1) + "\",\"horaInicio\":\"10:00\",\"horaFin\":\"11:00\",\"laboratorio\":{\"id\":" + lab.getId() + "}}", bearer).statusCode()).isEqualTo(400);
    }

    @Test void contactoAdmiteMasDe255CaracteresEnEsquemaMigrado() throws Exception {
        String mensaje = "A".repeat(300);
        String tipo = jdbc.queryForObject("SELECT data_type FROM information_schema.columns WHERE table_schema = 'public' AND table_name = 'contacto' AND column_name = 'mensaje'", String.class);
        assertThat(tipo).isEqualTo("text");
        var respuesta = post("/api/contacto", "{\"nombre\":\"Prueba\",\"email\":\"" + correo() + "\",\"mensaje\":\"" + mensaje + "\"}", null);
        assertThat(respuesta.statusCode()).isEqualTo(200);
        assertThat(contactos.findAll()).anyMatch(c -> mensaje.equals(c.getMensaje()));
    }

    @Test void desactivarCuentaRevocaTokenEmitido() throws Exception {
        assertThat(get("/api/usuarios", bearer).statusCode()).isEqualTo(200);
        admin.setEnable(false); usuarios.save(admin);
        assertThat(get("/api/usuarios", bearer).statusCode()).isEqualTo(401);
    }

    @Test void noExponeCuentaEnPerfilPublico() throws Exception {
        var respuesta = get("/api/investigadores/" + perfil.getId(), null);
        assertThat(respuesta.statusCode()).isEqualTo(200);
        assertThat(respuesta.body()).doesNotContain("\"usuario\"", "\"password\"", "\"tokenInvitacion\"");
    }

    @Test void quitarEncargadoPersisteYBorradoConDependenciasSeRechaza() {
        lab.setEncargado(perfil); labs.save(lab);
        assertThatThrownBy(() -> investigadores.eliminar(perfil.getId())).isInstanceOf(IllegalStateException.class);
        Laboratorio cambios = new Laboratorio(); cambios.setNombre(lab.getNombre());
        laboratorios.actualizar(lab.getId(), cambios);
        assertThat(labs.findById(lab.getId()).orElseThrow().getEncargado()).isNull();
    }

    @Test void soloUnaReservaConcurrentePuedeOcuparElHorario() throws Exception {
        var inicio = new CountDownLatch(1);
        var solicitud = new ReservaRequest(null, LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(11, 0),
                new ReservaRequest.Referencia(lab.getId()), new ReservaRequest.Referencia(perfil.getId()));
        Callable<Boolean> tarea = () -> {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("test", "", List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
            try {
                inicio.await(5, TimeUnit.SECONDS);
                reservas.crear(solicitud);
                return true;
            } catch (IllegalStateException e) { return false; }
            finally { SecurityContextHolder.clearContext(); }
        };
        var ejecutor = Executors.newFixedThreadPool(2);
        try {
            var a = ejecutor.submit(tarea); var b = ejecutor.submit(tarea); inicio.countDown();
            assertThat(List.of(a.get(10, TimeUnit.SECONDS), b.get(10, TimeUnit.SECONDS))).containsExactlyInAnyOrder(true, false);
        } finally {
            ejecutor.shutdownNow();
        }
        assertThat(reservaRepo.findByLaboratorioIdAndFecha(lab.getId(), solicitud.fecha())).hasSize(1);
    }

    @Test void proyectoSeSerializaSinOpenInView() throws Exception {
        Proyecto proyecto = new Proyecto(); proyecto.setTitulo("Prueba"); proyecto.setInvestigadores(List.of(perfil)); proyectos.save(proyecto);
        assertThat(get("/api/proyectos", null).statusCode()).isEqualTo(200);
    }

    @Test void reservaPublicaNoExponeDatosPersonalesDelInvestigador() throws Exception {
        String email = correo();
        perfil.setCorreoInstitucional(email);
        perfiles.save(perfil);
        ReservaLaboratorio reserva = new ReservaLaboratorio();
        reserva.setFecha(LocalDate.now().plusDays(1));
        reserva.setHoraInicio(LocalTime.of(10, 0));
        reserva.setHoraFin(LocalTime.of(11, 0));
        reserva.setLaboratorio(lab);
        reserva.setInvestigador(perfil);
        reservaRepo.save(reserva);

        var respuesta = get("/api/reservas", null);
        assertThat(respuesta.statusCode()).isEqualTo(200);
        assertThat(respuesta.body()).contains("\"laboratorio\"", "\"investigador\"")
                .doesNotContain(email, "correoInstitucional", "\"usuario\"");
    }

    @Test void noPermiteReservarUnaHoraDeHoyQueYaPaso() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                "test", "", List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
        var solicitud = new ReservaRequest(null, LocalDate.now(), LocalTime.MIN, LocalTime.of(1, 0),
                new ReservaRequest.Referencia(lab.getId()), new ReservaRequest.Referencia(perfil.getId()));
        assertThatThrownBy(() -> reservas.crear(solicitud))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("ya comenzó");
    }

    @Test void proyectoIgnoraAutorDelClienteYValidaInvestigadores() throws Exception {
        String titulo = "Proyecto seguro " + UUID.randomUUID();
        var respuesta = post("/api/proyectos", "{\"titulo\":\"" + titulo + "\",\"createdBy\":999,\"investigadorIds\":[" + perfil.getId() + "]}", bearer);
        assertThat(respuesta.statusCode()).isEqualTo(201);
        Proyecto guardado = proyectos.findAll().stream().filter(p -> titulo.equals(p.getTitulo())).findFirst().orElseThrow();
        assertThat(guardado.getCreatedBy()).isEqualTo(admin.getId());
        assertThat(guardado.getInvestigadores()).extracting(Investigador::getId).containsExactly(perfil.getId());
        var invalido = post("/api/proyectos", "{\"titulo\":\"Inválido\",\"investigadorIds\":[999999]}", bearer);
        assertThat(invalido.statusCode()).isEqualTo(404);
    }

    @Test void falloSmtpSeInformaSinPerderLaInvitacionConfirmada() throws Exception {
        String email = correo();
        var respuesta = post("/api/auth/invitaciones", "{\"nombre\":\"Prueba\",\"email\":\"" + email + "\"}", bearer);
        assertThat(respuesta.statusCode()).isEqualTo(201);
        assertThat(respuesta.body()).contains("\"enviado\":false").doesNotContain("\"token\"");
        assertThat(usuarios.findByUsername(email).orElseThrow().getTokenInvitacion()).isNotBlank();
    }

    private Rol rol(String nombre) { Rol rol = new Rol(); rol.setNombre(nombre); return roles.save(rol); }
    private String correo() { return UUID.randomUUID() + "@example.org"; }
    private Usuario usuario(Rol rol) {
        Usuario u = new Usuario(); u.setUsername(correo()); u.setPassword("test-only-hash"); u.setEnable(true); u.setRol(rol);
        return usuarios.save(u);
    }
    private HttpResponse<String> get(String path, String token) throws Exception {
        return enviar(path, token, null);
    }
    private HttpResponse<String> post(String path, String body, String token) throws Exception {
        return enviar(path, token, body);
    }
    private HttpResponse<String> enviar(String path, String token, String body) throws Exception {
        var request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).timeout(Duration.ofSeconds(10));
        if (token != null) request.header("Authorization", "Bearer " + token);
        if (body != null) request.header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(body));
        return HttpClient.newHttpClient().send(request.build(), HttpResponse.BodyHandlers.ofString());
    }
}
