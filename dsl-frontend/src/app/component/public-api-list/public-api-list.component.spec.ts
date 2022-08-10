import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PublicApiListComponent } from './public-api-list.component';

describe('PublicApiListComponent', () => {
  let component: PublicApiListComponent;
  let fixture: ComponentFixture<PublicApiListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PublicApiListComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PublicApiListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
