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

  get selectedCount(): number {
    return Object.keys(this.selections).length;
  }

  get allCorrect(): boolean {
    if (!this.submitted || !this.questions.length) return false;
    return this.questions.every(q => this.results[q.id] === true);
  }

  trackByQuestion = (_index: number, q: Question) => q.id;

  constructor(private triviaService: TriviaService, private sessionService: SessionService) { }

  ngOnInit(): void {
    this.sessionService.token$
      .pipe(
        filter((t): t is string => !!t),
        take(1)
      )
      .subscribe(token => {
        this.token = token;
      });

    this.route.paramMap.subscribe((params: ParamMap) => {
      const path = this.router.url;

      if (path.startsWith('/trivia/random-questions')) {
        this.mode = 'random-questions';
        this.categoryId = null;
      } else if (path.startsWith('/trivia/random-category')) {
        this.mode = 'random-category';

        if (this.categoryId === null) {
          this.triviaService.getCategories()
            .pipe(take(1))
            .subscribe(categories => {
              const random = categories[Math.floor(Math.random() * categories.length)];
              this.categoryId = random.id;
              this.categoryName = random.name;
              this.loadQuestions();
            });
        } else {
          this.loadQuestions();
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
      }
    });
    this.loadQuestions();
  }



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
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        const backendMessage = typeof err.error === 'string' ? err.error : null;
        if (err.status === BackendError.RATE_LIMIT) {
          this.error = backendMessage ?? 'Trivia is temporarily rate limited. Please try again in a moment.';
        } else if (err.status === BackendError.SESSION_EXPIRED) {
          //Token expired so we require a new one
          this.sessionService.clearToken();

          this.error = backendMessage ?? 'Your trivia session expired. Go back to home to start a new game.';
        } else if (err.status === BackendError.PROVIDER_ERROR) {
          this.error = backendMessage ?? 'Trivia provider error. Please try again later.';
        } else {
          this.error = backendMessage ?? 'Failed to load questions. Please try again.';
        }
      }
    });
  }

  selectMultiple(questionId: string, option: string) {
    this.selections[questionId] = option;
  }

  selectBoolean(questionId: string, value: boolean) {
    this.selections[questionId] = String(value);
  }

  canSubmit(): boolean {
    const selectedCount = Object.keys(this.selections).length;
    return selectedCount == this.questionCount;
  }

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
      },
      error: (err) => {
        console.error(err);
        this.loading = false;
        this.error = 'Failed to submit answers. Please try again.';
      },
    });


  }

  newQuestions(): void {
    this.loadQuestions();
  }
}
