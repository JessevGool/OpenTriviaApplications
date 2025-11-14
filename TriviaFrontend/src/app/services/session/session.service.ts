import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, map, Observable, of, tap } from 'rxjs';
import { TriviaState } from '../../models/triviaState';

@Injectable({
  providedIn: 'root'
})
export class SessionService {
  private _baseUrl = "http://localhost:8080/"
  private tokenSubject = new BehaviorSubject<string | null>(this.loadTokenFromStorage());
  private readonly TRIVIA_STATE_KEY = 'triviaState';
  token$ = this.tokenSubject.asObservable();
  constructor(private http: HttpClient) { }

  /**
   * Retrieves the current trivia state from local storage.
   * @returns The current trivia state or null if not set.
   */
  getTriviaState(): TriviaState | null {
    const raw = localStorage.getItem(this.TRIVIA_STATE_KEY);
    if (!raw) return null;

    try {
      return JSON.parse(raw) as TriviaState;
    } catch {
      localStorage.removeItem(this.TRIVIA_STATE_KEY);
      return null;
    }
  }

  /**
   * Loads the session token from session storage.
   * @returns The session token as a string, or null if not found.
   */
  private loadTokenFromStorage(): string | null {
    return sessionStorage.getItem('triviaToken'); // or localStorage
  }

  /**
   * Saves the session token to session storage.
   * @param token The session token to be saved.
   */
  private saveToken(token: string) {
    sessionStorage.setItem('triviaToken', token);
  }

  /**
   * Persists the given trivia state to local storage.
   * @param state The trivia state to be saved.
   */
  setTriviaState(state: TriviaState): void {
    localStorage.setItem(this.TRIVIA_STATE_KEY, JSON.stringify(state));
  }

  /**
   * Clears the stored trivia state from local storage.
   */
  clearTriviaState(): void {
    localStorage.removeItem(this.TRIVIA_STATE_KEY);
  }

  /**
   * Retrieves the current session token, fetching a new one if not already available.
   * @returns An Observable emitting the session token as a string.
   */
  ensureToken(): Observable<string> {
    const existing = this.tokenSubject.value;
    if (existing) {
      return of(existing);
    }

    return this.http.get<{ token: string }>(this._baseUrl + 'token').pipe(
      map(r => r.token),
      tap(token => {
        this.tokenSubject.next(token);
        this.saveToken(token);
      })
    );
  }


  clearToken() {
    this.tokenSubject.next(null);
    sessionStorage.removeItem('triviaToken');
  }
}
