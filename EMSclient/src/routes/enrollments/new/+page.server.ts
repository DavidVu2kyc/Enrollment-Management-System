import { error, fail, redirect } from '@sveltejs/kit';
import type { PageServerLoad, Actions } from './$types';
import { createServerApiClient } from '$lib/api/client';
import type { Section } from '$lib/types';

export const load: PageServerLoad = async ({ locals, fetch }) => {
    if (!locals.token) {
        throw error(401, 'Unauthorized');
    }

    const client = createServerApiClient(locals.token, fetch);

    try {
        const availableSections = await client.get<Section[]>('/sections');

        return {
            availableSections,
        };
    } catch (err: any) {
        console.error('Failed to load sections:', err.message);
        throw error(500, 'Failed to load sections data');
    }
};

export const actions: Actions = {
    default: async ({ request, locals, fetch }) => {
        if (!locals.token) {
            return fail(401, { message: 'Unauthorized' });
        }

        const data = await request.formData();
        const sectionId = data.get('sectionId') as string;

        if (!sectionId) {
            return fail(400, { message: 'Section ID is required' });
        }

        const client = createServerApiClient(locals.token, fetch);

        try {
            const meResponse = await client.get<any>('/students/me');

            await client.post('/enrollments', {
                studentId: meResponse.studentId,
                sectionId
            });
        } catch (err: any) {
            console.error('Enrollment creation error:', err.message);
            return fail(500, { message: err.message || 'Failed to enroll in section' });
        }

        throw redirect(303, '/enrollments');
    },
};