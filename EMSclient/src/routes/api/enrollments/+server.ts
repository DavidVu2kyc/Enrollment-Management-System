import { json } from '@sveltejs/kit';
import type { RequestHandler } from './$types';

const API_BASE = process.env.VITE_API_URL || 'http://localhost:8080/api';

export const GET: RequestHandler = async ({ cookies }) => {
	try {
		const token = cookies.get('authToken');

		if (!token) {
			return json(
				{ success: false, message: 'Unauthorized' },
				{ status: 401 }
			);
		}

		const response = await fetch(`${API_BASE}/enrollments`, {
			method: 'GET',
			headers: {
				'Authorization': `Bearer ${token}`,
				'Content-Type': 'application/json'
			}
		});

		if (!response.ok) {
			return json(
				{ success: false, message: 'Failed to fetch enrollments' },
				{ status: response.status }
			);
		}

		const enrollments = await response.json();
		return json({ success: true, data: enrollments });
	} catch (error) {
		console.error('Enrollments GET error:', error);
		return json(
			{ success: false, message: 'Internal server error' },
			{ status: 500 }
		);
	}
};

export const POST: RequestHandler = async ({ request, cookies }) => {
	try {
		const token = cookies.get('authToken');

		if (!token) {
			return json(
				{ success: false, message: 'Unauthorized' },
				{ status: 401 }
			);
		}

		const body = await request.json();

		const response = await fetch(`${API_BASE}/enrollments`, {
			method: 'POST',
			headers: {
				'Authorization': `Bearer ${token}`,
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(body)
		});

		if (!response.ok) {
			const error = await response.json();
			return json(error, { status: response.status });
		}

		const result = await response.json();
		return json({ success: true, data: result }, { status: 201 });
	} catch (error) {
		console.error('Enrollments POST error:', error);
		return json(
			{ success: false, message: 'Internal server error' },
			{ status: 500 }
		);
	}
};
