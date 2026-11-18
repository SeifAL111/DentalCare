import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-dashboard',
  imports: [MatCardModule],
  template: `
    <section class="page">
      <h1>DentalCare</h1>
      <p>Frontend foundation ready. Business screens will consume the API Gateway.</p>
      <mat-card appearance="outlined">
        <mat-card-header>
          <mat-card-title>API Gateway</mat-card-title>
          <mat-card-subtitle>http://localhost:8080</mat-card-subtitle>
        </mat-card-header>
        <mat-card-content>
          <p>No business endpoints are wired yet. Use feature modules under <code>src/app/features/</code>.</p>
        </mat-card-content>
      </mat-card>
    </section>
  `,
  styles: `
    .page {
      max-width: 720px;
      margin: 2rem auto;
      padding: 0 1rem;
      display: grid;
      gap: 1rem;
    }
    h1 {
      margin: 0;
      font: var(--mat-sys-headline-medium);
    }
  `,
})
export class Dashboard {}
