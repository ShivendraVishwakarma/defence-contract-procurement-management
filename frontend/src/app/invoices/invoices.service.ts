import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Invoice, InvoiceOptions, InvoiceRequest, PaymentRequest } from './invoice.models';

@Injectable({ providedIn: 'root' })
export class InvoicesService {
  constructor(private readonly http: HttpClient) {}
  list(): Observable<Invoice[]> { return this.http.get<Invoice[]>('/api/invoices', { withCredentials: true }); }
  options(): Observable<InvoiceOptions> { return this.http.get<InvoiceOptions>('/api/invoices/options', { withCredentials: true }); }
  create(request: InvoiceRequest): Observable<Invoice> { return this.http.post<Invoice>('/api/invoices', request, { withCredentials: true }); }
  changeStatus(id: number, statusCode: string): Observable<Invoice> {
    return this.http.patch<Invoice>(`/api/invoices/${id}/status`, { statusCode }, { withCredentials: true });
  }
  addPayment(id: number, request: PaymentRequest): Observable<void> {
    return this.http.post<void>(`/api/invoices/${id}/payments`, request, { withCredentials: true });
  }
}
