import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap, Router, RouterLink } from '@angular/router';
import { NgFor, NgIf } from '@angular/common';
import { Question } from '../../models/question';
import { TriviaService } from '../../services/trivia.service';

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
  loading = false;
  get selectedCount(): number {
    return Object.keys(this.selections).length;
  }
  constructor(private triviaService: TriviaService) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe((params: ParamMap) => {
      const path = this.router.url;

      if (path.startsWith('/trivia/random-questions')) {
        this.mode = 'random-questions';
        this.categoryId = null;


      } else if (path.startsWith('/trivia/random-category')) {
        this.mode = 'random-category';
        this.categoryId = null;
      } else {
        this.mode = 'category';
        const idStr = params.get('categoryId');
        this.categoryId = idStr ? +idStr : null;
      }
    });
    this.loadQuestions();
  }

  trackByQuestion = (_index: number, q: Question) => q.id;

  loadQuestions(): void {
    this.loading = true;
    this.questions = [];
    this.selections = {};
    let obs;
    if (this.mode === 'random-questions') {
      obs = this.triviaService.getRandomQuestions(10);
    }

    obs?.subscribe({
      next: (questions) => {
        this.questions = questions ?? [];
        this.loading = false;
      },
      error: () => {
        this.loading = false;
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
    const qCount = this.questions.length;
    const selectedCount = Object.keys(this.selections).length;
    return qCount > 0 && selectedCount > 0;
  }

  submit(): void {
    if (!this.canSubmit()) return;

    const payload = {
      answers: Object.entries(this.selections).map(([questionId, answer]) => ({
        questionId,
        answer,
      })),
    };

    //this.loading = true;
    // this.triviaService.submitAnswers(payload).subscribe({
    //   next: () => {
    //     this.loading = false;
    //     // Navigate or toast; for now, simple confirmation.
    //     alert('Answers submitted! 🎉');
    //     // Optionally reset:
    //     // this.selections = {};
    //   },
    //   error: (err) => {
    //     console.error(err);
    //     this.loading = false;
    //     this.error = 'Failed to submit answers. Please try again.';
    //   },
    // });


  }
}
