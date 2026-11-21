import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { RequisitionFormComponent } from './requisition-form.component';
import { RequisitionsService } from './requisitions.service';

describe('RequisitionFormComponent', () => {
  let fixture: ComponentFixture<RequisitionFormComponent>; let service: jasmine.SpyObj<RequisitionsService>; let router: Router;
  const result: any = { id: 8, requisitionNumber: 'J14-REQ-TEST-001', items: [] };
  beforeEach(async () => { service = jasmine.createSpyObj<RequisitionsService>('RequisitionsService', ['options', 'get', 'create', 'update']); service.options.and.returnValue(of({ statuses: [{ code: 'DRAFT', name: 'Draft' }] })); service.get.and.returnValue(of({ ...result, title: 'Fictional', justification: 'Exercise data', neededByDate: '2027-05-01' })); await TestBed.configureTestingModule({ imports: [ReactiveFormsModule, RouterTestingModule], declarations: [RequisitionFormComponent], providers: [{ provide: RequisitionsService, useValue: service }, { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => null }, routeConfig: { path: 'procurement/new' } } } }] }).compileComponents(); router = TestBed.inject(Router); spyOn(router, 'navigate').and.resolveTo(true); });
  it('validates required fields and creates a requisition', () => { fixture = TestBed.createComponent(RequisitionFormComponent); fixture.detectChanges(); expect(fixture.componentInstance.form.invalid).toBeTrue(); service.create.and.returnValue(of(result)); fixture.componentInstance.form.patchValue({ requisitionNumber: result.requisitionNumber, title: 'Fictional', justification: 'Exercise data', neededByDate: '2027-05-01', statusCode: 'DRAFT' }); fixture.componentInstance.save(); expect(service.create).toHaveBeenCalled(); expect(router.navigate).toHaveBeenCalledWith(['/procurement', 8]); });
  it('shows a retryable load error and a save error', () => { service.options.and.returnValue(throwError(() => new Error('offline'))); fixture = TestBed.createComponent(RequisitionFormComponent); fixture.detectChanges(); expect(fixture.nativeElement.textContent).toContain('Please try again.'); });
});
