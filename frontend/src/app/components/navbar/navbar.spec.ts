import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NavbarComponent } from './navbar';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';

describe('NavbarComponent', () => {
  let component: NavbarComponent;
  let fixture: ComponentFixture<NavbarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NavbarComponent],
      providers: [provideRouter([]), provideHttpClient()]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NavbarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('abre y cierra el menú móvil sin depender del JavaScript de Bootstrap', () => {
    const boton: HTMLButtonElement = fixture.nativeElement.querySelector('.navbar-toggler');
    const menu: HTMLElement = fixture.nativeElement.querySelector('#menuPrincipal');
    boton.click(); fixture.detectChanges();
    expect(menu.classList.contains('show')).toBeTrue();
    expect(boton.getAttribute('aria-expanded')).toBe('true');
    menu.querySelector('.navbar-nav')!.dispatchEvent(new MouseEvent('click', { bubbles: true }));
    fixture.detectChanges();
    expect(menu.classList.contains('show')).toBeFalse();
  });
});
