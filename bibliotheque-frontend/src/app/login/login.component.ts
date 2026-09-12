import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { UserAuthService } from '../_service/user-auth.service';
import { UsersService } from '../_service/users.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  loading = false;
  errorMessage = '';

  constructor(private userService: UsersService,
    private userAuthSerivce: UserAuthService,
    private router: Router
  ) { }

  ngOnInit() {
  }

  login(loginForm: NgForm) {
    if (loginForm.invalid || this.loading) { return; }
    this.loading = true;
    this.errorMessage = '';
    this.userService.login(loginForm.value).subscribe(
      (response: any)=>{
        this.userAuthSerivce.setRoles(response.user.role);
        this.userAuthSerivce.setToken(response.jwtToken);
        this.userAuthSerivce.setUserId(response.user.userId);
        this.userAuthSerivce.setName(response.user.name);

        this.loading = false;
        const role = response.user.role[0].roleName;
        if (role === 'ADMINISTRATEUR') {
          this.router.navigate(['/']);
        } else if (role === 'BIBLIOTHECAIRE') {
          this.router.navigate(['/reservations']);
        } else {
          this.router.navigate(['/borrow-book']);
        }
      },
      (error)=>{
        this.loading = false;
        this.errorMessage = error.status === 0
          ? 'Le service de la bibliothèque est momentanément injoignable. Vérifiez votre connexion puis réessayez.'
          : 'Identifiant ou mot de passe incorrect. Vérifiez vos informations puis réessayez.';
      }
    );
  }

}
