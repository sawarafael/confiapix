import { Component } from '@angular/core';

import { RouterLink } from '@angular/router';

import { AuthService } from '../../core/auth/auth.service';

import { formatUserRole } from '../../core/utils/display-labels';



@Component({

  selector: 'app-settings-page',

  standalone: true,

  imports: [RouterLink],

  template: `

    <section class="page">

      <div class="page__header">

        <h2>Configurações</h2>

        <p>Preferências da conta e da organização.</p>

      </div>



      <article class="card">

        <div class="card__header">

          <h3>Conta</h3>

          <a routerLink="/profile" class="btn btn--ghost">Editar perfil</a>

        </div>

        <dl class="info-list">

          <div><dt>Nome</dt><dd>{{ auth.displayName() }}</dd></div>

          <div><dt>E-mail</dt><dd>{{ auth.user()?.email }}</dd></div>

          <div><dt>Perfil</dt><dd>{{ formatRole(auth.user()?.role) }}</dd></div>

          <div><dt>ID da organização</dt><dd class="mono">{{ auth.user()?.tenantId }}</dd></div>

        </dl>

      </article>

    </section>

  `,

  styles: `

    .page__header p {

      margin: 0.35rem 0 0;

      color: var(--text-muted);

    }



    .card__header {

      display: flex;

      align-items: center;

      justify-content: space-between;

      gap: 1rem;

      margin-bottom: 1rem;

    }



    h3 {

      margin: 0;

    }



    .info-list {

      margin: 0;

      display: grid;

      gap: 0.85rem;

    }



    .info-list div {

      display: grid;

      grid-template-columns: 140px 1fr;

      gap: 1rem;

    }



    dt {

      color: var(--text-muted);

      font-weight: 500;

    }



    dd {

      margin: 0;

      font-weight: 600;

    }



    .mono {

      font-family: ui-monospace, monospace;

      font-size: 0.85rem;

      word-break: break-all;

    }



    @media (max-width: 640px) {

      .card__header {

        flex-direction: column;

        align-items: flex-start;

      }



      .info-list div {

        grid-template-columns: 1fr;

        gap: 0.25rem;

      }

    }

  `,

})

export class SettingsPageComponent {

  protected readonly formatRole = formatUserRole;



  constructor(protected readonly auth: AuthService) {}

}

