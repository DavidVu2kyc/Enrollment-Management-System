import type { PageServerLoad } from './$types';
import type { Section } from '$lib/types/section';

export const load: PageServerLoad = async ({ fetch, url }) => {
  const department = url.searchParams.get('dept') || '';
  const semester = url.searchParams.get('semester') || '';

  try {
    const queryParams = new URLSearchParams();
    if (department) queryParams.append('department', department);
    if (semester) queryParams.append('semester', semester);

    const response = await fetch(`/api/sections?${queryParams}`);
    if (!response.ok) throw new Error('Failed to fetch sections');

    const sections: Section[] = await response.json();

    return {
      sections,
      filters: { department, semester },
    };
  } catch (error) {
    console.error('Error loading courses:', error);
    return { sections: [], filters: { department, semester } };
  }
};