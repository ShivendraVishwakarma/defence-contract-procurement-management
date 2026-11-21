import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { RequisitionDetail, RequisitionOptions } from './requisition.models';
import { RequisitionsService } from './requisitions.service';

@Component({ selector: 'app-requisition-form', templateUrl: './requisition-form.component.html', styleUrls: ['./requisitions.component.css'] })
export class RequisitionFormComponent implements OnInit {
  readonly id = Number(this.route.snapshot.paramMap.get('id'));
  readonly editing = this.route.snapshot.routeConfig?.path === 'procurement/:id/edit';
  form = this.fb.nonNullable.group({ requisitionNumber: ['', [Validators.required, Validators.maxLength(40)]], title: ['', [Validators.required, Validators.maxLength(180)]], justification: ['', [Validators.required, Validators.maxLength(1000)]], neededByDate: ['', Validators.required], statusCode: ['', Validators.required] });
  options: RequisitionOptions = { statuses: [] }; loading = true; saving = false; loadError = ''; error = ''; success = '';
  constructor(private fb: FormBuilder, private route: ActivatedRoute, private router: Router, private service: RequisitionsService) {
    if (this.editing) { this.form.controls.statusCode.clearValidators(); this.form.controls.statusCode.updateValueAndValidity(); }
  }
  ngOnInit(): void { this.load(); }
  load(): void {
    this.loading = true; this.loadError = '';
    this.service.options().subscribe({ next: options => { this.options = options; if (!this.editing) this.loading = false; }, error: () => { this.loadError = 'Form options could not be loaded. Please try again.'; this.loading = false; } });
    if (this.editing) this.service.get(this.id).pipe(finalize(() => this.loading = false)).subscribe({ next: row => this.form.patchValue({ requisitionNumber: row.requisitionNumber, title: row.title, justification: row.justification, neededByDate: row.neededByDate, statusCode: row.statusCode }), error: () => this.loadError = 'Requisition could not be loaded. Please try again.' });
  }
  save(): void {
    this.form.markAllAsTouched(); this.error = ''; if (this.form.invalid || this.saving) return;
    this.saving = true; const v = this.form.getRawValue();
    const request = this.editing ? this.service.update(this.id, { requisitionNumber: v.requisitionNumber, title: v.title, justification: v.justification, neededByDate: v.neededByDate }) : this.service.create(v);
    request.pipe(finalize(() => this.saving = false)).subscribe({ next: row => { this.success = this.editing ? 'Requisition updated.' : 'Requisition created.'; void this.router.navigate(['/procurement', row.id]); }, error: () => this.error = 'Requisition could not be saved. Review the fields and try again.' });
  }
}
