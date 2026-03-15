export interface Person {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string | null;
  birthDate: string | null;
  photoUrl: string | null;
  createdAt: string;
}

export interface CreatePersonRequest {
  firstName: string;
  lastName: string;
  email: string;
  phone: string | null;
  birthDate: string | null;
}
