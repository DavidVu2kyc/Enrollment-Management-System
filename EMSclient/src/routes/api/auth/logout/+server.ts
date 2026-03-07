import { json } from '@sveltejs/kit';
import type { RequestHandler } from './$types';

export const POST: RequestHandler = async ({ cookies }) => {
	try {
		// Clear authentication cookies
		cookies.delete('jwt', { path: '/' });
		cookies.delete('user', { path: '/' });

		return json({
			success: true,
			message: 'Logged out successfully'
		});
	} catch (error) {
		console.error('Logout error:', error);
		return json(
			{ success: false, message: 'Failed to logout' },
			{ status: 500 }
		);
	}
};