import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Books } from '../_model/books'
import { BooksService } from '../_service/books.service';

@Component({
  selector: 'app-books-list',
  templateUrl: './books-list.component.html',
  styleUrls: ['./books-list.component.css']
})
export class BooksListComponent implements OnInit {
  books: Books[] = [];
  loading = true;
  errorMessage = '';
  feedbackMessage = '';
  pendingDelete: Books | null = null;

  constructor(private booksService: BooksService,
    private router: Router) { }

  ngOnInit(): void {
    this.loadBooks();
  }

  loadBooks(): void {
    this.loading = true;
    this.errorMessage = '';
    this.booksService.getBooksList().subscribe(data =>{
      this.books = data;
      this.loading = false;
    }, () => {
      this.loading = false;
      this.errorMessage = 'Nous ne pouvons pas charger les livres. Vérifiez que le serveur est démarré, puis réessayez.';
    });
  }

  updateBook(bookId: number) {
    this.router.navigate(['update-book', bookId ]);
  }

  askDelete(book: Books): void { this.pendingDelete = book; }
  closeDelete(): void { this.pendingDelete = null; }
  confirmDelete(): void {
    if (!this.pendingDelete) { return; }
    const bookId = this.pendingDelete.bookId;
    this.pendingDelete = null;
    this.booksService.deleteBook(bookId).subscribe(() => {
      this.feedbackMessage = 'Le livre a été supprimé avec succès.';
      this.loadBooks();
    }, () => this.errorMessage = 'Le livre n’a pas pu être supprimé. Il est peut-être lié à un emprunt en cours.');
  }

  bookDetails(bookId: number) {
    this.router.navigate(['book-details', bookId ]);
  }

}
