import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';

import { HeaderComponent } from '../../components/header/header.component';
import { ThemeService } from '../../services/theme.service';
import { ArticleService } from '../../services/article.service';
import type { ThemeDto } from '../../models/theme.models';

@Component({
  selector: 'app-article-create',
  standalone: true,
  imports: [
    CommonModule,
    HeaderComponent,
    RouterLink,
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatSelectModule,
    ReactiveFormsModule,
  ],
  templateUrl: './article-create.component.html',
  styleUrl: './article-create.component.scss',
})
export class ArticleCreateComponent {
  private fb = inject(FormBuilder);
  private themesApi = inject(ThemeService);
  private articlesApi = inject(ArticleService);
  private router = inject(Router);

  themes: ThemeDto[] = [];
  isLoadingThemes = false;

  form = this.fb.nonNullable.group({
    themeId: [null as number | null, [Validators.required]],
    title: ['', [Validators.required, Validators.maxLength(255)]],
    description: ['', [Validators.required, Validators.maxLength(2000)]],
  });

  isSubmitting = false;
  error = '';

  ngOnInit(): void {
    this.loadThemes();
  }

  submit(): void {
    this.error = '';

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    if (this.isSubmitting) return;

    this.isSubmitting = true;

    const { themeId, title, description } = this.form.getRawValue();

    this.articlesApi.create({ themeId: themeId!, title, description }).subscribe({
      next: (created) => {
        this.isSubmitting = false;
        this.router.navigate(['/articles', created.id]);
      },
      error: (err: HttpErrorResponse) => {
        this.isSubmitting = false;
        this.error = (err.error as { message: string }).message; // message backend
      },
    });
  }

  private loadThemes(): void {
    this.isLoadingThemes = true;
    this.error = '';

    this.themesApi.list().subscribe({
      next: (items) => {
        this.themes = items;
        this.isLoadingThemes = false;
      },
      error: (err: HttpErrorResponse) => {
        this.isLoadingThemes = false;
        this.error = (err.error as { message: string }).message; // message backend
      },
    });
  }
}