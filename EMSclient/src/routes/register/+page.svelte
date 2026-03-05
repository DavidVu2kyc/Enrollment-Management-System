<script lang="ts">
  import { superForm } from "sveltekit-superforms";
  import { yupClient } from "sveltekit-superforms/adapters";
  import { registerSchema } from "$lib/schemas/register.schema";
  import type { PageData } from "./$types";
  import { fly } from "svelte/transition";

  let { data }: { data: PageData } = $props();

  const { form, errors, constraints, message, enhance, delayed, allErrors } =
    superForm(data.form, {
      validators: yupClient(registerSchema),
      delayMs: 500,
      resetForm: false,
    });

  let passwordVisible = $state(false);
  let focusedField = $state<string | null>(null);
</script>

<svelte:head>
  <title>Identity Hub &mdash; Enrollment Management System</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link
    rel="preconnect"
    href="https://fonts.gstatic.com"
    crossorigin="anonymous"
  />
  <link
    href="https://fonts.googleapis.com/css2?family=DM+Serif+Display:ital@0;1&family=DM+Sans:wght@300;400;500;600;700&display=swap"
    rel="stylesheet"
  />
</svelte:head>

<main class="canvas" in:fly={{ y: 20, duration: 800 }}>
  <div class="void"></div>
  <div class="nebula nebula-1"></div>
  <div class="nebula nebula-2"></div>

  <div class="citadel">
    <div class="monolith">
      <header class="brand-nexus">
        <div class="sigil">
          <svg
            width="32"
            height="32"
            viewBox="0 0 24 24"
            fill="none"
            stroke="white"
            stroke-width="2.5"
          >
            <path
              d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"
            />
          </svg>
        </div>
        <h1>EMS</h1>
        <p class="manifesto">Enrollment Management System</p>
      </header>

      <div class="separator"></div>

      <form method="POST" action="?/register" use:enhance>
        <!-- Identity Field -->
        <div class="sector">
          <label class="sector-label" for="username">Username</label>
          <div class="matrix-input" class:active={focusedField === "username"}>
            <span class="glyph">
              <svg
                width="20"
                height="20"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
              >
                <path d="M16 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2" />
                <circle cx="8.5" cy="7" r="4" />
                <line x1="20" y1="8" x2="20" y2="14" />
                <line x1="23" y1="11" x2="17" y2="11" />
              </svg>
            </span>
            <input
              id="username"
              name="username"
              type="text"
              class="field-core"
              placeholder="student_123"
              bind:value={$form.username}
              onfocus={() => (focusedField = "username")}
              onblur={() => (focusedField = null)}
              {...$constraints.username}
            />
          </div>
          {#if $errors.username}<p class="field-anomaly">
              {$errors.username}
            </p>{/if}
        </div>

        <div class="flex-row">
          <div class="sector" style="flex: 1;">
            <label class="sector-label" for="firstName">First Name</label>
            <div
              class="matrix-input"
              class:active={focusedField === "firstName"}
            >
              <input
                id="firstName"
                name="firstName"
                type="text"
                class="field-core no-glyph"
                placeholder="Juan"
                bind:value={$form.firstName}
                onfocus={() => (focusedField = "firstName")}
                onblur={() => (focusedField = null)}
                {...$constraints.firstName}
              />
            </div>
            {#if $errors.firstName}<p class="field-anomaly">
                {$errors.firstName}
              </p>{/if}
          </div>
          <div class="sector" style="flex: 1;">
            <label class="sector-label" for="lastName">Last Name</label>
            <div
              class="matrix-input"
              class:active={focusedField === "lastName"}
            >
              <input
                id="lastName"
                name="lastName"
                type="text"
                class="field-core no-glyph"
                placeholder="Dela Cruz"
                bind:value={$form.lastName}
                onfocus={() => (focusedField = "lastName")}
                onblur={() => (focusedField = null)}
                {...$constraints.lastName}
              />
            </div>
            {#if $errors.lastName}<p class="field-anomaly">
                {$errors.lastName}
              </p>{/if}
          </div>
        </div>

        <div class="flex-row">
          <div class="sector" style="flex: 1;">
            <label class="sector-label" for="studentNumber"
              >Student Number</label
            >
            <div
              class="matrix-input"
              class:active={focusedField === "studentNumber"}
            >
              <input
                id="studentNumber"
                name="studentNumber"
                type="text"
                class="field-core no-glyph"
                placeholder="2024-XXXX"
                bind:value={$form.studentNumber}
                onfocus={() => (focusedField = "studentNumber")}
                onblur={() => (focusedField = null)}
                {...$constraints.studentNumber}
              />
            </div>
            {#if $errors.studentNumber}<p class="field-anomaly">
                {$errors.studentNumber}
              </p>{/if}
          </div>

          <div class="sector" style="flex: 1;">
            <label class="sector-label" for="degreeId">Degree</label>
            <div
              class="matrix-input"
              class:active={focusedField === "degreeId"}
            >
              <select
                id="degreeId"
                name="degreeId"
                class="field-core no-glyph custom-select"
                bind:value={$form.degreeId}
                onfocus={() => (focusedField = "degreeId")}
                onblur={() => (focusedField = null)}
              >
                <option value="" disabled selected>Select Degree</option>
                {#each data.degrees as degree}
                  <option value={degree.id}>{degree.code}</option>
                {/each}
              </select>
            </div>
            {#if $errors.degreeId}<p class="field-anomaly">
                {$errors.degreeId}
              </p>{/if}
          </div>
        </div>

        <!-- Security Field -->
        <div class="sector">
          <label class="sector-label" for="password">Security Protocol</label>
          <div class="matrix-input" class:active={focusedField === "password"}>
            <span class="glyph">
              <svg
                width="20"
                height="20"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
              >
                <path
                  d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"
                />
              </svg>
            </span>
            <input
              id="password"
              name="password"
              type={passwordVisible ? "text" : "password"}
              class="field-core"
              placeholder="••••••••••••"
              bind:value={$form.password}
              onfocus={() => (focusedField = "password")}
              onblur={() => (focusedField = null)}
              {...$constraints.password}
            />
            <button
              type="button"
              class="visibility-toggle"
              onclick={() => (passwordVisible = !passwordVisible)}
              aria-label="Toggle password visibility"
            >
              {#if passwordVisible}
                <svg
                  width="18"
                  height="18"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  viewBox="0 0 24 24"
                  ><path d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" /><path
                    d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"
                  /></svg
                >
              {:else}
                <svg
                  width="18"
                  height="18"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  viewBox="0 0 24 24"
                  ><path
                    d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.878 9.878L5.12 5.12m9.002 9.002L18.88 18.88M12 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21"
                  /></svg
                >
              {/if}
            </button>
          </div>
          {#if $errors.password}
            <p class="field-anomaly">{$errors.password}</p>
          {/if}
        </div>

        {#if $message}
          <div class="anomaly" in:fly={{ y: -10 }}>
            <svg
              width="18"
              height="18"
              viewBox="0 0 20 20"
              fill="currentColor"
              class="text-red-500"
            >
              <path
                fill-rule="evenodd"
                d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z"
                clip-rule="evenodd"
              />
            </svg>
            <p class="anomaly-msg">{$message}</p>
          </div>
        {/if}

        <button class="cmd-btn" type="submit" disabled={$delayed}>
          {#if $delayed}
            <div class="flex items-center justify-center gap-3">
              <div class="loading-ring"></div>
              <span>Authenticating...</span>
            </div>
          {:else}
            Register Account
          {/if}
        </button>
        <div class="mt-4 text-center">
          <a
            href="/login"
            class="text-sm text-blue-400 hover:text-blue-300 transition-colors"
            >Already have an account? Access Dashboard</a
          >
        </div>
      </form>

      <footer class="footer-sig">Institutional System &middot; 2026</footer>
    </div>
  </div>
</main>

<style>
  :global(body) {
    font-family: "DM Sans", sans-serif;
    background-color: #020617;
    margin: 0;
    overflow-x: hidden;
  }

  .canvas {
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 2rem 1.5rem;
    position: relative;
    overflow: hidden;
  }

  /* ───── Atmospheric Background ───── */
  .void {
    position: absolute;
    inset: 0;
    background: radial-gradient(
        circle at 20% 10%,
        rgba(30, 64, 175, 0.15) 0%,
        transparent 40%
      ),
      radial-gradient(
        circle at 80% 90%,
        rgba(88, 28, 135, 0.12) 0%,
        transparent 40%
      ),
      #020617;
    z-index: 0;
  }

  .void::after {
    content: "";
    position: absolute;
    inset: 0;
    background-image: linear-gradient(
        rgba(255, 255, 255, 0.015) 1px,
        transparent 1px
      ),
      linear-gradient(90deg, rgba(255, 255, 255, 0.015) 1px, transparent 1px);
    background-size: 80px 80px;
    z-index: 1;
  }

  /* ───── Floating Orbs ───── */
  .nebula {
    position: absolute;
    border-radius: 50%;
    filter: blur(100px);
    opacity: 0.6;
    pointer-events: none;
    z-index: 2;
  }
  .nebula-1 {
    width: 500px;
    height: 500px;
    top: -10%;
    left: -10%;
    background: rgba(59, 130, 246, 0.1);
    animation: drift 20s infinite alternate;
  }
  .nebula-2 {
    width: 400px;
    height: 400px;
    bottom: -5%;
    right: -5%;
    background: rgba(139, 92, 246, 0.08);
    animation: drift 15s infinite alternate-reverse;
  }

  @keyframes drift {
    from {
      transform: translate(0, 0);
    }
    to {
      transform: translate(50px, 30px);
    }
  }

  /* ───── Card Architecture ───── */
  .citadel {
    width: 100%;
    max-width: 460px;
    position: relative;
    z-index: 10;
  }

  .monolith {
    background: rgba(15, 23, 42, 0.7);
    backdrop-filter: blur(32px) saturate(180%);
    border: 1px solid rgba(255, 255, 255, 0.08);
    border-radius: 32px;
    padding: 3.5rem;
    box-shadow:
      0 0 0 1px rgba(255, 255, 255, 0.03),
      0 40px 100px -20px rgba(0, 0, 0, 0.8),
      inset 0 1px 1px rgba(255, 255, 255, 0.07);
  }

  /* ───── Branding ───── */
  .brand-nexus {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 2.5rem;
    text-align: center;
  }

  .sigil {
    width: 68px;
    height: 68px;
    background: linear-gradient(135deg, #2563eb, #1e40af);
    border-radius: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 1.5rem;
    box-shadow:
      0 12px 40px rgba(37, 99, 235, 0.4),
      inset 0 1px 0 rgba(255, 255, 255, 0.2);
    transform: rotate(-3deg);
    transition: transform 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  }
  .sigil:hover {
    transform: rotate(0deg) scale(1.05);
  }

  h1 {
    font-family: "DM Serif Display", serif;
    font-size: 2.75rem;
    font-weight: 400;
    color: #f8fafc;
    letter-spacing: -0.02em;
    margin: 0;
    line-height: 1;
  }

  .manifesto {
    font-size: 0.75rem;
    font-weight: 600;
    letter-spacing: 0.25em;
    text-transform: uppercase;
    color: rgba(148, 163, 184, 0.6);
    margin-top: 0.75rem;
  }

  .separator {
    height: 1px;
    background: linear-gradient(
      90deg,
      transparent,
      rgba(255, 255, 255, 0.08) 50%,
      transparent
    );
    margin: 2.5rem 0;
  }

  /* ───── Field Interface ───── */
  .sector {
    margin-bottom: 1.5rem;
  }

  .sector-label {
    display: block;
    font-size: 0.65rem;
    font-weight: 700;
    letter-spacing: 0.15em;
    text-transform: uppercase;
    color: rgba(148, 163, 184, 0.7);
    margin-bottom: 0.75rem;
    padding-left: 0.5rem;
  }

  .flex-row {
    display: flex;
    gap: 1rem;
    margin-bottom: 1.5rem;
  }
  .flex-row .sector {
    margin-bottom: 0;
  }

  .matrix-input {
    position: relative;
    transition: transform 0.2s cubic-bezier(0.2, 0, 0, 1);
  }
  .matrix-input.active {
    transform: scale(1.01);
  }

  .glyph {
    position: absolute;
    left: 1.25rem;
    top: 50%;
    transform: translateY(-50%);
    color: rgba(148, 163, 184, 0.4);
    pointer-events: none;
    transition: color 0.3s;
  }

  .field-core {
    width: 100%;
    background: rgba(255, 255, 255, 0.03);
    border: 1px solid rgba(255, 255, 255, 0.08);
    border-radius: 16px;
    padding: 1rem 1.25rem 1rem 3.25rem;
    color: #f1f5f9;
    font-family: "DM Sans", sans-serif;
    font-size: 0.95rem;
    font-weight: 500;
    outline: none;
    transition: all 0.3s cubic-bezier(0.2, 0, 0, 1);
    box-sizing: border-box;
  }

  .field-core.no-glyph {
    padding-left: 1.25rem;
  }

  .custom-select {
    appearance: none;
    -webkit-appearance: none;
    -moz-appearance: none;
    background-image: url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='rgba(255,255,255,0.4)' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3e%3cpolyline points='6 9 12 15 18 9'%3e%3c/polyline%3e%3c/svg%3e");
    background-repeat: no-repeat;
    background-position: right 1rem center;
    background-size: 1.25em;
  }
  .custom-select option {
    background-color: #020617;
    color: #f1f5f9;
  }

  .field-core:focus {
    background: rgba(30, 58, 138, 0.15);
    border-color: rgba(59, 130, 246, 0.5);
    box-shadow:
      0 0 20px rgba(59, 130, 246, 0.1),
      0 0 0 4px rgba(59, 130, 246, 0.05);
  }

  .visibility-toggle {
    position: absolute;
    right: 1.25rem;
    top: 50%;
    transform: translateY(-50%);
    color: rgba(148, 163, 184, 0.4);
    cursor: pointer;
    background: none;
    border: none;
    padding: 0.25rem;
    transition: color 0.2s;
  }
  .visibility-toggle:hover {
    color: #f1f5f9;
  }

  /* ───── Protocol Errors ───── */
  .anomaly {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    background: rgba(239, 68, 68, 0.1);
    border: 1px solid rgba(239, 68, 68, 0.25);
    border-radius: 14px;
    padding: 0.85rem 1.25rem;
    margin: 1.5rem 0;
  }

  .anomaly-msg {
    font-size: 0.85rem;
    font-weight: 500;
    color: #f87171;
    margin: 0;
  }

  .field-anomaly {
    font-size: 0.7rem;
    color: #f87171;
    font-weight: 600;
    margin-top: 0.5rem;
    padding-left: 0.5rem;
  }

  /* ───── Command Button ───── */
  .cmd-btn {
    width: 100%;
    height: 60px;
    background: linear-gradient(135deg, #2563eb, #1d4ed8);
    color: white;
    border: none;
    border-radius: 16px;
    font-size: 1rem;
    font-weight: 700;
    letter-spacing: 0.02em;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: all 0.3s cubic-bezier(0.2, 0, 0, 1);
    box-shadow: 0 15px 35px -5px rgba(37, 99, 235, 0.5);
    margin-top: 1rem;
  }

  .cmd-btn:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 20px 45px -5px rgba(37, 99, 235, 0.6);
  }

  .cmd-btn:active:not(:disabled) {
    transform: translateY(0);
  }
  .cmd-btn:disabled {
    opacity: 0.5;
    cursor: wait;
  }

  .loading-ring {
    width: 20px;
    height: 20px;
    border: 2.5px solid rgba(255, 255, 255, 0.2);
    border-top-color: white;
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
  }

  @keyframes spin {
    to {
      transform: rotate(360deg);
    }
  }

  /* ───── Sandbox Access ───── */
  .sandbox-header {
    display: flex;
    align-items: center;
    gap: 1rem;
    margin-top: 3rem;
    margin-bottom: 1.25rem;
  }
  .sandbox-line {
    flex: 1;
    height: 1px;
    background: rgba(255, 255, 255, 0.06);
  }
  .sandbox-label {
    font-size: 0.65rem;
    font-weight: 800;
    letter-spacing: 0.2em;
    text-transform: uppercase;
    color: rgba(148, 163, 184, 0.5);
  }

  .sandbox-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 1rem;
  }

  .sandbox-key {
    background: rgba(255, 255, 255, 0.02);
    border: 1px solid rgba(255, 255, 255, 0.05);
    border-radius: 16px;
    padding: 1.25rem;
    text-align: left;
    cursor: pointer;
    transition: all 0.2s;
  }
  .sandbox-key:hover {
    background: rgba(59, 130, 246, 0.05);
    border-color: rgba(59, 130, 246, 0.2);
  }

  .key-role {
    font-size: 0.65rem;
    font-weight: 800;
    text-transform: uppercase;
    letter-spacing: 0.1em;
    margin-bottom: 0.5rem;
  }
  .key-email {
    font-size: 0.75rem;
    color: rgba(148, 163, 184, 0.7);
    font-family: "DM Mono", monospace;
  }

  .footer-sig {
    text-align: center;
    margin-top: 3.5rem;
    font-size: 0.65rem;
    font-weight: 600;
    letter-spacing: 0.25em;
    text-transform: uppercase;
    color: rgba(71, 85, 105, 0.6);
  }
</style>
