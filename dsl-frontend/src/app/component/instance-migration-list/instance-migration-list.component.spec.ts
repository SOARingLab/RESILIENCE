import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InstanceMigrationListComponent } from './instance-migration-list.component';

describe('InstanceMigrationListComponent', () => {
  let component: InstanceMigrationListComponent;
  let fixture: ComponentFixture<InstanceMigrationListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InstanceMigrationListComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InstanceMigrationListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
