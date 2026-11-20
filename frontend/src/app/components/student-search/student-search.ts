import {
  Component,
  inject,
  OnDestroy,
  output,
  signal
} from '@angular/core';
import { Subscription } from 'rxjs';
import { ButtonModule } from 'primeng/button';
import {
  StudentService,
  type StudentDetails
} from '../../services/student';

@Component({
  selector: 'app-student-search',
  imports: [ButtonModule],
  templateUrl: './student-search.html',
  styleUrl: './student-search.scss'
})
export class StudentSearch implements OnDestroy {
  private api = inject(StudentService);
  private request?: Subscription;

  studentSelected = output<StudentDetails | null>();

  query = signal('');
  results = signal<StudentDetails[]>([]);
  selectedStudent = signal<StudentDetails | null>(null);

  loading = signal(false);
  searched = signal(false);
  error = signal('');

  updateQuery(event: Event): void {
    const input = event.target as HTMLInputElement;

    this.query.set(input.value);
    this.resetResults();
  }

  search(event: Event): void {
    event.preventDefault();

    const text = this.query().trim();

    if (text.length < 2) {
      return;
    }

    this.resetResults();
    this.loading.set(true);
    this.searched.set(true);

    this.request = this.api.search(text).subscribe({
      next: (students) => {
        this.results.set(students);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Pretraga studenata nije uspela.');
      }
    });
  }

  selectStudent(student: StudentDetails): void {
    this.selectedStudent.set(student);
    this.results.set([]);
    this.searched.set(false);

    this.studentSelected.emit(student);
  }

  clearSelection(): void {
    this.query.set('');
    this.resetResults();
  }

  private resetResults(): void {
    this.request?.unsubscribe();

    this.results.set([]);
    this.selectedStudent.set(null);
    this.loading.set(false);
    this.searched.set(false);
    this.error.set('');

    this.studentSelected.emit(null);
  }

  ngOnDestroy(): void {
    this.request?.unsubscribe();
  }
}
