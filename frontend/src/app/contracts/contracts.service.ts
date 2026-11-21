import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  ContractCreateRequest,
  ContractFormOptions,
  ContractStatusRequest,
  ContractSummary,
  ContractUpdateRequest
} from './contract.models';

@Injectable({ providedIn: 'root' })
export class ContractsService {
  constructor(private readonly http: HttpClient) {}

  list(): Observable<ContractSummary[]> {
    return this.http.get<ContractSummary[]>('/api/contracts', { withCredentials: true });
  }

  get(id: number): Observable<ContractSummary> {
    return this.http.get<ContractSummary>(`/api/contracts/${id}`, { withCredentials: true });
  }

  options(): Observable<ContractFormOptions> {
    return this.http.get<ContractFormOptions>('/api/contracts/options', { withCredentials: true });
  }

  create(request: ContractCreateRequest): Observable<ContractSummary> {
    return this.http.post<ContractSummary>('/api/contracts', request, { withCredentials: true });
  }

  update(id: number, request: ContractUpdateRequest): Observable<ContractSummary> {
    return this.http.put<ContractSummary>(`/api/contracts/${id}`, request, { withCredentials: true });
  }

  changeStatus(id: number, request: ContractStatusRequest): Observable<ContractSummary> {
    return this.http.patch<ContractSummary>(`/api/contracts/${id}/status`, request, { withCredentials: true });
  }
}
