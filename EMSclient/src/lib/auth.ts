export async function logout(fetch: typeof globalThis.fetch) {
  return fetch("http://localhost:8081/api/auth/logout", {
    method: "POST",
    credentials: "include"
  });
}