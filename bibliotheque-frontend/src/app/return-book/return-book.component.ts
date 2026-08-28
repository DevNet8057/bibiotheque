import { Component, OnInit } from '@angular/core';
import { Borrow } from '../_model/borrow';
import { BorrowService } from '../_service/borrow.service';
import { UserAuthService } from '../_service/user-auth.service';

@Component({
  selector: 'app-return-book',
  templateUrl: './return-book.component.html',
  styleUrls: ['./return-book.component.css']
})
export class ReturnBookComponent implements OnInit {

  borrow: Borrow[] = [];
  loading = true;
  errorMessage = '';
  feedbackMessage = '';
  returningId: number | null = null;

  constructor(
    private borrowService: BorrowService,
    private userAuthService: UserAuthService
  ) { }

  userId = this.userAuthService.getUserId();

  ngOnInit(): void {
    this.loadBorrowedBooks();
  }

  loadBorrowedBooks(): void {
    this.loading = true;
    this.errorMessage = '';
    this.borrowService.getBooksBorrowedByUser(this.userId).subscribe(data => {
      this.borrow = data;
      this.loading = false;
    }, () => { this.loading = false; this.errorMessage = 'Nous ne pouvons pas charger vos emprunts. Vérifiez que le serveur est démarré puis réessayez.'; });
  }

  brw: Borrow = new Borrow();
  public returnBook(borrowId: number) {
    this.brw.borrowId = borrowId;
    this.returningId = borrowId;
    this.errorMessage = '';
    this.borrowService.returnBook(this.brw).subscribe(data => {
      this.returningId = null;
      this.feedbackMessage = 'Le retour a été enregistré avec succès.';
      this.loadBorrowedBooks();
    },
    () => { this.returningId = null; this.errorMessage = 'Le retour n’a pas pu être enregistré. Actualisez la liste puis réessayez.'; });
  }

}
