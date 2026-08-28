import { Component } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'Bibliothèque';
  standalonePage = false;

  constructor(private router: Router) {
    this.updateLayout(this.router.url);
    this.router.events.pipe(
      filter((event): event is NavigationEnd => event instanceof NavigationEnd)
    ).subscribe(event => this.updateLayout(event.urlAfterRedirects));
  }

  private updateLayout(url: string): void {
    this.standalonePage = url.startsWith('/login') || url.startsWith('/forbidden');
  }
}
