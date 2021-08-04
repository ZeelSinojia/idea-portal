import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IdeaDescriptionPageComponent } from './idea-description-page.component';

describe('IdeaDescriptionPageComponent', () => {
  let component: IdeaDescriptionPageComponent;
  let fixture: ComponentFixture<IdeaDescriptionPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ IdeaDescriptionPageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(IdeaDescriptionPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
