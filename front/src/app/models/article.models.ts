import type { ThemeLightDto } from './theme.models';
import type { UserLightDto } from './user.models';
import type { CommentDto } from './comment.models';

export interface ArticleLightDto {
  id: number;
  title: string;
  description: string;
  createdAt?: string;
  user?: UserLightDto;
  theme?: ThemeLightDto;
}

export interface ArticleDto extends ArticleLightDto {
  updatedAt?: string;
  comments?: CommentDto[];
}

export interface CreateArticleRequest {
  themeId: number;
  title: string;
  description: string;
}
