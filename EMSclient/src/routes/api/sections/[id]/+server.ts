import { json, error } from "@sveltejs/kit";
import { getSectionById } from "$lib/server/section";
import type { RequestHandler } from "./$types";

export const GET: RequestHandler = async ({ params, cookies, fetch }) => {
  const token = cookies.get("jwt") ?? null;
  const sectionId = parseInt(params.id);

  if (isNaN(sectionId)) {
    throw error(400, "Invalid section ID");
  }

  try {
    const section = await getSectionById(sectionId, token, fetch);
    return json(section);
  } catch (err: any) {
    throw error(err.status || 500, err.message || "Failed to fetch section details");
  }
};
