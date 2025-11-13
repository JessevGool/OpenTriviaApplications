import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, map, Observable, of, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SessionService {
   private _baseUrl = "http://localhost:8080/"
  private tokenSubject = new BehaviorSubject<string | null>(null);
  token$ = this.tokenSubject.asObservable();
  constructor(private http: HttpClient) { }

  getToken(): Observable<string> {
    const existing = this.tokenSubject.value;


    if (existing) {
      return of(existing);
    }

    return this.http.get<{ token: string }>(this._baseUrl + 'token').pipe(
      map(res => res.token), 
      tap(token => this.tokenSubject.next(token))
    );

  }

  clearToken(): void {
    this.tokenSubject.next(null);
  }
}
