import { fail, redirect } from '@sveltejs/kit';
import { superValidate } from 'sveltekit-superforms';
import { yup } from 'sveltekit-superforms/adapters';
import { registerSchema } from '$lib/schemas/register.schema';
import type { Actions, PageServerLoad } from './$types';
import { createServerApiClient } from '$lib/api/client';
import type { Degree } from '$lib/types';

export const load: PageServerLoad = async ({ fetch }) => {
    const form = await superValidate(yup(registerSchema));
    const client = createServerApiClient(null, fetch);

    let degrees: Degree[] = [];
    try {
        debugger
        const response = await client.get<Degree[]>('/terms/degrees');
        degrees = response;
    } catch (error) {
        console.error('Failed to fetch degrees:', error);
    }

    return { form, degrees };
};

export const actions: Actions = {
    register: async ({ request, fetch }) => {
        const form = await superValidate(request, yup(registerSchema));

        if (!form.valid) {
            return fail(400, { form });
        }

        try {
            const client = createServerApiClient(null, fetch);

            // Hit the backend registration endpoint
            await client.post('/auth/register', form.data);

        } catch (error: any) {
            console.error('Registration error:', error);
            const errorMessage = error.message || 'Registration failed or username already exists.';
            return fail(400, {
                form,
                message: errorMessage
            });
        }

        // Redirect to login after successful registration
        throw redirect(303, '/login');
    }
};
