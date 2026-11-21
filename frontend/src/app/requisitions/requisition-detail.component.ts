import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { RequisitionDetail, RequisitionOptions } from './requisition.models';
import { RequisitionsService } from './requisitions.service';

@Component({ selector: 'app-requisition-detail', templateUrl: './requisition-detail.component.html', styleUrls: ['./requisitions.component.css'] })
export class RequisitionDetailComponent implements OnInit {
  readonly id = Number(this.route.snapshot.paramMap.get('id')); record: RequisitionDetail | null = null; options: RequisitionOptions = { statuses: [] };
  loading = true; error = ''; message = ''; savingItem = false;
  statusForm = this.fb.nonNullable.group({ statusCode: ['', Validators.required] });
  itemForm = this.fb.nonNullable.group({ lineNumber: [1, [Validators.required, Validators.min(1), Validators.max(99999)]], itemDescription: ['', [Validators.required, Validators.maxLength(240)]], quantity: [1, [Validators.required, Validators.min(0.001)]], unitOfMeasure: ['', [Validators.required, Validators.maxLength(30)]], estimatedUnitCost: [0, [Validators.required, Validators.min(0)]] });
  editingItemId: number | null = null;
  constructor(private route: ActivatedRoute, private fb: FormBuilder, private service: RequisitionsService) {}
  ngOnInit(): void { this.load(); }
  load(): void { this.loading = true; this.error = ''; this.service.get(this.id).subscribe({ next: r => { this.record = r; this.statusForm.patchValue({ statusCode: r.statusCode }); this.service.options().subscribe({ next: o => this.options = o, error: () => this.error = 'Statuses could not be loaded. Please retry.' }); this.loading = false; }, error: () => { this.loading = false; this.error = 'Requisition could not be loaded. Please try again.'; } }); }
  changeStatus(): void { if (this.statusForm.invalid || !this.record) return; this.service.changeStatus(this.id, this.statusForm.controls.statusCode.value).subscribe({ next: r => { this.record = r; this.message = 'Status updated.'; }, error: () => this.error = 'Status could not be updated.' }); }
  editItem(item: NonNullable<RequisitionDetail['items']>[number]): void { this.editingItemId = item.id; this.itemForm.patchValue({ lineNumber: item.lineNumber, itemDescription: item.itemDescription, quantity: item.quantity, unitOfMeasure: item.unitOfMeasure, estimatedUnitCost: item.estimatedUnitCost }); }
  saveItem(): void { if (this.itemForm.invalid || this.savingItem) { this.itemForm.markAllAsTouched(); return; } this.savingItem = true; const request = this.itemForm.getRawValue(); const call = this.editingItemId === null ? this.service.addItem(this.id, request) : this.service.updateItem(this.id, this.editingItemId, request); call.pipe(finalize(() => this.savingItem = false)).subscribe({ next: r => { this.record = r; this.editingItemId = null; this.itemForm.reset({ lineNumber: (r.items.length || 0) + 1, itemDescription: '', quantity: 1, unitOfMeasure: '', estimatedUnitCost: 0 }); this.message = 'Item saved.'; }, error: () => this.error = 'Item could not be saved. Check the line number and values.' }); }
}
