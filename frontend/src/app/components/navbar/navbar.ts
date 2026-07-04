import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive], // Esto es lo más importante
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class NavbarComponent { }