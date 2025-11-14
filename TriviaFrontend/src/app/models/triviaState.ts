import { Question } from "./question";

export interface TriviaState {
  mode: 'category' | 'random-questions' | 'random-category';
  categoryId: number | null;
  categoryName: string | null;
  questionCount: number;
  questions: Question[];
  selections: Record<string, string>;
  results: Record<string, boolean>;
  submitted: boolean;
}