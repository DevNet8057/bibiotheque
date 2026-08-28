import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Books } from '../_model/books';
import { Users } from '../_model/users';
import { ReservationRequest } from '../_model/reservation';

@Component({ selector: 'app-reservation-form', templateUrl: './reservation-form.component.html', styleUrls: ['./reservation-form.component.css'] })
export class ReservationFormComponent {
  @Input() books: Books[] = [];
  @Input() users: Users[] = [];
  @Input() saving = false;
  @Input() errorMessage = '';
  @Output() submitted = new EventEmitter<ReservationRequest>();
  livreId: number | null = null;
  adherentId: number | null = null;

  submit(): void {
    if (this.livreId !== null && this.adherentId !== null) {
      this.submitted.emit({ livreId: this.livreId, adherentId: this.adherentId });
    }
  }

  reset(): void { this.livreId = null; this.adherentId = null; }
}
