import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { forkJoin } from 'rxjs';
import { Books } from '../_model/books';
import { Reservation, ReservationRequest, ReservationStatus } from '../_model/reservation';
import { Users } from '../_model/users';
import { BooksService } from '../_service/books.service';
import { ReservationService } from '../_service/reservation.service';
import { UsersService } from '../_service/users.service';
import { ReservationFormComponent } from './reservation-form.component';

@Component({ selector: 'app-reservation-page', templateUrl: './reservation-page.component.html', styleUrls: ['./reservation-page.component.css'] })
export class ReservationPageComponent implements OnInit {
  @ViewChild(ReservationFormComponent) reservationForm?: ReservationFormComponent;
  books: Books[] = [];
  users: Users[] = [];
  reservations: Reservation[] = [];
  filter: ReservationStatus | 'TOUS' = 'TOUS';
  loading = true;
  saving = false;
  cancelling = false;
  errorMessage = '';
  formErrorMessage = '';
  feedbackMessage = '';
  pendingCancellation: Reservation | null = null;
  readonly statuses: ReservationStatus[] = ['EN_ATTENTE', 'DISPONIBLE', 'ANNULEE', 'EXPIREE', 'HONOREE'];

  constructor(private booksService: BooksService, private usersService: UsersService, private reservationService: ReservationService) { }
  ngOnInit(): void { this.loadData(); }

  loadData(): void {
    this.loading = true;
    this.errorMessage = '';
    forkJoin({ books: this.booksService.getBooksList(), users: this.usersService.getUsersList(), reservations: this.reservationService.getReservations() }).subscribe(data => {
      this.books = data.books;
      this.users = data.users.filter(user => !user.role || !user.role.length || user.role.some((role: { roleName: string }) => role.roleName === 'User'));
      this.reservations = data.reservations;
      this.loading = false;
    }, () => {
      this.loading = false;
      this.errorMessage = 'Le service de la bibliothèque est momentanément injoignable. Vérifiez votre connexion puis réessayez.';
    });
  }

  count(status: ReservationStatus): number { return this.reservations.filter(item => item.statut === status).length; }

  createReservation(request: ReservationRequest): void {
    this.saving = true;
    this.formErrorMessage = '';
    this.feedbackMessage = '';
    this.reservationService.createReservation(request).subscribe(() => {
      this.saving = false;
      this.feedbackMessage = 'Réservation créée avec succès.';
      this.reservationForm?.reset();
      this.loadData();
    }, error => {
      this.saving = false;
      this.formErrorMessage = this.userMessage(error, 'Nous n’avons pas pu créer la réservation. Réessayez dans quelques instants.');
    });
  }

  cancelReservation(reservation: Reservation): void { this.pendingCancellation = reservation; }
  closeCancellation(): void { if (!this.cancelling) { this.pendingCancellation = null; } }

  confirmCancellation(): void {
    if (!this.pendingCancellation || this.cancelling) { return; }
    const reservation = this.pendingCancellation;
    this.cancelling = true;
    this.errorMessage = '';
    this.reservationService.cancelReservation(reservation.reservationId).subscribe(() => {
      this.cancelling = false;
      this.pendingCancellation = null;
      this.feedbackMessage = 'Réservation annulée avec succès.';
      this.loadData();
    }, error => {
      this.cancelling = false;
      this.pendingCancellation = null;
      this.errorMessage = this.userMessage(error, 'Nous n’avons pas pu annuler la réservation. Réessayez dans quelques instants.');
    });
  }

  private userMessage(error: HttpErrorResponse, fallback: string): string {
    if (error.status === 0) { return 'Nous n’arrivons pas à contacter le serveur. Vérifiez votre connexion ou réessayez dans quelques instants.'; }
    if (error.status === 400) { return 'Sélectionnez un livre et un adhérent avant de continuer.'; }
    if (error.status === 401) { return 'Votre session a expiré. Reconnectez-vous pour continuer.'; }
    if (error.status === 403) { return 'Vous n’avez pas l’autorisation d’effectuer cette action.'; }
    if (error.status === 404) { return 'Le livre ou l’adhérent sélectionné est introuvable. Actualisez les listes puis réessayez.'; }
    if (error.status === 409) {
      const message = error.error && (error.error.message || error.error.error);
      if (message && message.indexOf('RG-01') >= 0) { return 'Ce livre est actuellement disponible. Vous pouvez l’emprunter directement, il n’est donc pas nécessaire de le réserver.'; }
      if (message && message.indexOf('RG-02') >= 0) { return 'Cet adhérent possède déjà une réservation en cours pour ce livre.'; }
      if (message && message.indexOf('RG-03') >= 0) { return 'Cet adhérent a atteint la limite de 3 réservations actives. Annulez ou terminez une réservation avant d’en créer une nouvelle.'; }
      return 'Cette action n’est plus possible avec l’état actuel de la réservation. Actualisez la page puis réessayez.';
    }
    return fallback;
  }
}
