import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProcessVariableListComponent } from './process-variable-list.component';

describe('ProcessVariableListComponent', () => {
  let component: ProcessVariableListComponent;
  let fixture: ComponentFixture<ProcessVariableListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProcessVariableListComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProcessVariableListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
