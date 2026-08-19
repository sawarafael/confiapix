import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import {
  BankProviderCatalogItem,
  CredentialFieldSchema,
  CredentialFormSchema,
} from '../models/bank-integration.models';

const ENTITY_FIELDS = new Set(['clientId', 'clientSecret', 'accountRef', 'merchantRef']);
const CONFIG_FIELD_NAMES = new Set(['authMode', 'businessModel']);

export function isConfigField(fieldName: string): boolean {
  if (ENTITY_FIELDS.has(fieldName)) {
    return false;
  }
  if (fieldName.startsWith('_section_')) {
    return false;
  }
  return true;
}

export function isInputField(field: CredentialFieldSchema): boolean {
  return field.type !== 'section';
}

export function buildCredentialForm(
  fb: FormBuilder,
  schema: CredentialFormSchema | null | undefined,
): FormGroup {
  const controls: Record<string, FormControl<string>> = {};

  for (const field of schema?.fields ?? []) {
    if (!isInputField(field)) {
      continue;
    }
    const validators = field.required ? [Validators.required] : [];
    controls[field.name] = fb.nonNullable.control('', validators);
  }

  return fb.group(controls);
}

export function defaultCredentialValues(schema: CredentialFormSchema | null | undefined): Record<string, string> {
  const defaults: Record<string, string> = {};
  for (const field of schema?.fields ?? []) {
    if (!isInputField(field)) {
      continue;
    }
    if (field.type === 'select' && field.options?.length) {
      defaults[field.name] = field.options[0].value;
    } else {
      defaults[field.name] = '';
    }
  }
  return defaults;
}

export function validateCredentialForm(
  schema: CredentialFormSchema | null | undefined,
  values: Record<string, string>,
): string | null {
  for (const field of schema?.fields ?? []) {
    if (!isInputField(field)) {
      continue;
    }
    const value = values[field.name]?.trim() ?? '';
    if (field.required && !value) {
      return `${field.label.replace(' *', '')} é obrigatório`;
    }
  }

  const authMode = values['authMode'];
  if (authMode === 'OPEN_BANKING' && !values['clientId']?.trim()) {
    return 'Client ID é obrigatório no modo Open Banking';
  }

  return null;
}

export function buildIntegrationPayload(
  schema: CredentialFormSchema | null | undefined,
  values: Record<string, string>,
): {
  clientId?: string;
  clientSecret?: string;
  accountRef: string;
  merchantRef?: string;
  active: boolean;
  config?: Record<string, string>;
} {
  const config: Record<string, string> = {};
  let clientId: string | undefined;
  let clientSecret: string | undefined;
  let accountRef = '';
  let merchantRef: string | undefined;

  for (const field of schema?.fields ?? []) {
    if (!isInputField(field)) {
      continue;
    }

    const value = values[field.name]?.trim() ?? '';
    if (!value) {
      continue;
    }

    if (isConfigField(field.name)) {
      config[field.name] = value;
      continue;
    }

    switch (field.name) {
      case 'clientId':
        clientId = value;
        break;
      case 'clientSecret':
        clientSecret = value;
        break;
      case 'accountRef':
        accountRef = value;
        break;
      case 'merchantRef':
        merchantRef = value;
        break;
    }
  }

  accountRef = resolveAccountRef(values, accountRef, config);

  return {
    clientId,
    clientSecret,
    accountRef,
    merchantRef,
    active: true,
    config: Object.keys(config).length ? config : undefined,
  };
}

export function applyIntegrationToFormValues(
  schema: CredentialFormSchema | null | undefined,
  data: {
    clientId?: string;
    accountRef?: string;
    merchantRef?: string;
    config?: Record<string, string>;
  },
): Record<string, string> {
  const patch = defaultCredentialValues(schema);

  patch['clientId'] = data.clientId ?? '';
  patch['merchantRef'] = data.merchantRef ?? '';
  patch['authMode'] = data.config?.['authMode'] ?? patch['authMode'] ?? 'API_KEY';
  patch['businessModel'] = data.config?.['businessModel'] ?? patch['businessModel'] ?? 'GATEWAY';
  patch['clientSecret'] = '';

  if (data.config) {
    for (const [key, value] of Object.entries(data.config)) {
      if (key in patch) {
        patch[key] = value;
      }
    }
  }

  const schemaId = schema?.id;
  if (schemaId === 'INTER_MTLS' && data.accountRef) {
    const parsed = parseInterAccountRef(data.accountRef, data.config);
    patch['agency'] = parsed.agency;
    patch['accountNumber'] = parsed.accountNumber;
    patch['accountDigit'] = parsed.accountDigit;
  } else if (schemaId === 'C6_OAUTH' && data.accountRef) {
    patch['accountNumber'] = data.accountRef;
    patch['companyDocument'] = data.config?.['companyDocument'] ?? '';
  } else if (schemaId === 'GENERIC_OPEN_BANKING') {
    patch['agency'] = data.config?.['agency'] ?? '';
    patch['accountNumber'] = data.accountRef ?? data.config?.['accountNumber'] ?? '';
  } else {
    patch['accountRef'] = data.accountRef ?? '';
  }

  return patch;
}

export function schemaFields(schema: CredentialFormSchema | null | undefined): CredentialFieldSchema[] {
  return schema?.fields ?? [];
}

export function reviewEntries(
  schema: CredentialFormSchema | null | undefined,
  values: Record<string, string>,
): Array<{ label: string; value: string }> {
  return schemaFields(schema)
    .filter(isInputField)
    .filter((field) => {
      const value = values[field.name];
      return value != null && value !== '';
    })
    .map((field) => ({
      label: field.label.replace(' *', ''),
      value: formatReviewValue(field, values[field.name]),
    }));
}

export function integrationReserveNotice(item: BankProviderCatalogItem | null): string | null {
  if (!item) {
    return null;
  }
  if (item.supportsSync || item.supportsConnectionTest) {
    return null;
  }
  return 'A sincronização automática deste banco ainda está em desenvolvimento. Você já pode cadastrar e reservar as credenciais.';
}

function resolveAccountRef(
  values: Record<string, string>,
  accountRef: string,
  config: Record<string, string>,
): string {
  if (accountRef) {
    return accountRef;
  }

  const agency = values['agency']?.trim();
  const accountNumber = values['accountNumber']?.trim();
  const accountDigit = values['accountDigit']?.trim();

  if (agency && accountNumber) {
    const digitSuffix = accountDigit ? `-${accountDigit}` : '';
    return `${agency}/${accountNumber}${digitSuffix}`;
  }

  if (accountNumber) {
    return accountNumber;
  }

  return config['accountNumber'] ?? '';
}

function parseInterAccountRef(
  accountRef: string,
  config?: Record<string, string>,
): { agency: string; accountNumber: string; accountDigit: string } {
  if (config?.['agency'] || config?.['accountNumber']) {
    return {
      agency: config['agency'] ?? '',
      accountNumber: config['accountNumber'] ?? '',
      accountDigit: config['accountDigit'] ?? '',
    };
  }

  const slashMatch = accountRef.match(/^(\d+)\/(\d+)(?:-(\d))?$/);
  if (slashMatch) {
    return {
      agency: slashMatch[1],
      accountNumber: slashMatch[2],
      accountDigit: slashMatch[3] ?? '',
    };
  }

  return { agency: '', accountNumber: accountRef, accountDigit: '' };
}

function formatReviewValue(field: CredentialFieldSchema, rawValue: string): string {
  if (field.type === 'select') {
    return field.options?.find((option) => option.value === rawValue)?.label ?? rawValue;
  }
  if (field.type === 'password' || field.name === 'privateKey') {
    return '••••••••';
  }
  if (field.type === 'textarea') {
    return rawValue.length > 48 ? `${rawValue.slice(0, 48)}…` : rawValue;
  }
  return rawValue;
}

function fieldLabel(field: CredentialFieldSchema): string {
  return field.required ? `${field.label} *` : field.label;
}

export { fieldLabel };
