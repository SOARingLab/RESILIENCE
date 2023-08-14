import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ControllabilityModelComponent } from './controllability-model.component';

describe('ControllabilityModelComponent', () => {
  let component: ControllabilityModelComponent;
  let fixture: ComponentFixture<ControllabilityModelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ControllabilityModelComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ControllabilityModelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
