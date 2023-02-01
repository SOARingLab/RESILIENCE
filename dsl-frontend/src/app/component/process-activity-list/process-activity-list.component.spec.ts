import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProcessActivityListComponent } from './process-activity-list.component';

describe('ProcessActivityListComponent', () => {
  let component: ProcessActivityListComponent;
  let fixture: ComponentFixture<ProcessActivityListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProcessActivityListComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProcessActivityListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
