export interface Question {
    id: string;
    question: string;
    type: 'multiple' | 'boolean';
    answers?: string[];
}