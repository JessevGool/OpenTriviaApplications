import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { OpenTdbCategory } from '../models/open-tdb-category';
import { map, Observable } from 'rxjs';
import { Question } from '../models/question';

@Injectable({
  providedIn: 'root'
})
export class TriviaService {

  private _baseUrl = "http://localhost:8080/"
  constructor(private http: HttpClient) { }

  getCategories(): Observable<OpenTdbCategory[]> {
    return this.http.get<OpenTdbCategory[]>(this._baseUrl + "categories");

  }

  getRandomQuestions(amount: number): Observable<any> {
    return this.http.get<Question[]>(this._baseUrl + `questions?amount=${amount}`)
  }
}
