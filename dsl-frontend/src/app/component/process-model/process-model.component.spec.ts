import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProcessModelComponent } from './process-model.component';

describe('ProcessModelComponent', () => {
  let component: ProcessModelComponent;
  let fixture: ComponentFixture<ProcessModelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProcessModelComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProcessModelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
