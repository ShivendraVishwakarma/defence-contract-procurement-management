import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormControl, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { ContractFormOptions, ContractSummary } from './contract.models';
import { ContractsService } from './contracts.service';

@Component({
  selector: 'app-contract-detail',
  templateUrl: './contract-detail.component.html',
  styleUrls: ['./contract-detail.component.css']
})
export class ContractDetailComponent implements OnInit {
  contract: ContractSummary | null = null;
  options: ContractFormOptions = { vendors: [], statuses: [] };
  statusControl = new FormControl('', { nonNullable: true, validators: [Validators.required] });
  loading = true;
  savingStatus = false;
  errorMessage = '';
  statusError = '';
  successMessage = '';

  private readonly contractId: number;

  constructor(private readonly route: ActivatedRoute, private readonly contracts: ContractsService) {
    this.contractId = Number(this.route.snapshot.paramMap.get('id'));
  }

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.errorMessage = '';
    if (!Number.isInteger(this.contractId) || this.contractId < 1) {
      this.errorMessage = 'This contract could not be found.';
      this.loading = false;
      return;
    }
    forkJoin({
      contract: this.contracts.get(this.contractId),
      options: this.contracts.options()
    }).pipe(finalize(() => this.loading = false)).subscribe({
      next: result => {
        this.contract = result.contract;
        this.options = result.options;
        this.statusControl.setValue(result.contract.statusCode);
      },
      error: () => {
        this.contract = null;
        this.errorMessage = 'This contract could not be loaded. Please try again.';
      }
    });
  }

  updateStatus(): void {
    this.statusError = '';
    this.successMessage = '';
    if (!this.contract || this.statusControl.invalid || this.savingStatus) {
      this.statusControl.markAsTouched();
      return;
    }
    this.savingStatus = true;
    this.contracts.changeStatus(this.contract.id, { statusCode: this.statusControl.value })
      .pipe(finalize(() => this.savingStatus = false))
      .subscribe({
        next: contract => {
          this.contract = contract;
          this.statusControl.setValue(contract.statusCode);
          this.successMessage = 'Contract status updated.';
        },
        error: () => {
          this.statusError = 'The status could not be updated. Please try again.';
        }
      });
  }
}
