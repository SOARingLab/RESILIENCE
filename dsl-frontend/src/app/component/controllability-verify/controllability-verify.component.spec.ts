import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ControllabilityVerifyComponent } from './controllability-verify.component';

describe('ControllabilityVerifyComponent', () => {
  let component: ControllabilityVerifyComponent;
  let fixture: ComponentFixture<ControllabilityVerifyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ControllabilityVerifyComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ControllabilityVerifyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
