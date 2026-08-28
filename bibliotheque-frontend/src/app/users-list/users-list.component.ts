import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Users } from '../_model/users';
import { UsersService } from '../_service/users.service';

@Component({
  selector: 'app-users-list',
  templateUrl: './users-list.component.html',
  styleUrls: ['./users-list.component.css']
})
export class UsersListComponent implements OnInit {

  users: Users[] = [];
  loading = true;
  errorMessage = '';

  constructor(private usersService: UsersService,
    private router: Router) { }

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;
    this.errorMessage = '';
    this.usersService.getUsersList().subscribe(data =>{
      this.users = data;
      this.loading = false;
    }, () => {
      this.loading = false;
      this.errorMessage = 'Nous ne pouvons pas charger les adhérents. Vérifiez que le serveur est démarré, puis réessayez.';
    });
  }

  userDetails(userId: number) {
    this.router.navigate(['user-details', userId ]);
  }

  updateUser(userId: number) {
    this.router.navigate(['update-user', userId ]);
  }

  roleName(user: Users): string { return user.role && user.role.length ? user.role[0].roleName : 'Non défini'; }

}
