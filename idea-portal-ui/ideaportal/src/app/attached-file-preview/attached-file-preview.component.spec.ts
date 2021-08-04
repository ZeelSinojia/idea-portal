import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AttachedFilePreviewComponent } from './attached-file-preview.component';

describe('AttachedFilePreviewComponent', () => {
  let component: AttachedFilePreviewComponent;
  let fixture: ComponentFixture<AttachedFilePreviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AttachedFilePreviewComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AttachedFilePreviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
