export interface UserLightDto {
  id: number;
  name: string;
}

export interface UserDto {
  id: number;
  email: string;
  name: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface UpdateUserRequest {
  email?: string;
  name?: string;
  password: string;
}
