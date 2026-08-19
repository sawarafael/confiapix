import { Component, OnInit, signal } from '@angular/core';

import { DatePipe, DecimalPipe } from '@angular/common';

import { RouterLink } from '@angular/router';

import { PixApiService } from '../../core/services/api.services';

import { PixTransaction } from '../../core/models/api.models';

import { formatPixSource, formatBankProvider } from '../../core/utils/display-labels';



@Component({

  selector: 'app-pix-page',

  standalone: true,

  imports: [DatePipe, DecimalPipe, RouterLink],

  templateUrl: './pix-page.component.html',

  styleUrl: './table-page.component.scss',

})

export class PixPageComponent implements OnInit {

  readonly loading = signal(true);

  readonly error = signal<string | null>(null);

  readonly items = signal<PixTransaction[]>([]);



  protected readonly formatSource = formatPixSource;
  protected readonly formatProvider = formatBankProvider;



  constructor(private readonly pixApi: PixApiService) {}



  ngOnInit(): void {

    this.pixApi.list().subscribe({

      next: (res) => {

        this.items.set(res.data.content);

        this.loading.set(false);

      },

      error: (err) => {

        this.error.set(err?.error?.message ?? 'Falha ao listar PIX');

        this.loading.set(false);

      },

    });

  }

}


