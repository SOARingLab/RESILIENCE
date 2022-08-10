import { TestBed } from '@angular/core/testing';

import { PublicApiService } from './public-api.service';

describe('PublicApiService', () => {
  let service: PublicApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PublicApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
