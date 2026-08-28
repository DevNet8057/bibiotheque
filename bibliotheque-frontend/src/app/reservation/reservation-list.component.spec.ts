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

  it('affiche toutes les reservations du filtre Tous et les actions autorisees', () => {
    component.reservations = [
      ...reservations,
      { reservationId: 3, livreId: 12, livreNom: 'Livre C', adherentId: 22, adherentNom: 'Sara', dateReservation: '2026-08-03', dateExpiration: '2026-08-05', statut: 'DISPONIBLE' }
    ];
    component.filter = 'TOUS';
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelectorAll('tbody tr').length).toBe(3);
    expect(fixture.nativeElement.querySelectorAll('.table-action--danger').length).toBe(2);
  });

  it('conserve la liste complète après un aller-retour entre filtres', () => {
    component.reservations = [
      ...reservations,
      { reservationId: 3, livreId: 12, livreNom: 'Livre C', adherentId: 22, adherentNom: 'Sara', dateReservation: '2026-08-03', dateExpiration: '2026-08-05', statut: 'DISPONIBLE' }
    ];

    component.filter = 'EN_ATTENTE';
    expect(component.filteredReservations.map(item => item.reservationId)).toEqual([1]);

    component.filter = 'TOUS';
    fixture.detectChanges();
    expect(component.filteredReservations.map(item => item.reservationId)).toEqual([1, 2, 3]);
    expect(fixture.nativeElement.querySelectorAll('tbody tr').length).toBe(3);
  });

  it('émet le filtre demandé et suit chaque ligne par son identifiant', () => {
    spyOn(component.filterChanged, 'emit');

    component.selectFilter('DISPONIBLE');

    expect(component.filterChanged.emit).toHaveBeenCalledWith('DISPONIBLE');
    expect(component.trackByReservationId(0, reservations[1])).toBe(2);
  });

  it('affiche les dates renvoyées par le backend au format français', () => {
    expect(component.formatDate('21-08-2026')).toBe('21/08/2026');
    expect(component.formatDate('')).toBe('—');
  });
});
