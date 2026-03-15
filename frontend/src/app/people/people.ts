import { Component, OnInit, signal } from '@angular/core';
import { ApiService } from '../services/api.service';
import { Person, CreatePersonRequest } from '../models/person.model';
import { PersonDialogComponent } from './person-dialog/person-dialog';

@Component({
  selector: 'app-people',
  standalone: true,
  imports: [PersonDialogComponent],
  templateUrl: './people.html',
  styleUrl: './people.scss'
})
export class PeopleComponent implements OnInit {
  people = signal<Person[]>([]);
  loading = signal(true);
  dialogVisible = signal(false);
  editingPerson = signal<Person | null>(null);

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadPeople();
  }

  loadPeople(): void {
    this.loading.set(true);
    this.api.getPeople().subscribe({
      next: (data) => {
        this.people.set(data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  openCreate(): void {
    this.editingPerson.set(null);
    this.dialogVisible.set(true);
  }

  openEdit(person: Person): void {
    this.editingPerson.set(person);
    this.dialogVisible.set(true);
  }

  onSave(event: { request: CreatePersonRequest; photo?: File }): void {
    const editing = this.editingPerson();
    const obs = editing
      ? this.api.updatePerson(editing.id, event.request, event.photo)
      : this.api.createPerson(event.request, event.photo);

    obs.subscribe({
      next: () => {
        this.dialogVisible.set(false);
        this.loadPeople();
      },
      error: (err) => alert(err.error?.message || 'Error saving person')
    });
  }

  onDelete(person: Person): void {
    if (!confirm(`Delete ${person.firstName} ${person.lastName}?`)) return;
    this.api.deletePerson(person.id).subscribe({
      next: () => this.loadPeople(),
      error: (err) => alert(err.error?.message || 'Error deleting person')
    });
  }

  closeDialog(): void {
    this.dialogVisible.set(false);
  }
}
