<script lang="ts">
  import { userStore } from "$lib/stores/user.svelte";
  import { fly, fade } from "svelte/transition";
  import { onMount } from "svelte";

  let firstName = $state("");
  let lastName  = $state("");
  let isUpdating = $state(false);
  let feedback = $state<{ msg: string; type: "success" | "error" } | null>(null);

  onMount(() => {
    if (userStore.current) {
      const parts = userStore.current.name.split(" ");
      firstName = parts[0] || "";
      lastName  = parts.slice(1).join(" ") || "";
    }
  });

  const handleSubmit = async (e: Event) => {
    e.preventDefault();
    isUpdating = true;
    feedback = null;
    try {
      const response = await fetch("/api/profile", {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name: `${firstName} ${lastName}`.trim() }),
      });
      if (!response.ok) throw new Error();
      userStore.update({ name: `${firstName} ${lastName}`.trim() });
      feedback = { msg: "Profile updated successfully.", type: "success" };
    } catch {
      feedback = { msg: "Failed to update profile. Please try again.", type: "error" };
    } finally {
      isUpdating = false;
    }
  };

  const initials = $derived(`${firstName.charAt(0)}${lastName.charAt(0)}`.toUpperCase());
  const fullName = $derived(`${firstName} ${lastName}`.trim());
</script>

<svelte:head>
  <title>Profile — EMS</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin="anonymous" />
  <link href="https://fonts.googleapis.com/css2?family=Syne:wght@400;600;700;800&family=Geist+Mono:wght@400;500&display=swap" rel="stylesheet" />
</svelte:head>

<style>
  :root {
    --page-bg:   #040d1a;
    --surface-1: rgba(12,28,55,0.6);
    --surface-2: rgba(255,255,255,0.025);
    --border-1:  rgba(255,255,255,0.06);
    --border-2:  rgba(255,255,255,0.04);
    --text-hi:   #e2ecff;
    --text-mid:  rgba(148,185,255,0.6);
    --text-lo:   rgba(96,130,200,0.45);
    --blue:      #3b82f6;
    --blue-dark: #1d4ed8;
    --font:      'Syne', sans-serif;
    --mono:      'Geist Mono', monospace;
  }

  /* ── PAGE ── */
  .page {
    min-height: 100vh;
    background: var(--page-bg);
    font-family: var(--font);
    padding: 2.5rem 2rem 6rem;
    position: relative;
    overflow-x: hidden;
  }

  .page::before {
    content: '';
    position: fixed;
    width: 600px; height: 600px;
    top: -200px; right: -150px;
    background: radial-gradient(circle, rgba(29,78,216,0.08) 0%, transparent 65%);
    pointer-events: none;
  }

  .inner {
    max-width: 860px;
    margin: 0 auto;
    position: relative;
    z-index: 1;
  }

  /* ── PAGE EYEBROW ── */
  .eyebrow-row {
    display: flex;
    align-items: center;
    gap: 0.55rem;
    margin-bottom: 2.5rem;
  }

  .eyebrow-line {
    width: 18px; height: 1px;
    background: var(--text-lo);
  }

  .eyebrow-text {
    font-size: 0.58rem;
    font-weight: 700;
    letter-spacing: 0.38em;
    text-transform: uppercase;
    color: var(--text-lo);
  }

  /* ── PROFILE HERO ── */
  .profile-hero {
    display: flex;
    align-items: flex-end;
    gap: 2rem;
    padding-bottom: 2.5rem;
    border-bottom: 1px solid var(--border-1);
    margin-bottom: 2.5rem;
    flex-wrap: wrap;
  }

  /* Avatar */
  .avatar-wrap {
    position: relative;
    flex-shrink: 0;
  }

  .avatar {
    width: 100px; height: 100px;
    background: linear-gradient(135deg, #1d4ed8, #1e3a8a);
    border-radius: 28px;
    display: flex; align-items: center; justify-content: center;
    font-size: 2.4rem;
    font-weight: 800;
    color: #fff;
    box-shadow:
      0 0 0 1px rgba(255,255,255,0.06),
      0 16px 48px rgba(29,78,216,0.35),
      inset 0 1px 0 rgba(255,255,255,0.15);
    position: relative;
    overflow: hidden;
    letter-spacing: -0.04em;
  }

  /* Sheen sweep */
  .avatar::after {
    content: '';
    position: absolute;
    inset: 0;
    background: linear-gradient(45deg, transparent 35%, rgba(255,255,255,0.1) 50%, transparent 65%);
    animation: sheen 4s ease-in-out infinite;
  }

  @keyframes sheen {
    0%,70%,100% { transform: translateX(-120%) rotate(0deg); }
    40%          { transform: translateX(120%) rotate(0deg); }
  }

  /* Online dot */
  .avatar-dot {
    position: absolute;
    bottom: -3px; right: -3px;
    width: 16px; height: 16px;
    border-radius: 50%;
    background: #22c55e;
    border: 3px solid var(--page-bg);
    box-shadow: 0 0 10px rgba(34,197,94,0.7);
  }

  /* Identity text */
  .identity {
    flex: 1;
    min-width: 0;
  }

  .identity-name {
    font-size: clamp(1.6rem, 3vw, 2.4rem);
    font-weight: 800;
    letter-spacing: -0.03em;
    color: var(--text-hi);
    line-height: 1.1;
    margin: 0 0 0.5rem;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .identity-chips {
    display: flex;
    flex-wrap: wrap;
    gap: 0.5rem;
    margin-bottom: 0.6rem;
  }

  .chip {
    display: flex;
    align-items: center;
    gap: 0.4rem;
    font-size: 0.6rem;
    font-weight: 700;
    letter-spacing: 0.15em;
    text-transform: uppercase;
    padding: 0.3rem 0.75rem;
    border-radius: 99px;
    border: 1px solid var(--border-1);
    background: var(--surface-2);
    color: var(--text-lo);
    font-family: var(--mono);
  }

  .chip.role {
    color: #60a5fa;
    background: rgba(59,130,246,0.07);
    border-color: rgba(59,130,246,0.2);
  }

  .chip-dot {
    width: 5px; height: 5px;
    border-radius: 50%;
    background: currentColor;
    opacity: 0.8;
  }

  .identity-email {
    font-size: 0.75rem;
    font-family: var(--mono);
    color: var(--text-lo);
    font-weight: 400;
  }

  /* ── FORM CARD ── */
  .card {
    background: var(--surface-1);
    border: 1px solid var(--border-1);
    border-radius: 24px;
    overflow: hidden;
    backdrop-filter: blur(16px);
    box-shadow: inset 0 1px 0 rgba(255,255,255,0.03);
    margin-bottom: 1.25rem;
  }

  .card-header {
    display: flex;
    align-items: center;
    gap: 0.65rem;
    padding: 1.1rem 1.5rem;
    border-bottom: 1px solid var(--border-2);
  }

  .card-pip {
    width: 3px; height: 18px;
    border-radius: 99px;
    background: linear-gradient(180deg, #3b82f6, #1e3a8a);
    box-shadow: 0 0 8px rgba(59,130,246,0.5);
    flex-shrink: 0;
  }

  .card-title {
    font-size: 0.68rem;
    font-weight: 700;
    letter-spacing: 0.16em;
    text-transform: uppercase;
    color: var(--text-hi);
  }

  .card-badge {
    margin-left: auto;
    font-size: 0.55rem;
    font-weight: 700;
    letter-spacing: 0.18em;
    text-transform: uppercase;
    color: var(--text-lo);
    background: var(--surface-2);
    border: 1px solid var(--border-1);
    border-radius: 99px;
    padding: 0.2rem 0.6rem;
  }

  .card-body {
    padding: 1.75rem 1.5rem;
  }

  /* ── FORM GRID ── */
  .form-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 1.25rem;
  }

  @media (max-width: 580px) {
    .form-grid { grid-template-columns: 1fr; }
  }

  .field { display: flex; flex-direction: column; gap: 0.5rem; }
  .field.span2 { grid-column: span 2; }
  @media (max-width: 580px) { .field.span2 { grid-column: span 1; } }

  .field-label {
    font-size: 0.6rem;
    font-weight: 700;
    letter-spacing: 0.2em;
    text-transform: uppercase;
    color: var(--text-lo);
  }

  .field-input {
    width: 100%;
    background: rgba(255,255,255,0.03);
    border: 1px solid var(--border-1);
    border-radius: 13px;
    padding: 0.85rem 1rem;
    font-family: var(--font);
    font-size: 0.88rem;
    font-weight: 500;
    color: var(--text-hi);
    outline: none;
    transition: all 0.2s ease;
    box-sizing: border-box;
  }

  .field-input::placeholder { color: rgba(96,130,200,0.25); }

  .field-input:hover {
    border-color: rgba(255,255,255,0.1);
    background: rgba(255,255,255,0.04);
  }

  .field-input:focus {
    border-color: rgba(59,130,246,0.45);
    background: rgba(29,78,216,0.08);
    box-shadow: 0 0 0 3px rgba(59,130,246,0.09);
  }

  /* Read-only field */
  .field-readonly {
    background: rgba(255,255,255,0.015);
    border: 1px dashed rgba(255,255,255,0.07);
    border-radius: 13px;
    padding: 0.85rem 1rem;
    font-family: var(--mono);
    font-size: 0.78rem;
    font-weight: 400;
    color: var(--text-lo);
    display: flex;
    align-items: center;
    gap: 0.5rem;
    cursor: default;
    user-select: all;
  }

  .readonly-icon {
    width: 13px; height: 13px;
    opacity: 0.5;
    flex-shrink: 0;
  }

  /* ── FEEDBACK ── */
  .feedback {
    display: flex;
    align-items: flex-start;
    gap: 0.75rem;
    padding: 1rem 1.25rem;
    border-radius: 14px;
    margin-top: 1.25rem;
    animation: slideIn 0.25s ease;
  }

  @keyframes slideIn {
    from { opacity: 0; transform: translateY(6px); }
    to   { opacity: 1; transform: translateY(0); }
  }

  .feedback.success {
    background: rgba(16,185,129,0.07);
    border: 1px solid rgba(16,185,129,0.2);
  }

  .feedback.error {
    background: rgba(239,68,68,0.07);
    border: 1px solid rgba(239,68,68,0.2);
  }

  .fb-icon {
    width: 28px; height: 28px;
    border-radius: 8px;
    display: flex; align-items: center; justify-content: center;
    flex-shrink: 0;
  }

  .feedback.success .fb-icon { background: rgba(16,185,129,0.12); }
  .feedback.error   .fb-icon { background: rgba(239,68,68,0.12); }

  .fb-label {
    font-size: 0.55rem;
    font-weight: 700;
    letter-spacing: 0.22em;
    text-transform: uppercase;
    margin-bottom: 0.15rem;
  }

  .feedback.success .fb-label { color: #10b981; }
  .feedback.error   .fb-label { color: #ef4444; }

  .fb-msg {
    font-size: 0.78rem;
    font-weight: 500;
    color: var(--text-hi);
    line-height: 1.4;
  }

  /* ── CARD FOOTER ── */
  .card-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 1rem;
    padding: 1rem 1.5rem;
    border-top: 1px solid var(--border-2);
    flex-wrap: wrap;
  }

  .footer-hint {
    font-size: 0.62rem;
    font-weight: 500;
    color: var(--text-lo);
    font-family: var(--mono);
  }

  .save-btn {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.7rem 1.5rem;
    background: linear-gradient(135deg, #1d4ed8, #1e3a8a);
    color: #fff;
    border: none;
    border-radius: 12px;
    font-family: var(--font);
    font-size: 0.65rem;
    font-weight: 700;
    letter-spacing: 0.14em;
    text-transform: uppercase;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: all 0.22s ease;
    box-shadow: 0 4px 20px rgba(29,78,216,0.35);
  }

  .save-btn::after {
    content: '';
    position: absolute;
    inset: 0;
    background: linear-gradient(90deg, transparent, rgba(255,255,255,0.07), transparent);
    transform: translateX(-100%);
    transition: transform 0.5s ease;
  }

  .save-btn:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 8px 28px rgba(29,78,216,0.5);
  }

  .save-btn:hover:not(:disabled)::after { transform: translateX(100%); }
  .save-btn:active:not(:disabled)       { transform: translateY(0); }

  .save-btn:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }

  .spinner {
    width: 12px; height: 12px;
    border: 2px solid rgba(255,255,255,0.25);
    border-top-color: white;
    border-radius: 50%;
    animation: spin 0.7s linear infinite;
  }

  @keyframes spin { to { transform: rotate(360deg); } }

  /* ── READ-ONLY INFO CARD ── */
  .info-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 1px;
    background: var(--border-2);
    border-radius: 0 0 24px 24px;
    overflow: hidden;
  }

  @media (max-width: 480px) { .info-grid { grid-template-columns: 1fr; } }

  .info-cell {
    background: var(--surface-1);
    padding: 1.1rem 1.5rem;
    display: flex;
    flex-direction: column;
    gap: 0.4rem;
  }

  .info-label {
    font-size: 0.55rem;
    font-weight: 700;
    letter-spacing: 0.28em;
    text-transform: uppercase;
    color: var(--text-lo);
  }

  .info-value {
    font-family: var(--mono);
    font-size: 0.78rem;
    font-weight: 500;
    color: var(--text-mid);
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .info-value.role-badge {
    display: inline-flex;
    align-items: center;
    gap: 0.4rem;
    color: #60a5fa;
    width: fit-content;
  }

  .role-pip {
    width: 5px; height: 5px;
    border-radius: 50%;
    background: #60a5fa;
    box-shadow: 0 0 6px rgba(96,165,250,0.7);
  }
</style>

<div class="page" in:fade={{ duration: 350 }}>
  <div class="inner">

    <!-- Eyebrow -->
    <div class="eyebrow-row" in:fly={{ y: 10, duration: 420, delay: 40 }}>
      <span class="eyebrow-line"></span>
      <span class="eyebrow-text">Profile Settings</span>
    </div>

    <!-- Profile hero -->
    <div class="profile-hero" in:fly={{ y: 14, duration: 480, delay: 80 }}>
      <div class="avatar-wrap">
        <div class="avatar">{initials || "?"}</div>
        <div class="avatar-dot"></div>
      </div>

      <div class="identity">
        <h1 class="identity-name">{fullName || "Your Name"}</h1>
        <div class="identity-chips">
          <span class="chip role">
            <span class="chip-dot"></span>
            {userStore.current?.role ?? "Student"}
          </span>
          <!-- <span class="chip">Institutional</span> -->
        </div>
      </div>
    </div>

    <!-- Edit card -->
    <form onsubmit={handleSubmit} in:fly={{ y: 16, duration: 460, delay: 160 }}>
      <div class="card">
        <div class="card-header">
          <div class="card-pip"></div>
          <span class="card-title">Personal Information</span>
          <span class="card-badge">Editable</span>
        </div>

        <div class="card-body">
          <div class="form-grid">
            <div class="field">
              <label class="field-label" for="fname">First Name</label>
              <input
                id="fname"
                class="field-input"
                type="text"
                bind:value={firstName}
                placeholder="Maria"
                required
                autocomplete="given-name"
              />
            </div>

            <div class="field">
              <label class="field-label" for="lname">Last Name</label>
              <input
                id="lname"
                class="field-input"
                type="text"
                bind:value={lastName}
                placeholder="Santos"
                required
                autocomplete="family-name"
              />
            </div>
          </div>

          {#if feedback}
            <div class="feedback {feedback.type}">
              <div class="fb-icon">
                {#if feedback.type === "success"}
                  <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="#10b981" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M20 6L9 17l-5-5"/>
                  </svg>
                {:else}
                  <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="#ef4444" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="12" r="10"/><path d="M12 8v4m0 4h.01"/>
                  </svg>
                {/if}
              </div>
              <div>
                <div class="fb-label">{feedback.type === "success" ? "Saved" : "Error"}</div>
                <div class="fb-msg">{feedback.msg}</div>
              </div>
            </div>
          {/if}
        </div>

        <div class="card-footer">
          <span class="footer-hint">Changes apply immediately after saving.</span>
          <button type="submit" class="save-btn" disabled={isUpdating}>
            {#if isUpdating}
              <span class="spinner"></span>
              Saving…
            {:else}
              <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                <path d="M19 21H5a2 2 0 01-2-2V5a2 2 0 012-2h11l5 5v11a2 2 0 01-2 2z"/><polyline points="17 21 17 13 7 13 7 21"/><polyline points="7 3 7 8 15 8"/>
              </svg>
              Save Changes
            {/if}
          </button>
        </div>
      </div>
    </form>

    <!-- Read-only account info -->
    <div class="card" in:fly={{ y: 16, duration: 460, delay: 240 }}>
      <div class="card-header">
        <div class="card-pip" style="background: linear-gradient(180deg,rgba(148,185,255,0.4),rgba(96,130,200,0.2));box-shadow:none;"></div>
        <span class="card-title">Account Details</span>
        <span class="card-badge">Read-only</span>
      </div>

      <div class="info-grid">
        <div class="info-cell">
          <span class="info-label">User ID</span>
          <span class="info-value">{userStore.current?.id ?? "—"}</span>
        </div>
        <div class="info-cell">
          <span class="info-label">Role</span>
          <span class="info-value role-badge">
            <span class="role-pip"></span>
            {userStore.current?.role ?? "STUDENT"}
          </span>
        </div>
        <div class="info-cell">
          <span class="info-label">Institutional Email</span>
          <span class="info-value">{userStore.current?.email ?? "—"}</span>
        </div>
        <div class="info-cell">
          <span class="info-label">Auth Method</span>
          <span class="info-value">Institutional SSO</span>
        </div>
      </div>
    </div>

  </div>
</div>