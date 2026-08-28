import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Books } from '../_model/books';
import { BooksService } from '../_service/books.service';

@Component({
  selector: 'app-create-book',
  templateUrl: './create-book.component.html',
  styleUrls: ['./create-book.component.css']
})
export class CreateBookComponent implements OnInit {

  book: Books = new Books();
  saving = false;
  errorMessage = '';
  successMessage = '';
  constructor(private booksService: BooksService,
    private router: Router) { }

  ngOnInit(): void {
  }

  saveBook() {
    this.saving = true;
    this.errorMessage = '';
    this.booksService.createBook(this.book).subscribe(data => {
      this.saving = false;
      this.successMessage = 'Le livre a été ajouté au catalogue.';
      setTimeout(() => this.goToBooksList(), 700);
    },
    () => { this.saving = false; this.errorMessage = 'Le livre n’a pas pu être ajouté. Vérifiez les informations puis réessayez.'; });
  }

  goToBooksList() {
    this.router.navigate(['/books']);
  }

  onSubmit() {
    this.saveBook();
  }

}
