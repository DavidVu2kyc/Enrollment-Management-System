import { superValidate } from 'sveltekit-superforms/server';
import { yup } from 'sveltekit-superforms/adapters';
import { enrollmentSchema } from '$lib/schemas/enrollmentSchema';
import { apiClient } from '$lib/api/client';
import { error } from '@sveltejs/kit';

export const load = async ({ params }) => {
    try {
        const enrollment = await apiClient.getEnrollment(params.id);
        const form = await superValidate(enrollment, yup(enrollmentSchema));
        return { form, enrollment };
    } catch (e) {
        throw error(404, 'Enrollment not found');
    }
};
