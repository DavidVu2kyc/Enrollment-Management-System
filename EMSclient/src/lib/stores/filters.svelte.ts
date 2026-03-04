import type { EnrollmentFilterType } from '$lib/types';
import { enrollmentsStore } from './enrollments.svelte';

class FilterStore {
	type = $state<EnrollmentFilterType>('all');
	query = $state('');

	get filtered() {
		const all = enrollmentsStore.all;
		if (this.type === 'enrolled') {
			return all.filter((e) => e.status === 'ENROLLED' || e.isEnrolled);
		}
		if (this.type === 'pending') {
			return all.filter((e) => e.status === 'PENDING' || !e.isEnrolled);
		}
		return all;
	}

	get searchedAndFiltered() {
		const filtered = this.filtered;
		if (!this.query.trim()) return filtered;
		
		const search = this.query.toLowerCase();
		return filtered.filter(
			(e) =>
				e.section?.courseName.toLowerCase().includes(search) ||
				e.section?.courseCode.toLowerCase().includes(search) ||
				e.section?.sectionNumber.toLowerCase().includes(search)
		);
	}

	setType(type: EnrollmentFilterType) {
		this.type = type;
	}

	setQuery(query: string) {
		this.query = query;
	}
}

export const filterStore = new FilterStore();
