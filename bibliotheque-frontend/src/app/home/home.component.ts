import { Component, OnInit } from '@angular/core';
import { forkJoin } from 'rxjs';
import { Books } from '../_model/books';
import { Reservation, ReservationStatus } from '../_model/reservation';
import { Users } from '../_model/users';
import { BooksService } from '../_service/books.service';
import { ReservationService } from '../_service/reservation.service';
import { UsersService } from '../_service/users.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  books: Books[] = [];
  users: Users[] = [];
  reservations: Reservation[] = [];
  loading = true;
  errorMessage = '';
  readonly statuses: ReservationStatus[] = ['EN_ATTENTE', 'DISPONIBLE', 'ANNULEE', 'EXPIREE', 'HONOREE'];

  constructor(private booksService: BooksService, private usersService: UsersService, private reservationService: ReservationService) { }

  ngOnInit(): void { this.loadDashboard(); }

  loadDashboard(): void {
    this.loading = true;
    this.errorMessage = '';
    forkJoin({ books: this.booksService.getBooksList(), users: this.usersService.getUsersList(), reservations: this.reservationService.getReservations() }).subscribe(data => {
      this.books = data.books;
      this.users = data.users;
      this.reservations = data.reservations;
      this.loading = false;
    }, () => {
      this.loading = false;
      this.errorMessage = 'Nous ne pouvons pas charger le tableau de bord pour le moment. Vérifiez que le serveur est démarré, puis réessayez.';
    });
  }

  count(status: ReservationStatus): number { return this.reservations.filter(item => item.statut === status).length; }
}
