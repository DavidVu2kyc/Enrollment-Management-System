import { json } from '@sveltejs/kit';
import type { RequestHandler } from './$types';

const API_BASE = process.env.VITE_API_URL || 'http://localhost:8080/api';

export const POST: RequestHandler = async ({ request, cookies }) => {
	try {
		const body = await request.json();

		const response = await fetch(`${API_BASE}/auth/login`, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(body)
		});

		if (!response.ok) {
			const error = await response.json();
			return json(error, { status: response.status });
		}

		const result = await response.json();

		// Set cookies if login successful
		if (result.token) {
			cookies.set('authToken', result.token, {
				path: '/',
				httpOnly: true,
				secure: process.env.NODE_ENV === 'production',
				sameSite: 'lax',
				maxAge: 7 * 24 * 60 * 60
			});
		}

		return json(result);
	} catch (error) {
		console.error('Auth login error:', error);
		return json(
			{ success: false, message: 'Internal server error' },
			{ status: 500 }
		);
	}
};
