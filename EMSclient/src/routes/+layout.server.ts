import type { LayoutServerLoad } from "./$types";
import type { User } from "$lib/types";
import { redirect } from "@sveltejs/kit";

// SSR Auth Check

export const load: LayoutServerLoad = async ({ locals }) => {
  // locals.user is set in hooks.server.ts based on JWT cookie
  // Return the user object to the layout for NavBar visibility and other UI needs
  return { user: locals.user };
};
