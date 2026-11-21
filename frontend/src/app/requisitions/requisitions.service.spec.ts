import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { RequisitionsService } from './requisitions.service';

describe('RequisitionsService', () => {
  let service: RequisitionsService; let http: HttpTestingController;
  beforeEach(() => { TestBed.configureTestingModule({ imports: [HttpClientTestingModule] }); service = TestBed.inject(RequisitionsService); http = TestBed.inject(HttpTestingController); });
  afterEach(() => http.verify());
  it('uses authenticated routes and expected verbs for requisition and item lifecycle', () => {
    service.list().subscribe(); const list = http.expectOne('/api/requisitions'); expect(list.request.method).toBe('GET'); expect(list.request.withCredentials).toBeTrue(); list.flush([]);
    service.get(8).subscribe(); http.expectOne('/api/requisitions/8').flush({});
    service.options().subscribe(); http.expectOne('/api/requisitions/options').flush({ statuses: [] });
    service.create({} as any).subscribe(); const create = http.expectOne('/api/requisitions'); expect(create.request.method).toBe('POST'); create.flush({});
    service.update(8, {} as any).subscribe(); const update = http.expectOne('/api/requisitions/8'); expect(update.request.method).toBe('PUT'); update.flush({});
    service.changeStatus(8, 'DRAFT').subscribe(); const status = http.expectOne('/api/requisitions/8/status'); expect(status.request.method).toBe('PATCH'); status.flush({});
    service.addItem(8, {} as any).subscribe(); const add = http.expectOne('/api/requisitions/8/items'); expect(add.request.method).toBe('POST'); add.flush({});
    service.updateItem(8, 2, {} as any).subscribe(); const item = http.expectOne('/api/requisitions/8/items/2'); expect(item.request.method).toBe('PUT'); item.flush({});
  });
});
