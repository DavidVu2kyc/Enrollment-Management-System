<script lang="ts">
  import { superForm } from "sveltekit-superforms";
  import { defaults } from "sveltekit-superforms";
  import type { PageData } from "./$types";
  import { goto } from "$app/navigation";
  import { fly } from "svelte/transition";
  import { yup } from "sveltekit-superforms/adapters";
  import { loginSchema } from "$lib/schemas/login.schema";
  import { registerSchema } from "$lib/schemas/register.schema";
  import { createServerApiClient } from "$lib/api/client";

  let { data }: { data: PageData } = $props();

  const {
    form: loginForm,
    errors: loginErrors,
    message: loginMessage,
    enhance: loginEnhance,
    delayed: loginDelayed,
  } = superForm(data.form, {
    onUpdated({ form }) {
      if (form?.message === "Login successful! Redirecting...") {
        setTimeout(() => goto("/enrollments"), 1000);
      }
    },
  });

  // Track focused field for UI styling
  let focusedField = $state<string | null>(null);
</script>

<svelte:head>
  <title>Login | EMS</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link
    rel="preconnect"
    href="https://fonts.gstatic.com"
    crossorigin="anonymous"
  />
  <link
    href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&display=swap"
    rel="stylesheet"
  />
</svelte:head>

<main class="container" in:fly={{ y: 20, duration: 600 }}>
  <div class="card">
    <header class="header">
      <h1>EMS</h1>
      <p>Enrollment Management System</p>
    </header>

      <form method="POST" action="?/login" use:loginEnhance>
        <!-- Username -->
        <div class="field">
          <label for="username">Username</label>
          <div class="input-group" class:active={focusedField === "username"}>
            <svg
              width="18"
              height="18"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              ><path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2" /><circle
                cx="12"
                cy="7"
                r="4"
              /></svg
            >
            <input
              id="username"
              name="username"
              type="text"
              placeholder="Enter username"
              bind:value={$loginForm.username}
              onfocus={() => (focusedField = "username")}
              onblur={() => (focusedField = null)}
              required
            />
          </div>
          {#if $loginErrors?.username}<span class="error"
              >{$loginErrors.username}</span
            >{/if}
        </div>

        <!-- Password -->
        <div class="field">
          <label for="password">Password</label>
          <div class="input-group" class:active={focusedField === "password"}>
            <svg
              width="18"
              height="18"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              ><rect x="3" y="11" width="18" height="11" rx="2" /><path
                d="M7 11V7a5 5 0 0110 0v4"
              /></svg
            >
            <input
              id="password"
              name="password"
              type="password"
              placeholder="••••••••"
              bind:value={$loginForm.password}
              onfocus={() => (focusedField = "password")}
              onblur={() => (focusedField = null)}
              required
            />
          </div>
          {#if $loginErrors?.password}<span class="error"
              >{$loginErrors.password}</span
            >{/if}
        </div>
        
        {#if $loginMessage}<p class="message">{$loginMessage}</p>{/if}
        <div class="actions">
          <button type="submit" disabled={$loginDelayed}>
            {#if $loginDelayed}<span class="loader"></span>{:else}Login{/if}
          </button>
          <a
            href="/register"
            onclick={(e) => {
              e.preventDefault();
              goto("/register");
            }}
          >
            Register
          </a>
        </div>
      </form>
  </div>
</main>

<style>
  :global(body) {
    margin: 0;
    font-family: "DM Sans", sans-serif;
    background: #020617;
    color: white;
  }
  .container {
    height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .card {
    width: 360px;
    padding: 2.5rem;
    border-radius: 20px;
    background: rgba(15, 23, 42, 0.7);
    backdrop-filter: blur(20px);
    border: 1px solid rgba(255, 255, 255, 0.08);
  }
  .header {
    text-align: center;
    margin-bottom: 2rem;
  }
  .field {
    margin-bottom: 1.4rem;
  }
  label {
    font-size: 0.7rem;
    text-transform: uppercase;
    letter-spacing: 0.08em;
    opacity: 0.7;
    margin-bottom: 0.4rem;
    display: block;
  }
  .input-group {
    display: flex;
    align-items: center;
    gap: 0.6rem;
    border: 1px solid rgba(255, 255, 255, 0.08);
    border-radius: 12px;
    padding: 0.8rem 0.9rem;
    background: rgba(255, 255, 255, 0.03);
    transition: all 0.2s;
  }
  .input-group.active {
    border-color: #3b82f6;
    background: rgba(30, 58, 138, 0.15);
  }
  input {
    flex: 1;
    background: transparent;
    border: none;
    outline: none;
    color: white;
    font-size: 0.9rem;
  }
  .error {
    color: #f87171;
    font-size: 0.75rem;
  }
  .message {
    text-align: center;
    margin-bottom: 1rem;
  }
  .actions {
    display: flex;
    gap: 0.8rem;
    margin-top: 1rem;
  }
  button {
    flex: 2;
    height: 44px;
    border: none;
    border-radius: 10px;
    background: #2563eb;
    color: white;
    font-weight: 600;
    cursor: pointer;
  }
  button:disabled {
    opacity: 0.7;
    cursor: wait;
  }
  a {
    flex: 1;
    height: 44px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 10px;
    text-decoration: none;
    color: white;
    border: 1px solid rgba(255, 255, 255, 0.15);
  }
  .loader {
    width: 18px;
    height: 18px;
    border: 2px solid rgba(255, 255, 255, 0.3);
    border-top: 2px solid white;
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
    display: inline-block;
  }
  @keyframes spin {
    to {
      transform: rotate(360deg);
    }
  }
</style>
