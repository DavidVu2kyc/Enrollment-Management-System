import { json } from '@sveltejs/kit';
import type { PageServerLoad } from './$types';

export const load: PageServerLoad = async ({ locals }) => {

  const user = locals.user;

  if (!user) {
    return json(
      { message: 'Unauthorized' },
      { status: 401 }
    );
  }

  const student = {
    studentId: user.studentId,
    name: user.name,
    username: user.username,
    role: user.role,
  };

  return { student };
};