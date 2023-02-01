import { TestBed } from '@angular/core/testing';

import { ProcessActivityService } from './process-activity.service';

describe('ProcessActivityService', () => {
  let service: ProcessActivityService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProcessActivityService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
