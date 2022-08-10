import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProcessVariableComponent } from './process-variable.component';

describe('ProcessVariableComponent', () => {
  let component: ProcessVariableComponent;
  let fixture: ComponentFixture<ProcessVariableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProcessVariableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProcessVariableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
