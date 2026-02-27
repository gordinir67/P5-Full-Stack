import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';

import { HeaderComponent } from '../../components/header/header.component';
import { ThemeService } from '../../services/theme.service';
import type { ThemeDto } from '../../models/theme.models';

@Component({
  selector: 'app-themes',
  standalone: true,
  imports: [
    CommonModule,
    HeaderComponent,
    MatButtonModule,
    MatCardModule,
  ],
  templateUrl: './themes.component.html',
  styleUrl: './themes.component.scss',
})
export class ThemesComponent {
  private themesApi = inject(ThemeService);

  themes: ThemeDto[] = [];
  isLoading = false;
  error = '';

  ngOnInit(): void {
    this.load();
  }

  subscribe(theme: ThemeDto): void {
    if (theme.subscribed) return;

    this.error = '';
    this.isLoading = true;

    this.themesApi.subscribe(theme.id).subscribe({
      next: () => this.load(),
      error: (err: HttpErrorResponse) => {
        this.isLoading = false;
        this.error = (err.error as { message: string }).message;
      },
    });
  }

  private load(): void {
    this.isLoading = true;
    this.error = '';

    this.themesApi.list().subscribe({
      next: (items) => {
        this.themes = items;
        this.isLoading = false;
      },
      error: (err: HttpErrorResponse) => {
        this.isLoading = false;
        this.error = (err.error as { message: string }).message;
      },
    });
  }
}