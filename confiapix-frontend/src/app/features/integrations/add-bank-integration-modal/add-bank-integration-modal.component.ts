import { Component, effect, inject, input, output, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { BankIntegrationApiService } from '../../../core/services/api.services';
import { BankProviderCatalogItem } from '../../../core/models/api.models';
import {
  buildCredentialForm,
  buildIntegrationPayload,
  defaultCredentialValues,
  integrationReserveNotice,
  reviewEntries,
  schemaFields,
  validateCredentialForm,
} from '../../../core/utils/credential-form.util';
import { DynamicCredentialFormComponent } from '../dynamic-credential-form/dynamic-credential-form.component';

type WizardStep = 1 | 2 | 3;

@Component({
  selector: 'app-add-bank-integration-modal',
  standalone: true,
  imports: [ReactiveFormsModule, DynamicCredentialFormComponent],
  templateUrl: './add-bank-integration-modal.component.html',
  styleUrl: './add-bank-integration-modal.component.scss',
})
export class AddBankIntegrationModalComponent {
  private readonly fb = inject(FormBuilder);
  private readonly bankApi = inject(BankIntegrationApiService);

  readonly open = input(false);
  readonly providers = input<BankProviderCatalogItem[]>([]);

  readonly closed = output<void>();
  readonly saved = output<void>();

  readonly step = signal<WizardStep>(1);
  readonly selectedProvider = signal<BankProviderCatalogItem | null>(null);
  readonly saving = signal(false);
  readonly error = signal<string | null>(null);
  readonly bankSearch = signal('');

  form: FormGroup = this.fb.group({});

  constructor() {
    effect(() => {
      if (!this.open()) {
        this.resetWizard();
      }
    });
  }

  filteredProviders(): BankProviderCatalogItem[] {
    const query = this.bankSearch().trim().toLowerCase();
    const items = this.selectableProviders();
    if (!query) {
      return items;
    }
    return items.filter((item) => {
      const haystack = [
        item.displayName,
        item.description,
        item.provider,
        item.compe ?? '',
        item.ispb ?? '',
      ]
        .join(' ')
        .toLowerCase();
      return haystack.includes(query);
    });
  }

  selectableProviders(): BankProviderCatalogItem[] {
    return this.providers().filter((item) => item.available && !item.configured);
  }

  canAddIntegration(): boolean {
    return this.selectableProviders().length > 0;
  }

  selectProvider(item: BankProviderCatalogItem): void {
    this.selectedProvider.set(item);
    this.error.set(null);
    this.form = buildCredentialForm(this.fb, item.credentialSchema);
    this.form.reset(defaultCredentialValues(item.credentialSchema));
  }

  nextStep(): void {
    if (this.step() === 1) {
      if (!this.selectedProvider()) {
        this.error.set('Selecione um banco para continuar');
        return;
      }
      this.error.set(null);
      this.step.set(2);
      return;
    }

    if (this.step() === 2) {
      const provider = this.selectedProvider();
      const values = this.form.getRawValue() as Record<string, string>;
      const validationError = validateCredentialForm(provider?.credentialSchema, values);
      if (this.form.invalid || validationError) {
        this.form.markAllAsTouched();
        this.error.set(validationError ?? 'Preencha os campos obrigatórios');
        return;
      }

      this.error.set(null);
      this.step.set(3);
    }
  }

  previousStep(): void {
    this.error.set(null);
    if (this.step() === 3) {
      this.step.set(2);
      return;
    }
    if (this.step() === 2) {
      this.step.set(1);
    }
  }

  close(): void {
    this.closed.emit();
  }

  save(): void {
    const provider = this.selectedProvider();
    if (!provider) {
      return;
    }

    const values = this.form.getRawValue() as Record<string, string>;
    const validationError = validateCredentialForm(provider.credentialSchema, values);
    if (this.form.invalid || validationError) {
      this.form.markAllAsTouched();
      this.error.set(validationError ?? 'Preencha os campos obrigatórios');
      return;
    }

    this.saving.set(true);
    this.error.set(null);

    this.bankApi.saveCredentials(provider.provider, buildIntegrationPayload(provider.credentialSchema, values)).subscribe({
      next: () => {
        this.saving.set(false);
        this.saved.emit();
        this.close();
      },
      error: (err) => {
        this.error.set(err?.error?.message ?? 'Falha ao salvar integração');
        this.saving.set(false);
      },
    });
  }

  providerIcon(item: BankProviderCatalogItem): string {
    if (item.provider === 'STONE') {
      return 'payments';
    }
    return 'account_balance';
  }

  reserveNotice(): string | null {
    return integrationReserveNotice(this.selectedProvider());
  }

  credentialFields() {
    return schemaFields(this.selectedProvider()?.credentialSchema);
  }

  reviewItems() {
    const provider = this.selectedProvider();
    return reviewEntries(provider?.credentialSchema, this.form.getRawValue() as Record<string, string>);
  }

  stepLabel(current: WizardStep): string {
    switch (current) {
      case 1:
        return 'Escolher banco';
      case 2:
        return 'Credenciais';
      default:
        return 'Confirmar';
    }
  }

  private resetWizard(): void {
    this.step.set(1);
    this.selectedProvider.set(null);
    this.saving.set(false);
    this.error.set(null);
    this.bankSearch.set('');
    this.form = this.fb.group({});
  }
}
