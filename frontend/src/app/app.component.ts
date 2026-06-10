import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

type Theme = 'light' | 'dark';

const themeStorageKey = 'theme';

@Component({
  selector: 'app-root',
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {
  readonly appName = 'Keep in Touch';

  theme: Theme = this.storedTheme();

  constructor() {
    this.applyTheme();
  }

  get themeButtonText(): string {
    return this.theme === 'light' ? 'Dark mode' : 'Light mode';
  }

  toggleTheme(): void {
    this.theme = this.theme === 'light' ? 'dark' : 'light';
    localStorage.setItem(themeStorageKey, this.theme);
    this.applyTheme();
  }

  private storedTheme(): Theme {
    return localStorage.getItem(themeStorageKey) === 'dark' ? 'dark' : 'light';
  }

  private applyTheme(): void {
    document.documentElement.dataset['theme'] = this.theme;
  }
}
