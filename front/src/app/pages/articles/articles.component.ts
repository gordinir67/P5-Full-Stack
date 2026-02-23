import { Component } from '@angular/core';
import { MatRippleModule } from '@angular/material/core';
import { MatCardModule } from '@angular/material/card';
import { RouterLink } from '@angular/router';
import { HeaderComponent } from '../../components/header/header.component';

@Component({
  selector: 'app-articles',
  imports: [
      HeaderComponent,
      RouterLink,
      MatRippleModule,
      MatCardModule,
  ],
  templateUrl: './articles.component.html',
  styleUrl: './articles.component.scss'
})
export class ArticlesComponent {
onSortDirChange(event: Event) {

}

}
