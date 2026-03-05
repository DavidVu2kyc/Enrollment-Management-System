import { error, fail, redirect } from '@sveltejs/kit';
import type { PageServerLoad, Actions } from './$types';
import { createServerApiClient } from '$lib/api/client';
import type { Student } from '$lib/types/student';

export const load: PageServerLoad = async ({ locals, fetch }) => {
    if (!locals.token) {
        throw redirect(302, '/login');
    }

    // Assuming only admin can access this route (logic in hooks.server.ts)
    const client = createServerApiClient(locals.token, fetch);

    try {
        // Fetch students and degrees
        const [studentsRes, degreesRes] = await Promise.all([
            client.get<any>('/students'), // Might be a Page object
            client.get<any[]>('/degrees')
        ]);

        // Flatten if paged
        const students = Array.isArray(studentsRes) ? studentsRes : (studentsRes.content || []);

        return {
            students: students as Student[],
            degrees: degreesRes
        };
    } catch (err: any) {
        console.error('Failed to load admin students data:', err.message);
        throw error(500, 'Failed to load system records');
    }
};

export const actions: Actions = {
    delete: async ({ request, locals, fetch }) => {
        if (!locals.token) return fail(401, { message: 'Unauthorized' });

        const data = await request.formData();
        const id = data.get('id') as string;

        if (!id) return fail(400, { message: 'Student ID required' });

        const client = createServerApiClient(locals.token, fetch);

        try {
            await client.delete(`/students/${id}`);
            return { success: true };
        } catch (err: any) {
            return fail(500, { message: err.message || 'Deletion failed' });
        }
    }
};