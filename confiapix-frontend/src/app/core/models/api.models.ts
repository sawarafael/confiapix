export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
  timestamp?: string;
}

export interface AuthData {
  token: string;
  refreshToken: string;
  expiresIn: number;
  userId: string;
  tenantId: string;
  email: string;
  name: string;
  role: 'ADMIN' | 'FINANCIAL' | 'VIEWER';
  platformOperator: boolean;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  tenantName: string;
  name: string;
  email: string;
  password: string;
}

export interface DashboardData {
  totalReceivable: number;
  totalPayable: number;
  overdueReceivable: number;
  overduePayable: number;
  projectedBalance: number;
}

export interface PixTransaction {
  id: string;
  txid: string;
  endToEndId?: string;
  amount: number;
  payerName?: string;
  payerDocument?: string;
  receivedAt: string;
  source: 'WEBHOOK' | 'SYNC' | 'STONE' | 'MANUAL';
  provider?: BankProviderCode;
  companyId?: string;
  createdAt: string;
}

export interface PixParty {
  name?: string;
  document?: string;
  documentType?: string;
}

export interface PixReconciliationSummary {
  id: string;
  status: ReconciliationStatus;
  expectedAmount?: number;
  receivedAmount: number;
  reconciledAt?: string;
  notes?: string;
}

export interface PixDetail extends PixTransaction {
  updatedAt?: string;
  stonePaymentId?: string;
  paymentType?: string;
  status?: string;
  stoneCreatedAt?: string;
  stoneSettledAt?: string;
  stoneAccountId?: string;
  eventType?: string;
  environment?: string;
  eventHappenedAt?: string;
  eventNotifiedAt?: string;
  payer?: PixParty;
  receiver?: PixParty;
  reconciliation?: PixReconciliationSummary | null;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export type ReconciliationStatus = 'PENDING' | 'MATCHED' | 'DIVERGENT' | 'MISMATCH' | 'MANUAL';

export interface Reconciliation {
  id: string;
  pixTransactionId: string;
  pixTxid: string;
  receivableId?: string;
  expectedAmount: number;
  receivedAmount: number;
  status: ReconciliationStatus;
  reconciledAt?: string;
  notes?: string;
  createdAt: string;
}

export interface StoneCredentials {
  authMode: 'OPEN_BANKING' | 'API_KEY';
  businessModel?: 'GATEWAY' | 'SUBACQUIRER';
  clientId?: string;
  accountId?: string;
  merchantId?: string;
  active?: boolean;
  updatedAt?: string;
}

export interface StoneCredentialsRequest {
  authMode: 'OPEN_BANKING' | 'API_KEY';
  businessModel?: 'GATEWAY' | 'SUBACQUIRER';
  clientId?: string;
  clientSecret?: string;
  accountId?: string;
  merchantId?: string;
}

export interface StoneConnectionTest {
  success: boolean;
  message: string;
  authMode?: string;
}

export type BankProviderCode = string;

export type {
  BankConnectionTest,
  BankIntegration,
  BankIntegrationRequest,
  BankProviderCatalogItem,
  BankSyncResult,
  CredentialFieldOption,
  CredentialFieldSchema,
  CredentialFormSchema,
} from './bank-integration.models';

export interface CreateTenantAccessRequest {
  tenantName: string;
  plan: string;
  adminName: string;
  adminEmail: string;
  adminPassword: string;
}

export interface UpdateTenantAccessRequest {
  tenantName: string;
  plan: string;
  active: boolean;
}

export interface TenantAccess {
  id: string;
  name: string;
  plan: string;
  active: boolean;
  adminEmail?: string;
  adminName?: string;
  createdAt: string;
}

export interface NavItem {
  label: string;
  route: string;
  icon: string;
  platformOnly?: boolean;
}

export type NotificationType =
  | 'PIX_RECEIVED'
  | 'RECONCILIATION_MATCHED'
  | 'RECONCILIATION_DIVERGENT'
  | 'RECONCILIATION_PENDING'
  | 'STONE_SYNC';

export interface AppNotification {
  id: string;
  type: NotificationType;
  title: string;
  message: string;
  read: boolean;
  referenceId?: string;
  referenceType?: string;
  createdAt: string;
}

export interface UnreadCount {
  count: number;
}

export interface UserProfile {
  id: string;
  name: string;
  email: string;
  role: AuthData['role'];
  active: boolean;
  tenantId: string;
  tenantName: string;
  plan: string;
  platformOperator: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface UpdateProfileRequest {
  name: string;
  email: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}

export interface UpdateProfileResponse {
  profile: UserProfile;
  token?: string;
  refreshToken?: string;
  expiresIn?: number;
}
