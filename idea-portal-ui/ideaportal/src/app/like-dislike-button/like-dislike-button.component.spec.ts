import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LikeDislikeButtonComponent } from './like-dislike-button.component';

describe('LikeDislikeButtonComponent', () => {
  let component: LikeDislikeButtonComponent;
  let fixture: ComponentFixture<LikeDislikeButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LikeDislikeButtonComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LikeDislikeButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
