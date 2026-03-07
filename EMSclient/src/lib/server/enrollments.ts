import { createServerApiClient } from '$lib/api/client';
import type { Enrollment } from '$lib/types/enrollment';

export async function getMyEnrollments(token: string, fetch: typeof globalThis.fetch) {
  const client = createServerApiClient(token, fetch);
  
  const meResponse = await client.get<any>('/api/students/me');
  const studentId = meResponse.studentId;
  
  const termResponse = await client.get<any>('/terms/active');
  
  if (studentId && termResponse?.termId) {
    return await client.get<Enrollment[]>(
      `/enrollments/my/${studentId}?termId=${termResponse.termId}`
    );
  }
  return [];
}

export async function deleteEnrollment(
  enrollmentId: string,
  token: string,
  fetch: typeof globalThis.fetch
) {
  const client = createServerApiClient(token, fetch);
  const meResponse = await client.get<any>('/api/students/me');
  await client.delete(`/enrollments/${enrollmentId}?studentId=${meResponse.studentId}`);
}