import { TestBed } from '@angular/core/testing';

import { ProcessLogService } from './process-log.service';

describe('ProcessLogService', () => {
  let service: ProcessLogService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProcessLogService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
