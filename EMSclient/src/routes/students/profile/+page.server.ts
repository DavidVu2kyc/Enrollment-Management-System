import { error, redirect } from "@sveltejs/kit";
import type { PageServerLoad, Actions } from './$types';
import { getProfile, updateProfile } from '$lib/server/students';

export const load: PageServerLoad = async ({ fetch, cookies }) => {
  const token = cookies.get("jwt") ?? null;

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
    const formData = await request.formData();
    const name = formData.get("name") as string;
    console.log("Updating profile with name:", name);
    try {
      const result = await updateProfile(name, token, fetch);
      return result;
    } catch (err: any) {
      return { success: false, error: err?.message ?? "Update failed" };
    }
  }
};