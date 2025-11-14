import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap, Router, RouterLink } from '@angular/router';
import { NgFor, NgIf } from '@angular/common';
import { Question } from '../../models/question';
import { TriviaService } from '../../services/trivia/trivia.service';
import { QuestionAnswer } from '../../models/questionAnswer';
import { SessionService } from '../../services/session/session.service';
import { filter, map, take } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { BackendError } from '../../models/backendError';

@Component({
  selector: 'app-trivia',
  standalone: true,
  imports: [NgIf, NgFor, RouterLink],
  templateUrl: './trivia.component.html',
  styleUrl: './trivia.component.css'
})
export class TriviaComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  mode: 'category' | 'random-questions' | 'random-category' = 'category';
  categoryId: number | null = null;
  error: string | null = null;
  questions: Question[] = [];
  selections: Record<string, string> = {};
  results: Record<string, boolean> = {};
  loading = false;
  submitted = false;
  token?: string;
  questionCount = 5;
  categoryName: string | null = null;

  /**
   * The number of questions the user has selected answers for.
   */
  get selectedCount(): number {
    return Object.keys(this.selections).length;
  }

  /**
   * Indicates whether all questions were answered correctly.
   */
  get allCorrect(): boolean {
    if (!this.submitted || !this.questions.length) return false;
    return this.questions.every(q => this.results[q.id] === true);
  }

  /**
   * TrackBy function for questions to optimize ngFor rendering.
   * @param _index The index of the question in the list.
   * @param q The question object.
   * @returns The unique identifier of the question.
   */
  trackByQuestion = (_index: number, q: Question) => q.id;

  constructor(private triviaService: TriviaService, private sessionService: SessionService) { }

  ngOnInit(): void {
    this.sessionService.ensureToken()
      .pipe(take(1))
      .subscribe(token => {
        this.token = token;

        // now safe to do the rest that may need token
        const saved = this.sessionService.getTriviaState();
        const hasSavedQuestions = !!saved?.questions?.length;

        if (saved) {
          this.mode = saved.mode;
          this.categoryId = saved.categoryId;
          this.categoryName = saved.categoryName;
          this.questionCount = saved.questionCount;
          this.questions = saved.questions;
          this.selections = saved.selections;
          this.results = saved.results;
          this.submitted = saved.submitted;
        }

        this.setupRouteHandler(hasSavedQuestions);
      });


  }

  setupRouteHandler(hasSavedQuestions: boolean): void {
    this.route.paramMap.subscribe((params: ParamMap) => {
      const path = this.router.url;

      if (path.startsWith('/trivia/random-questions')) {
        this.mode = 'random-questions';
        this.categoryId = null;
        if (!hasSavedQuestions) {
          this.loadQuestions();
        }
      } else if (path.startsWith('/trivia/random-category')) {
        this.mode = 'random-category';
        if (!hasSavedQuestions) {
          this.triviaService.getCategories()
            .pipe(take(1))
            .subscribe(categories => {
              const random = categories[Math.floor(Math.random() * categories.length)];
              this.categoryId = random.id;
              this.categoryName = random.name;
              this.loadQuestions();
            });
        }

      } else {
        this.mode = 'category';
        const idStr = params.get('categoryId');
        this.categoryId = idStr ? +idStr : null;
        this.triviaService.getCategories()
          .pipe(
            take(1),
            map(categories => categories.find(cat => cat.id === this.categoryId))
          )
          .subscribe(category => {
            this.categoryName = category ? category.name : null;
          });
        if (!hasSavedQuestions && this.categoryId !== null) {
          this.loadQuestions();
        }
      }

    });
  }

  /**
   * Loads trivia questions based on the current mode and category.
   */
  loadQuestions(): void {
    this.loading = true;
    this.questions = [];
    this.selections = {};
    this.error = null;
    this.submitted = false;
    let obs;

    if (this.mode === 'random-questions') {
      obs = this.triviaService.getRandomQuestions(this.questionCount, this.token);
    } else if ((this.mode === 'category' || this.mode === 'random-category') && this.categoryId !== null) {
      obs = this.triviaService.getQuestionsByCategory(this.categoryId, this.questionCount, this.token);
    }

    obs?.subscribe({
      next: (questions) => {
        this.questions = questions ?? [];
        this.loading = false;
        this.persistState();
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        const backendMessage = typeof err.error === 'string' ? err.error : null;
        if (err.status === BackendError.RATE_LIMIT) {
          this.error = backendMessage ?? 'Trivia is temporarily rate limited. Please try again in a moment.';
        } else if (err.status === BackendError.SESSION_EXPIRED) {
          this.sessionService.clearToken();
          this.error = 'Your trivia session expired. Go back to home to start a new game.';
        } else if (err.status === BackendError.PROVIDER_ERROR) {
          this.error = backendMessage ?? 'Trivia provider error. Please try again later.';
        } else {
          this.error = backendMessage ?? 'Failed to load questions. Please try again.';
        }
      }
    });
  }

  /**
   * Records the user's selection for a multiple-choice question.
   * @param questionId The ID of the question.
   * @param option The selected option for the question.
   */
  selectMultiple(questionId: string, option: string) {
    this.selections[questionId] = option;
    this.persistState();
  }

  /**
   * Records the user's selection for a boolean question.
   * @param questionId The ID of the question.
   * @param value The selected boolean value for the question.
   */
  selectBoolean(questionId: string, value: boolean) {
    this.selections[questionId] = String(value);
    this.persistState();
  }

  /**
   * Determines if the user can submit their answers.
   * @returns True if all questions have been answered; otherwise, false.
   */
  canSubmit(): boolean {
    const selectedCount = Object.keys(this.selections).length;
    return selectedCount == this.questionCount;
  }

  /**
   * Submits the user's answers and processes the results.
  */
  submit(): void {
    if (!this.canSubmit()) return;


    const payload: QuestionAnswer[] = this.questions.map((q) => {
      return {
        questionId: q.id,
        answer: this.selections[q.id]
      };
    });

    this.loading = true;
    this.triviaService.submitAnswers(payload, this.token!).subscribe({
      next: (checkedAnswers: QuestionAnswer[]) => {
        this.loading = false;
        this.submitted = true;
        this.results = {};

        checkedAnswers.forEach(qa => {
          this.results[qa.questionId] = !!qa.correct;
        });

        this.persistState();
      },
      error: (err) => {
        console.error(err);
        this.loading = false;
        this.error = 'Failed to submit answers. Please try again.';
      },
    });


  }

  /**
   * Loads a new set of trivia questions.
   */
  newQuestions(): void {
    this.sessionService.clearTriviaState();
    this.loadQuestions();
  }

  /**
   * Persists the current trivia state to the session service.
   */
  private persistState(): void {
    this.sessionService.setTriviaState({
      mode: this.mode,
      categoryId: this.categoryId,
      categoryName: this.categoryName,
      questionCount: this.questionCount,
      questions: this.questions,
      selections: this.selections,
      results: this.results,
      submitted: this.submitted,
    });
  }

}
