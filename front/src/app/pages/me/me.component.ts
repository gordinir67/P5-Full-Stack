import { Component } from '@angular/core';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCardModule } from '@angular/material/card';
import { HeaderComponent } from '../../components/header/header.component';

@Component({
  selector: 'app-me',
  imports: [
      HeaderComponent,
      MatDividerModule,
      MatFormFieldModule,
      MatInputModule,
      MatCardModule,
  ],
  templateUrl: './me.component.html',
  styleUrl: './me.component.scss'
})
export class MeComponent {

}

