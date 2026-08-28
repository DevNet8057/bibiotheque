export type ReservationStatus = 'EN_ATTENTE' | 'DISPONIBLE' | 'ANNULEE' | 'EXPIREE' | 'HONOREE';

export interface Reservation {
  reservationId: number;
  livreId: number;
  livreNom: string;
  adherentId: number;
  adherentNom: string;
  dateReservation: string;
  dateExpiration: string;
  statut: ReservationStatus;
}

export interface ReservationRequest {
  livreId: number;
  adherentId: number;
}
