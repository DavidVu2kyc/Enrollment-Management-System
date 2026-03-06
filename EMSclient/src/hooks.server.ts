import { redirect } from "@sveltejs/kit";
import type { Handle } from "@sveltejs/kit";

export const handle: Handle = async ({ event, resolve }) => {
  // Get user from cookies
  const authToken = event.cookies.get("jwt");
  const userCookie = event.cookies.get("user"); // Optional: store user profile separately if frontend needs it. Or decode JWT.

  if (authToken) {
    event.locals.token = authToken;
    try {
      if (userCookie) {
        event.locals.user = JSON.parse(userCookie);
      } else {
        // Very basic JWT decode just for role/username if user cookie isn't present
        const base64Url = authToken.split(".")[1];
        const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
        const jsonPayload = decodeURIComponent(
          atob(base64)
            .split("")
            .map(function (c) {
              return "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2);
            })
            .join(""),
        );
        const decoded = JSON.parse(jsonPayload);
        event.locals.user = {
          username: decoded.username || decoded.sub,
          name: decoded.name || decoded.sub,
          role: decoded.role || "STUDENT",
        };
      }
    } catch (err) {
      console.error("Failed to parse auth data:", err);
    }
  }

  const isLoginUrl =
    event.url.pathname === "/login" || event.url.pathname === "/register";

  // Protected routes list
  const protectedRoutes = ["/enrollments", "/students/profile", "/admin"];
  const isRoot = event.url.pathname === "/";
  const isProtectedRoute =
    isRoot ||
    protectedRoutes.some((route) => event.url.pathname.startsWith(route));

  console.log(
    `[Hooks] ${event.url.pathname} - Token: ${!!event.locals.token} - Protected: ${isProtectedRoute}`,
  );

  if (isProtectedRoute && !event.locals.token) {
    console.log(`[Hooks] Redirecting to /login (302)`);
    throw redirect(302, "/login");
  }

  if (
    event.url.pathname.startsWith("/admin") &&
    event.locals.user?.role !== "ADMIN"
  ) {
    // Prevent non-admins from accessing /admin
    console.log(`[Hooks] Non-admin accessing /admin, redirecting to /`);
    throw redirect(302, "/");
  }

  if (isLoginUrl && event.locals.token) {
    // Prevent logged-in users from seeing the login page
    console.log(`[Hooks] Logged in user at login/register, redirecting to /`);
    throw redirect(302, "/");
  }

  return await resolve(event);
};
