import { error } from "@sveltejs/kit";
import type { PageServerLoad, Actions } from "./$types";

export const load: PageServerLoad = async ({ fetch }) => {
  try {
    const response = await fetch("/api/students/me");
    console.log(response);
    if (!response.ok) throw error(401, "Unauthorized");
    const profile = await response.json();
    return { profile };
  } catch (err) {
    throw error(500, "Failed to load profile");
  }
};

export const actions: Actions = {
  updateProfile: async ({ request, fetch }) => {
    const data = await request.formData();
    try {
      // Get current student ID
      const meRes = await fetch("/api/students/me");
      if (!meRes.ok)
        return { success: false, error: "Unable to fetch user ID" };
      const me = await meRes.json();
      const id = me.id;

      const response = await fetch(`/api/students/${id}`, {
        method: "PUT",
        body: data,
      });

      if (!response.ok) {
        return { success: false, error: "Update failed" };
      }

      return { success: true };
    } catch (err) {
      return { success: false, error: "Update error" };
    }
  },
};
