const LEGACY_PROVIDER_ALIASES: Record<string, string> = {
  INTER: '077',
  C6: '336',
};

export function normalizeBankProviderCode(raw: string | null | undefined): string {
  if (!raw) {
    return '';
  }

  const trimmed = raw.trim().toUpperCase();
  if (LEGACY_PROVIDER_ALIASES[trimmed]) {
    return LEGACY_PROVIDER_ALIASES[trimmed];
  }

  if (/^\d{1,3}$/.test(trimmed)) {
    return trimmed.padStart(3, '0');
  }

  return trimmed;
}

export function bankProvidersMatch(left: string | null | undefined, right: string | null | undefined): boolean {
  return normalizeBankProviderCode(left) === normalizeBankProviderCode(right);
}
