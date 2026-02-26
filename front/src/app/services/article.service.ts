import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import type { ArticleDto, ArticleLightDto, CreateArticleRequest } from '../models/article.models';
import type { CommentDto, CreateCommentRequest } from '../models/comment.models';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

@Injectable({ providedIn: 'root' })
export class ArticleService {
  private readonly http = inject(HttpClient);
  private baseUrl = environment.baseUrl;
  
  list(sort: 'asc' | 'desc' = 'desc'): Observable<ArticleLightDto[]> {
    const params = new HttpParams().set('sort', sort);
    return this.http.get<ArticleLightDto[]>(`${this.baseUrl}/articles`, { params });
  }

  getById(id: number): Observable<ArticleDto> {
    return this.http.get<ArticleDto>(`${this.baseUrl}/articles/${id}`);
  }

  create(payload: CreateArticleRequest): Observable<ArticleDto> {
    return this.http.post<ArticleDto>(`${this.baseUrl}/articles`, payload);
  }

  addComment(articleId: number, payload: CreateCommentRequest): Observable<CommentDto> {
    return this.http.post<CommentDto>(`${this.baseUrl}/articles/${articleId}/comments`, payload);
  }
}
