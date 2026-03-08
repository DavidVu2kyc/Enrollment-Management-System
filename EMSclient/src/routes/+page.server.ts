import { createServerApiClient } from "$lib/api/client";
import type { UpdateEnrollmentRequest } from "$lib/types/enrollment";
import type { StudentResponse } from "$lib/types/student";
import type { PageServerLoad } from "./$types";

export const load: PageServerLoad = async ({ locals }) => {
  return {
    token: locals.token,
    stats: null,
  };
};

export const deleteEnrollment = async function (
  enrollmentId: number, // ✅ number, not string
  token: string,
  fetch: typeof globalThis.fetch,
) {
  const client = createServerApiClient(token, fetch);
  const { studentId } = await client.get<StudentResponse>("students/profile");
  await client.delete(`/enrollments/${enrollmentId}?studentId=${studentId}`);
}

export async function updateEnrollment(
  enrollmentId: number,
  updateData: UpdateEnrollmentRequest,
  token: string,
  fetch: typeof globalThis.fetch,
) {
  const client = createServerApiClient(token, fetch);
  await client.put(`/enrollments/${enrollmentId}/status`, updateData);
}
