import { createServerApiClient } from '$lib/api/client';
import type { EnrollmentResponse, UpdateEnrollmentRequest } from '$lib/types/enrollment';
import type { StudentResponse } from '$lib/types/student';

export async function getMyEnrollments(token: string, fetch: typeof globalThis.fetch) {
  const client = createServerApiClient(token, fetch);
  const { studentId } = await client.get<StudentResponse>('students/profile');
  if (!studentId) return [];
  return await client.get<EnrollmentResponse[]>(`/enrollments/my/${studentId}`);
}

export async function deleteEnrollment(
  enrollmentId: number, // ✅ number, not string
  token: string,
  fetch: typeof globalThis.fetch
) {
  const client = createServerApiClient(token, fetch);
  const { studentId } = await client.get<StudentResponse>('students/profile');
  await client.delete(`/enrollments/${enrollmentId}?studentId=${studentId}`);
}

export async function updateEnrollment(
  enrollmentId: number,
  updateData: UpdateEnrollmentRequest,
  token: string,
  fetch: typeof globalThis.fetch
) {
  const client = createServerApiClient(token, fetch);
  await client.put(`/enrollments/${enrollmentId}/status`, updateData);
}