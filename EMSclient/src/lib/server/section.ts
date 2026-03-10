import { createServerApiClient } from "$lib/api/client";
import type { CourseResponse, DegreeResponse } from "./degree";
import type { TermResponse } from "./term";
import type { StudentResponse } from "$lib/types/student";

/** Matches backend ScheduleResponse DTO */
export interface ScheduleResponse {
  scheduleId: number;
  dayOfWeek: string;
  startTime: string; // HH:mm:ss from LocalTime
  endTime: string;
}

/** Matches the nested RoomResponse inside SectionResponse */
export interface RoomResponse {
  roomId: number;
  building: string;
  roomNumber: string;
}

/** Matches backend SectionResponse DTO */
export interface  SectionResponse {
  sectionId: number;
  sectionCode: string;
  course: CourseResponse;
  term: TermResponse;
  schedule: ScheduleResponse | null;
  room: RoomResponse | null;
  facultyName: string | null;
  maxSeats: number;
  enrolledCount: number;
  availableSeats: number;
}

/** Matches backend EnrollmentResponse DTO */
export interface EnrollmentResponse {
  enrollmentId: number;
  student: StudentResponse;
  section: SectionResponse;
  status: "PENDING" | "ENROLLED" | "DROPPED";
  enrolledAt: string; // ISO datetime string
  message?: string;
}

/**
 * Fetch all sections, optionally filtered by course and/or term.
 * Maps to: GET /api/sections
 */
export async function getAllSections(
  params: { courseId?: number; termId?: number },
  token: string | null,
  fetch: typeof globalThis.fetch,
): Promise<SectionResponse[]> {
  const client = createServerApiClient(token, fetch);
  const query = new URLSearchParams();
  if (params.courseId != null) query.set("courseId", String(params.courseId));
  if (params.termId != null) query.set("termId", String(params.termId));
  const qs = query.toString();
  return client.get<SectionResponse[]>(`/sections${qs ? `?${qs}` : ""}`);
}

/**
 * Fetch a single section by its ID.
 * Maps to: GET /api/sections/{sectionId}
 */
export async function getSectionById(
  sectionId: number,
  token: string | null,
  fetch: typeof globalThis.fetch,
): Promise<SectionResponse> {
  const client = createServerApiClient(token, fetch);
  return client.get<SectionResponse>(`/sections/${sectionId}`);
}

/**
 * Fetch the list of enrolled students for a section.
 * Maps to: GET /api/sections/{sectionId}/enrollments
 */
export async function getSectionEnrollments(
  sectionId: number,
  token: string | null,
  fetch: typeof globalThis.fetch,
): Promise<EnrollmentResponse[]> {
  const client = createServerApiClient(token, fetch);
  return client.get<EnrollmentResponse[]>(`/sections/${sectionId}/enrollments`);
}
