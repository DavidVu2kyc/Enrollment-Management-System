import { createServerApiClient } from "$lib/api/client";
import type { EnrollmentResponse } from "$lib/types/enrollment";
import { fail } from "@sveltejs/kit";
import type { PageServerLoad } from "./$types";

// authen with local tokens
export const load: PageServerLoad = async ({ locals }) => {
  return {
    token: locals.token,
    stats: null,
  };
};

// update enrollments and drop enrollments - confirm registration ( PENDING  -> ROLLED ) -allow students testing -already fixed in backend
export const actions = {
  // ── Confirm registration (PENDING → ENROLLED) ──
  update: async ({ request, locals, fetch }) => {
    const formData = await request.formData();
    const token = locals.token ?? "";
    const enrollmentId = formData.get("enrollmentId");
    const status = formData.get("status");

    if (!enrollmentId || !status) {
      return fail(400, { message: "Enrollment ID and status are required" });
    }

    try {
      const client = createServerApiClient(token, fetch);
      // update request confirmation
      // update and confirm registration 
      const result = await client.put<EnrollmentResponse>(
        `/enrollments/${enrollmentId}/apply`, // ← was /status, now /apply
        // { status },
      );
      return { success: true, enrollment: result };
    } catch (error: any) {
      return fail(error.status ?? 500, {
        message: error.message ?? "Failed to update enrollment",
      });
    }
  },

  // Drop with draw from a course
  delete: async ({ request, locals, fetch }) => {
    const formData = await request.formData();
    const token = locals.token ?? "";

    const enrollmentId = formData.get("enrollmentId");
    const studentId = formData.get("studentId");

    if (!enrollmentId || !studentId) {
      return fail(400, {
        message: "Enrollment ID and student ID are required",
      });
    }

    try {
      const client = createServerApiClient(token, fetch);
      const result = await client.delete<void>(
        `/enrollments/${enrollmentId}?studentId=${studentId}`,
      );
      return { success: true, enrollment: result };
    } catch (error: any) {
      return fail(error.status ?? 500, {
        message: error.message ?? "Failed to drop enrollment",
      });
    }
  },
  // ── Enroll in a new course (from modal form on this page) ──
  create: async ({ request, locals, fetch }) => {
    const formData = await request.formData();
    const token = locals.token ?? "";

    const sectionId = formData.get("sectionId");
    const studentId = formData.get("studentId");

    if (!sectionId || !studentId) {
      return fail(400, { message: "Section ID and student ID are required" });
    }

    try {
      const client = createServerApiClient(token, fetch);
      const result = await client.post<EnrollmentResponse>("/enrollments", {
        sectionId: Number(sectionId),
        studentId: Number(studentId),
        status: "PENDING",
      });
      return { success: true, enrollment: result };
    } catch (error: any) {
      return fail(error.status ?? 500, {
        message: error.message ?? "Failed to create enrollment",
      });
    }
  },
};
