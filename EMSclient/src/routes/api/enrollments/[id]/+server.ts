import { json } from '@sveltejs/kit';
import type { RequestHandler } from './$types';

const API_BASE = process.env.VITE_API_URL || 'http://localhost:8080/api';

export const GET: RequestHandler = async ({ params, cookies }) => {
	try {
		const token = cookies.get('authToken');

		if (!token) {
			return json(
				{ success: false, message: 'Unauthorized' },
				{ status: 401 }
			);
		}

		const response = await fetch(`${API_BASE}/enrollments/${params.id}`, {
			method: 'GET',
			headers: {
				'Authorization': `Bearer ${token}`,
				'Content-Type': 'application/json'
			}
		});

		if (!response.ok) {
			return json(
				{ success: false, message: 'Enrollment not found' },
				{ status: response.status }
			);
		}

		const enrollment = await response.json();
		return json({ success: true, data: enrollment });
	} catch (error) {
		console.error('Enrollment GET error:', error);
		return json(
			{ success: false, message: 'Internal server error' },
			{ status: 500 }
		);
	}
};

export const PUT: RequestHandler = async ({ params, request, cookies }) => {
	try {
		const token = cookies.get('authToken');

		if (!token) {
			return json(
				{ success: false, message: 'Unauthorized' },
				{ status: 401 }
			);
		}

		const body = await request.json();

		const response = await fetch(`${API_BASE}/enrollments/${params.id}`, {
			method: 'PUT',
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
		return json({ success: true, data: result });
	} catch (error) {
		console.error('Enrollment PUT error:', error);
		return json(
			{ success: false, message: 'Internal server error' },
			{ status: 500 }
		);
	}
};

export const DELETE: RequestHandler = async ({ params, cookies }) => {
	try {
		const token = cookies.get('authToken');

		if (!token) {
			return json(
				{ success: false, message: 'Unauthorized' },
				{ status: 401 }
			);
		}

		const response = await fetch(`${API_BASE}/enrollments/${params.id}`, {
			method: 'DELETE',
			headers: {
				'Authorization': `Bearer ${token}`,
				'Content-Type': 'application/json'
			}
		});

		if (!response.ok) {
			return json(
				{ success: false, message: 'Failed to delete enrollment' },
				{ status: response.status }
			);
		}

		return json({ success: true, message: 'Enrollment deleted successfully' });
	} catch (error) {
		console.error('Enrollment DELETE error:', error);
		return json(
			{ success: false, message: 'Internal server error' },
			{ status: 500 }
		);
	}
};
