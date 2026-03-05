import type { ApiError } from '$lib/types/apierror';

const API_BASE_URL = 'http://localhost:8081/api';

export class ApiClient {
  private accessToken: string | null = null;
  private fetchFn: typeof fetch;

  constructor(fetchFn?: typeof fetch) {
    // Determine whether to use the provided fetch (from SvelteKit) or the global fetch
    this.fetchFn = fetchFn || (typeof window !== 'undefined' ? window.fetch.bind(window) : fetch);
  }

  setAccessToken(token: string | null) {
    this.accessToken = token;
  }

  private async request<T>(
    endpoint: string,
    options: RequestInit = {}
  ): Promise<T> {
    const url = `${API_BASE_URL}${endpoint}`;

    const headers: HeadersInit = {
      'Content-Type': 'application/json',
      ...options.headers
    };

    if (this.accessToken) {
      (headers as Record<string, string>)['Authorization'] = `Bearer ${this.accessToken}`;
    }

    const response = await this.fetchFn(url, {
      ...options,
      headers
    });

    if (!response.ok) {
      const error: ApiError = await response.json().catch(() => ({
        status: response.status,
        error: response.statusText,
        message: 'An error occurred',
        timestamp: new Date().toISOString()
      }));
      throw error;
    }

    if (response.status === 204) {
      return {} as T;
    }

    return response.json();
  }

  get<T>(endpoint: string): Promise<T> {
    return this.request<T>(endpoint, { method: 'GET' });
  }

  post<T>(endpoint: string, data?: unknown): Promise<T> {
    return this.request<T>(endpoint, {
      method: 'POST',
      body: data ? JSON.stringify(data) : undefined
    });
  }

  put<T>(endpoint: string, data?: unknown): Promise<T> {
    return this.request<T>(endpoint, {
      method: 'PUT',
      body: data ? JSON.stringify(data) : undefined
    });
  }

  delete<T>(endpoint: string): Promise<T> {
    return this.request<T>(endpoint, { method: 'DELETE' });
  }
}

export const apiClient = new ApiClient();

// Server-side API client factory
export function createServerApiClient(accessToken: string | null, customFetch?: typeof fetch): ApiClient {
  const client = new ApiClient(customFetch);
  client.setAccessToken(accessToken);
  return client;
}
