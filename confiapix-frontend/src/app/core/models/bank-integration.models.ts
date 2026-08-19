export type CredentialFieldType = 'text' | 'password' | 'select' | 'textarea' | 'section';

export interface CredentialFieldOption {
  value: string;
  label: string;
}

export interface CredentialFieldSchema {
  name: string;
  label: string;
  type: CredentialFieldType;
  required: boolean;
  placeholder?: string;
  helpText?: string;
  fullWidth?: boolean;
  options?: CredentialFieldOption[];
}

export interface CredentialFormSchema {
  id: string;
  fields: CredentialFieldSchema[];
}

export interface BankProviderCatalogItem {
  provider: string;
  compe?: string;
  ispb?: string;
  displayName: string;
  description: string;
  available: boolean;
  configured: boolean;
  active: boolean;
  supportsSync: boolean;
  supportsWebhook: boolean;
  supportsConnectionTest: boolean;
  credentialSchemaId: string;
  credentialSchema: CredentialFormSchema;
}

export type BankProviderCode = string;

export interface BankIntegration {
  id?: string;
  tenantId?: string;
  provider: BankProviderCode;
  clientId?: string;
  accountRef?: string;
  merchantRef?: string;
  config?: Record<string, string>;
  active?: boolean;
  updatedAt?: string;
}

export interface BankIntegrationRequest {
  clientId?: string;
  clientSecret?: string;
  accountRef: string;
  merchantRef?: string;
  config?: Record<string, string>;
  active?: boolean;
}

export interface BankSyncResult {
  provider: BankProviderCode;
  fetched: number;
  imported: number;
  reconciled: number;
}

export interface BankConnectionTest {
  provider: BankProviderCode;
  success: boolean;
  message: string;
  authMode?: string;
}
