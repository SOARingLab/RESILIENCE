import { TestBed } from '@angular/core/testing';

import { ControllabilityModelService } from './controllability-model.service';

describe('ControllabilityModelService', () => {
  let service: ControllabilityModelService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ControllabilityModelService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
