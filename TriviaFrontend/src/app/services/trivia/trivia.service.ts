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

  private _baseUrl = "https://abundant-tiff-jessevgool-b70bf725.koyeb.app/"

  private difficultySubject = new BehaviorSubject<Difficulty | null>(null);
  private categoriesSubject = new BehaviorSubject<OpenTdbCategory[] | null>(null);
  private typeSubject = new BehaviorSubject<QuestionType | null>(null);

  difficulty$ = this.difficultySubject.asObservable();
  type$ = this.typeSubject.asObservable();
  categories$ = this.categoriesSubject.asObservable();
  constructor(private http: HttpClient) { }

  /**
   * Sets the difficulty level for trivia questions.
   * @param diff The desired difficulty level, or null to unset.
   */
  setDifficulty(diff: Difficulty | null) {
    this.difficultySubject.next(diff);
  }

  /**
   * Sets the question type for trivia questions.
   * @param t The desired question type, or null to unset.
   */
  setType(t: QuestionType | null) {
    this.typeSubject.next(t);
  }

  /**
   * Retrieves the list of trivia categories.
   * @returns An Observable emitting an array of OpenTdbCategory objects.
   */
  getCategories(): Observable<OpenTdbCategory[]> {
    const existing = this.categoriesSubject.value;
    if (existing) {
      return of(existing);
    }
    return this.http.get<OpenTdbCategory[]>(this._baseUrl + "categories").pipe(
      tap(categories => this.categoriesSubject.next(categories))
    )
  }

  /**
   * Retrieves a set of random trivia questions.
   * @param amount The number of questions to retrieve.
   * @param token An optional session token.
   * @returns An Observable emitting an array of Question objects.
   */
  getRandomQuestions(amount: number, token?: string): Observable<Question[]> {
    let params = new HttpParams().set('amount', amount);
    const diff = this.difficultySubject.value;
    const type = this.typeSubject.value;

    if (diff) params = params.set('difficulty', diff);
    if (type) params = params.set('type', type);
    if (token) params = params.set('token', token);

    return this.http.get<Question[]>(this._baseUrl + "questions", { params });
  }

  /**
   * Retrieves trivia questions for a specific category.
   * @param categoryId The ID of the category.
   * @param amount The number of questions to retrieve.
   * @param token An optional session token.
   * @returns An Observable emitting an array of Question objects.
   */
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

  /**
   * Submits the user's answers for evaluation.
   * @param answers An array of QuestionAnswer objects representing the user's answers.
   * @param token The session token.
   * @returns An Observable emitting an array of QuestionAnswer objects with results.
   */
  submitAnswers(answers: QuestionAnswer[], token: string): Observable<QuestionAnswer[]> {
    return this.http.post<QuestionAnswer[]>(this._baseUrl + "submit?token=" + token, answers)
  }
}
