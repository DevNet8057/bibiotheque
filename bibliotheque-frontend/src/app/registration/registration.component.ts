import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Users } from '../_model/users';
import { UsersService } from '../_service/users.service';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent implements OnInit {

  user: Users = new Users();
  saving = false;
  errorMessage = '';
  successMessage = '';
  constructor(private usersService: UsersService,
    private router: Router) { }

  ngOnInit(): void {
    this.user.role = [{ roleName: 'User' }];
  }

  saveUser() {
    this.saving = true;
    this.errorMessage = '';
    this.usersService.createUser(this.user).subscribe(data => {
      this.saving = false;
      this.successMessage = 'L’adhérent a été créé avec succès.';
      setTimeout(() => this.goToUsersList(), 700);
    },
    () => { this.saving = false; this.errorMessage = 'Le compte n’a pas pu être créé. Vérifiez que l’identifiant n’est pas déjà utilisé.'; });
  }

  goToUsersList() {
    this.router.navigate(['/users']);
  }

  onSubmit() {
    this.saveUser();
  }

}
