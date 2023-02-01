import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProcessActivityComponent } from './process-activity.component';

describe('ProcessActivityComponent', () => {
  let component: ProcessActivityComponent;
  let fixture: ComponentFixture<ProcessActivityComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProcessActivityComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProcessActivityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
