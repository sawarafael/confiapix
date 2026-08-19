import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  ApiResponse,
  DashboardData,
  PageResponse,
  PixDetail,
  PixTransaction,
  Reconciliation,
  StoneConnectionTest,
  StoneCredentials,
  StoneCredentialsRequest,
  BankProviderCatalogItem,
  BankIntegration,
  BankIntegrationRequest,
  BankConnectionTest,
  BankSyncResult,
  TenantAccess,
  CreateTenantAccessRequest,
  UpdateTenantAccessRequest,
  AppNotification,
  UnreadCount,
  UserProfile,
  UpdateProfileRequest,
  ChangePasswordRequest,
  UpdateProfileResponse,
} from '../models/api.models';

@Injectable({ providedIn: 'root' })
export class DashboardApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl;

  getSummary(): Observable<ApiResponse<DashboardData>> {
    return this.http.get<ApiResponse<DashboardData>>(`${this.baseUrl}/dashboard`);
  }
}

@Injectable({ providedIn: 'root' })
export class PixApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl;

  list(page = 0, size = 20): Observable<ApiResponse<PageResponse<PixTransaction>>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<ApiResponse<PageResponse<PixTransaction>>>(`${this.baseUrl}/api/v1/pix`, {
      params,
    });
  }

  getDetail(id: string): Observable<ApiResponse<PixDetail>> {
    return this.http.get<ApiResponse<PixDetail>>(`${this.baseUrl}/api/v1/pix/${id}`);
  }

  getDetailByTxid(txid: string): Observable<ApiResponse<PixDetail>> {
    return this.http.get<ApiResponse<PixDetail>>(`${this.baseUrl}/api/v1/pix/txid/${txid}`);
  }
}

@Injectable({ providedIn: 'root' })
export class ReconciliationApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl;

  list(): Observable<ApiResponse<Reconciliation[]>> {
    return this.http.get<ApiResponse<Reconciliation[]>>(`${this.baseUrl}/api/v1/reconciliations`);
  }
}

@Injectable({ providedIn: 'root' })
export class TenantAccessApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl;

  list(): Observable<ApiResponse<TenantAccess[]>> {
    return this.http.get<ApiResponse<TenantAccess[]>>(`${this.baseUrl}/api/v1/admin/tenant-access`);
  }

  create(payload: CreateTenantAccessRequest): Observable<ApiResponse<TenantAccess>> {
    return this.http.post<ApiResponse<TenantAccess>>(
      `${this.baseUrl}/api/v1/admin/tenant-access`,
      payload,
    );
  }

  update(id: string, payload: UpdateTenantAccessRequest): Observable<ApiResponse<TenantAccess>> {
    return this.http.put<ApiResponse<TenantAccess>>(
      `${this.baseUrl}/api/v1/admin/tenant-access/${id}`,
      payload,
    );
  }
}

@Injectable({ providedIn: 'root' })
export class StoneApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl;

  getCredentials(): Observable<ApiResponse<StoneCredentials>> {
    return this.http.get<ApiResponse<StoneCredentials>>(
      `${this.baseUrl}/api/v1/integrations/stone/credentials`,
    );
  }

  saveCredentials(payload: StoneCredentialsRequest): Observable<ApiResponse<StoneCredentials>> {
    return this.http.put<ApiResponse<StoneCredentials>>(
      `${this.baseUrl}/api/v1/integrations/stone/credentials`,
      payload,
    );
  }

  testConnection(): Observable<ApiResponse<StoneConnectionTest>> {
    return this.http.post<ApiResponse<StoneConnectionTest>>(
      `${this.baseUrl}/api/v1/integrations/stone/test-connection`,
      {},
    );
  }
}

@Injectable({ providedIn: 'root' })
export class BankIntegrationApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl;

  listCatalog(): Observable<ApiResponse<BankProviderCatalogItem[]>> {
    return this.http.get<ApiResponse<BankProviderCatalogItem[]>>(
      `${this.baseUrl}/api/v1/integrations`,
    );
  }

  getCredentials(provider: string): Observable<ApiResponse<BankIntegration>> {
    return this.http.get<ApiResponse<BankIntegration>>(
      `${this.baseUrl}/api/v1/integrations/${provider}/credentials`,
    );
  }

  saveCredentials(
    provider: string,
    payload: BankIntegrationRequest,
  ): Observable<ApiResponse<BankIntegration>> {
    return this.http.put<ApiResponse<BankIntegration>>(
      `${this.baseUrl}/api/v1/integrations/${provider}/credentials`,
      payload,
    );
  }

  testConnection(provider: string): Observable<ApiResponse<BankConnectionTest>> {
    return this.http.post<ApiResponse<BankConnectionTest>>(
      `${this.baseUrl}/api/v1/integrations/${provider}/test-connection`,
      {},
    );
  }

  deactivate(provider: string): Observable<ApiResponse<BankIntegration>> {
    return this.http.post<ApiResponse<BankIntegration>>(
      `${this.baseUrl}/api/v1/integrations/${provider}/deactivate`,
      {},
    );
  }

  remove(provider: string): Observable<ApiResponse<null>> {
    return this.http.post<ApiResponse<null>>(
      `${this.baseUrl}/api/v1/integrations/${provider}/remove`,
      {},
    );
  }

  sync(provider: string): Observable<ApiResponse<BankSyncResult>> {
    return this.http.post<ApiResponse<BankSyncResult>>(
      `${this.baseUrl}/api/v1/integrations/${provider}/sync`,
      {},
    );
  }
}

@Injectable({ providedIn: 'root' })
export class NotificationApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl;

  list(page = 0, size = 10): Observable<ApiResponse<PageResponse<AppNotification>>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<ApiResponse<PageResponse<AppNotification>>>(
      `${this.baseUrl}/api/v1/notifications`,
      { params },
    );
  }

  unreadCount(): Observable<ApiResponse<UnreadCount>> {
    return this.http.get<ApiResponse<UnreadCount>>(
      `${this.baseUrl}/api/v1/notifications/unread-count`,
    );
  }

  markAsRead(id: string): Observable<ApiResponse<AppNotification>> {
    return this.http.patch<ApiResponse<AppNotification>>(
      `${this.baseUrl}/api/v1/notifications/${id}/read`,
      {},
    );
  }

  markAllAsRead(): Observable<ApiResponse<UnreadCount>> {
    return this.http.patch<ApiResponse<UnreadCount>>(
      `${this.baseUrl}/api/v1/notifications/read-all`,
      {},
    );
  }
}

@Injectable({ providedIn: 'root' })
export class ProfileApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl;

  getProfile(): Observable<ApiResponse<UserProfile>> {
    return this.http.get<ApiResponse<UserProfile>>(`${this.baseUrl}/api/v1/profile`);
  }

  updateProfile(payload: UpdateProfileRequest): Observable<ApiResponse<UpdateProfileResponse>> {
    return this.http.put<ApiResponse<UpdateProfileResponse>>(
      `${this.baseUrl}/api/v1/profile`,
      payload,
    );
  }

  changePassword(payload: ChangePasswordRequest): Observable<ApiResponse<null>> {
    return this.http.put<ApiResponse<null>>(`${this.baseUrl}/api/v1/profile/password`, payload);
  }
}
