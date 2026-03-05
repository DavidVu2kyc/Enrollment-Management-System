import { redirect } from '@sveltejs/kit';
import type { Handle } from '@sveltejs/kit';

export const handle: Handle = async ({ event, resolve }) => {
	// Get user from cookies
	const authToken = event.cookies.get('jwt');
	const userCookie = event.cookies.get('user'); // Optional: store user profile separately if frontend needs it. Or decode JWT.

	if (authToken) {
		event.locals.token = authToken;
		try {
			if (userCookie) {
				event.locals.user = JSON.parse(userCookie);
			} else {
				// Very basic JWT decode just for role/username if user cookie isn't present
				const base64Url = authToken.split('.')[1];
				const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
				const jsonPayload = decodeURIComponent(atob(base64).split('').map(function (c) {
					return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
				}).join(''));
				const decoded = JSON.parse(jsonPayload);
				event.locals.user = {
					username: decoded.username || decoded.sub,
					name: decoded.name || decoded.sub,
					role: decoded.role || 'STUDENT'
				};
			}
		} catch (err) {
			console.error('Failed to parse auth data:', err);
		}
	}

	const isLoginUrl = event.url.pathname === '/login' || event.url.pathname === '/register';

	// Protected routes list
	const protectedRoutes = ['/enrollments', '/profile', '/admin'];
	// Dashboard (`/`) doesn't strictly have to be protected, but if it is, add it here.
	const isProtectedRoute = protectedRoutes.some(route => event.url.pathname.startsWith(route));

	if (isProtectedRoute && !event.locals.token) {
		throw redirect(303, '/login');
	}

	if (event.url.pathname.startsWith('/admin') && event.locals.user?.role !== 'ADMIN') {
		// Prevent non-admins from accessing /admin
		throw redirect(303, '/');
	}

	if (isLoginUrl && event.locals.token) {
		// Prevent logged-in users from seeing the login page
		throw redirect(303, '/');
	}

	return await resolve(event);
};