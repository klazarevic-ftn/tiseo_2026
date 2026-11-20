import { Component, input, model, output } from '@angular/core';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-modal',
  imports: [DialogModule, ButtonModule],
  template: `
    <p-dialog
      [header]="title()"
      [visible]="visible()"
      (visibleChange)="visible.set($event)"
      (onHide)="closed.emit()"
      [modal]="true"
      [draggable]="false"
      [resizable]="false"
      [blockScroll]="true"
      [style]="{ width: width(), maxWidth: '95vw' }"
    >
      <ng-content />

      <ng-template #footer>
        <div class="modal-footer">
          <ng-content select="[modalFooter]" />

          <button pButton type="button" (click)="visible.set(false)">
            Zatvori
          </button>
        </div>
      </ng-template>
    </p-dialog>
  `,
  styles: `
    .modal-footer {
      display: flex;
      justify-content: flex-end;
      gap: 0.5rem;
    }
  `
})
export class Modal {
  title = input('');
  width = input('50rem');
  visible = model(false);
  closed = output<void>();
}
