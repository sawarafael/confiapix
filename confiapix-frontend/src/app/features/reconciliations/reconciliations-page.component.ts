import { Component, OnInit, signal } from '@angular/core';

import { DatePipe, DecimalPipe } from '@angular/common';

import { ReconciliationApiService } from '../../core/services/api.services';

import { Reconciliation } from '../../core/models/api.models';

import {

  formatReconciliationStatus,

  formatReconciliationStatusClass,

} from '../../core/utils/display-labels';



@Component({

  selector: 'app-reconciliations-page',

  standalone: true,

  imports: [DatePipe, DecimalPipe],

  templateUrl: './reconciliations-page.component.html',

  styleUrl: '../pix/table-page.component.scss',

})

export class ReconciliationsPageComponent implements OnInit {

  readonly loading = signal(true);

  readonly error = signal<string | null>(null);

  readonly items = signal<Reconciliation[]>([]);



  protected readonly formatStatus = formatReconciliationStatus;

  protected readonly statusClass = formatReconciliationStatusClass;



  constructor(private readonly reconciliationApi: ReconciliationApiService) {}



  ngOnInit(): void {

    this.reconciliationApi.list().subscribe({

      next: (res) => {

        this.items.set(res.data);

        this.loading.set(false);

      },

      error: (err) => {

        this.error.set(err?.error?.message ?? 'Falha ao listar conciliações');

        this.loading.set(false);

      },

    });

  }

}


