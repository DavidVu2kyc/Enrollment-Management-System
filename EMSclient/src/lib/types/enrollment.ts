import type { SectionResponse } from "$lib/server/section";
import type { Section } from "./section";
import type { Degree, Student, StudentResponse } from "./student";

// Course
export interface Course {
	courseId: number ;
	code: string;
	title: string;
	units: number;
	degree?:Degree
}

// Enrollment
export interface Enrollment {
	enrollmentId: number
	studentId: number;
	sectionId: number;
	status: 'PENDING' | 'ENROLLED' | 'DROPPED'| 'CANCELLED';
	course?: Course;
	section?: Section;
	enrolledAt: string;
	isEnrolled: boolean;
}

export interface EnrollmentResponse {
	enrollmentId: number
	student: StudentResponse;
	section: SectionResponse;
	status: 'PENDING' | 'ENROLLED' | 'DROPPED'| 'CANCELLED';
	enrolledAt: string;
	message?: string;

}

export interface CreateEnrollmentRequest {
	sectionId: number;
}

export interface UpdateEnrollmentRequest {
	status?: 'PENDING' | 'ENROLLED' | 'DROPPED'| 'CANCELLED';
	isEnrolled?: boolean;
}

// Filter state
export type EnrollmentFilterType = 'all' | 'enrolled' | 'pending';

