import { TestBed } from '@angular/core/testing';

import { UsersService } from './users.service';
import { APP_TEST_IMPORTS } from '../testing/app-test-imports';

describe('UsersService', () => {
  let service: UsersService;

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: APP_TEST_IMPORTS });
    service = TestBed.inject(UsersService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
