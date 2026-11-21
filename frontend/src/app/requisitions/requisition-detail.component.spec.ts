import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { RequisitionDetailComponent } from './requisition-detail.component';
import { RequisitionsService } from './requisitions.service';

describe('RequisitionDetailComponent', () => {
  let fixture: ComponentFixture<RequisitionDetailComponent>; let service: jasmine.SpyObj<RequisitionsService>;
  const row: any = { id: 8, requisitionNumber: 'J14-REQ-TEST-001', requesterId: 3, requesterName: 'Fictional User', statusCode: 'DRAFT', statusName: 'Draft', title: 'Fictional training', justification: 'Exercise data', neededByDate: '2027-05-01', items: [] };
  beforeEach(async () => { service = jasmine.createSpyObj<RequisitionsService>('RequisitionsService', ['get', 'options', 'changeStatus', 'addItem', 'updateItem']); service.get.and.returnValue(of(row)); service.options.and.returnValue(of({ statuses: [{ code: 'DRAFT', name: 'Draft' }, { code: 'SUBMITTED', name: 'Submitted' }] })); service.changeStatus.and.returnValue(of({ ...row, statusCode: 'SUBMITTED' })); const withItem = { ...row, items: [{ id: 9, lineNumber: 1, itemDescription: 'Fictional radios', quantity: 2, unitOfMeasure: 'each', estimatedUnitCost: 15 }] }; service.addItem.and.returnValue(of(withItem)); service.updateItem.and.returnValue(of(withItem)); await TestBed.configureTestingModule({ imports: [ReactiveFormsModule, RouterTestingModule], declarations: [RequisitionDetailComponent], providers: [{ provide: RequisitionsService, useValue: service }, { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => '8' } } } }] }).compileComponents(); });
  it('loads detail, changes status, adds and edits items', () => { fixture = TestBed.createComponent(RequisitionDetailComponent); fixture.detectChanges(); expect(fixture.nativeElement.textContent).toContain(row.requisitionNumber); fixture.componentInstance.statusForm.patchValue({ statusCode: 'SUBMITTED' }); fixture.componentInstance.changeStatus(); expect(service.changeStatus).toHaveBeenCalledWith(8, 'SUBMITTED'); fixture.componentInstance.itemForm.patchValue({ lineNumber: 1, itemDescription: 'Fictional radios', quantity: 2, unitOfMeasure: 'each', estimatedUnitCost: 15 }); fixture.componentInstance.saveItem(); expect(service.addItem).toHaveBeenCalled(); fixture.componentInstance.editItem({ id: 9, lineNumber: 1, itemDescription: 'Fictional radios', quantity: 2, unitOfMeasure: 'each', estimatedUnitCost: 15 }); fixture.componentInstance.saveItem(); expect(service.updateItem).toHaveBeenCalled(); });
});
