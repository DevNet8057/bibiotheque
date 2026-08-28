import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ReservationService } from './reservation.service';

describe('ReservationService', () => {
  let service: ReservationService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [HttpClientTestingModule] });
    service = TestBed.inject(ReservationService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('charge les réservations avec le filtre demandé', () => {
    service.getReservations('EN_ATTENTE').subscribe(result => expect(result).toEqual([]));

    const request = http.expectOne(req => req.url === 'http://localhost:8080/api/reservations');
    expect(request.request.method).toBe('GET');
    expect(request.request.params.get('statut')).toBe('EN_ATTENTE');
    request.flush([]);
  });

  it('crée puis annule une réservation via les endpoints attendus', () => {
    service.createReservation({ livreId: 4, adherentId: 7 }).subscribe();
    const creation = http.expectOne('http://localhost:8080/api/reservations');
    expect(creation.request.method).toBe('POST');
    expect(creation.request.body).toEqual({ livreId: 4, adherentId: 7 });
    creation.flush({});

    service.cancelReservation(12).subscribe();
    const cancellation = http.expectOne('http://localhost:8080/api/reservations/12/annuler');
    expect(cancellation.request.method).toBe('PATCH');
    cancellation.flush({});
  });
});
