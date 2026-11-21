import { CommonModule } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, throwError } from 'rxjs';
import { ContractFormComponent } from './contract-form.component';
import { ContractSummary } from './contract.models';
import { ContractsService } from './contracts.service';

describe('ContractFormComponent', () => {
  let fixture: ComponentFixture<ContractFormComponent>;
  let service: jasmine.SpyObj<ContractsService>;
  const contract: ContractSummary = {
    id: 1, contractNumber: 'J14-CN-TEST-001', title: 'Fictional training', vendorId: 5,
    vendorName: 'Fictional vendor', statusCode: 'DRAFT', statusName: 'Draft',
    startDate: '2026-01-01', endDate: '2026-12-31', contractValue: 1200, summary: 'Fictional record'
  };
  const options = { vendors: [{ id: 5, code: 'V-TEST', name: 'Fictional vendor' }], statuses: [{ code: 'DRAFT', name: 'Draft' }] };

  function setup(editing = false): void {
    service.options.and.returnValue(of(options));
    const routeConfig = editing ? { path: 'contracts/:id/edit' } : { path: 'contracts/new' };
    TestBed.overrideProvider(ActivatedRoute, { useValue: { snapshot: { paramMap: { get: () => editing ? '1' : null }, routeConfig } } });
  }

  beforeEach(async () => {
    service = jasmine.createSpyObj<ContractsService>('ContractsService', ['options', 'get', 'create', 'update']);
    await TestBed.configureTestingModule({
      imports: [CommonModule, ReactiveFormsModule, RouterTestingModule],
      declarations: [ContractFormComponent],
      providers: [
        { provide: ContractsService, useValue: service },
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => null }, routeConfig: { path: 'contracts/new' } } } }
      ]
    }).compileComponents();
  });

  it('shows loading then create fields and validation errors', () => {
    const pending = new Subject<any>();
    service.options.and.returnValue(pending);
    fixture = TestBed.createComponent(ContractFormComponent);
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Loading contract form');
    pending.next(options); pending.complete();
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('form')).toBeTruthy();
    fixture.componentInstance.save();
    fixture.detectChanges();
    expect(fixture.componentInstance.form.invalid).toBeTrue();
  });

  it('loads edit values and saves an update', () => {
    setup(true);
    service.get.and.returnValue(of(contract));
    service.update.and.returnValue(of(contract));
    fixture = TestBed.createComponent(ContractFormComponent);
    fixture.detectChanges();
    expect(fixture.componentInstance.form.get('vendorId')?.value).toBe('5');
    fixture.componentInstance.form.patchValue({ title: 'Updated fictional training' });
    fixture.componentInstance.save();
    fixture.detectChanges();
    expect(service.update).toHaveBeenCalledWith(1, jasmine.objectContaining({ title: 'Updated fictional training' }));
    expect(fixture.nativeElement.textContent).toContain('Contract changes saved.');
  });

  it('creates a contract and shows a success state', () => {
    setup(false);
    service.create.and.returnValue(of(contract));
    fixture = TestBed.createComponent(ContractFormComponent);
    fixture.detectChanges();
    fixture.componentInstance.form.patchValue({ contractNumber: 'J14-CN-TEST-001', title: 'Fictional training',
      vendorId: '5', statusCode: 'DRAFT', startDate: '2026-01-01', endDate: '2026-12-31',
      contractValue: '1200', summary: 'Fictional record' });
    fixture.componentInstance.save();
    fixture.detectChanges();
    expect(service.create).toHaveBeenCalled();
    expect(fixture.nativeElement.textContent).toContain('Contract created.');
  });

  it('shows a retryable form-data load error', () => {
    service.options.and.returnValue(throwError(() => new Error('offline')));
    fixture = TestBed.createComponent(ContractFormComponent);
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Please try again.');
    expect(fixture.nativeElement.querySelector('button')).toBeTruthy();
  });

  it('shows an error when saving fails', () => {
    service.options.and.returnValue(of(options));
    service.create.and.returnValue(throwError(() => new Error('offline')));
    fixture = TestBed.createComponent(ContractFormComponent);
    fixture.detectChanges();
    fixture.componentInstance.form.patchValue({ contractNumber: 'J14-CN-TEST-001', title: 'Fictional training',
      vendorId: '5', statusCode: 'DRAFT', startDate: '2026-01-01', endDate: '2026-12-31',
      contractValue: '1200', summary: 'Fictional record' });
    fixture.componentInstance.save();
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('The contract could not be saved.');
  });
});
