import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCardModule } from '@angular/material/card';

import { HeaderComponent } from '../../components/header/header.component';
import { MeService } from '../../services/me.service';
import { ThemeService } from '../../services/theme.service';
import type { ThemeDto } from '../../models/theme.models';
import type { UserDto } from '../../models/user.models';

@Component({
  selector: 'app-me',
  standalone: true,
  imports: [
    CommonModule,
    HeaderComponent,
    MatDividerModule,
    MatFormFieldModule,
    MatInputModule,
    MatCardModule,
    ReactiveFormsModule,
  ],
  templateUrl: './me.component.html',
  styleUrl: './me.component.scss',
})
export class MeComponent {
  private meApi = inject(MeService);
  private themesApi = inject(ThemeService);
  private fb = inject(FormBuilder);

  me: UserDto | null = null;
  subscriptions: ThemeDto[] = [];

  private readonly passwordRegex =
    /^(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;

  form = this.fb.nonNullable.group({
    name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(255)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(255)]],
    password: ['', [Validators.required, Validators.pattern(this.passwordRegex)]],
  });

  isSaving = false;
  error = '';
  success = '';

  ngOnInit(): void {
    this.loadMe();
    this.loadSubscriptions();
  }

  save(): void {
    this.error = '';
    this.success = '';

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    if (this.isSaving) return;

    this.isSaving = true;
    const { name, email, password } = this.form.getRawValue();

    this.meApi.updateMe({ name, email, password }).subscribe({
      next: (updated) => {
        this.isSaving = false;
        this.me = updated;
        this.form.patchValue({ password: '' });
        this.success = 'Profil mis à jour.';
      },
      error: (err: HttpErrorResponse) => {
        this.isSaving = false;
        this.error = (err.error as { message: string }).message; // message backend
      },
    });
  }

  unsubscribe(theme: ThemeDto): void {
    this.error = '';
    this.success = '';

    this.themesApi.unsubscribe(theme.id).subscribe({
      next: () => this.loadSubscriptions(),
      error: (err: HttpErrorResponse) => {
        this.error = (err.error as { message: string }).message; // message backend
      },
    });
  }

  private loadMe(): void {
    this.error = '';
    this.success = '';

    this.meApi.getMe().subscribe({
      next: (me) => {
        this.me = me;
        this.form.patchValue({ name: me.name, email: me.email });
      },
      error: (err: HttpErrorResponse) => {
        this.error = (err.error as { message: string }).message; // message backend
      },
    });
  }

  private loadSubscriptions(): void {
    this.error = '';
    this.success = '';

    // /themes contient subscribed
    this.themesApi.list().subscribe({
      next: (themes) => (this.subscriptions = themes.filter((t) => t.subscribed)),
      error: (err: HttpErrorResponse) => {
        this.error = (err.error as { message: string }).message; // message backend
      },
    });
  }
}