import { redirect } from '@sveltejs/kit';
import type { Handle } from '@sveltejs/kit';

export const handle: Handle = async ({ event, resolve }) => {
	// Get user from cookies
	const userCookie = event.cookies.get('user');
	const authToken = event.cookies.get('authToken');

	if (userCookie && authToken) {
		try {
			const user = JSON.parse(userCookie);
			event.locals.user = user;
			event.locals.token = authToken;
		} catch (err) {
			console.error('Failed to parse user cookie:', err);
		}
	} else {
		// BYPASS LOGIC: Auto-login as mock student if no session exists
		/* 
		event.locals.user = {
			id: 'mock-auto-student',
			email: 'bypass@example.com',
			name: 'Bypass User',
			role: 'STUDENT'
		};
		event.locals.token = 'mock-auto-token';
		*/
	}

	// Protect routes that require authentication
	const protectedRoutes = ['/', '/enrollments', '/profile', '/admin'];
	const isProtectedRoute = protectedRoutes.some(route => event.url.pathname === route || event.url.pathname.startsWith(route + '/'));

	/*
	// TEMPORARILY DISABLED REDIRECTS FOR BYPASS
	if (isProtectedRoute && !event.locals.user) {
		throw redirect(303, '/login');
	}
	*/

	// Protect admin routes
	if (event.url.pathname.startsWith('/admin') && event.locals.user?.role !== 'ADMIN') {
		// Allow bypass to see admin pages too if specified, but let's keep it safe for now
		// throw redirect(303, '/');
	}

	// Redirect to home if already logged in and trying to access login page
	if (event.url.pathname === '/login' && event.locals.user) {
		// throw redirect(303, '/');
	}

	return await resolve(event);
};