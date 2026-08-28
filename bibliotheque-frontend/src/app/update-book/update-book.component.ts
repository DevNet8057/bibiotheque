import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Books } from '../_model/books';
import { BooksService } from '../_service/books.service';

@Component({
  selector: 'app-update-book',
  templateUrl: './update-book.component.html',
  styleUrls: ['./update-book.component.css']
})
export class UpdateBookComponent implements OnInit {

  bookId: number;
  book: Books = new Books();
  loading = true;
  saving = false;
  errorMessage = '';
  successMessage = '';
  constructor(private booksService: BooksService,
    private route: ActivatedRoute,
    private router: Router) { }

  ngOnInit(): void {
    this.bookId = this.route.snapshot.params['bookId'];
    this.booksService.getBookById(this.bookId).subscribe(data => {
      this.book = data;
      this.loading = false;
    }, () => { this.loading = false; this.errorMessage = 'Ce livre est introuvable. Retournez au catalogue puis réessayez.'; });
  }

  onSubmit() {
    this.saving = true;
    this.errorMessage = '';
    this.booksService.updateBook(this.bookId, this.book).subscribe( data =>{
        this.saving = false;
        this.successMessage = 'Les modifications ont été enregistrées.';
        setTimeout(() => this.goToBooksList(), 700);
    },
    () => { this.saving = false; this.errorMessage = 'Les modifications n’ont pas pu être enregistrées. Vérifiez les informations puis réessayez.'; });
  }

  goToBooksList() {
    this.router.navigate(['/books']);
  }

}
