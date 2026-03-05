import type { Section } from "./section";

// Course
export interface Course {
	id: string;
	code: string;
	name: string;
	units: number;
	prerequisiteIds?: string[];
	createdAt: string;
	updatedAt: string;
}

// Enrollment
export interface Enrollment {
	id: string;
	studentId: string;
	sectionId: string;
	course?: Course;
	section?: Section;
	enrolledAt: string;
	status: 'PENDING' | 'ENROLLED' | 'DROPPED';
	isEnrolled: boolean;
	scheduledConflict?: boolean;
	enrollmentDate: string;
	updatedAt: string;
}

export interface CreateEnrollmentRequest {
	sectionId: string;
}

export interface UpdateEnrollmentRequest {
	status?: 'PENDING' | 'ENROLLED' | 'DROPPED';
	isEnrolled?: boolean;
}

// Filter state
export type EnrollmentFilterType = 'all' | 'enrolled' | 'pending';

