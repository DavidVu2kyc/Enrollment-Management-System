import type { Section } from "./section";
import type { Degree } from "./student";

// Course
export interface Course {
	courseId: string;
	code: string;
	title: string;
	units: number;
	degree?:Degree
}

// Enrollment
export interface Enrollment {
	enrollmentId: string;
	studentId: string;
	sectionId: string;
	status: 'PENDING' | 'ENROLLED' | 'DROPPED'| 'CANCELLED';
	course?: Course;
	section?: Section;
	enrolledAt: string;
	isEnrolled: boolean;
}

export interface CreateEnrollmentRequest {
	sectionId: string;
}

export interface UpdateEnrollmentRequest {
	status?: 'PENDING' | 'ENROLLED' | 'DROPPED'| 'CANCELLED';
	isEnrolled?: boolean;
}

// Filter state
export type EnrollmentFilterType = 'all' | 'enrolled' | 'pending';

