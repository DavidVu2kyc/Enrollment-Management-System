import { createServerApiClient } from '$lib/api/client';
import type { StudentResponse } from '$lib/types/student';

/**
 * Fetch the current student's profile
 */
export async function getProfile(
  token: string | null,
  fetch: typeof globalThis.fetch
): Promise<StudentResponse> {
  const client = createServerApiClient(token, fetch);
  return client.get<StudentResponse>('/students/profile');
}

/**
 * Update the current student's profile
 */
export async function updateProfile(
  name: string,
  token: string | null,
  fetch: typeof globalThis.fetch
): Promise<{ success: boolean }> {
  const client = createServerApiClient(token, fetch);
  
  // Get current profile to retrieve student ID
  const profile = await client.get<{ id: number }>('/students/profile');
  
  // Update with new name
  await client.put(`/students/${profile.id}`, { name });
  
  return { success: true };
}