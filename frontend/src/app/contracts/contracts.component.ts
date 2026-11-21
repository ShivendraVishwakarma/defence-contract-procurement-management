import { Component, OnInit } from '@angular/core';
import { finalize } from 'rxjs';
import { ContractSummary } from './contract.models';
import { ContractsService } from './contracts.service';

@Component({
  selector: 'app-contracts',
  templateUrl: './contracts.component.html',
  styleUrls: ['./contracts.component.css']
})
export class ContractsComponent implements OnInit {
  contracts: ContractSummary[] = [];
  loading = true;
  errorMessage = '';

  constructor(private readonly contractsService: ContractsService) {}

  ngOnInit(): void {
    this.loadContracts();
  }

  loadContracts(): void {
    this.loading = true;
    this.errorMessage = '';
    this.contractsService.list()
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: contracts => this.contracts = contracts,
        error: () => {
          this.contracts = [];
          this.errorMessage = 'Contracts could not be loaded. Please try again.';
        }
      });
  }
}
