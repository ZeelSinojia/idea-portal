import { ComponentFixture, TestBed } from '@angular/core/testing';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { SolutionListComponent } from './solution-list.component';

describe('SolutionListComponent', () => {
  let component: SolutionListComponent;
  let fixture: ComponentFixture<SolutionListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SolutionListComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SolutionListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
