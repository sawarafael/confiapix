import { Component, OnInit, signal, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { TenantAccessApiService } from '../../core/services/api.services';
import { TenantAccess } from '../../core/models/api.models';
import { formatPlan } from '../../core/utils/display-labels';

@Component({
  selector: 'app-tenant-access-page',
  standalone: true,
  imports: [ReactiveFormsModule, DatePipe],
  templateUrl: './tenant-access-page.component.html',
  styleUrl: './tenant-access-page.component.scss',
})
export class TenantAccessPageComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly tenantAccessApi = inject(TenantAccessApiService);

  readonly loading = signal(true);
  readonly saving = signal(false);
  readonly error = signal<string | null>(null);
  readonly success = signal<string | null>(null);
  readonly items = signal<TenantAccess[]>([]);
  readonly showForm = signal(false);
  readonly editingId = signal<string | null>(null);

  protected readonly formatPlan = formatPlan;

  readonly createForm = this.fb.nonNullable.group({
    tenantName: ['', Validators.required],
    plan: ['FREE', Validators.required],
    adminName: ['', Validators.required],
    adminEmail: ['', [Validators.required, Validators.email]],
    adminPassword: ['', [Validators.required, Validators.minLength(6)]],
  });

  readonly editForm = this.fb.nonNullable.group({
    tenantName: ['', Validators.required],
    plan: ['FREE', Validators.required],
    active: [true],
  });

  ngOnInit(): void {
    this.loadItems();
  }

  loadItems(): void {
    this.loading.set(true);
    this.tenantAccessApi.list().subscribe({
      next: (res) => {
        this.items.set(res.data);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.message ?? 'Falha ao carregar empresas');
        this.loading.set(false);
      },
    });
  }

  openCreate(): void {
    this.editingId.set(null);
    this.createForm.reset({ plan: 'FREE' });
    this.showForm.set(true);
    this.error.set(null);
    this.success.set(null);
  }

  openEdit(item: TenantAccess): void {
    this.editingId.set(item.id);
    this.editForm.patchValue({
      tenantName: item.name,
      plan: item.plan,
      active: item.active,
    });
    this.showForm.set(true);
    this.error.set(null);
    this.success.set(null);
  }

  cancelForm(): void {
    this.showForm.set(false);
    this.editingId.set(null);
  }

  submitCreate(): void {
    if (this.createForm.invalid) {
      this.createForm.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    this.tenantAccessApi.create(this.createForm.getRawValue()).subscribe({
      next: () => {
        this.success.set('Empresa provisionada com sucesso');
        this.showForm.set(false);
        this.saving.set(false);
        this.loadItems();
      },
      error: (err) => {
        this.error.set(err?.error?.message ?? 'Falha ao criar acesso');
        this.saving.set(false);
      },
    });
  }

  submitEdit(): void {
    const id = this.editingId();
    if (!id || this.editForm.invalid) {
      this.editForm.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    this.tenantAccessApi.update(id, this.editForm.getRawValue()).subscribe({
      next: () => {
        this.success.set('Acesso atualizado');
        this.showForm.set(false);
        this.editingId.set(null);
        this.saving.set(false);
        this.loadItems();
      },
      error: (err) => {
        this.error.set(err?.error?.message ?? 'Falha ao atualizar');
        this.saving.set(false);
      },
    });
  }
}
