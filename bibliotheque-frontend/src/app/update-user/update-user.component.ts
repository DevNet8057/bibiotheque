import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Users } from '../_model/users';
import { UsersService } from '../_service/users.service';

@Component({
  selector: 'app-update-user',
  templateUrl: './update-user.component.html',
  styleUrls: ['./update-user.component.css']
})
export class UpdateUserComponent implements OnInit {

  userId: number;
  user: Users = new Users();
  loading = true;
  saving = false;
  errorMessage = '';
  successMessage = '';
  constructor(private usersService: UsersService,
    private route: ActivatedRoute,
    private router: Router) { }

  ngOnInit(): void {
    this.userId = this.route.snapshot.params['userId'];
    this.usersService.getUserById(this.userId).subscribe(data => {
      this.user = data;
      if (!this.user.role || !this.user.role.length) { this.user.role = [{ roleName: 'User' }]; }
      this.loading = false;
    }, () => { this.loading = false; this.errorMessage = 'Cet adhérent est introuvable. Retournez à la liste puis réessayez.'; });
  }

  onSubmit() {
    this.saving = true;
    this.errorMessage = '';
    this.usersService.updateUser(this.userId, this.user).subscribe( data =>{
        this.saving = false;
        this.successMessage = 'Les informations ont été mises à jour.';
        setTimeout(() => this.goToUsersList(), 700);
    },
    () => { this.saving = false; this.errorMessage = 'Les modifications n’ont pas pu être enregistrées. Réessayez dans quelques instants.'; });
  }

  goToUsersList() {
    this.router.navigate(['/users']);
  }

}
