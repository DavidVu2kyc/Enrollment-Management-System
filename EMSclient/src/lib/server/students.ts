import { createServerApiClient } from "$lib/api/client";
import type { StudentResponse, UpdateStudentRequest } from "$lib/types/student";

/**
 * Fetch the current student's profile
 */
export async function getProfile(
  token: string | null,
  fetch: typeof globalThis.fetch,
): Promise<StudentResponse> {
    const client = createServerApiClient(token, fetch);
  return client.get<StudentResponse>("/students/profile");
}

/**
 * Update the current student's profile
 */
export async function updateProfile(
  studentId: number,
  request: UpdateStudentRequest,
  token: string,
  fetch: typeof globalThis.fetch,
): Promise<{ success: boolean }> {
  const client = createServerApiClient(token, fetch);

  await client.put(`/students/${studentId}`, request); // ✅ sends { firstName, lastName } directly

  return { success: true };
}
