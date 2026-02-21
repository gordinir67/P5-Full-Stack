import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { ArticlesComponent } from './pages/articles/articles.component';
import { ArticleCreateComponent } from './pages/article-create/article-create.component';
import { ArticleDetailComponent } from './pages/article-detail/article-detail.component';
import { MeComponent } from './pages/me/me.component';
import { ThemesComponent } from './pages/themes/themes.component';

export const routes: Routes = [
      { path: '', component: HomeComponent },
      { path: 'login', component: LoginComponent },
      { path: 'register', component: RegisterComponent },  
      { path: 'articles', component: ArticlesComponent },   
      { path: 'articles/create', component: ArticleCreateComponent },
      { path: 'articles/:id', component: ArticleDetailComponent },
      { path: 'themes', component: ThemesComponent },        
      { path: 'me', component: MeComponent },
      { path: '**', redirectTo: '' },      
];
