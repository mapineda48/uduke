import { Component, EventEmitter, Input, Output, OnChanges } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Person, CreatePersonRequest } from '../../models/person.model';

@Component({
  selector: 'app-person-dialog',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './person-dialog.html',
  styleUrl: './person-dialog.scss'
})
export class PersonDialogComponent implements OnChanges {
  @Input() person: Person | null = null;
  @Input() visible = false;
  @Output() save = new EventEmitter<{ request: CreatePersonRequest; photo?: File }>();
  @Output() cancel = new EventEmitter<void>();

  form: CreatePersonRequest = { firstName: '', lastName: '', email: '', phone: null, birthDate: null };
  selectedPhoto: File | null = null;
  photoPreview: string | null = null;

  ngOnChanges(): void {
    if (this.person) {
      this.form = {
        firstName: this.person.firstName,
        lastName: this.person.lastName,
        email: this.person.email,
        phone: this.person.phone,
        birthDate: this.person.birthDate
      };
      this.photoPreview = this.person.photoUrl;
    } else {
      this.form = { firstName: '', lastName: '', email: '', phone: null, birthDate: null };
      this.photoPreview = null;
    }
    this.selectedPhoto = null;
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      this.selectedPhoto = input.files[0];
      const reader = new FileReader();
      reader.onload = () => this.photoPreview = reader.result as string;
      reader.readAsDataURL(this.selectedPhoto);
    }
  }

  onSubmit(): void {
    this.save.emit({ request: { ...this.form }, photo: this.selectedPhoto ?? undefined });
  }

  onCancel(): void {
    this.cancel.emit();
  }
}
