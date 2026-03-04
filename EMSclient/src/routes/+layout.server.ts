import type { LayoutServerLoad } from './$types';
import type { User } from '$lib/types';

export const load: LayoutServerLoad = async ({ locals }) => {
	let user: User | null = null;

	if (locals.user) {
		user = locals.user;
	}

	return {
		user
	};
};