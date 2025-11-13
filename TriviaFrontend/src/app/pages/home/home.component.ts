import { Component, inject, OnInit } from '@angular/core';
import { Difficulty, QuestionType, TriviaService } from '../../services/trivia/trivia.service';
import { OpenTdbCategory } from '../../models/open-tdb-category';
import { Router } from '@angular/router';
import { NgFor, NgIf,TitleCasePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SessionService } from '../../services/session/session.service';

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

    this.sessionService.getToken().subscribe(token => {
      this.token = token;
    });
  }

  goToCategory(categoryId: number): void {
    this.router.navigate(['/trivia/category', categoryId]);
  }

  goToRandomQuestions(): void {
    this.router.navigate(['/trivia/random-questions']);
  }

  goToRandomCategory(): void {
    this.router.navigate(['/trivia/random-category']);
  }

  onDifficultyChange(value: string) {
    this.difficulty = value as Difficulty;
    this.triviaService.setDifficulty(this.difficulty || null);
  }

  onTypeChange(value: string) {
    this.questionType = value as QuestionType;
    this.triviaService.setType(this.questionType || null);
  }
}
