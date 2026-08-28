import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReturnBookComponent } from './return-book.component';
import { APP_TEST_IMPORTS } from '../testing/app-test-imports';

describe('ReturnBookComponent', () => {
  let component: ReturnBookComponent;
  let fixture: ComponentFixture<ReturnBookComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: APP_TEST_IMPORTS
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReturnBookComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
