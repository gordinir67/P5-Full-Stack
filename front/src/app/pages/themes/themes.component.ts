import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { HeaderComponent } from '../../components/header/header.component';

@Component({
  selector: 'app-themes',
  imports: [
      HeaderComponent,
      MatButtonModule,
      MatCardModule,
  ],
  templateUrl: './themes.component.html',
  styleUrl: './themes.component.scss'
})
export class ThemesComponent {

}
