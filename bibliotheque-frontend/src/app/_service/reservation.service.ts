import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Reservation, ReservationRequest, ReservationStatus } from '../_model/reservation';

@Injectable({ providedIn: 'root' })
export class ReservationService {
  private readonly baseUrl = 'http://localhost:8080/api/reservations';

  constructor(private httpClient: HttpClient) { }

  getReservations(status?: ReservationStatus): Observable<Reservation[]> {
    let params = new HttpParams();
    if (status) { params = params.set('statut', status); }
    return this.httpClient.get<Reservation[]>(this.baseUrl, { params });
  }

  createReservation(request: ReservationRequest): Observable<Reservation> {
    return this.httpClient.post<Reservation>(this.baseUrl, request);
  }

  cancelReservation(reservationId: number): Observable<Reservation> {
    return this.httpClient.patch<Reservation>(`${this.baseUrl}/${reservationId}/annuler`, {});
  }
}
