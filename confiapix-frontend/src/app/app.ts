import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  template: `
    <div class="app-route">
      <router-outlet />
    </div>
  `,
  styles: `
    :host {
      display: block;
      height: 100%;
    }

    .app-route {
      display: block;
      height: 100%;
    }
  `,
})
export class App {}
