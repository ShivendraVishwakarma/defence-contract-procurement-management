import { Component, OnInit } from '@angular/core';
import { finalize } from 'rxjs/operators';
import { RequisitionSummary } from './requisition.models';
import { RequisitionsService } from './requisitions.service';

@Component({ selector: 'app-requisitions', templateUrl: './requisitions.component.html', styleUrls: ['./requisitions.component.css'] })
export class RequisitionsComponent implements OnInit {
  requisitions: RequisitionSummary[] = []; loading = true; error = '';
  constructor(private readonly service: RequisitionsService) {}
  ngOnInit(): void { this.load(); }
  load(): void {
    this.loading = true; this.error = '';
    this.service.list().pipe(finalize(() => this.loading = false)).subscribe({
      next: rows => this.requisitions = rows,
      error: () => { this.requisitions = []; this.error = 'Requisitions could not be loaded. Please try again.'; }
    });
  }
}
