import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, of } from 'rxjs';
import { catchError, finalize, map, shareReplay, tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import {
  ApiResponse,
  AuthData,
  LoginRequest,
  RegisterRequest,
} from '../models/api.models';

const TOKEN_KEY = 'confiapix_token';
const REFRESH_KEY = 'confiapix_refresh';
const USER_KEY = 'confiapix_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly baseUrl = environment.apiUrl;

  private readonly userSignal = signal<AuthData | null>(this.loadUser());
  private refreshInFlight: Observable<boolean> | null = null;

  readonly user = this.userSignal.asReadonly();
  readonly isAuthenticated = computed(() => {
    const current = this.userSignal();
    if (!current?.token) {
      return false;
    }
    if (!this.isTokenExpired(current.token)) {
      return true;
    }
    return !!this.getRefreshToken();
  });
  readonly displayName = computed(() => this.userSignal()?.name ?? 'Usuário');
  readonly isPlatformAdmin = computed(
    () => this.userSignal()?.role === 'ADMIN' && !!this.userSignal()?.platformOperator,
  );

  constructor(
    private readonly http: HttpClient,
    private readonly router: Router,
  ) {}

  login(payload: LoginRequest): Observable<ApiResponse<AuthData>> {
    return this.http
      .post<ApiResponse<AuthData>>(`${this.baseUrl}/auth/login`, payload)
      .pipe(tap((res) => this.persistSession(res.data)));
  }

  register(payload: RegisterRequest): Observable<ApiResponse<AuthData>> {
    return this.http
      .post<ApiResponse<AuthData>>(`${this.baseUrl}/auth/register`, payload)
      .pipe(tap((res) => this.persistSession(res.data)));
  }

  refreshSession(): Observable<boolean> {
    const refreshToken = this.getRefreshToken();
    if (!refreshToken) {
      return of(false);
    }

    if (!this.refreshInFlight) {
      this.refreshInFlight = this.http
        .post<ApiResponse<AuthData>>(`${this.baseUrl}/auth/refresh`, { refreshToken })
        .pipe(
          tap((res) => this.persistSession(res.data)),
          map(() => true),
          catchError(() => of(false)),
          finalize(() => {
            this.refreshInFlight = null;
          }),
          shareReplay(1),
        );
    }

    return this.refreshInFlight;
  }

  logout(): void {
    this.clearStorage();
    this.userSignal.set(null);
    void this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return this.userSignal()?.token ?? localStorage.getItem(TOKEN_KEY);
  }

  getRefreshToken(): string | null {
    return this.userSignal()?.refreshToken ?? localStorage.getItem(REFRESH_KEY);
  }

  isTokenExpired(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1])) as { exp?: number };
      if (!payload.exp) {
        return false;
      }
      return payload.exp * 1000 <= Date.now();
    } catch {
      return true;
    }
  }

  updateProfileSession(
    profile: Pick<AuthData, 'name' | 'email'>,
    tokens?: Pick<AuthData, 'token' | 'refreshToken' | 'expiresIn'>,
  ): void {
    const current = this.userSignal();
    if (!current) {
      return;
    }

    const updated: AuthData = {
      ...current,
      name: profile.name,
      email: profile.email,
      ...(tokens?.token ? { token: tokens.token } : {}),
      ...(tokens?.refreshToken ? { refreshToken: tokens.refreshToken } : {}),
      ...(tokens?.expiresIn ? { expiresIn: tokens.expiresIn } : {}),
    };

    if (tokens?.token) {
      localStorage.setItem(TOKEN_KEY, tokens.token);
    }
    if (tokens?.refreshToken) {
      localStorage.setItem(REFRESH_KEY, tokens.refreshToken);
    }
    localStorage.setItem(USER_KEY, JSON.stringify(updated));
    this.userSignal.set(updated);
  }

  private persistSession(data: AuthData): void {
    localStorage.setItem(TOKEN_KEY, data.token);
    localStorage.setItem(REFRESH_KEY, data.refreshToken);
    localStorage.setItem(USER_KEY, JSON.stringify(data));
    this.userSignal.set(data);
  }

  private loadUser(): AuthData | null {
    const raw = localStorage.getItem(USER_KEY);
    if (!raw) {
      return null;
    }
    try {
      const parsed = JSON.parse(raw) as AuthData;
      const user: AuthData = { ...parsed, platformOperator: parsed.platformOperator ?? false };
      const token = user.token ?? localStorage.getItem(TOKEN_KEY);
      const refreshToken = user.refreshToken ?? localStorage.getItem(REFRESH_KEY);

      if (token && this.isTokenExpired(token) && !refreshToken) {
        this.clearStorage();
        return null;
      }

      return user;
    } catch {
      this.clearStorage();
      return null;
    }
  }

  private clearStorage(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(REFRESH_KEY);
    localStorage.removeItem(USER_KEY);
  }

  homeRoute(): string {
    return this.isPlatformAdmin() ? '/access' : '/dashboard';
  }
}
