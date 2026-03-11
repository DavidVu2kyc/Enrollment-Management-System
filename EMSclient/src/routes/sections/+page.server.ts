import { error, redirect } from "@sveltejs/kit";
import type { PageServerLoad } from "./$types";
import { getAllSections } from "$lib/server/section";

export const load: PageServerLoad = async ({ fetch, cookies }) => {
  const token = cookies.get("jwt") ?? null;
  if (!token) throw redirect(302, "/login");

  try {
    const sections = await getAllSections({}, token, fetch);
    return { sections };
  } catch (err: any) {
    if (err?.status === 401) throw redirect(302, "/login");
    throw error(500, "Failed to load sections");
  }
};
