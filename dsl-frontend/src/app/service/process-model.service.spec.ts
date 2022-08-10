import { TestBed } from '@angular/core/testing';

import { ProcessModelService } from './process-model.service';

describe('ProcessModelService', () => {
  let service: ProcessModelService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProcessModelService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
