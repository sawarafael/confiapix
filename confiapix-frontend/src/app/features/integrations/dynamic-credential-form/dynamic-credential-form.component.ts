import { Component, computed, input } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CredentialFieldSchema } from '../../../core/models/bank-integration.models';

interface CredentialFormSection {
  title: string | null;
  icon: string;
  fields: CredentialFieldSchema[];
}

@Component({
  selector: 'app-dynamic-credential-form',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './dynamic-credential-form.component.html',
  styleUrl: './dynamic-credential-form.component.scss',
})
export class DynamicCredentialFormComponent {
  readonly form = input.required<FormGroup>();
  readonly fields = input.required<CredentialFieldSchema[]>();
  readonly secretOptionalHint = input(false);

  readonly sections = computed(() => groupFieldsBySection(this.fields()));

  sectionIcon(title: string | null): string {
    if (!title) {
      return 'tune';
    }
    const normalized = title.toLowerCase();
    if (normalized.includes('certificado') || normalized.includes('mtls')) {
      return 'verified_user';
    }
    if (normalized.includes('conta')) {
      return 'account_balance_wallet';
    }
    if (normalized.includes('stone') || normalized.includes('c6') || normalized.includes('inter')) {
      return 'vpn_key';
    }
    return 'key';
  }
}

function groupFieldsBySection(fields: CredentialFieldSchema[]): CredentialFormSection[] {
  const sections: CredentialFormSection[] = [];
  let current: CredentialFormSection = { title: null, icon: 'tune', fields: [] };

  for (const field of fields) {
    if (field.type === 'section') {
      if (current.title || current.fields.length > 0) {
        sections.push(current);
      }
      current = { title: field.label, icon: 'key', fields: [] };
      continue;
    }
    current.fields.push(field);
  }

  if (current.title || current.fields.length > 0) {
    sections.push(current);
  }

  return sections;
}
