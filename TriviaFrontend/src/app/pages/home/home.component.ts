import { Component, inject, OnInit } from '@angular/core';
import { TriviaService } from '../../services/trivia.service';
import { OpenTdbCategory } from '../../models/open-tdb-category';
import { Router } from '@angular/router';
import { NgFor, NgIf } from '@angular/common';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [NgIf,NgFor],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  private router = inject(Router);
  categories: OpenTdbCategory[] = [];
  loading = false;
  constructor(private triviaService: TriviaService) { }

  ngOnInit(): void {
    this.loading = true;
    this.triviaService.getCategories().subscribe(categories => {
      this.categories.push(...categories);
      this.loading = false;
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

}
