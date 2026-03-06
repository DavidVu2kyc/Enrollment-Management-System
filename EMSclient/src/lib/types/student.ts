

export interface Degree {
    id: number;
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
    status?: string; // Mocking 'Active' status if not in spec, or from User field
}
export interface StudentResponse {
    id: number;
    studentNumber: string;
    firstName: string;
    lastName: string;
    username: string;
    name:string; 
    email?: string;
    status?: string; // Mocking 'Active' status if not in spec, or from User field
}

export interface UpdateStudentRequest {
    firstName: string;
    lastName: string;
    middleName: string;
    studentNumber: string;
    yearLevel: number;
    degreeId: number;
}
