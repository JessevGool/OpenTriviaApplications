import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { OpenTdbCategory } from '../../models/openTdbCategory';
import { BehaviorSubject, count, map, Observable, of, tap } from 'rxjs';
import { Question } from '../../models/question';
import { QuestionAnswer } from '../../models/questionAnswer';


export type Difficulty = 'easy' | 'medium' | 'hard';
export type QuestionType = 'multiple' | 'boolean';

@Injectable({
  providedIn: 'root'
})
export class TriviaService {

  private _baseUrl = "http://localhost:8080/"

  private difficultySubject = new BehaviorSubject<Difficulty | null>(null);
  private categoriesSubject = new BehaviorSubject<OpenTdbCategory[] | null>(null);
  private typeSubject = new BehaviorSubject<QuestionType | null>(null);

  difficulty$ = this.difficultySubject.asObservable();
  type$ = this.typeSubject.asObservable();
  categories$ = this.categoriesSubject.asObservable();
  constructor(private http: HttpClient) { }


  setDifficulty(diff: Difficulty | null) {
    this.difficultySubject.next(diff);
  }

  setType(t: QuestionType | null) {
    this.typeSubject.next(t);
  }

  getCategories(): Observable<OpenTdbCategory[]> {
    const existing = this.categoriesSubject.value;
    if (existing) {
      return of(existing);
    }
    return this.http.get<OpenTdbCategory[]>(this._baseUrl + "categories").pipe(
      tap(categories => this.categoriesSubject.next(categories))
    )
  }

  getRandomQuestions(amount: number, token?: string): Observable<Question[]> {
    let params = new HttpParams().set('amount', amount);
    const diff = this.difficultySubject.value;
    const type = this.typeSubject.value;

    if (diff) params = params.set('difficulty', diff);
    if (type) params = params.set('type', type);
    if (token) params = params.set('token', token);

    return this.http.get<Question[]>(this._baseUrl + "questions", { params });
  }

  getQuestionsByCategory(categoryId: number, amount: number, token?: string): Observable<Question[]> {
    let params = new HttpParams()
      .set('amount', amount)
      .set('category', categoryId);
    const diff = this.difficultySubject.value;
    const type = this.typeSubject.value;

    if (diff) params = params.set('difficulty', diff);
    if (type) params = params.set('type', type);
    if (token) params = params.set('token', token);

    return this.http.get<Question[]>(this._baseUrl + "questions", { params });
  }

  submitAnswers(answers: QuestionAnswer[], token: string): Observable<QuestionAnswer[]> {
    return this.http.post<QuestionAnswer[]>(this._baseUrl + "submit?token=" + token, answers)
  }
}
