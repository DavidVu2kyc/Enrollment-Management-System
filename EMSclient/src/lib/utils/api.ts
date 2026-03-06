// api helper functions

import { apiClient, createServerApiClient } from '$lib/api/client';

export async function fetchUserEnrollments(token: string, studentId: string, fetch: typeof globalThis.fetch) {
  const client = createServerApiClient(token, fetch);
  return client.get(`/enrollments/my/${studentId}`);
}

export async function deleteEnrollment(id: string) {
  return apiClient.delete(`/enrollments/${id}`);
}
