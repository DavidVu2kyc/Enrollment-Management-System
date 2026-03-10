import type { EnrollmentFilterType } from '$lib/types/enrollment';
import { enrollmentsStore } from './enrollments.svelte';

class FilterStore {
	type = $state<EnrollmentFilterType>('all');
	query = $state('');

	get filtered() {
		const all = enrollmentsStore.all;
		if (this.type === 'enrolled') {
			return all.filter((e) => e.status === 'ENROLLED');
		}
		if (this.type === 'pending') {
			return all.filter((e) => e.status === 'PENDING');
		}
		if (this.type === 'dropped') {
			return all.filter((e) => e.status === 'DROPPED');
		}
		return all;
	}

	get searchedAndFiltered() {
		const filtered = this.filtered;
		if (!this.query.trim()) return filtered;
		
		const search = this.query.toLowerCase();
		return filtered.filter(
			(e) =>
				e.section?.course?.title?.toLowerCase()?.includes(search) ||
				e.section?.course?.code?.toLowerCase()?.includes(search) ||
				e.section?.sectionCode?.toLowerCase()?.includes(search)
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
