import type { Enrollment } from '$lib/types';

class EnrollmentsStore {
	#enrollments = $state<Enrollment[]>([]);

	get all() {
		return this.#enrollments;
	}

	get totalCount() {
		return this.#enrollments.length;
	}

	get enrolledCount() {
		return this.#enrollments.filter((e) => e.status === 'ENROLLED' || e.isEnrolled).length;
	}

	get pendingCount() {
		return this.#enrollments.filter((e) => e.status === 'PENDING' || !e.isEnrolled).length;
	}

	get pending() {
		return this.#enrollments.filter((e) => e.status === 'PENDING' || !e.isEnrolled);
	}

	get enrolled() {
		return this.#enrollments.filter((e) => e.status === 'ENROLLED' || e.isEnrolled);
	}

	get conflicted() {
		return this.#enrollments.filter((e) => e.scheduledConflict);
	}

	set(enrollments: Enrollment[]) {
		this.#enrollments = enrollments;
	}

	add(enrollment: Enrollment) {
		this.#enrollments.push(enrollment);
	}

	remove(id: string) {
		this.#enrollments = this.#enrollments.filter((e) => e.id !== id);
	}

	update(id: string, enrollment: Partial<Enrollment>) {
		this.#enrollments = this.#enrollments.map((e) =>
			e.id === id ? { ...e, ...enrollment } : e
		);
	}

	clear() {
		this.#enrollments = [];
	}
}

export const enrollmentsStore = new EnrollmentsStore();
