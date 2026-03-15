import { Routes } from '@angular/router';
import { HomeComponent } from './home/home';
import { PeopleComponent } from './people/people';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'people', component: PeopleComponent },
];
