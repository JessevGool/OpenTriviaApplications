import { Routes } from '@angular/router';
import { TriviaComponent } from './pages/trivia/trivia.component';
import { HomeComponent } from './pages/home/home.component';

export const routes: Routes = [
    { path: '', component: HomeComponent },
    { path: 'trivia/category/:categoryId', component: TriviaComponent },
    { path: 'trivia/random-questions', component: TriviaComponent },
    { path: 'trivia/random-category', component: TriviaComponent },
];
