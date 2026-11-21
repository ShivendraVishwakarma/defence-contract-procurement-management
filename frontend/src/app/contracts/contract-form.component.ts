import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { ContractCreateRequest, ContractFormOptions, ContractSummary, ContractUpdateRequest } from './contract.models';
import { ContractsService } from './contracts.service';

const dateRangeValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const startDate = control.get('startDate')?.value as string | undefined;
  const endDate = control.get('endDate')?.value as string | undefined;
  return startDate && endDate && endDate < startDate ? { dateRange: true } : null;
};

@Component({
  selector: 'app-contract-form',
  templateUrl: './contract-form.component.html',
  styleUrls: ['./contract-form.component.css']
})
export class ContractFormComponent implements OnInit {
  readonly contractId: number;
  readonly editing: boolean;
  readonly form: FormGroup;

  options: ContractFormOptions = { vendors: [], statuses: [] };
  loading = true;
  saving = false;
  loadError = '';
  saveError = '';
  successMessage = '';
  savedContract: ContractSummary | null = null;

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly contracts: ContractsService
  ) {
    this.contractId = Number(this.route.snapshot.paramMap.get('id'));
    this.editing = this.route.snapshot.routeConfig?.path === 'contracts/:id/edit';
    this.form = this.formBuilder.nonNullable.group({
      contractNumber: ['', [Validators.required, Validators.maxLength(40)]],
      title: ['', [Validators.required, Validators.maxLength(180)]],
      vendorId: ['', Validators.required],
      statusCode: ['', Validators.required],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required],
      contractValue: ['', [Validators.required, Validators.min(0.01)]],
      summary: ['', [Validators.required, Validators.maxLength(1000)]]
    }, { validators: dateRangeValidator });
    if (this.editing) {
      this.form.get('statusCode')?.clearValidators();
      this.form.get('statusCode')?.updateValueAndValidity();
    }
  }

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.loadError = '';
    if (this.editing && (!Number.isInteger(this.contractId) || this.contractId < 1)) {
      this.loadError = 'This contract could not be found.';
      this.loading = false;
      return;
    }
    forkJoin({
      options: this.contracts.options(),
      contract: this.editing ? this.contracts.get(this.contractId) : of(null)
    }).pipe(finalize(() => this.loading = false)).subscribe({
      next: result => {
        this.options = result.options;
        if (result.contract) this.fillForm(result.contract);
      },
      error: () => {
        this.loadError = 'Contract form data could not be loaded. Please try again.';
      }
    });
  }

  save(): void {
    this.saveError = '';
    this.successMessage = '';
    this.form.markAllAsTouched();
    if (this.form.invalid || this.saving) return;

    const value = this.form.getRawValue();
    const common = {
      contractNumber: value.contractNumber,
      title: value.title,
      vendorId: Number(value.vendorId),
      startDate: value.startDate,
      endDate: value.endDate,
      contractValue: Number(value.contractValue),
      summary: value.summary
    };
    this.saving = true;
    const request = this.editing
      ? this.contracts.update(this.contractId, common as ContractUpdateRequest)
      : this.contracts.create({ ...common, statusCode: value.statusCode } as ContractCreateRequest);

    request.pipe(finalize(() => this.saving = false)).subscribe({
      next: contract => {
        this.savedContract = contract;
        this.successMessage = this.editing ? 'Contract changes saved.' : 'Contract created.';
      },
      error: () => {
        this.saveError = 'The contract could not be saved. Review the form and try again.';
      }
    });
  }

  private fillForm(contract: ContractSummary): void {
    this.form.patchValue({
      contractNumber: contract.contractNumber,
      title: contract.title,
      vendorId: String(contract.vendorId),
      startDate: contract.startDate,
      endDate: contract.endDate,
      contractValue: String(contract.contractValue),
      summary: contract.summary
    });
  }
}
