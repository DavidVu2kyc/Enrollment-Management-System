import { createServerApiClient } from "$lib/api/client";

/** Matches backend TermResponse DTO */
export interface TermResponse {
  termId: number;
  name: string;
  year: number;
  semester: string;
  isActive: boolean;
}

/**
 * Fetch all academic terms.
 * Maps to: GET /api/terms
 */
export async function getAllTerms(
  token: string | null,
  fetch: typeof globalThis.fetch,
): Promise<TermResponse[]> {
  const client = createServerApiClient(token, fetch);
  return client.get<TermResponse[]>("/terms");
}

/**
 * Fetch the currently active term.
 * Maps to: GET /api/terms/active
 */
export async function getActiveTerm(
  token: string | null,
  fetch: typeof globalThis.fetch,
): Promise<TermResponse> {
  const client = createServerApiClient(token, fetch);
  return client.get<TermResponse>("/terms/active");
}
