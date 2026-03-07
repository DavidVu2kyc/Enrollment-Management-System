import { createServerApiClient } from "$lib/api/client";

/** Matches backend DegreeResponse DTO */
export interface DegreeResponse {
  degreeId: number;
  name: string;
  description: string;
}

/** Matches backend CourseResponse DTO */
export interface CourseResponse {
  courseId: number;
  code: string;
  title: string;
  units: number;
  degreeId: number;
}

/**
 * Fetch all degrees.
 * Maps to: GET /api/degrees
 */
export async function getAllDegrees(
  token: string | null,
  fetch: typeof globalThis.fetch,
): Promise<DegreeResponse[]> {
  const client = createServerApiClient(token, fetch);
  return client.get<DegreeResponse[]>("/degrees");
}

/**
 * Fetch all courses belonging to a specific degree.
 * Maps to: GET /api/degrees/{id}/courses
 */
export async function getCoursesByDegree(
  degreeId: number,
  token: string | null,
  fetch: typeof globalThis.fetch,
): Promise<CourseResponse[]> {
  const client = createServerApiClient(token, fetch);
  return client.get<CourseResponse[]>(`/degrees/${degreeId}/courses`);
}
