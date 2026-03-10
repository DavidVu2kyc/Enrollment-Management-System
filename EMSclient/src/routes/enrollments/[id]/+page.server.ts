// src/routes/enrollments/[id]/+page.server.ts
import { error, fail, redirect } from "@sveltejs/kit";
import type { PageServerLoad, Actions } from "./$types";
import { createServerApiClient } from "$lib/api/client";
import type { EnrollmentResponse } from "$lib/types/enrollment";
import type { SectionResponse } from "$lib/server/section";
import type { StudentResponse } from "$lib/types/student";

export const load: PageServerLoad = async ({ params, locals, fetch }) => {
  if (!locals.token) throw error(401, "Unauthorized");

  const client = createServerApiClient(locals.token, fetch);

  try {
    const availableSections = await client.get<SectionResponse[]>("/sections");

    let enrollment: EnrollmentResponse | null = null;
    if (params.id && params.id !== "new") {
      enrollment = await client.get<EnrollmentResponse>(`/enrollments/${params.id}`);
    }

    return { enrollment, availableSections };
  } catch (err: any) {
    console.error("Failed to load enrollment data:", err.message);
    throw error(500, "Failed to load enrollment data");
  }
};

export const actions: Actions = {
  save: async ({ request, params, locals, fetch }) => {  // ← renamed from "default"
    if (!locals.token) return fail(401, { message: "Unauthorized" });

    const data      = await request.formData();
    const sectionId = data.get("sectionId") as string | null;
    const status    = data.get("status")    as string | null;
    const client    = createServerApiClient(locals.token, fetch);

    try {
      let enrollment: EnrollmentResponse;

      if (params.id && params.id !== "new") {
        // ── EDIT: PUT /apply accepts sectionId + status ──
        enrollment = await client.put<EnrollmentResponse>(
          `/enrollments/${params.id}/apply`,   // ← was /registration
          {
            ...(sectionId ? { sectionId: Number(sectionId) } : {}),
            ...(status    ? { status }                        : {}),
          },
        );
        return { enrollment };                 // ← return so store can sync
      } else if (sectionId) {
        // ── CREATE: POST /enrollments ─────────────────────
        const me = await client.get<StudentResponse>("/students/profile");
        enrollment = await client.post<EnrollmentResponse>("/enrollments", {
          studentId: me.studentId,
          sectionId: Number(sectionId),
          status: "PENDING",
        });
        return { enrollment };
      } else {
        return fail(400, { message: "Section ID is required for new enrollment" });
      }
    } catch (err: any) {
      console.error("Enrollment action error:", err.message);
      return fail(err.status ?? 500, {
        message: err.message ?? "Failed to process enrollment",
      });
    }
  },
};