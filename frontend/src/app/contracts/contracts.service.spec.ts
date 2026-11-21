import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ContractsService } from './contracts.service';

describe('ContractsService', () => {
  let service: ContractsService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [HttpClientTestingModule] });
    service = TestBed.inject(ContractsService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('loads contract summaries through the authenticated API route', () => {
    const records = [{
      id: 1,
      contractNumber: 'J14-CN-2025-004',
      title: 'Fictional support',
      vendorId: 5,
      vendorName: 'Fictional vendor',
      statusCode: 'ACTIVE',
      statusName: 'Active',
      startDate: '2025-06-15',
      endDate: '2026-06-14',
      contractValue: 740000,
      summary: 'Training record'
    }];

    service.list().subscribe(result => expect(result).toEqual(records));

    const request = http.expectOne('/api/contracts');
    expect(request.request.method).toBe('GET');
    expect(request.request.withCredentials).toBeTrue();
    request.flush(records);
  });

  it('uses the authenticated routes and verbs for contract lifecycle operations', () => {
    const result = { id: 1 } as any;
    service.get(1).subscribe();
    http.expectOne('/api/contracts/1').flush(result);
    service.options().subscribe();
    http.expectOne('/api/contracts/options').flush({ vendors: [], statuses: [] });
    service.create({} as any).subscribe();
    const create = http.expectOne('/api/contracts');
    expect(create.request.method).toBe('POST');
    create.flush(result);
    service.update(1, {} as any).subscribe();
    const update = http.expectOne('/api/contracts/1');
    expect(update.request.method).toBe('PUT');
    update.flush(result);
    service.changeStatus(1, { statusCode: 'ACTIVE' }).subscribe();
    const status = http.expectOne('/api/contracts/1/status');
    expect(status.request.method).toBe('PATCH');
    expect(status.request.withCredentials).toBeTrue();
    status.flush(result);
  });
});
