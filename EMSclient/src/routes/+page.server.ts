import type { PageServerLoad } from './$types';

export const load: PageServerLoad = async ({ locals }) => {
	// User is already validated by hooks.server.ts
	// We'll load enrollments on the client side for real-time updates

	return {
		user: locals.user
	};
};
