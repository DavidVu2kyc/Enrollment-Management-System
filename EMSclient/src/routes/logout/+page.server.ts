import { redirect } from '@sveltejs/kit';
import type { Actions, PageServerLoad } from './$types';

export const load: PageServerLoad = async ({ cookies }) => {
    // Clear authentication cookies
    cookies.delete('jwt', { path: '/' });
    cookies.delete('user', { path: '/' });
    // Redirect to login page after logout
    throw redirect(303, '/login');
};
