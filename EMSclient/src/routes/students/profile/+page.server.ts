import { error, redirect } from "@sveltejs/kit";
import type { PageServerLoad, Actions } from "./$types";
import { getProfile, updateProfile } from "$lib/server/students";

export const load: PageServerLoad = async ({ fetch, cookies }) => {
  debugger;
  const token = cookies.get("jwt") ?? null;
  if (!token) throw redirect(302, "/login");
  try {
    const profile = await getProfile(token, fetch);
    console.log("Loaded profile:", profile);

    return { profile };
  } catch (err: any) {
    if (err?.status === 401) throw redirect(302, "/login");
    throw error(500, "Failed to load profile");
  }
};

export const actions: Actions = {
  updateProfile: async ({ request, fetch, cookies }) => {
    const token = cookies.get("jwt") ?? null;
    if (!token) throw redirect(302, "/login");
    
    const formData = await request.formData();
    const profile = await getProfile(token, fetch);

    console.log("Updating profile with ID:", profile.studentId);
    debugger;
    
    try {
      const result = await updateProfile(
        profile.studentId, // This is now correctly passed
        {
          firstName: formData.get("firstName") as string,
          lastName: formData.get("lastName") as string,
          degreeId: profile.degreeId,
          // Add email if needed, but careful not to change it
          // email: profile.email,
        },
        token, // Removed the ! non-null assertion
        fetch,
      );
      return result;
    } catch (err: any) {
      console.error("Update failed:", err);
      return { success: false, error: err?.message ?? "Update failed" };
    }
  },
};
