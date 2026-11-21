import { CommonModule } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, throwError } from 'rxjs';
import { ContractDetailComponent } from './contract-detail.component';
import { ContractSummary } from './contract.models';
import { ContractsService } from './contracts.service';

describe('ContractDetailComponent', () => {
  let fixture: ComponentFixture<ContractDetailComponent>;
  let service: jasmine.SpyObj<ContractsService>;
  const contract: ContractSummary = {
    id: 1, contractNumber: 'J14-CN-TEST-001', title: 'Fictional training', vendorId: 5,
    vendorName: 'Fictional vendor', statusCode: 'DRAFT', statusName: 'Draft',
    startDate: '2026-01-01', endDate: '2026-12-31', contractValue: 1200, summary: 'Fictional record'
  };
  const options = { vendors: [{ id: 5, code: 'V-TEST', name: 'Fictional vendor' }],
    statuses: [{ code: 'DRAFT', name: 'Draft' }, { code: 'ACTIVE', name: 'Active' }] };

  beforeEach(async () => {
    service = jasmine.createSpyObj<ContractsService>('ContractsService', ['get', 'options', 'changeStatus']);
    await TestBed.configureTestingModule({
      imports: [CommonModule, ReactiveFormsModule, RouterTestingModule],
      declarations: [ContractDetailComponent],
      providers: [
        { provide: ContractsService, useValue: service },
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => '1' } } } }
      ]
    }).compileComponents();
  });

  it('loads and renders a contract detail', () => {
    service.get.and.returnValue(of(contract));
    service.options.and.returnValue(of(options));
    fixture = TestBed.createComponent(ContractDetailComponent);
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('J14-CN-TEST-001');
    expect(fixture.nativeElement.textContent).toContain('Fictional vendor');
  });

  it('shows loading and retryable error states', () => {
    const pending = new Subject<ContractSummary>();
    service.get.and.returnValue(pending);
    service.options.and.returnValue(of(options));
    fixture = TestBed.createComponent(ContractDetailComponent);
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Loading contract');
    pending.error(new Error('offline'));
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Please try again.');
    expect(fixture.nativeElement.querySelector('button')).toBeTruthy();
  });

  it('submits a status change and reports success', () => {
    service.get.and.returnValue(of(contract));
    service.options.and.returnValue(of(options));
    service.changeStatus.and.returnValue(of({ ...contract, statusCode: 'ACTIVE', statusName: 'Active' }));
    fixture = TestBed.createComponent(ContractDetailComponent);
    fixture.detectChanges();
    fixture.componentInstance.statusControl.setValue('ACTIVE');
    fixture.componentInstance.updateStatus();
    fixture.detectChanges();
    expect(service.changeStatus).toHaveBeenCalledWith(1, { statusCode: 'ACTIVE' });
    expect(fixture.nativeElement.textContent).toContain('Contract status updated.');
  });

  it('reports a status update error', () => {
    service.get.and.returnValue(of(contract));
    service.options.and.returnValue(of(options));
    service.changeStatus.and.returnValue(throwError(() => new Error('offline')));
    fixture = TestBed.createComponent(ContractDetailComponent);
    fixture.detectChanges();
    fixture.componentInstance.statusControl.setValue('ACTIVE');
    fixture.componentInstance.updateStatus();
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('The status could not be updated.');
  });
});
