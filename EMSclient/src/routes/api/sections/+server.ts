import { json } from '@sveltejs/kit';
import type { RequestHandler } from './$types';

const API_BASE = process.env.VITE_API_URL || 'http://localhost:8080/api';

export const GET: RequestHandler = async ({ cookies, url }) => {
	try {
		const token = cookies.get('authToken');

		if (!token) {
			return json(
				{ success: false, message: 'Unauthorized' },
				{ status: 401 }
			);
		}

		// Pass through any query parameters
		const queryString = url.searchParams.toString();
		const endpoint = queryString ? `${API_BASE}/sections?${queryString}` : `${API_BASE}/sections`;

		const response = await fetch(endpoint, {
			method: 'GET',
			headers: {
				'Authorization': `Bearer ${token}`,
				'Content-Type': 'application/json'
			}
		});

		if (!response.ok) {
			return json(
				{ success: false, message: 'Failed to fetch sections' },
				{ status: response.status }
			);
		}

		const sections = await response.json();
		return json({ success: true, data: sections });
	} catch (error) {
		console.error('Sections GET error:', error);
		return json(
			{ success: false, message: 'Internal server error' },
			{ status: 500 }
		);
	}
};
