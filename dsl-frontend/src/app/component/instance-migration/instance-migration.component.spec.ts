import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InstanceMigrationComponent } from './instance-migration.component';

describe('InstanceMigrationComponent', () => {
  let component: InstanceMigrationComponent;
  let fixture: ComponentFixture<InstanceMigrationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InstanceMigrationComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InstanceMigrationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
