import { error, redirect } from '@sveltejs/kit';
import type { PageServerLoad } from './$types';
import { createServerApiClient } from '$lib/api/client';
import type { Section } from '$lib/types/section';

export const load: PageServerLoad = async ({ locals, fetch }) => {
    if (!locals.token) {
        throw redirect(302, '/login');
    }

    const client = createServerApiClient(locals.token, fetch);

    try {
        const sections = await client.get<Section[]>('/sections');
        return {
            sections
        };
    } catch (err: any) {
        console.error('Failed to load admin sections data:', err.message);
        throw error(500, 'Failed to load curriculum records');
    }
};
