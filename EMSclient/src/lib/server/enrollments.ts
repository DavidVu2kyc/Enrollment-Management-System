import { createServerApiClient } from '$lib/api/client';
import type { Enrollment, EnrollmentResponse } from '$lib/types/enrollment';

export async function getMyEnrollments(token: string, fetch: typeof globalThis.fetch) {
  const client = createServerApiClient(token, fetch);
  
  // get studentId 
  const meResponse = await client.get<any>('students/profile');
  console.log("My response"+meResponse);
  const studentId = meResponse.studentId;
  
 // 2️⃣ (optional) fetch the active term – not used for enrollments here
  await client.get<any>('/terms/active');
  if (studentId) {
    return await client.get<EnrollmentResponse[]>(
      `/enrollments/my/${studentId}`
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
  const meResponse = await client.get<any>('students/profile');
  await client.delete(`/enrollments/${enrollmentId}?studentId=${meResponse.studentId}`);
}

