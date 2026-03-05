import type { LayoutServerLoad } from './$types';
import type { User } from '$lib/types';
import { redirect } from '@sveltejs/kit';

export const load: LayoutServerLoad = async ({ locals }) => {
	// locals.user is set in hooks.server.ts based on JWT cookie
	if (!locals.user) {
		// Not authenticated, redirect to login page
		throw redirect(302, '/login');
	}

	// Return the user object to the layout for NavBar visibility and other UI needs
	const user: User = locals.user;
	return { user };
};