import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { OpenTdbCategory } from '../models/open-tdb-category';
import { map, Observable } from 'rxjs';
import { Question } from '../models/question';
import { QuestionAnswer } from '../models/questionAnswer';

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

  getQuestionsByCategory(categoryId: number, amount: number): Observable<any> {
    return this.http.get<Question[]>(this._baseUrl + `questions?category=${categoryId}&amount=${amount}`)
  }

  submitAnswers(answers: QuestionAnswer[]): Observable<QuestionAnswer[]> {
    return this.http.post<QuestionAnswer[]>(this._baseUrl + "submit", answers)
  }
}
