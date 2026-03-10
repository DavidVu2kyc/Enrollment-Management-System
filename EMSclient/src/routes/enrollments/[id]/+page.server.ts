// src/routes/enrollments/[id]/+page.server.ts
import { error, fail, redirect } from "@sveltejs/kit";
import type { PageServerLoad, Actions } from "./$types";
import { createServerApiClient } from "$lib/api/client";
import type { Enrollment, EnrollmentResponse } from "$lib/types/enrollment";
import type { SectionResponse } from "$lib/server/section";
import type { StudentResponse } from "$lib/types/student";

export const load: PageServerLoad = async ({ params, locals, fetch }) => {
  if (!locals.token) {
    throw error(401, "Unauthorized");
  }
  // ✅ Correct arg order: JWT first, fetch second
  const client = createServerApiClient(locals.token, fetch);
  console.log("token : " + locals.token);
  try {
    
    // ✅ GET /api/sections — no sectionId here, we want the full catalog
    debugger;
    const availableSections = await client.get<SectionResponse[]>("/sections");
    console.log("available sections existing" , availableSections);

    // ✅ Only fetch when editing an existing record
    let enrollment: EnrollmentResponse | null = null;
    let sectionId = params.id ;
   
    if (sectionId && sectionId !== "new") {
      enrollment = await client.get<EnrollmentResponse>(
        `/enrollments/${sectionId}`,
      );
    }

    return { enrollment, availableSections };
  } catch (err: any) {
    console.error("Failed to load enrollment data: in detail ", err.message);
    throw error(500, "Failed to load enrollment data in detail ");
  }
};

export const actions: Actions = {
  default: async ({ request, params, locals, fetch }) => {
    // fetch token first
    if (!locals.token) {
      return fail(401, { message: "Unauthorized" });
    }

    const data = await request.formData();
    const sectionId = data.get("sectionId") as string;
    const status = data.get("status") as string;
    console.log("Data + sectioNid + status : " + data + " " + sectionId + " " + status);

    // ✅ Correct arg order to api client
    const client = createServerApiClient(locals.token, fetch);

    try {
      if (params.id && params.id !== "new") {
        // ── UPDATE: PUT /api/enrollments/{id}/status ──────────
        await client.put(`/enrollments/${params.id}/status`, { status });
      } else if (sectionId) {
        // ── CREATE: POST /api/enrollments ─────────────────────
    
        const me = await client.get<StudentResponse>("/students/profile");
        console.log("profile existed  : " + me);


        await client.post("/enrollments", {
          studentId: me.studentId,
          sectionId: Number(sectionId),
          status: "PENDING",
        });
      } else {
        return fail(400, {
          message: "Section ID is required for new enrollment",
        });
      }
    } catch (err: any) {
      console.error("Enrollment action error:", err.message);
      return fail(err.status ?? 500, {
        message: err.message ?? "Failed to process enrollment",
      });
    }

    // ✅ Redirect back to the dashboard, not /enrollments (which may not exist)
    redirect(303, "/");
  },
};
