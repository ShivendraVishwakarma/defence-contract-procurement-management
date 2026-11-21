import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  VendorEvaluation,
  VendorEvaluationOptions,
  VendorEvaluationRequest
} from './vendor-evaluation.models';

@Injectable({ providedIn: 'root' })
export class VendorEvaluationsService {
  constructor(private readonly http: HttpClient) {}

  list(): Observable<VendorEvaluation[]> {
    return this.http.get<VendorEvaluation[]>('/api/vendor-evaluations', { withCredentials: true });
  }

  options(): Observable<VendorEvaluationOptions> {
    return this.http.get<VendorEvaluationOptions>('/api/vendor-evaluations/options', { withCredentials: true });
  }

  create(request: VendorEvaluationRequest): Observable<VendorEvaluation> {
    return this.http.post<VendorEvaluation>('/api/vendor-evaluations', request, { withCredentials: true });
  }
}
