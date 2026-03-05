import { error } from '@sveltejs/kit';
import type { PageServerLoad, Actions } from './$types';

export const load: PageServerLoad = async ({ fetch }) => {
  try {
    const response = await fetch('/api/user/profile');
    if (!response.ok) throw error(401, 'Unauthorized');

    const profile = await response.json();
    return { profile };
  } catch (err) {
    throw error(500, 'Failed to load profile');
  }
};

export const actions: Actions = {
  updateProfile: async ({ request, fetch }) => {
    const data = await request.formData();
    
    try {
      const response = await fetch('/api/user/profile', {
        method: 'PATCH',
        body: data,
      });

      if (!response.ok) {
        return { success: false, error: 'Update failed' };
      }

      return { success: true };
    } catch (err) {
      return { success: false, error: 'Update error' };
    }
  },
};