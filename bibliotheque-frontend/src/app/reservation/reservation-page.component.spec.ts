import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { APP_TEST_IMPORTS } from '../testing/app-test-imports';
import { ReservationPageComponent } from './reservation-page.component';

describe('ReservationPageComponent', () => {
  let component: ReservationPageComponent;
  let fixture: ComponentFixture<ReservationPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({ imports: APP_TEST_IMPORTS }).compileComponents();
    fixture = TestBed.createComponent(ReservationPageComponent);
    component = fixture.componentInstance;
  });

  it('explique les conflits métier sans afficher de jargon technique', () => {
    const conflict = new HttpErrorResponse({ status: 409, error: { message: 'RG-03' } });
    const message = (component as any).userMessage(conflict, 'Erreur');

    expect(message).toContain('limite de 3 réservations actives');
    expect(message).not.toContain('409');
  });

  it('explique clairement une session expirée', () => {
    const expiredSession = new HttpErrorResponse({ status: 401 });

    expect((component as any).userMessage(expiredSession, 'Erreur')).toContain('session a expiré');
  });
});
