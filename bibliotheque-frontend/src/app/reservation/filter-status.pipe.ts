import { Pipe, PipeTransform } from '@angular/core';
import { Reservation } from '../_model/reservation';

@Pipe({ name: 'filterStatus' })
export class FilterStatusPipe implements PipeTransform {
  transform(reservations: Reservation[], status: string): Reservation[] {
    return reservations.filter(reservation => reservation.statut === status);
  }
}
