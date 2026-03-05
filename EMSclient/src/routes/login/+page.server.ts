import { fail, redirect } from '@sveltejs/kit';
import { superValidate } from 'sveltekit-superforms';
import { yup } from 'sveltekit-superforms/adapters';
import { loginSchema } from '$lib/schemas/login.schema';
import type { PageServerLoad, Actions } from './$types';
import { createServerApiClient } from '$lib/api/client';

export const load: PageServerLoad = async () => {
	const form = await superValidate(yup(loginSchema));
	return { form };
};

export const actions: Actions = {
	login: async ({ request, cookies }) => {
		const form = await superValidate(request, yup(loginSchema));

		if (!form.valid) {
			return fail(400, { form });
		}

		const { username, password } = form.data;

		try {
			const client = createServerApiClient(null, fetch); // Pass the SvelteKit fetch
			const response = await client.post<{ token: string, tokenType: string, username: string, role: string }>('/auth/login', {
				username,
				password
			});

			// Set the JWT token cookie
			cookies.set('jwt', response.token, {
				path: '/',
				httpOnly: true,
				secure: false, // Set to true in production
				sameSite: 'lax',
				maxAge: 60 * 60 * 24 * 7 // 1 week
			});

			// Optional: store user details if needed
			cookies.set('user', JSON.stringify({
				username: response.username,
				role: response.role
			}), {
				path: '/',
				secure: false,
				sameSite: 'lax',
				maxAge: 60 * 60 * 24 * 7
			});

		} catch (error: any) {
			console.error('Login error:', error);
			const errorMessage = error.message || 'Invalid credentials or server error';
			return fail(401, {
				form,
				message: errorMessage
			});
		}

		throw redirect(303, '/');
	}
};