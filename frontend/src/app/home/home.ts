import { Component, signal, OnInit, ChangeDetectorRef } from '@angular/core';
import { ApiService, HealthResponse } from '../services/api.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.html',
  styleUrl: './home.scss'
})
export class HomeComponent implements OnInit {
  health = signal<HealthResponse | null>(null);
  error = signal<string | null>(null);
  loading = signal(true);

  constructor(private apiService: ApiService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.apiService.getHealth().subscribe({
      next: (data) => {
        this.health.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('Could not connect to backend: ' + err.message);
        this.loading.set(false);
      }
    });
  }
}
