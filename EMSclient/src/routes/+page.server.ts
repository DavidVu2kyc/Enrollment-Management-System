import type { PageServerLoad } from './$types';

export const load: PageServerLoad = async ({ fetch }) => {
  try {
    // Fetch dashboard stats from backend API
    const response = await fetch('/api/stats');
    if (!response.ok) {
      throw new Error(`Failed to fetch stats: ${response.status}`);
    }
    const stats = await response.json();

    return {
      stats,
    };
  } catch (error) {
    console.error('Failed to load home page data:', error);
    return { stats: null };
  }
};