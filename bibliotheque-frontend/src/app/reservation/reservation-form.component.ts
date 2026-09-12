import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { Books } from '../_model/books';
import { Users } from '../_model/users';
import { ReservationRequest } from '../_model/reservation';

@Component({ selector: 'app-reservation-form', templateUrl: './reservation-form.component.html', styleUrls: ['./reservation-form.component.css'] })
export class ReservationFormComponent implements OnChanges {
  @Input() books: Books[] = [];
  @Input() users: Users[] = [];
  @Input() adherentLocked = false;
  @Input() currentAdherentId: number | null = null;
  @Input() saving = false;
  @Input() errorMessage = '';
  @Output() submitted = new EventEmitter<ReservationRequest>();
  livreId: number | null = null;
  adherentId: number | null = null;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['currentAdherentId'] && this.adherentLocked) {
      this.adherentId = this.currentAdherentId;
    }
  }

  submit(): void {
    if (this.livreId !== null && this.adherentId !== null) {
      this.submitted.emit({ livreId: this.livreId, adherentId: this.adherentId });
    }
  }

  reset(): void { this.livreId = null; this.adherentId = this.adherentLocked ? this.currentAdherentId : null; }
  bookStatus(book: Books): string { return book.noOfCopies > 0 ? 'DISPONIBLE' : 'INDISPONIBLE'; }
  get selectedBook(): Books | undefined { return this.books.find(book => book.bookId === this.livreId); }
}
