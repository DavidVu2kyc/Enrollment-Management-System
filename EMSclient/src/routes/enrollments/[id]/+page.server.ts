import { error, fail, redirect } from "@sveltejs/kit";
import type { PageServerLoad, Actions } from "./$types";
import { createServerApiClient } from "$lib/api/client";
import type { Enrollment } from "$lib/types/enrollment";
import type { Section } from "$lib/types/section";

export const load: PageServerLoad = async ({ params, locals, fetch }) => {
  if (!locals.token) {
    throw error(401, "Unauthorized");
  }

  const cookie = `access_token=${locals.token}`;
  const client = createServerApiClient(fetch, cookie);

  try {
    // Fetch available sections
    const availableSections = await client.get<Section[]>("/sections");

    // Fetch existing enrollment
    let enrollment: Enrollment | null = null;
    if (params.id && params.id !== "new") {
      enrollment = await client.get<Enrollment>(`/enrollments/${params.id}`);
    }

    return {
      enrollment,
      availableSections,
    };
  } catch (err: any) {
    console.error("Failed to load enrollment data:", err.message);
    throw error(500, "Failed to load enrollment data");
  }
};

export const actions: Actions = {
  default: async ({ request, params, locals, fetch }) => {
    if (!locals.token) {
      return fail(401, { message: "Unauthorized" });
    }

    const data = await request.formData();
    const sectionId = data.get("sectionId") as string;
    const status = data.get("status") as string;
    
    const cookie = `access_token=${locals.token}`;
    const client = createServerApiClient(fetch, cookie);

    try {
      if (params.id && params.id !== "new") {
        // Update existing
        await client.put(`/enrollments/${params.id}/status`, { status });
      } else if (sectionId) {
        // Create new enrollment
        const meResponse = await client.get<any>("students/profile");
        await client.post("/enrollments", {
          studentId: meResponse.studentId,
          sectionId,
        });
      } else {
        return fail(400, {
          message: "Section ID is required for new enrollment",
        });
      }
    } catch (err: any) {
      console.error("Enrollment action error:", err.message);
      return fail(500, {
        message: err.message || "Failed to process enrollment",
      });
    }

    throw redirect(303, "/enrollments");
  },
};
