import type { SectionResponse } from "$lib/server/section";
import type { Course } from "./enrollment";
import type { Room } from "./room";
import type { Section } from "./section";
import type { StudentResponse } from "./student";

// User types
export interface User {
	username: string;
	name?: string;
	role: 'STUDENT' | 'ADMIN';
	studentId?: string;
	token?: string;
}

// Authentication
export interface LoginCredentials {
	email: string;
	password: string;
}

export interface AuthResponse {
	success: boolean;
	message: string;
	user?: User;
	token?: string;
}

// Student Profile
export interface StudentProfile {
	id: string;
	userId: string;
	studentId: string;
	firstName: string;
	lastName: string;
	degreeId: string;
	createdAt: string;
	updatedAt: string;
}

// Schedule
export interface Schedule {
	id: string;
	sectionId: string;
	dayOfWeek: string; // MONDAY, TUESDAY, etc.
	startTime: string; // HH:mm format
	endTime: string; // HH:mm format
	roomId?: string;
	room?: Room;
}
	
// Degree
export interface Degree {
	id: string;
	name: string;
	code: string;
	requiredUnits: number;
	courseIds: string[];
	createdAt: string;
	updatedAt: string;
}

// API Response wrapper
export interface ApiResponse<T> {
	success: boolean;
	data?: T;
	message?: string;
	error?: string;
}

export interface EnrollmentResponse {
	enrollmentId: number;	
	student: StudentResponse
	section: SectionResponse
	status: string;
	enrolledAt: string;
	message: string ;
}



