import { error, fail } from '@sveltejs/kit';
import { getMyEnrollments, deleteEnrollment } from '$lib/server/enrollments';

export const load = async ({ locals, fetch }) => {
  if (!locals.token) throw error(401, 'Unauthorized');
  
  try {
    const enrollments = await getMyEnrollments(locals.token, fetch);
    return { enrollments };
  } catch (err: any) {
    console.error('Failed to load enrollments:', err.message);
    return { enrollments: [] };
  }
};

export const actions = {
  delete: async ({ request, locals, fetch }) => {
    if (!locals.token) return fail(401, { message: 'Unauthorized' });
    
    const data = await request.formData();
    const enrollmentId = data.get('id') as string;
    
    if (!enrollmentId) return fail(400, { message: 'Enrollment ID required' });
    
    try {
      await deleteEnrollment(enrollmentId, locals.token, fetch);
      return { success: true };
    } catch (err: any) {
      return fail(500, { message: 'Failed to drop enrollment' });
    }
  }
};