import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { UserProfile } from './auth.models';

describe('AuthService', () => {
  let service: AuthService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [HttpClientTestingModule] });
    service = TestBed.inject(AuthService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('requests a CSRF token before posting credentials and stores the returned profile', () => {
    const expected: UserProfile = { id: 3, username: 'c.mehta', displayName: 'Charu Mehta', email: 'charu@example.invalid', roles: ['ROLE_CONTRACTS_MANAGER'] };
    let received: UserProfile | undefined;

    service.login({ username: 'c.mehta', password: 'placeholder-value' }).subscribe(profile => received = profile);

    const csrf = http.expectOne('/api/auth/csrf');
    expect(csrf.request.method).toBe('GET');
    csrf.flush({ token: 'csrf-token' });
    const login = http.expectOne('/api/auth/login');
    expect(login.request.method).toBe('POST');
    expect(login.request.body).toEqual({ username: 'c.mehta', password: 'placeholder-value' });
    expect(login.request.withCredentials).toBeTrue();
    login.flush(expected);
    expect(received).toEqual(expected);
  });

  it('loads the profile from the authenticated session', () => {
    let received: UserProfile | undefined;
    service.currentProfile().subscribe(profile => received = profile);
    http.expectOne('/api/auth/me').flush({ id: 4, username: 'a.sharma', displayName: 'Aarav Sharma', email: 'aarav@example.invalid', roles: ['ROLE_SAFETY_OFFICER'] });
    expect(received?.username).toBe('a.sharma');
  });
});
