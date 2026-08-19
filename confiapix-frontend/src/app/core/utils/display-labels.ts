export function formatUserRole(role?: string | null): string {
  const labels: Record<string, string> = {
    ADMIN: 'Administrador',
    FINANCIAL: 'Financeiro',
    VIEWER: 'Visualizador',
  };

  return role ? labels[role] ?? role : '—';
}

export function formatPlan(plan?: string | null): string {
  const labels: Record<string, string> = {
    FREE: 'Gratuito',
    STARTER: 'Inicial',
    PRO: 'Profissional',
    ENTERPRISE: 'Empresarial',
  };

  return plan ? labels[plan] ?? plan : '—';
}

export function formatBankProvider(provider?: string | null): string {
  const labels: Record<string, string> = {
    STONE: 'Stone',
    INTER: 'Banco Inter',
    C6: 'C6 Bank',
  };

  return provider ? labels[provider] ?? provider : '—';
}

export function formatPixSource(source?: string | null): string {
  const labels: Record<string, string> = {
    WEBHOOK: 'Webhook',
    SYNC: 'Sincronização',
    STONE: 'Stone',
    MANUAL: 'Manual',
  };

  return source ? labels[source] ?? source : '—';
}

export function formatReconciliationStatus(status?: string | null): string {
  const labels: Record<string, string> = {
    MATCHED: 'Conciliado',
    PENDING: 'Pendente',
    DIVERGENT: 'Divergente',
    MISMATCH: 'Divergente',
    MANUAL: 'Manual',
  };

  return status ? labels[status] ?? status : '—';
}

export function formatReconciliationStatusClass(status?: string | null): string {
  if (!status) {
    return 'badge';
  }

  const normalized = status.toLowerCase().replace('mismatch', 'divergent');
  return `badge badge--${normalized}`;
}

export function formatStoneStatus(status?: string | null): string {
  const labels: Record<string, string> = {
    SETTLED: 'Liquidado',
    PENDING: 'Pendente',
    FAILED: 'Falhou',
    CANCELLED: 'Cancelado',
    CANCELED: 'Cancelado',
  };

  return status ? labels[status.toUpperCase()] ?? status : '—';
}

export function formatDocumentType(type?: string | null): string {
  if (!type) {
    return '—';
  }

  const labels: Record<string, string> = {
    CPF: 'CPF',
    CNPJ: 'CNPJ',
    INDIVIDUAL: 'Pessoa física',
    COMPANY: 'Pessoa jurídica',
  };

  return labels[type.toUpperCase()] ?? type.toUpperCase();
}

export function formatPaymentType(type?: string | null): string {
  if (!type) {
    return '—';
  }

  const labels: Record<string, string> = {
    inbound_pix_payment: 'PIX recebido',
    outbound_pix_payment: 'PIX enviado',
    pix_payment: 'Pagamento PIX',
  };

  const normalized = type.toLowerCase();
  return labels[normalized] ?? type.replaceAll('_', ' ');
}

export function formatAuthMode(mode?: string | null): string {
  const labels: Record<string, string> = {
    API_KEY: 'Chave API',
    OPEN_BANKING: 'Open Banking',
  };

  return mode ? labels[mode] ?? mode : '—';
}

export function formatBusinessModel(model?: string | null): string {
  const labels: Record<string, string> = {
    GATEWAY: 'Gateway',
    SUBACQUIRER: 'Subadquirente',
  };

  return model ? labels[model] ?? model : '—';
}

export function formatEnvironment(env?: string | null): string {
  if (!env) {
    return '—';
  }

  const labels: Record<string, string> = {
    sandbox: 'Homologação',
    production: 'Produção',
    live: 'Produção',
  };

  return labels[env.toLowerCase()] ?? env;
}

export function formatWebhookEvent(event?: string | null): string {
  if (!event) {
    return '—';
  }

  const labels: Record<string, string> = {
    pix_inbound_payment_received: 'PIX recebido',
  };

  return labels[event.toLowerCase()] ?? event.replaceAll('_', ' ');
}
