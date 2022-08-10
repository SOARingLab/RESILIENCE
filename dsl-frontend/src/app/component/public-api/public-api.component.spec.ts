import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PublicApiComponent } from './public-api.component';

describe('PublicApiComponent', () => {
  let component: PublicApiComponent;
  let fixture: ComponentFixture<PublicApiComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PublicApiComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PublicApiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
