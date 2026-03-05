import { error, fail } from '@sveltejs/kit';
import type { PageServerLoad, Actions } from './$types';
import { createServerApiClient } from '$lib/api/client';
import type { Enrollment } from '$lib/types/enrollment';

export const load: PageServerLoad = async ({ locals, fetch }) => {
  if (!locals.token) {
    throw error(401, 'Unauthorized');
  }

  const client = createServerApiClient(locals.token, fetch);

  try {
    // Example: get the current user profile first to get the studentId:
    const meResponse = await client.get<any>('/students/me');
    const studentId = meResponse.studentId;

    // Fetch active term to pass to enrollments query
    const termResponse = await client.get<any>('/terms/active');

    let enrollments: Enrollment[] = [];
    if (studentId && termResponse?.termId) {
      enrollments = await client.get<Enrollment[]>(`/enrollments/my/${studentId}?termId=${termResponse.termId}`);
    }

    return {
      enrollments,
      studentId
    };
  } catch (err: any) {
    console.error('Failed to load enrollments:', err.message);
    return {
      enrollments: [],
      studentId: null
    };
  }
};

export const actions: Actions = {
  delete: async ({ request, locals, fetch }) => {
    if (!locals.token) {
      return fail(401, { message: 'Unauthorized' });
    }

    const data = await request.formData();
    const enrollmentId = data.get('id');

    if (!enrollmentId) {
      return fail(400, { message: 'Enrollment ID required' });
    }

    const client = createServerApiClient(locals.token, fetch);

    try {
      // Get studentId for the delete request as required by OpenAPI spec
      const meResponse = await client.get<any>('/students/me');

      await client.delete(`/enrollments/${enrollmentId}?studentId=${meResponse.studentId}`);
      return { success: true };
    } catch (err: any) {
      console.error('Failed to delete enrollment:', err.message);
      return fail(500, { message: 'Failed to drop enrollment' });
    }
  }
};