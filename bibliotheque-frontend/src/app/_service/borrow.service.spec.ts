import { TestBed } from '@angular/core/testing';

import { BorrowService } from './borrow.service';
import { APP_TEST_IMPORTS } from '../testing/app-test-imports';

describe('BorrowService', () => {
  let service: BorrowService;

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: APP_TEST_IMPORTS });
    service = TestBed.inject(BorrowService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
