import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';

import { HeaderComponent } from '../../components/header/header.component';
import { ArticleService } from '../../services/article.service';
import type { ArticleDto } from '../../models/article.models';

@Component({
  selector: 'app-article-detail',
  standalone: true,
  imports: [
    CommonModule,
    HeaderComponent,
    RouterLink,
    MatButtonModule,
    MatDividerModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    ReactiveFormsModule,
  ],
  templateUrl: './article-detail.component.html',
  styleUrl: './article-detail.component.scss',
})
export class ArticleDetailComponent {
  private route = inject(ActivatedRoute);
  private api = inject(ArticleService);
  private fb = inject(FormBuilder);

  article: ArticleDto | null = null;
  isLoading = false;
  error = '';

  commentForm = this.fb.nonNullable.group({
    description: ['', [Validators.required, Validators.maxLength(2000)]],
  });

  isSending = false;

  private articleId = Number(this.route.snapshot.paramMap.get('id'));

  ngOnInit(): void {
    this.load();
  }

  sendComment(): void {
    this.error = '';

    if (this.commentForm.invalid) {
      this.commentForm.markAllAsTouched();
      return;
    }
    if (this.isSending) return;

    this.isSending = true;
    const { description } = this.commentForm.getRawValue();

    this.api.addComment(this.articleId, { description }).subscribe({
      next: (comment) => {
        this.isSending = false;
        this.commentForm.reset();

        if (this.article) {
          this.article = {
            ...this.article,
            comments: [...(this.article.comments ?? []), comment],
          };
        } else {
          this.load();
        }
      },
      error: (err: HttpErrorResponse) => {
        this.isSending = false;
        this.error = (err.error as { message: string }).message; // message backend
      },
    });
  }

  private load(): void {
    this.isLoading = true;
    this.error = '';

    this.api.getById(this.articleId).subscribe({
      next: (a) => {
        this.article = a;
        this.isLoading = false;
      },
      error: (err: HttpErrorResponse) => {
        this.isLoading = false;
        this.error = (err.error as { message: string }).message; // message backend
      },
    });
  }
}