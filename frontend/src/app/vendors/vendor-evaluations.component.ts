import { Component, OnInit } from '@angular/core';
import { finalize } from 'rxjs/operators';
import {
  VendorEvaluation,
  VendorEvaluationOptions,
  VendorEvaluationRequest
} from './vendor-evaluation.models';
import { VendorEvaluationsService } from './vendor-evaluations.service';

@Component({
  selector: 'app-vendor-evaluations',
  templateUrl: './vendor-evaluations.component.html',
  styleUrls: ['./vendor-evaluations.component.css']
})
export class VendorEvaluationsComponent implements OnInit {
  evaluations: VendorEvaluation[] = [];
  options: VendorEvaluationOptions = { vendors: [], requisitions: [] };
  request: VendorEvaluationRequest = { requisitionId: 0, vendorId: 0, score: 0, recommendation: '' };
  loading = true;
  saving = false;
  error = '';
  success = '';

  constructor(private readonly service: VendorEvaluationsService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';
    this.service.list().pipe(finalize(() => this.loading = false)).subscribe({
      next: rows => this.evaluations = rows,
      error: () => { this.evaluations = []; this.error = 'Vendor evaluations could not be loaded.'; }
    });
    this.service.options().subscribe({
      next: options => {
        this.options = options;
        if (!this.request.requisitionId && options.requisitions.length) this.request.requisitionId = options.requisitions[0].id;
        if (!this.request.vendorId && options.vendors.length) this.request.vendorId = options.vendors[0].id;
      },
      error: () => this.error = 'Vendor evaluation options could not be loaded.'
    });
  }

  save(): void {
    this.saving = true;
    this.error = '';
    this.success = '';
    this.service.create(this.request).pipe(finalize(() => this.saving = false)).subscribe({
      next: evaluation => {
        this.evaluations = [evaluation, ...this.evaluations];
        this.request = { requisitionId: this.request.requisitionId, vendorId: this.request.vendorId, score: 0, recommendation: '' };
        this.success = 'Vendor evaluation saved.';
      },
      error: response => this.error = response?.error?.message || 'Vendor evaluation could not be saved.'
    });
  }
}
