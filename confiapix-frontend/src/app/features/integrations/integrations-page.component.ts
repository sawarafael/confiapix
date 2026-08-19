import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { BankIntegrationApiService } from '../../core/services/api.services';
import { BankProviderCatalogItem } from '../../core/models/api.models';
import { AddBankIntegrationModalComponent } from './add-bank-integration-modal/add-bank-integration-modal.component';

@Component({
  selector: 'app-integrations-page',
  standalone: true,
  imports: [RouterLink, AddBankIntegrationModalComponent],
  templateUrl: './integrations-page.component.html',
  styleUrl: './integrations-page.component.scss',
})
export class IntegrationsPageComponent implements OnInit {
  private readonly bankApi = inject(BankIntegrationApiService);

  readonly loading = signal(true);
  readonly openingWizard = signal(false);
  readonly error = signal<string | null>(null);
  readonly info = signal<string | null>(null);
  readonly providers = signal<BankProviderCatalogItem[]>([]);
  readonly wizardOpen = signal(false);

  readonly configuredProviders = computed(() =>
    this.providers().filter((item) => item.configured),
  );

  readonly availableToAdd = computed(() =>
    this.providers().filter((item) => item.available && !item.configured),
  );

  readonly catalogSize = computed(() => this.providers().length);

  ngOnInit(): void {
    this.loadCatalog();
  }

  canAddIntegration(): boolean {
    return this.availableToAdd().length > 0;
  }

  openWizard(): void {
    if (this.openingWizard()) {
      return;
    }

    this.openingWizard.set(true);
    this.error.set(null);
    this.info.set(null);

    this.bankApi.listCatalog().subscribe({
      next: (res) => {
        this.providers.set(res.data);
        const available = res.data.filter((item) => item.available && !item.configured);

        if (available.length === 0) {
          this.info.set(
            res.data.length <= 3
              ? 'O backend precisa ser atualizado para liberar o catálogo completo de bancos. Execute: docker compose up -d --build na pasta confiapix.'
              : 'Todos os bancos do catálogo já foram integrados. Remova uma integração existente para adicionar outra.',
          );
          this.openingWizard.set(false);
          return;
        }

        this.wizardOpen.set(true);
        this.openingWizard.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.message ?? 'Falha ao carregar bancos disponíveis');
        this.openingWizard.set(false);
      },
    });
  }

  closeWizard(): void {
    this.wizardOpen.set(false);
  }

  onIntegrationSaved(): void {
    this.wizardOpen.set(false);
    this.loadCatalog();
  }

  statusLabel(item: BankProviderCatalogItem): string {
    if (!item.active) return 'Inativo';
    return 'Ativo';
  }

  featureItems(item: BankProviderCatalogItem): Array<{ label: string; enabled: boolean }> {
    return [
      { label: 'Webhook PIX', enabled: item.supportsWebhook },
      { label: 'Sincronização', enabled: item.supportsSync },
      { label: 'Teste de conexão', enabled: item.supportsConnectionTest },
    ];
  }

  providerIcon(item: BankProviderCatalogItem): string {
    if (item.provider === 'STONE') {
      return 'payments';
    }
    return 'account_balance';
  }

  private loadCatalog(): void {
    this.loading.set(true);
    this.error.set(null);

    this.bankApi.listCatalog().subscribe({
      next: (res) => {
        this.providers.set(res.data);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.message ?? 'Falha ao carregar integrações');
        this.loading.set(false);
      },
    });
  }
}
