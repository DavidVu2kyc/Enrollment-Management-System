import { fail, redirect } from '@sveltejs/kit';
import type { Actions } from './$types';

const API_BASE = process.env.VITE_API_URL || 'http://localhost:8080/api';

export const actions: Actions = {
	login: async ({ request, cookies }) => {
		const data = await request.formData();
		const email = data.get('email') as string;
		const password = data.get('password') as string;

		if (!email || !password) {
			return fail(400, { email, error: 'Email and password are required' });
		}

		/* 
		// TEMPORARILY DISABLED FOR BYPASS
		try {
			const response = await fetch(`${API_BASE}/auth/login`, {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify({ email, password })
			});

			if (!response.ok) {
				const error = await response.json();
				return fail(response.status, {
					email,
					error: error.message || 'Invalid credentials'
				});
			}

			const result = await response.json();

			// Store token in httpOnly cookie
			if (result.token) {
				cookies.set('authToken', result.token, {
					path: '/',
					httpOnly: true,
					secure: process.env.NODE_ENV === 'production',
					sameSite: 'lax',
					maxAge: 7 * 24 * 60 * 60 // 7 days
				});
			}

			// Store user data in session
			cookies.set(
				'user',
				JSON.stringify({
					id: result.user.id,
					email: result.user.email,
					name: result.user.name,
					role: result.user.role
				}),
				{
					path: '/',
					secure: process.env.NODE_ENV === 'production',
					sameSite: 'lax',
					maxAge: 7 * 24 * 60 * 60
				}
			);

			throw redirect(303, '/');
		} catch (error) {
			if (error instanceof redirect) {
				throw error;
			}
			console.error('Login error:', error);
			return fail(500, {
				email,
				error: 'An error occurred during login. Please try again.'
			});
		}
		*/

		// BYPASS LOGIC: Always succeed with a mock user
		console.log('BYPASSING LOGIN FOR:', email);
		
		const role = email.includes('admin') ? 'ADMIN' : 'STUDENT';
		const name = email.split('@')[0].charAt(0).toUpperCase() + email.split('@')[0].slice(1);

		cookies.set('authToken', 'mock-token-' + Date.now(), {
			path: '/',
			httpOnly: true,
			secure: false,
			sameSite: 'lax',
			maxAge: 7 * 24 * 60 * 60
		});

		cookies.set(
			'user',
			JSON.stringify({
				id: 'mock-id-' + role.toLowerCase(),
				email: email,
				name: name + ' (Sandbox)',
				role: role
			}),
			{
				path: '/',
				secure: false,
				sameSite: 'lax',
				maxAge: 7 * 24 * 60 * 60
			}
		);

		throw redirect(303, '/');
	}
};