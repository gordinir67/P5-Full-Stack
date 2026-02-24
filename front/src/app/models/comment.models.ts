import type { UserLightDto } from './user.models';

export interface CommentDto {
  id: number;
  description: string;
  createdAt?: string;
  updatedAt?: string;
  user?: UserLightDto;
}

export interface CreateCommentRequest {
  description: string;
}
