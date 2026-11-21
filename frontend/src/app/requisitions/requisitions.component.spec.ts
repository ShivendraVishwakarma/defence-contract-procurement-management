import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { RequisitionSummary } from './requisition.models';
import { RequisitionsComponent } from './requisitions.component';
import { RequisitionsService } from './requisitions.service';

describe('RequisitionsComponent', () => {
  let fixture: ComponentFixture<RequisitionsComponent>; let service: jasmine.SpyObj<RequisitionsService>;
  const row: RequisitionSummary = { id: 8, requisitionNumber: 'J14-REQ-TEST-001', requesterId: 3, requesterName: 'Fictional User', statusCode: 'DRAFT', statusName: 'Draft', title: 'Fictional training', justification: 'Exercise data', neededByDate: '2027-05-01' };
  beforeEach(async () => { service = jasmine.createSpyObj<RequisitionsService>('RequisitionsService', ['list']); await TestBed.configureTestingModule({ imports: [RouterTestingModule], declarations: [RequisitionsComponent], providers: [{ provide: RequisitionsService, useValue: service }] }).compileComponents(); });
  it('shows records, empty and retryable error states', () => {
    service.list.and.returnValue(of([row])); fixture = TestBed.createComponent(RequisitionsComponent); fixture.detectChanges(); expect(fixture.nativeElement.textContent).toContain(row.requisitionNumber);
    service.list.and.returnValue(of([])); fixture.componentInstance.load(); fixture.detectChanges(); expect(fixture.nativeElement.textContent).toContain('No requisitions are available.');
    service.list.and.returnValue(throwError(() => new Error('offline'))); fixture.componentInstance.load(); fixture.detectChanges(); expect(fixture.nativeElement.textContent).toContain('Please try again.'); expect(fixture.nativeElement.querySelector('[role="alert"] button')).toBeTruthy();
  });
});
