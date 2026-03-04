<script lang="ts">
  import { userStore } from "$lib/stores/user.svelte";

  let firstName = $state("");
  let lastName  = $state("");
  let email     = $state("");
  let isLoading = $state(false);
  let message   = $state("");
  let error     = $state("");

  $effect(() => {
    if (userStore.current) {
      email = userStore.current.email;
      const parts = userStore.current.name.split(" ");
      firstName = parts[0] || "";
      lastName  = parts.slice(1).join(" ") || "";
    }
  });

  const handleSubmit = async (e: Event) => {
    e.preventDefault();
    isLoading = true;
    message   = "";
    error     = "";

    try {
      const response = await fetch("/api/profile", {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          firstName,
          lastName,
          name: `${firstName} ${lastName}`.trim(),
        }),
      });

      if (!response.ok) throw new Error("Failed to update profile");

      const result = await response.json();
      userStore.update(result.data);
      message = "Profile synchronized successfully.";
    } catch (err) {
      error = err instanceof Error ? err.message : "An error occurred";
    } finally {
      isLoading = false;
    }
  };

  $derived: const initials = `${firstName.charAt(0)}${lastName.charAt(0)}`.toUpperCase();
</script>

<svelte:head>
  <title>Identity Settings — EMS</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin="anonymous" />
  <link href="https://fonts.googleapis.com/css2?family=DM+Serif+Display:ital@0;1&family=DM+Sans:wght@300;400;500;600&display=swap" rel="stylesheet" />
</svelte:head>

<style>
  :global(body) {
    font-family: 'DM Sans', sans-serif;
    background-color: #05101f;
  }

  .page {
    min-height: 100vh;
    background-color: #05101f;
    background-image:
      radial-gradient(ellipse 70% 50% at 15% -5%, rgba(14, 60, 120, 0.45) 0%, transparent 60%),
      radial-gradient(ellipse 50% 40% at 85% 100%, rgba(8, 40, 90, 0.3) 0%, transparent 60%);
    padding: 2.5rem 2rem 6rem;
    position: relative;
    overflow-x: hidden;
  }

  .page::before {
    content: '';
    position: fixed;
    inset: 0;
    background-image:
      linear-gradient(rgba(255,255,255,0.022) 1px, transparent 1px),
      linear-gradient(90deg, rgba(255,255,255,0.022) 1px, transparent 1px);
    background-size: 60px 60px;
    pointer-events: none;
    z-index: 0;
  }

  .content {
    position: relative;
    z-index: 1;
    max-width: 800px;
    margin: 0 auto;
  }

  /* ── Back ────────────────────────────────────── */
  .back-link {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    color: rgba(120, 160, 220, 0.5);
    text-decoration: none;
    font-size: 0.72rem;
    font-weight: 600;
    letter-spacing: 0.1em;
    text-transform: uppercase;
    margin-bottom: 2.5rem;
    transition: color 0.2s;
    animation: fadeUp 0.4s cubic-bezier(0.22,1,0.36,1) both;
    cursor: pointer;
    border: none; background: none;
  }
  .back-link:hover { color: rgba(160, 200, 255, 0.8); }

  /* ── Header ──────────────────────────────────── */
  .header {
    display: flex;
    align-items: flex-start;
    gap: 1.5rem;
    padding-bottom: 2rem;
    margin-bottom: 2.5rem;
    border-bottom: 1px solid rgba(255,255,255,0.06);
    animation: fadeUp 0.5s 0.05s cubic-bezier(0.22,1,0.36,1) both;
  }

  .avatar {
    width: 64px; height: 64px;
    background: linear-gradient(135deg, #1e4db7, #0d2d7a);
    border-radius: 18px;
    display: flex; align-items: center; justify-content: center;
    flex-shrink: 0;
    font-family: 'DM Serif Display', serif;
    font-style: italic;
    font-size: 1.5rem;
    color: rgba(255,255,255,0.9);
    box-shadow: 0 8px 28px rgba(30,77,183,0.4), inset 0 1px 0 rgba(255,255,255,0.15);
    letter-spacing: -0.02em;
    position: relative;
    overflow: hidden;
    transition: box-shadow 0.3s;
  }

  .avatar::after {
    content: '';
    position: absolute;
    top: -50%; left: -50%;
    width: 200%; height: 200%;
    background: linear-gradient(45deg, transparent 40%, rgba(255,255,255,0.08) 50%, transparent 60%);
    animation: sheen 4s 1.5s ease-in-out infinite;
  }

  @keyframes sheen {
    0%,75%,100% { transform: translateX(-100%); }
    40% { transform: translateX(100%); }
  }

  .eyebrow {
    font-size: 0.62rem;
    font-weight: 700;
    letter-spacing: 0.22em;
    text-transform: uppercase;
    color: rgba(80, 140, 255, 0.65);
    margin-bottom: 0.5rem;
  }

  h1 {
    font-family: 'DM Serif Display', serif;
    font-style: italic;
    font-size: clamp(1.8rem, 3.5vw, 2.6rem);
    font-weight: 400;
    color: #ffffff;
    line-height: 1.05;
    letter-spacing: -0.02em;
    margin: 0 0 0.5rem;
  }

  .header-sub {
    font-size: 0.83rem;
    color: rgba(140, 170, 210, 0.55);
    line-height: 1.6;
    margin: 0;
  }

  /* ── Card ────────────────────────────────────── */
  .card {
    background: linear-gradient(145deg, rgba(12,28,55,0.92), rgba(7,18,38,0.96));
    border: 1px solid rgba(255,255,255,0.07);
    border-radius: 24px;
    overflow: hidden;
    box-shadow: 0 16px 48px rgba(0,0,0,0.45), inset 0 1px 0 rgba(255,255,255,0.05);
    animation: fadeUp 0.5s 0.12s cubic-bezier(0.22,1,0.36,1) both;
  }

  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 1.25rem 1.75rem;
    border-bottom: 1px solid rgba(255,255,255,0.05);
    background: rgba(255,255,255,0.02);
  }

  .card-title {
    font-size: 0.62rem;
    font-weight: 700;
    letter-spacing: 0.2em;
    text-transform: uppercase;
    color: rgba(100, 140, 200, 0.5);
    margin: 0;
  }

  .card-body {
    padding: 2rem 1.75rem;
    display: flex;
    flex-direction: column;
    gap: 2rem;
  }

  /* ── Feedback banners ────────────────────────── */
  .feedback {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    padding: 0.9rem 1.1rem;
    border-radius: 12px;
    font-size: 0.82rem;
    font-weight: 500;
    animation: shake-in 0.4s ease, fadeUp 0.3s ease;
  }

  .feedback.success {
    background: rgba(10,40,18,0.9);
    border: 1px solid rgba(74,222,128,0.22);
    color: #86efac;
  }

  .feedback.error {
    background: rgba(50,12,12,0.9);
    border: 1px solid rgba(248,113,113,0.22);
    color: #fca5a5;
    animation: shake 0.4s ease, fadeUp 0.3s ease;
  }

  @keyframes shake {
    0%,100% { transform: translateX(0); }
    20% { transform: translateX(-5px); }
    40% { transform: translateX(5px); }
    60% { transform: translateX(-3px); }
    80% { transform: translateX(3px); }
  }

  /* ── Info tiles ──────────────────────────────── */
  .info-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 0.85rem;
  }

  @media (max-width: 480px) {
    .info-grid { grid-template-columns: 1fr; }
  }

  .info-tile {
    background: rgba(255,255,255,0.03);
    border: 1px solid rgba(255,255,255,0.06);
    border-radius: 14px;
    padding: 1.1rem 1.25rem;
    transition: border-color 0.2s;
  }

  .info-tile:hover { border-color: rgba(60,120,255,0.15); }

  .tile-label {
    font-size: 0.6rem;
    font-weight: 700;
    letter-spacing: 0.2em;
    text-transform: uppercase;
    color: rgba(100, 140, 200, 0.45);
    margin-bottom: 0.45rem;
  }

  .tile-value {
    font-size: 0.83rem;
    font-weight: 500;
    color: rgba(200, 220, 255, 0.8);
    word-break: break-all;
  }

  .role-row {
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }

  .role-dot {
    width: 6px; height: 6px;
    border-radius: 50%;
    background: #60a5fa;
    box-shadow: 0 0 6px rgba(96,165,250,0.7);
    animation: pulse-blue 2s ease-in-out infinite;
    flex-shrink: 0;
  }

  @keyframes pulse-blue {
    0%,100% { box-shadow: 0 0 5px rgba(96,165,250,0.7); }
    50%      { box-shadow: 0 0 10px rgba(96,165,250,0.9), 0 0 18px rgba(96,165,250,0.4); }
  }

  .role-value {
    font-size: 0.78rem;
    font-weight: 600;
    color: #93c5fd;
    letter-spacing: 0.05em;
    text-transform: capitalize;
  }

  /* ── Divider ─────────────────────────────────── */
  .section-divider {
    height: 1px;
    background: rgba(255,255,255,0.05);
  }

  /* ── Form grid ───────────────────────────────── */
  .form-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 1.25rem;
  }

  @media (max-width: 520px) {
    .form-grid { grid-template-columns: 1fr; }
  }

  .field-label {
    display: block;
    font-size: 0.68rem;
    font-weight: 700;
    letter-spacing: 0.12em;
    text-transform: uppercase;
    color: rgba(140, 170, 210, 0.7);
    margin-bottom: 0.5rem;
  }

  .field-input {
    width: 100%;
    background: rgba(255,255,255,0.04);
    border: 1px solid rgba(255,255,255,0.08);
    border-radius: 12px;
    padding: 0.85rem 1rem;
    color: #e8f0ff;
    font-family: 'DM Sans', sans-serif;
    font-size: 0.88rem;
    outline: none;
    transition: all 0.2s ease;
    box-sizing: border-box;
  }

  .field-input::placeholder { color: rgba(120, 150, 200, 0.3); }

  .field-input:hover {
    border-color: rgba(255,255,255,0.13);
    background: rgba(255,255,255,0.05);
  }

  .field-input:focus {
    border-color: rgba(80, 140, 255, 0.45);
    background: rgba(30,70,160,0.1);
    box-shadow: 0 0 0 3px rgba(60,120,255,0.09);
  }

  /* ── Footer ──────────────────────────────────── */
  .card-footer {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    gap: 0.75rem;
    padding: 1.25rem 1.75rem;
    border-top: 1px solid rgba(255,255,255,0.05);
    background: rgba(255,255,255,0.015);
  }

  .btn-discard {
    background: rgba(255,255,255,0.05);
    border: 1px solid rgba(255,255,255,0.08);
    color: rgba(160,190,230,0.65);
    border-radius: 10px;
    padding: 0.7rem 1.25rem;
    font-family: 'DM Sans', sans-serif;
    font-size: 0.78rem;
    font-weight: 600;
    letter-spacing: 0.06em;
    text-transform: uppercase;
    cursor: pointer;
    transition: all 0.2s;
  }

  .btn-discard:hover {
    background: rgba(255,255,255,0.09);
    color: rgba(200,220,255,0.8);
  }

  .btn-save {
    display: inline-flex;
    align-items: center;
    gap: 0.45rem;
    background: linear-gradient(135deg, #1e4db7, #0f3298);
    color: white;
    border: none;
    border-radius: 10px;
    padding: 0.7rem 1.5rem;
    font-family: 'DM Sans', sans-serif;
    font-size: 0.78rem;
    font-weight: 600;
    letter-spacing: 0.06em;
    text-transform: uppercase;
    cursor: pointer;
    transition: all 0.25s ease;
    box-shadow: 0 4px 18px rgba(30,77,183,0.35);
  }

  .btn-save:hover:not(:disabled) {
    transform: translateY(-1px);
    box-shadow: 0 8px 28px rgba(30,77,183,0.5);
  }

  .btn-save:disabled {
    opacity: 0.6; cursor: not-allowed;
  }

  .spinner {
    width: 13px; height: 13px;
    border: 2px solid rgba(255,255,255,0.25);
    border-top-color: white;
    border-radius: 50%;
    animation: spin 0.7s linear infinite;
  }

  @keyframes spin    { to { transform: rotate(360deg); } }

  @keyframes fadeUp {
    from { opacity: 0; transform: translateY(14px); }
    to   { opacity: 1; transform: translateY(0); }
  }
</style>

<div class="page">
  <div class="content">

    <!-- Back -->
    <button class="back-link" onclick={() => window.history.back()}>
      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
        <path stroke-linecap="round" stroke-linejoin="round" d="M19 12H5m7-7l-7 7 7 7"/>
      </svg>
      Back
    </button>

    <!-- Header -->
    <div class="header">
      <div class="avatar">
        {firstName.charAt(0)}{lastName.charAt(0)}
      </div>
      <div>
        <p class="eyebrow">Account Management</p>
        <h1>Identity Settings</h1>
        <p class="header-sub">
          Manage your institutional profile and authentication credentials within EMS.
        </p>
      </div>
    </div>

    <!-- Card -->
    <div class="card">
      <div class="card-header">
        <p class="card-title">Security Profile &amp; Metadata</p>
      </div>

      <form onsubmit={handleSubmit}>
        <div class="card-body">

          <!-- Feedback -->
          {#if message}
            <div class="feedback success">
              <svg width="15" height="15" fill="none" stroke="currentColor" stroke-width="2.5" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7"/>
              </svg>
              {message}
            </div>
          {/if}
          {#if error}
            <div class="feedback error">
              <svg width="15" height="15" viewBox="0 0 20 20" fill="#fca5a5">
                <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clip-rule="evenodd"/>
              </svg>
              {error}
            </div>
          {/if}

          <!-- Read-only info -->
          <div class="info-grid">
            <div class="info-tile">
              <p class="tile-label">Institutional Identifier</p>
              <p class="tile-value">{userStore.current?.email ?? '—'}</p>
            </div>
            <div class="info-tile">
              <p class="tile-label">Authorization Clearance</p>
              <div class="role-row">
                <span class="role-dot"></span>
                <span class="role-value">{userStore.current?.role ?? 'Standard User'}</span>
              </div>
            </div>
          </div>

          <div class="section-divider"></div>

          <!-- Editable fields -->
          <div class="form-grid">
            <div>
              <label class="field-label" for="firstName">Given Name</label>
              <input
                id="firstName"
                class="field-input"
                type="text"
                placeholder="e.g. Maria"
                bind:value={firstName}
                required
                autocomplete="given-name"
              />
            </div>
            <div>
              <label class="field-label" for="lastName">Family Name</label>
              <input
                id="lastName"
                class="field-input"
                type="text"
                placeholder="e.g. Santos"
                bind:value={lastName}
                required
                autocomplete="family-name"
              />
            </div>
          </div>

        </div>

        <!-- Footer -->
        <div class="card-footer">
          <button type="button" class="btn-discard" onclick={() => window.history.back()}>
            Discard
          </button>
          <button type="submit" class="btn-save" disabled={isLoading}>
            {#if isLoading}
              <span class="spinner"></span>
              Saving…
            {:else}
              <svg width="13" height="13" fill="none" stroke="currentColor" stroke-width="2.5" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" d="M9 12.75L11.25 15 15 9.75M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
              </svg>
              Update Identity
            {/if}
          </button>
        </div>
      </form>
    </div>

  </div>
</div>