import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { MatRippleModule } from '@angular/material/core';
import { MatCardModule } from '@angular/material/card';
import { RouterLink } from '@angular/router';
import { HeaderComponent } from '../../components/header/header.component';
import { ArticleService } from '../../services/article.service';
import type { ArticleLightDto } from '../../models/article.models';

@Component({
  selector: 'app-articles',
  standalone: true,
  imports: [
    CommonModule,
    HeaderComponent,
    RouterLink,
    MatRippleModule,
    MatCardModule,
  ],
  templateUrl: './articles.component.html',
  styleUrl: './articles.component.scss',
})
export class ArticlesComponent implements OnInit {
  articles: ArticleLightDto[] = [];
  sort: 'asc' | 'desc' = 'desc';
  isLoading = false;
  error = '';

  constructor(private readonly articlesApi: ArticleService) {}

  ngOnInit(): void {
    this.load();
  }

  onSortDirChange(event: Event): void {
    const checked = (event.target as HTMLInputElement | null)?.checked ?? false;
    this.sort = checked ? 'asc' : 'desc';
    this.load();
  }

  private load(): void {
    this.error = '';
    this.isLoading = true;

    this.articlesApi.list(this.sort).subscribe({
      next: (items) => {
        this.articles = items;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
        this.error = 'Impossible de charger les articles.';
      },
    });
  }
}