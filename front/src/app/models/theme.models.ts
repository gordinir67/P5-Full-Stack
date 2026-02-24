export interface ThemeLightDto {
  id: number;
  title: string;
}

export interface ThemeDto extends ThemeLightDto {
  description: string;
  subscribed: boolean;
}
