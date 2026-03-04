<script lang="ts">
  import { enhance } from "$app/forms";
  import Button from "$lib/components/Button.svelte";
  import Input from "$lib/components/Input.svelte";
  import type { ActionData } from "./$types";

  let email = $state("");
  let password = $state("");
  let isLoading = $state(false);
  let serverError = $state("");
  let focused = $state<string | null>(null);

  let { form }: { form: ActionData } = $props();

  $effect(() => {
    if (form?.error) {
      serverError = form.error;
      isLoading = false;
    }
  });

  const handleSubmit = () => {
    serverError = "";
    isLoading = true;
  };
</script>

<svelte:head>
  <title>Login — Enrollment Management System</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin="anonymous" />
  <link
    href="https://fonts.googleapis.com/css2?family=DM+Serif+Display:ital@0;1&family=DM+Sans:wght@300;400;500;600&display=swap"
    rel="stylesheet"
  />
</svelte:head>

<style>
  :global(body) {
    font-family: 'DM Sans', sans-serif;
  }

  .page {
    min-height: 100vh;
    background-color: #05101f;
    background-image:
      radial-gradient(ellipse 80% 50% at 20% -10%, rgba(14, 60, 120, 0.5) 0%, transparent 60%),
      radial-gradient(ellipse 60% 40% at 80% 110%, rgba(8, 40, 90, 0.4) 0%, transparent 60%);
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 2rem 1rem;
    overflow: hidden;
    position: relative;
  }

  /* Grid lines */
  .grid-overlay {
    position: absolute;
    inset: 0;
    background-image:
      linear-gradient(rgba(255,255,255,0.025) 1px, transparent 1px),
      linear-gradient(90deg, rgba(255,255,255,0.025) 1px, transparent 1px);
    background-size: 60px 60px;
    pointer-events: none;
  }

  /* Glow orbs */
  .orb {
    position: absolute;
    border-radius: 50%;
    filter: blur(80px);
    pointer-events: none;
  }
  .orb-1 {
    width: 400px; height: 400px;
    top: -100px; left: -100px;
    background: radial-gradient(circle, rgba(30, 90, 200, 0.18) 0%, transparent 70%);
  }
  .orb-2 {
    width: 350px; height: 350px;
    bottom: -80px; right: -80px;
    background: radial-gradient(circle, rgba(10, 50, 150, 0.15) 0%, transparent 70%);
  }
  .orb-3 {
    width: 200px; height: 200px;
    top: 40%; left: 60%;
    background: radial-gradient(circle, rgba(60, 130, 255, 0.08) 0%, transparent 70%);
  }

  .card {
    width: 100%;
    max-width: 440px;
    position: relative;
    z-index: 10;
    animation: cardIn 0.6s cubic-bezier(0.22, 1, 0.36, 1) both;
  }

  @keyframes cardIn {
    from { opacity: 0; transform: translateY(24px) scale(0.98); }
    to   { opacity: 1; transform: translateY(0) scale(1); }
  }

  .card-inner {
    background: linear-gradient(145deg, rgba(12, 28, 55, 0.95) 0%, rgba(7, 18, 38, 0.98) 100%);
    border: 1px solid rgba(255, 255, 255, 0.08);
    border-radius: 24px;
    padding: 3rem;
    box-shadow:
      0 0 0 1px rgba(255,255,255,0.03),
      0 32px 80px rgba(0, 0, 0, 0.6),
      0 0 60px rgba(20, 70, 180, 0.12),
      inset 0 1px 0 rgba(255, 255, 255, 0.06);
    backdrop-filter: blur(20px);
  }

  /* Header */
  .logo-wrap {
    display: flex;
    justify-content: center;
    margin-bottom: 1.75rem;
    animation: logoIn 0.7s 0.1s cubic-bezier(0.22, 1, 0.36, 1) both;
  }

  @keyframes logoIn {
    from { opacity: 0; transform: scale(0.7) rotate(-8deg); }
    to   { opacity: 1; transform: scale(1) rotate(0deg); }
  }

  .logo-icon {
    width: 60px; height: 60px;
    background: linear-gradient(135deg, #1e4db7, #0d2d7a);
    border-radius: 18px;
    display: flex; align-items: center; justify-content: center;
    box-shadow: 0 8px 32px rgba(30, 77, 183, 0.4), inset 0 1px 0 rgba(255,255,255,0.15);
    position: relative;
    overflow: hidden;
  }

  .logo-icon::after {
    content: '';
    position: absolute;
    top: -50%; left: -50%;
    width: 200%; height: 200%;
    background: linear-gradient(45deg, transparent 40%, rgba(255,255,255,0.1) 50%, transparent 60%);
    animation: sheen 3s 1s ease-in-out infinite;
  }

  @keyframes sheen {
    0%, 80%, 100% { transform: translateX(-100%) rotate(0deg); }
    40% { transform: translateX(100%) rotate(0deg); }
  }

  .header-text {
    text-align: center;
    animation: fadeUp 0.5s 0.2s both;
  }

  @keyframes fadeUp {
    from { opacity: 0; transform: translateY(10px); }
    to   { opacity: 1; transform: translateY(0); }
  }

  h1 {
    font-family: 'DM Serif Display', serif;
    font-size: 2.5rem;
    font-weight: 400;
    color: #ffffff;
    letter-spacing: -0.02em;
    line-height: 1;
    margin: 0 0 0.4rem;
  }

  .subtitle {
    font-size: 0.8rem;
    font-weight: 400;
    color: rgba(160, 185, 220, 0.7);
    letter-spacing: 0.18em;
    text-transform: uppercase;
  }

  /* Divider */
  .divider {
    height: 1px;
    background: linear-gradient(90deg, transparent, rgba(255,255,255,0.07) 40%, rgba(255,255,255,0.07) 60%, transparent);
    margin: 2rem 0;
  }

  /* Form */
  .form-group {
    margin-bottom: 1.25rem;
    animation: fadeUp 0.5s both;
  }
  .form-group:nth-child(1) { animation-delay: 0.3s; }
  .form-group:nth-child(2) { animation-delay: 0.4s; }

  label {
    display: block;
    font-size: 0.72rem;
    font-weight: 600;
    letter-spacing: 0.12em;
    text-transform: uppercase;
    color: rgba(140, 170, 210, 0.8);
    margin-bottom: 0.5rem;
  }

  .input-wrap {
    position: relative;
  }

  .input-icon {
    position: absolute;
    left: 1rem;
    top: 50%; transform: translateY(-50%);
    color: rgba(100, 140, 200, 0.5);
    pointer-events: none;
    transition: color 0.2s;
    width: 16px; height: 16px;
  }

  .form-input {
    width: 100%;
    background: rgba(255, 255, 255, 0.04);
    border: 1px solid rgba(255, 255, 255, 0.09);
    border-radius: 12px;
    padding: 0.85rem 1rem 0.85rem 2.75rem;
    color: #e8f0ff;
    font-family: 'DM Sans', sans-serif;
    font-size: 0.9rem;
    outline: none;
    transition: all 0.2s ease;
    box-sizing: border-box;
  }

  .form-input::placeholder { color: rgba(120, 150, 200, 0.35); }

  .form-input:hover {
    border-color: rgba(255, 255, 255, 0.15);
    background: rgba(255, 255, 255, 0.05);
  }

  .form-input:focus {
    border-color: rgba(80, 140, 255, 0.5);
    background: rgba(30, 70, 160, 0.12);
    box-shadow: 0 0 0 3px rgba(60, 120, 255, 0.1), 0 0 20px rgba(60, 120, 255, 0.08);
  }

  .form-input:focus + .focus-ring {
    opacity: 1;
  }

  /* Error state */
  .error-box {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    background: rgba(220, 38, 38, 0.1);
    border: 1px solid rgba(220, 38, 38, 0.25);
    border-radius: 12px;
    padding: 0.85rem 1rem;
    margin-bottom: 1.25rem;
    animation: shake 0.4s ease;
  }

  @keyframes shake {
    0%, 100% { transform: translateX(0); }
    20% { transform: translateX(-6px); }
    40% { transform: translateX(6px); }
    60% { transform: translateX(-4px); }
    80% { transform: translateX(4px); }
  }

  .error-box p {
    font-size: 0.82rem;
    font-weight: 500;
    color: #fca5a5;
    margin: 0;
  }

  /* Submit button */
  .btn-submit {
    width: 100%;
    background: linear-gradient(135deg, #1e4db7 0%, #0f3298 100%);
    color: white;
    border: none;
    border-radius: 12px;
    padding: 0.95rem;
    font-family: 'DM Sans', sans-serif;
    font-size: 0.88rem;
    font-weight: 600;
    letter-spacing: 0.05em;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: all 0.25s ease;
    box-shadow: 0 4px 20px rgba(30, 77, 183, 0.35);
    margin-top: 0.5rem;
    animation: fadeUp 0.5s 0.5s both;
  }

  .btn-submit::before {
    content: '';
    position: absolute;
    inset: 0;
    background: linear-gradient(135deg, rgba(255,255,255,0.1) 0%, transparent 100%);
    opacity: 0;
    transition: opacity 0.2s;
  }

  .btn-submit:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 8px 32px rgba(30, 77, 183, 0.5);
  }
  .btn-submit:hover:not(:disabled)::before { opacity: 1; }
  .btn-submit:active:not(:disabled) { transform: translateY(0); }
  .btn-submit:disabled { opacity: 0.6; cursor: not-allowed; }

  .btn-inner {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
  }

  .spinner {
    width: 16px; height: 16px;
    border: 2px solid rgba(255,255,255,0.3);
    border-top-color: white;
    border-radius: 50%;
    animation: spin 0.7s linear infinite;
  }

  @keyframes spin { to { transform: rotate(360deg); } }

  /* Sandbox section */
  .sandbox-label {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    margin: 2rem 0 1rem;
    animation: fadeUp 0.5s 0.55s both;
  }

  .sandbox-line {
    flex: 1;
    height: 1px;
    background: rgba(255,255,255,0.07);
  }

  .sandbox-tag {
    font-size: 0.65rem;
    font-weight: 700;
    letter-spacing: 0.2em;
    text-transform: uppercase;
    color: rgba(100, 140, 200, 0.5);
    white-space: nowrap;
  }

  .demo-accounts {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 0.75rem;
    animation: fadeUp 0.5s 0.6s both;
  }

  .demo-card {
    background: rgba(255, 255, 255, 0.03);
    border: 1px solid rgba(255, 255, 255, 0.06);
    border-radius: 12px;
    padding: 0.85rem;
    cursor: pointer;
    transition: all 0.2s ease;
  }

  .demo-card:hover {
    background: rgba(30, 77, 183, 0.1);
    border-color: rgba(60, 120, 255, 0.2);
  }

  .demo-card-role {
    font-size: 0.65rem;
    font-weight: 700;
    letter-spacing: 0.12em;
    text-transform: uppercase;
    margin-bottom: 0.35rem;
  }

  .role-student { color: #60a5fa; }
  .role-admin   { color: #a78bfa; }

  .demo-creds {
    font-size: 0.72rem;
    color: rgba(160, 185, 220, 0.55);
    line-height: 1.5;
    font-family: 'DM Mono', monospace;
  }

  /* Footer */
  .footer {
    text-align: center;
    margin-top: 2rem;
    font-size: 0.65rem;
    font-weight: 600;
    letter-spacing: 0.2em;
    text-transform: uppercase;
    color: rgba(80, 110, 160, 0.5);
    animation: fadeUp 0.5s 0.65s both;
  }
</style>

<div class="page">
  <div class="grid-overlay"></div>
  <div class="orb orb-1"></div>
  <div class="orb orb-2"></div>
  <div class="orb orb-3"></div>

  <div class="card">
    <div class="card-inner">

      <!-- Logo -->
      <div class="logo-wrap">
        <div class="logo-icon">
          <svg width="28" height="28" fill="none" stroke="white" stroke-width="1.8" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
          </svg>
        </div>
      </div>

      <!-- Header -->
      <div class="header-text">
        <h1>EMS</h1>
        <p class="subtitle">Enrro</p>
      </div>

      <div class="divider"></div>

      <!-- Form -->
      <form method="POST" action="?/login" use:enhance={handleSubmit}>

        <div class="form-group">
          <label for="email">University Email</label>
          <div class="input-wrap">
            <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
              <path stroke-linecap="round" stroke-linejoin="round" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"/>
            </svg>
            <input
              class="form-input"
              type="email"
              id="email"
              name="email"
              placeholder="student@university.edu"
              bind:value={email}
              required
              autocomplete="email"
            />
          </div>
        </div>

        <div class="form-group">
          <label for="password">Password</label>
          <div class="input-wrap">
            <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"/>
            </svg>
            <input
              class="form-input"
              type="password"
              id="password"
              name="password"
              placeholder="••••••••••"
              bind:value={password}
              required
              autocomplete="current-password"
            />
          </div>
        </div>

        {#if form?.error && serverError}
          <div class="error-box">
            <svg width="16" height="16" viewBox="0 0 20 20" fill="#fca5a5">
              <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clip-rule="evenodd" />
            </svg>
            <p>{serverError}</p>
          </div>
        {/if}

        <button class="btn-submit" type="submit" disabled={isLoading}>
          <span class="btn-inner">
            {#if isLoading}
              <span class="spinner"></span>
              Signing in…
            {:else}
              Sign into Dashboard
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M13 7l5 5m0 0l-5 5m5-5H6" />
              </svg>
            {/if}
          </span>
        </button>
      </form>

      <!-- Demo Accounts -->
      <div class="sandbox-label">
        <div class="sandbox-line"></div>
        <span class="sandbox-tag">Sandbox Credentials</span>
        <div class="sandbox-line"></div>
      </div>

      <div class="demo-accounts">
        <button
          class="demo-card"
          type="button"
          onclick={() => { email = 'student@example.com'; password = 'password123'; }}
        >
          <div class="demo-card-role role-student">Student</div>
          <div class="demo-creds">student@example.com<br/>password123</div>
        </button>
        <button
          class="demo-card"
          type="button"
          onclick={() => { email = 'admin@example.com'; password = 'admin123'; }}
        >
          <div class="demo-card-role role-admin">Admin</div>
          <div class="demo-creds">admin@example.com<br/>admin123</div>
        </button>
      </div>

      <p class="footer">Enrollment Management System · 2026</p>

    </div>
  </div>
</div>