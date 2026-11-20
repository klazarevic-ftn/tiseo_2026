import {
  Component,
  inject,
  OnDestroy,
  OnInit,
  signal
} from '@angular/core';
import {
  DomSanitizer,
  type SafeResourceUrl
} from '@angular/platform-browser';
import { Subscription } from 'rxjs';
import { ButtonModule } from 'primeng/button';
import {
  DocumentService,
  type DocumentDetails
} from '../../services/document';
import { Modal } from '../../components/modal/modal';
import { keycloak } from '../../keycloak';
import { StudentSearch } from '../../components/student-search/student-search';
import type { StudentDetails } from '../../services/student';

@Component({
  imports: [ButtonModule, Modal, StudentSearch],
  selector: 'app-dokumenta',
  styleUrl: './dokumenta.scss',
  templateUrl: './dokumenta.html',
})
export class Dokumenta implements OnInit, OnDestroy {
  private api = inject(DocumentService);
  private sanitizer = inject(DomSanitizer);

  private objectUrl: string | null = null;
  private previewRequest?: Subscription;

  isStudent = keycloak.hasRealmRole('STUDENT');
  isAdmin = keycloak.hasRealmRole('ADMINISTRATOR');

  selectedStudent = signal<StudentDetails | null>(null);
  private documentRequest?: Subscription;

  documents = signal<DocumentDetails[]>([]);
  loading = signal(false);
  message = signal('');

  selectedFile = signal<File | null>(null);
  uploading = signal(false);
  saving = signal(false);

  previewVisible = signal(false);
  previewName = signal('');
  previewUrl = signal<SafeResourceUrl | null>(null);
  previewLoading = signal(false);
  previewError = signal('');

  ngOnInit(): void {
    this.loadDocuments();
  }

  ngOnDestroy(): void {
    this.documentRequest?.unsubscribe();
    this.clearPreview();
  }

  loadDocuments(): void {
    this.documentRequest?.unsubscribe();
    this.documents.set([]);

    const student = this.selectedStudent();

    if (this.isAdmin && !student) {
      this.loading.set(false);
      return;
    }

    const userId = this.isAdmin ? student?.user.id : undefined;

    this.loading.set(true);

    this.documentRequest = this.api.findAll(userId).subscribe({
      next: (data) => {
        this.documents.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.message.set('Učitavanje dokumenata nije uspelo.');
      }
    });
  }

  selectFile(event: Event): void {
    const input = event.target as HTMLInputElement;

    this.selectedFile.set(input.files?.item(0) ?? null);
    this.message.set('');
  }

  upload(event: Event, form: HTMLFormElement): void {
    event.preventDefault();

    const file = this.selectedFile();

    if (!file || this.uploading()) {
      return;
    }

    if (file.size === 0 || file.size > 10 * 1024 * 1024) {
      this.message.set(
        'Izaberite dokument koji nije prazan i nije veći od 10 MB.'
      );
      return;
    }

    this.uploading.set(true);
    this.message.set('');

    this.api.upload(file).subscribe({
      next: () => {
        this.uploading.set(false);
        this.selectedFile.set(null);
        form.reset();
        this.message.set('Dokument je uspešno otpremljen.');
        this.loadDocuments();
      },
      error: () => {
        this.uploading.set(false);
        this.message.set('Otpremanje dokumenta nije uspelo.');
      }
    });
  }

  openPreview(document: DocumentDetails): void {
    this.clearPreview();

    this.previewName.set(document.fileName);
    this.previewLoading.set(true);
    this.previewError.set('');
    this.previewVisible.set(true);

    this.previewRequest = this.api.download(document.id).subscribe({
      next: (data) => {
        const isPdf = document.fileName.toLowerCase().endsWith('.pdf');

        const file = isPdf
          ? new Blob([data], { type: 'application/pdf' })
          : data;

        this.objectUrl = URL.createObjectURL(file);

        if (isPdf) {
          this.previewUrl.set(
            this.sanitizer.bypassSecurityTrustResourceUrl(this.objectUrl)
          );
        }

        this.previewLoading.set(false);
      },
      error: () => {
        this.previewLoading.set(false);
        this.previewError.set('Učitavanje dokumenta nije uspelo.');
      }
    });
  }

  downloadPreview(): void {
    if (!this.objectUrl) {
      return;
    }

    const link = document.createElement('a');
    link.href = this.objectUrl;
    link.download = this.previewName();
    link.click();
  }

  clearPreview(): void {
    this.previewRequest?.unsubscribe();
    this.previewUrl.set(null);

    if (this.objectUrl) {
      URL.revokeObjectURL(this.objectUrl);
      this.objectUrl = null;
    }
  }

  editDocument(document: DocumentDetails): void {
    if (!this.isAdmin || this.saving()) {
      return;
    }

    const name = window.prompt('Naziv dokumenta:', document.fileName);

    if (name === null || name.trim() === '') {
      return;
    }

    if (name.trim().length > 255) {
      this.message.set('Naziv može imati najviše 255 znakova.');
      return;
    }

    this.saving.set(true);
    this.message.set('');

    this.api.update(document.id, name.trim()).subscribe({
      next: () => {
        this.saving.set(false);
        this.message.set('Naziv dokumenta je promenjen.');
        this.loadDocuments();
      },
      error: () => {
        this.saving.set(false);
        this.message.set('Izmena dokumenta nije uspela.');
      }
    });
  }

  onStudentSelected(student: StudentDetails | null): void {
    this.selectedStudent.set(student);
    this.message.set('');

    this.previewVisible.set(false);
    this.clearPreview();

    this.loadDocuments();
  }

  deleteDocument(document: DocumentDetails): void {
    if (!this.isAdmin || this.saving()) {
      return;
    }

    if (!window.confirm(`Obrisati dokument „${document.fileName}“?`)) {
      return;
    }

    this.saving.set(true);
    this.message.set('');

    this.api.delete(document.id).subscribe({
      next: () => {
        this.saving.set(false);
        this.message.set('Dokument je obrisan.');
        this.loadDocuments();
      },
      error: () => {
        this.saving.set(false);
        this.message.set('Brisanje dokumenta nije uspelo.');
      }
    });
  }
}
