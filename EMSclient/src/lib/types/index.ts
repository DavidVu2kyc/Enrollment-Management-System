// User types
export interface User {
	id: string;
	email: string;
	name: string;
	role: 'STUDENT' | 'ADMIN';
	studentId?: string;
	createdAt: string;
	updatedAt: string;
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
	yearlevel: number;
	totalUnits: number;
	gpa: number;
	createdAt: string;
	updatedAt: string;
}

// Section
export interface Section {
	id: string;
	courseId: string;
	courseName: string;
	courseCode: string;
	term: string;
	year: number;
	sectionNumber: string;
	capacity: number;
	enrolledCount: number;
	schedules: Schedule[];
	roomId?: string;
	room?: Room;
	instructor?: string;
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

// Room
export interface Room {
	id: string;
	building: string;
	roomNumber: string;
	capacity: number;
	createdAt: string;
	updatedAt: string;
}

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

// Filter state
export type EnrollmentFilterType = 'all' | 'enrolled' | 'pending';

// Modal types
export interface ModalState {
	isOpen: boolean;
	title: string;
	message: string;
	type: 'confirm' | 'alert' | 'success' | 'error';
	onConfirm?: () => void;
	onCancel?: () => void;
}
