import { Component, OnInit } from '@angular/core';
import { finalize } from 'rxjs/operators';
import { Invoice, InvoiceOptions, InvoiceRequest, PaymentRequest } from './invoice.models';
import { InvoicesService } from './invoices.service';

@Component({ selector: 'app-invoices', templateUrl: './invoices.component.html', styleUrls: ['./invoices.component.css'] })
export class InvoicesComponent implements OnInit {
  invoices: Invoice[] = [];
  options: InvoiceOptions = { contracts: [], invoiceStatuses: [], paymentStatuses: [] };
  request: InvoiceRequest = { invoiceNumber: '', contractId: 0, statusCode: '', invoiceDate: '', dueDate: '', totalAmount: 0 };
  payment: PaymentRequest = { paymentReference: '', statusCode: '', paymentDate: '', amount: 0, paymentMethod: '' };
  paymentInvoiceId = 0; loading = true; saving = false; error = ''; success = '';
  constructor(private readonly service: InvoicesService) {}
  ngOnInit(): void { this.load(); }
  load(): void {
    this.loading = true; this.error = '';
    this.service.list().pipe(finalize(() => this.loading = false)).subscribe({
      next: rows => this.invoices = rows, error: () => { this.invoices = []; this.error = 'Invoices could not be loaded.'; }
    });
    this.service.options().subscribe({
      next: options => {
        this.options = options;
        if (!this.request.contractId && options.contracts.length) this.request.contractId = options.contracts[0].id;
        if (!this.request.statusCode && options.invoiceStatuses.length) this.request.statusCode = options.invoiceStatuses[0].code;
        if (!this.payment.statusCode && options.paymentStatuses.length) this.payment.statusCode = options.paymentStatuses[0].code;
      }, error: () => this.error = 'Invoice options could not be loaded.'
    });
  }
  save(): void {
    this.saving = true; this.error = ''; this.success = '';
    this.service.create(this.request).pipe(finalize(() => this.saving = false)).subscribe({
      next: invoice => { this.invoices = [invoice, ...this.invoices]; this.request = { ...this.request, invoiceNumber: '', invoiceDate: '', dueDate: '', totalAmount: 0 }; this.success = 'Invoice saved.'; },
      error: response => this.error = response?.error?.message || 'Invoice could not be saved.'
    });
  }
  updateStatus(invoice: Invoice, event: Event): void {
    const statusCode = (event.target as HTMLSelectElement).value;
    this.service.changeStatus(invoice.id, statusCode).subscribe({
      next: updated => invoice.statusCode = updated.statusCode,
      error: response => this.error = response?.error?.message || 'Invoice status could not be updated.'
    });
  }
  openPayment(id: number): void { this.paymentInvoiceId = id; this.payment = { ...this.payment, paymentReference: '', paymentDate: '', amount: 0, paymentMethod: '' }; }
  savePayment(): void {
    this.service.addPayment(this.paymentInvoiceId, this.payment).subscribe({
      next: () => { this.success = 'Payment recorded.'; this.paymentInvoiceId = 0; },
      error: response => this.error = response?.error?.message || 'Payment could not be recorded.'
    });
  }
}
