import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Reservation, ReservationStatus } from '../_model/reservation';

@Component({ selector: 'app-reservation-list', templateUrl: './reservation-list.component.html', styleUrls: ['./reservation-list.component.css'] })
export class ReservationListComponent {
  @Input() reservations: Reservation[] = [];
  @Input() allReservations: Reservation[] = [];
  @Input() loading = false;
  @Input() errorMessage = '';
  @Input() filter: ReservationStatus | 'TOUS' = 'TOUS';
  @Output() filterChanged = new EventEmitter<ReservationStatus | 'TOUS'>();
  @Output() cancelRequested = new EventEmitter<Reservation>();
  @Output() retryRequested = new EventEmitter<void>();
  readonly filters: Array<{ value: ReservationStatus | 'TOUS'; label: string }> = [
    { value: 'TOUS', label: 'Tous' }, { value: 'EN_ATTENTE', label: 'EN_ATTENTE' }, { value: 'DISPONIBLE', label: 'DISPONIBLE' },
    { value: 'ANNULEE', label: 'ANNULEE' }, { value: 'EXPIREE', label: 'EXPIREE' }, { value: 'HONOREE', label: 'HONOREE' }
  ];
  selectFilter(filter: ReservationStatus | 'TOUS'): void { this.filterChanged.emit(filter); }
  retry(): void { this.retryRequested.emit(); }
  countFor(status: ReservationStatus | 'TOUS'): number {
    const source = this.allReservations.length > 0 ? this.allReservations : this.reservations;
    return status === 'TOUS' ? source.length : source.filter(item => item.statut === status).length;
  }
  get filteredReservations(): Reservation[] {
    return this.filter === 'TOUS' ? this.reservations : this.reservations.filter(item => item.statut === this.filter);
  }
  canCancel(reservation: Reservation): boolean { return reservation.statut === 'EN_ATTENTE' || reservation.statut === 'DISPONIBLE'; }
  requestCancel(reservation: Reservation): void { this.cancelRequested.emit(reservation); }
}
