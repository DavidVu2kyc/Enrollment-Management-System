import { error, fail, redirect } from "@sveltejs/kit";
import type { PageServerLoad, Actions } from "./$types";
import { createServerApiClient } from "$lib/api/client";
import type { Section } from "$lib/types/section";
import type { SectionResponse } from "$lib/server/section";
import type { StudentResponse } from "$lib/types/student";

export const load: PageServerLoad = async ({ locals, fetch }) => {
  if (!locals.token) {
    throw error(401, "Unauthorized");
  }

  const client = createServerApiClient(locals.token, fetch);

  try {
    //  SectionResponse[] to match EnrollmentForm's availableSections prop
    const availableSections = await client.get<SectionResponse[]>("/sections");

    return {
      availableSections,
      token: locals.token,
    };
  } catch (err: any) {
    console.error("Failed to load sections:", err.message);
    throw error(500, "Failed to load sections data");
  }
};

export const actions: Actions = {
  default: async ({ request, locals, fetch }) => {
    if (!locals.token) {
      return fail(401, { message: "Unauthorized" });
    }

    const data = await request.formData();
    const sectionId = data.get("sectionId") as string;

    if (!sectionId) {
      return fail(400, { message: "Section ID is required" });
    }

    const client = createServerApiClient(locals.token, fetch);

    try {
      const meResponse = await client.get<StudentResponse>("/students/profile");
      const studentId = meResponse?.studentId;

      await client.post("/enrollments", {
        studentId: Number(studentId),
        sectionId: Number(sectionId),
        status: "PENDING",
      });
    } catch (err: any) {
      console.error("Enrollment creation error:", err.message);
      return fail(500, {
        message: err.message || "Failed to enroll in section",
      });
    }
    // redirect to dashboard
    throw redirect(303, "/");
  },
};
