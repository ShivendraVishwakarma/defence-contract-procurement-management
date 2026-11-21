import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RequisitionCreateRequest, RequisitionDetail, RequisitionItemRequest, RequisitionOptions,
  RequisitionSummary, RequisitionUpdateRequest } from './requisition.models';

@Injectable({ providedIn: 'root' })
export class RequisitionsService {
  constructor(private readonly http: HttpClient) {}
  list(): Observable<RequisitionSummary[]> { return this.http.get<RequisitionSummary[]>('/api/requisitions', { withCredentials: true }); }
  get(id: number): Observable<RequisitionDetail> { return this.http.get<RequisitionDetail>(`/api/requisitions/${id}`, { withCredentials: true }); }
  options(): Observable<RequisitionOptions> { return this.http.get<RequisitionOptions>('/api/requisitions/options', { withCredentials: true }); }
  create(body: RequisitionCreateRequest): Observable<RequisitionDetail> { return this.http.post<RequisitionDetail>('/api/requisitions', body, { withCredentials: true }); }
  update(id: number, body: RequisitionUpdateRequest): Observable<RequisitionDetail> { return this.http.put<RequisitionDetail>(`/api/requisitions/${id}`, body, { withCredentials: true }); }
  changeStatus(id: number, statusCode: string): Observable<RequisitionDetail> {
    return this.http.patch<RequisitionDetail>(`/api/requisitions/${id}/status`, { statusCode }, { withCredentials: true });
  }
  addItem(id: number, body: RequisitionItemRequest): Observable<RequisitionDetail> {
    return this.http.post<RequisitionDetail>(`/api/requisitions/${id}/items`, body, { withCredentials: true });
  }
  updateItem(id: number, itemId: number, body: RequisitionItemRequest): Observable<RequisitionDetail> {
    return this.http.put<RequisitionDetail>(`/api/requisitions/${id}/items/${itemId}`, body, { withCredentials: true });
  }
}
