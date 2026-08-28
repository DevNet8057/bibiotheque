import { TestBed } from '@angular/core/testing';

import { BooksService } from './books.service';
import { APP_TEST_IMPORTS } from '../testing/app-test-imports';

describe('BooksService', () => {
  let service: BooksService;

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: APP_TEST_IMPORTS });
    service = TestBed.inject(BooksService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
