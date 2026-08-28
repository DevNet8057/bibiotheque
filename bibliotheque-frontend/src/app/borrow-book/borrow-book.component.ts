import { Component, OnInit } from '@angular/core';
import { Books } from '../_model/books';
import { Borrow } from '../_model/borrow';
import { BooksService } from '../_service/books.service';
import { BorrowService } from '../_service/borrow.service';
import { UserAuthService } from '../_service/user-auth.service';

@Component({
  selector: 'app-borrow-book',
  templateUrl: './borrow-book.component.html',
  styleUrls: ['./borrow-book.component.css']
})
export class BorrowBookComponent implements OnInit {

  books: Books[] = [];
  loading = true;
  errorMessage = '';
  feedbackMessage = '';
  borrowingId: number | null = null;

  constructor(
    private booksService: BooksService,
    private userAuthService: UserAuthService,
    private borrowService: BorrowService,
  ) { }

  userId = this.userAuthService.getUserId();

  ngOnInit(): void {
    this.loadBooks();
  }

  loadBooks(): void {
    this.loading = true;
    this.errorMessage = '';
    this.booksService.getBooksList().subscribe(data =>{
      this.books = data;
      this.loading = false;
    }, () => { this.loading = false; this.errorMessage = 'Nous ne pouvons pas charger les livres disponibles. Vérifiez que le serveur est démarré puis réessayez.'; });
  }

  borrow: Borrow = new Borrow();

  borrowBook(bookId: number) {
    this.borrow.bookId = bookId;
    this.borrow.userId = this.userId;
    this.borrowingId = bookId;
    this.errorMessage = '';
    this.feedbackMessage = '';
    this.borrowService.borrowBook(this.borrow).subscribe(data => {
      this.borrowingId = null;
      this.feedbackMessage = 'Le livre a été emprunté avec succès.';
      this.loadBooks();
    },
    () => { this.borrowingId = null; this.errorMessage = 'Cet emprunt n’a pas pu être enregistré. Le livre n’est peut-être plus disponible.'; });
  }
}
