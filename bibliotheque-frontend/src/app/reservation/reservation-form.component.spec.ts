import { ComponentFixture, TestBed } from '@angular/core/testing';

import { APP_TEST_IMPORTS } from '../testing/app-test-imports';
import { ReservationFormComponent } from './reservation-form.component';

describe('ReservationFormComponent', () => {
  let component: ReservationFormComponent;
  let fixture: ComponentFixture<ReservationFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({ imports: APP_TEST_IMPORTS }).compileComponents();
    fixture = TestBed.createComponent(ReservationFormComponent);
    component = fixture.componentInstance;
  });

  it('ne soumet pas un formulaire incomplet', () => {
    spyOn(component.submitted, 'emit');
    component.livreId = 4;

    component.submit();

    expect(component.submitted.emit).not.toHaveBeenCalled();
  });

  it('soumet les identifiants sélectionnés et peut se réinitialiser', () => {
    spyOn(component.submitted, 'emit');
    component.livreId = 4;
    component.adherentId = 7;

    component.submit();
    expect(component.submitted.emit).toHaveBeenCalledWith({ livreId: 4, adherentId: 7 });

    component.reset();
    expect(component.livreId).toBeNull();
    expect(component.adherentId).toBeNull();
  });
});
