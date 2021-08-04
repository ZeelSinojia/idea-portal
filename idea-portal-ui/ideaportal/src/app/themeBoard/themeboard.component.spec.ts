import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ThemeboardComponent } from './themeboard.component';

describe('ThemeboardComponent', () => {
  let component: ThemeboardComponent;
  let fixture: ComponentFixture<ThemeboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ThemeboardComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ThemeboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
