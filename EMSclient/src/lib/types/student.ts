/** Matches backend DegreeResponse DTO */
export interface DegreeResponse {
  degreeId: number;
  name: string;
  description?: string;
}

/** Legacy - kept for other usages */
export interface Degree {
  degree: number;
  code: string;
  name: string;
  description?: string;
}

export interface Student {
  id: number;
  studentNumber: string;
  firstName: string;
  lastName: string;
  middleName?: string;
  username: string;
  email?: string;
  yearLevel: number;
  degree?: Degree;
  status?: string;
}

/** Matches backend StudentResponse DTO exactly */
export interface StudentResponse {
  studentId: number;
  userId?: number;
  studentNumber: string;
  firstName: string;
  lastName: string;
  degree?: DegreeResponse;
}

export interface UpdateStudentRequest {
  firstName: string;
  lastName: string;
  degreeId: number;
}
