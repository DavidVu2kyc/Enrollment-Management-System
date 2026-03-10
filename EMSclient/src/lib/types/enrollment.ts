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

// ✅ FIX: added 'CANCELLED' to the shared union so it's defined once and reused
export type EnrollmentStatus = 'PENDING' | 'ENROLLED' | 'DROPPED' | 'CANCELLED';

// Enrollment
export interface Enrollment {
	enrollmentId: number
	studentId: number;
	sectionId: number;
	status: EnrollmentStatus;
	course?: Course;
	section?: Section;
	enrolledAt: string;
	isEnrolled: boolean;
}

export interface EnrollmentResponse {
	enrollmentId: number
	student: StudentResponse;
	section: SectionResponse;
	status: EnrollmentStatus;
	enrolledAt: string;
	message?: string;

}

export interface CreateEnrollmentRequest {
	studentId : number ;
	sectionId: number;
}

export interface UpdateEnrollmentRequest {
	status?: EnrollmentStatus;
	sectionId?: number;
}

// Filter state
export type EnrollmentFilterType = 'all' | 'enrolled' | 'pending' | 'dropped';
