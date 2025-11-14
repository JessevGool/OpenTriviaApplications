import { Component, inject, OnInit } from '@angular/core';
import { Difficulty, QuestionType, TriviaService } from '../../services/trivia/trivia.service';
import { OpenTdbCategory } from '../../models/openTdbCategory';
import { Router } from '@angular/router';
import { NgFor, NgIf, TitleCasePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SessionService } from '../../services/session/session.service';
import { take } from 'rxjs';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [NgIf, NgFor, FormsModule, TitleCasePipe],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {

  private router = inject(Router);
  categories: OpenTdbCategory[] = [];
  loading = false;
  token?: string | null = null;

  difficultyOptions: Difficulty[] = ['easy', 'medium', 'hard'];
  typeOptions: QuestionType[] = ['multiple', 'boolean'];

  difficulty: Difficulty | '' = '';
  questionType: QuestionType | '' = '';

  constructor(private triviaService: TriviaService, private sessionService: SessionService) { }

  ngOnInit(): void {
    this.loading = true;
    this.triviaService.getCategories().subscribe(categories => {
      this.categories.push(...categories);
      this.loading = false;
    });

    this.sessionService.ensureToken()
      .pipe(take(1))
      .subscribe(token => {
        this.token = token;
      });
  }

  /**
   * Navigates to the trivia questions page for the selected category.
   * @param categoryId The ID of the selected category.
   */
  goToCategory(categoryId: number): void {
    this.sessionService.clearTriviaState();
    this.router.navigate(['/trivia/category', categoryId]);
  }

  /**
   * Navigates to the random questions page.
  */
  goToRandomQuestions(): void {
    this.sessionService.clearTriviaState();
    this.router.navigate(['/trivia/random-questions']);
  }

  /**
   * Navigates to the random category page.
   * */
  goToRandomCategory(): void {
    this.sessionService.clearTriviaState();
    this.router.navigate(['/trivia/random-category']);
  }

  /**
   * Handles the change event for the difficulty selection.
   * @param value The selected difficulty value.
   */
  onDifficultyChange(value: string) {
    this.difficulty = value as Difficulty;
    this.triviaService.setDifficulty(this.difficulty || null);
  }

  /**
   * Handles the change event for the question type selection.
   * @param value The selected question type value.
   */
  onTypeChange(value: string) {
    this.questionType = value as QuestionType;
    this.triviaService.setType(this.questionType || null);
  }
}
