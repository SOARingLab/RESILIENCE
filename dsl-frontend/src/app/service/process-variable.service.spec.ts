import { TestBed } from '@angular/core/testing';

import { ProcessVariableService } from './process-variable.service';

describe('ProcessVariableService', () => {
  let service: ProcessVariableService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProcessVariableService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
