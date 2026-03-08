import type { EnrollmentResponse } from '$lib/types/enrollment';

class EnrollmentsStore {
	#enrollments = $state<EnrollmentResponse[]>([]);

	get all() {
		return this.#enrollments;
	}

	get totalCount() {
		return this.#enrollments.length;
	}

	get enrolledCount() {
		return this.#enrollments.filter((e) => e.status === 'ENROLLED').length;
	}

	get pendingCount() {
		return this.#enrollments.filter((e) => e.status === 'PENDING').length;
	}

	get pending() {
		return this.#enrollments.filter((e) => e.status === 'PENDING');
	}

	get enrolled() {
		return this.#enrollments.filter((e) => e.status === 'ENROLLED');
	}

	set(enrollments: EnrollmentResponse[]) {
		this.#enrollments = enrollments;
	}

	add(enrollment: EnrollmentResponse) {
		this.#enrollments.push(enrollment);
	}

	remove(id: number) {
		this.#enrollments = this.#enrollments.filter(
			(e) => e.enrollmentId !== id
		);
	}

	update(id: number, enrollment: Partial<EnrollmentResponse>) {
		this.#enrollments = this.#enrollments.map((e) =>
			e.enrollmentId === id ? { ...e, ...enrollment } : e
		);
	}

	clear() {
		this.#enrollments = [];
	}
}

export const enrollmentsStore = new EnrollmentsStore();