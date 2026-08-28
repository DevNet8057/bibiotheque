import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Reservation } from '../_model/reservation';
import { APP_TEST_IMPORTS } from '../testing/app-test-imports';
import { ReservationListComponent } from './reservation-list.component';

describe('ReservationListComponent', () => {
  let component: ReservationListComponent;
  let fixture: ComponentFixture<ReservationListComponent>;

  const reservations: Reservation[] = [
    { reservationId: 1, livreId: 10, livreNom: 'Livre A', adherentId: 20, adherentNom: 'Awa', dateReservation: '2026-08-01', dateExpiration: '2026-08-03', statut: 'EN_ATTENTE' },
    { reservationId: 2, livreId: 11, livreNom: 'Livre B', adherentId: 21, adherentNom: 'Moussa', dateReservation: '2026-08-02', dateExpiration: '2026-08-04', statut: 'HONOREE' }
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({ imports: APP_TEST_IMPORTS }).compileComponents();
    fixture = TestBed.createComponent(ReservationListComponent);
    component = fixture.componentInstance;
    component.reservations = reservations;
  });

  it('filtre les réservations et calcule les compteurs', () => {
    component.filter = 'EN_ATTENTE';

    expect(component.filteredReservations).toEqual([reservations[0]]);
    expect(component.countFor('TOUS')).toBe(2);
    expect(component.countFor('HONOREE')).toBe(1);
  });

  it('autorise uniquement l’annulation des statuts actifs', () => {
    expect(component.canCancel(reservations[0])).toBeTrue();
    expect(component.canCancel(reservations[1])).toBeFalse();
  });
});
