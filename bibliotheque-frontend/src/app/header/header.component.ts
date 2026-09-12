import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { UserAuthService } from '../_service/user-auth.service';
import { UsersService } from '../_service/users.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {
  menuOpen = false;

  constructor(
    private userAuthService: UserAuthService,
    private router: Router,
    public userService: UsersService,
  ) { }

  get name(): string { return this.userAuthService.getName() || 'Lecteur'; }
  get isLoggedIn(): boolean { return !!this.userAuthService.isLoggedIn(); }
  get isAdministrator(): boolean { return this.userService.roleMatch(['ADMINISTRATEUR']); }
  get isLibrarian(): boolean { return this.userService.roleMatch(['BIBLIOTHECAIRE']); }
  get isAdherent(): boolean { return this.userService.roleMatch(['ADHERENT']); }
  get canManageBooks(): boolean { return this.isAdministrator || this.isLibrarian; }
  get roleLabel(): string {
    if (this.isAdministrator) { return 'Administrateur'; }
    if (this.isLibrarian) { return 'Bibliothécaire'; }
    return 'Adhérent';
  }

  toggleMenu(): void { this.menuOpen = !this.menuOpen; }
  closeMenu(): void { this.menuOpen = false; }

  logout(): void {
    this.userAuthService.clear();
    this.menuOpen = false;
    this.router.navigate(['/login']);
  }
}
