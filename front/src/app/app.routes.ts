import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { ArticlesComponent } from './pages/articles/articles.component';
import { ArticleCreateComponent } from './pages/article-create/article-create.component';
import { ArticleDetailComponent } from './pages/article-detail/article-detail.component';
import { MeComponent } from './pages/me/me.component';
import { ThemesComponent } from './pages/themes/themes.component';
import { authGuard } from './guards/auth.guard';
import { guestGuard } from './guards/guest.guard';

export const routes: Routes = [
      { path: '', component: HomeComponent, canActivate: [guestGuard] },
      { path: 'login', component: LoginComponent, canActivate: [guestGuard] },
      { path: 'register', component: RegisterComponent, canActivate: [guestGuard] },  
      { path: 'articles', component: ArticlesComponent, canActivate: [authGuard] },   
      { path: 'articles/create', component: ArticleCreateComponent, canActivate: [authGuard] },
      { path: 'articles/:id', component: ArticleDetailComponent, canActivate: [authGuard] },
      { path: 'themes', component: ThemesComponent, canActivate: [authGuard] },        
      { path: 'me', component: MeComponent, canActivate: [authGuard] },
      { path: '**', redirectTo: '' },      
];
