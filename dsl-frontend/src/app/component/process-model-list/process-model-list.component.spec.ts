import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProcessModelListComponent } from './process-model-list.component';

describe('ProcessModelListComponent', () => {
  let component: ProcessModelListComponent;
  let fixture: ComponentFixture<ProcessModelListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProcessModelListComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProcessModelListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
