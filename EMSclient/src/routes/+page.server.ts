import { createServerApiClient } from "$lib/api/client";
import type { EnrollmentResponse } from "$lib/types/enrollment";
import { fail } from "sveltekit-superforms";
import type { PageServerLoad } from "./$types";

export const load: PageServerLoad = async ({ locals }) => {
  return {
    token: locals.token,
    stats: null,
  };
};

// update enrollments and drop enrollments
export const actions = {
  update: async ({ request, locals, fetch }) => {
    const formData = await request.formData();
    const token = locals.token?? "";

    const enrollmentId = formData.get("enrollmentId");
    const status = formData.get("status");

    if (!enrollmentId || !status) {
      return fail(400, { message: "Enrollment ID and status are required" });
    }

    try {
    
      const client = createServerApiClient(token, fetch);
      const result = await client.put<EnrollmentResponse>(
        `/enrollments/${enrollmentId}/status`,
        { status }
      );
      return { success: true, enrollment: result };
    } catch (error: any) {
      return fail(error.status ?? 500, {
        message: error.message ?? "Failed to update enrollment",
      });
    }
  },

  delete: async ({ request, locals, fetch }) => {
    const formData = await request.formData();
    const token = locals.token?? "";

    const enrollmentId = formData.get("enrollmentId");
    const studentId = formData.get("studentId");

    if (!enrollmentId || !studentId) {
      return fail(400, { message: "Enrollment ID and student ID are required" });
    }

    try {
      const client = createServerApiClient(token, fetch);
      const result = await client.delete<EnrollmentResponse>(
        `/enrollments/${enrollmentId}?studentId=${studentId}`
      );
      return { success: true, enrollment: result };
    } catch (error: any) {
      return fail(error.status ?? 500, {
        message: error.message ?? "Failed to drop enrollment",
      });
    }
  },
};
