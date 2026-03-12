import { fail, redirect } from "@sveltejs/kit";
import { superValidate } from "sveltekit-superforms";
import { yup } from "sveltekit-superforms/adapters";
import { loginSchema } from "$lib/schemas/login.schema";
import type { PageServerLoad, Actions } from "./$types";
import { createServerApiClient } from "$lib/api/client";
import { env } from "$env/dynamic/private";

export const load: PageServerLoad = async () => {
  const form = await superValidate(yup(loginSchema));
  return { form };
};

export const actions: Actions = {
  login: async ({ request, cookies, fetch }) => {
    const form = await superValidate(request, yup(loginSchema));

    if (!form.valid) {
      return fail(400, { form });
    }

    const { username, password } = form.data;

    try {
      const client = createServerApiClient(null, fetch);

      const response = await client.post<{
        token: string;
        tokenType: string;
        username: string;
        role: string;
        studentId: number | null;
      }>("/auth/login", {
        username,
        password,
      });

      cookies.set("jwt", response.token, {
        path: "/",
        httpOnly: true,
        secure: env.NODE_ENV === "production",
        sameSite: "lax",
        maxAge: 60 * 60 * 24 * 7,
      });

      cookies.set(
        "user",
        JSON.stringify({
          username: response.username,
          role: response.role,
          studentId: response.studentId,
        }),
        {
          path: "/",
          secure: env.NODE_ENV === "production",
          sameSite: "lax",
          maxAge: 60 * 60 * 24 * 7,
        },
      );

      throw redirect(303, "/dashboard");
    } catch (error: unknown) {
      console.error("Login error:", error);

      return fail(401, {
        form,
        message: "Invalid username or password",
      });
    }
  },
};
