import { createServerApiClient } from "$lib/api/client";
import type { StudentResponse } from "$lib/types/student";

export interface AuthResponse {
  token: string;
  tokenType: string;
  username: string;
  role: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  firstName: string;
  lastName: string;
  studentNumber: string;
  degreeId: number;
}

/**
 * Authenticate a user and return a JWT token + user info.
 * Maps to: POST /api/auth/login
 */
export async function login(
  credentials: LoginRequest,
  fetch: typeof globalThis.fetch,
): Promise<AuthResponse> {
  const client = createServerApiClient(null, fetch);
  return client.post<AuthResponse>("/auth/login", credentials);
}

/**
 * Register a new student account.
 * Maps to: POST /api/auth/register
 */
export async function register(
  data: RegisterRequest,
  fetch: typeof globalThis.fetch,
): Promise<StudentResponse> {
  const client = createServerApiClient(null, fetch);
  return client.post<StudentResponse>("/auth/register", data);
}



