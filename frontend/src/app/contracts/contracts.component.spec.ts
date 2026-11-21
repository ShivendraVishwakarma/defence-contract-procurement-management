import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { ContractSummary } from './contract.models';
import { ContractsComponent } from './contracts.component';
import { ContractsService } from './contracts.service';

describe('ContractsComponent', () => {
  let fixture: ComponentFixture<ContractsComponent>;
  let service: jasmine.SpyObj<ContractsService>;

  const record: ContractSummary = {
    id: 1,
    contractNumber: 'J14-CN-2025-004',
    title: 'Fictional support',
    vendorId: 5,
    vendorName: 'Fictional vendor',
    statusCode: 'ACTIVE',
    statusName: 'Active',
    startDate: '2025-06-15',
    endDate: '2026-06-14',
    contractValue: 740000,
    summary: 'Training record'
  };

  beforeEach(async () => {
    service = jasmine.createSpyObj<ContractsService>('ContractsService', ['list']);
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [ContractsComponent],
      providers: [{ provide: ContractsService, useValue: service }]
    }).compileComponents();
  });

  it('renders returned contract records', () => {
    service.list.and.returnValue(of([record]));
    fixture = TestBed.createComponent(ContractsComponent);

    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('J14-CN-2025-004');
    expect(fixture.nativeElement.textContent).toContain('Fictional vendor');
    expect(fixture.nativeElement.textContent).toContain('Active');
  });

  it('shows an empty state when there are no records', () => {
    service.list.and.returnValue(of([]));
    fixture = TestBed.createComponent(ContractsComponent);

    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('No contracts are available.');
  });

  it('shows a retryable message when loading fails', () => {
    service.list.and.returnValue(throwError(() => new Error('unavailable')));
    fixture = TestBed.createComponent(ContractsComponent);

    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Contracts could not be loaded. Please try again.');
    expect(fixture.nativeElement.querySelector('button.retry-button')).toBeTruthy();
  });
});
