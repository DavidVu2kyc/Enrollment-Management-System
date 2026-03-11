import { createServerApiClient } from "$lib/api/client";
import type { StudentResponse, UpdateStudentRequest } from "$lib/types/student";

/**
 * Fetch the current student's profile
 */
export async function getProfile(
  token: string | null,
  fetch: typeof globalThis.fetch,
): Promise<StudentResponse> {
  // debugger
  const client = createServerApiClient(token, fetch);
  return client.get<StudentResponse>("/students/profile");
}

/**
 * Update the current student's profile
 */
export async function updateProfile(
  studentId: number,
  request: UpdateStudentRequest,
  token: string | null,  // Changed to accept null
  fetch: typeof globalThis.fetch,
): Promise<{ success: boolean }> {
  // debugger;
  const client = createServerApiClient(token, fetch);
  console.log("TOKEN:", token);

  await client.put(`/students/${studentId}`, request); 

  return { success: true };
}
