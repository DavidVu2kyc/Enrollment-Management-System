<!-- create new enrollments -->
<script lang="ts">
  import EnrollmentForm from "$lib/components/EnrollmentForm.svelte";
  import { enrollmentsStore } from "$lib/stores/enrollments.svelte";
  import { goto } from "$app/navigation";
  import type { PageData } from "./$types";
  import { deserialize } from "$app/forms";
  import type { SectionResponse } from "$lib/server/section"; 
    import type { ActionResult } from "@sveltejs/kit";

  let { data }: { data: PageData } = $props();

  let isLoading = $state(false);
  let error = $state<string | null>(null);

  let submitError = $state<string | null>(null);

  const handleEnroll = async (formData: any) => {
    isLoading = true;
    submitError = null;
    try {
      const body = new FormData();

      body.append("sectionId", String(formData.sectionId));

      const response = await fetch("?/default", {
        method: "POST",
        body,
      });

      if (!response.ok) {
        throw new Error("Failed to enroll in course");
      }

      const result: ActionResult = deserialize(await response.text());

      if (result.type === "failure") {
        submitError = (result.data as any)?.message ?? "Failed to enroll in course";
        return;
      }

       if (result.type === "error") {
        submitError = result.error?.message ?? "An unexpected error occurred";
        return;
      }

      await goto("/");
    } catch (err) {
      submitError = err instanceof Error ? err.message : "An error occurred";
    } finally {
      isLoading = false;
    }
  };
</script>

<svelte:head>
  <title>New Registration — EMS</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link
    rel="preconnect"
    href="https://fonts.gstatic.com"
    crossorigin="anonymous"
  />
  <link
    href="https://fonts.googleapis.com/css2?family=DM+Serif+Display:ital@0;1&family=DM+Sans:wght@300;400;500;600&display=swap"
    rel="stylesheet"
  />
</svelte:head>

<div class="page">
  <div class="content">
    <!-- Back -->
    <a href="/" class="back-link">
      <svg
        width="14"
        height="14"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="2.5"
      >
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          d="M19 12H5m7-7l-7 7 7 7"
        />
      </svg>
      Back to Dashboard
    </a>

    <!-- Header -->
    <div class="header">
      <div class="header-icon">
        <svg
          width="26"
          height="26"
          fill="none"
          stroke="white"
          stroke-width="2"
          viewBox="0 0 24 24"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            d="M12 4.5v15m7.5-7.5h-15"
          />
        </svg>
      </div>
      <div>
        <p class="eyebrow">Academic Registration</p>
        <h1>New Enrollment</h1>
        <p class="header-sub">
          Initiate a course enrollment for the upcoming academic cycle. Slot
          authorization is processed in real time.
        </p>
      </div>
    </div>

    <!-- States -->
    {#if error}
      <div class="error-card">
        <div class="error-icon-wrap">
          <svg
            width="22"
            height="22"
            fill="none"
            stroke="#fca5a5"
            stroke-width="2"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M12 9v3.75m-9.303 3.376c-.866 1.5.217 3.374 1.948 3.374h14.71c1.73 0 2.813-1.874 1.948-3.374L13.949 3.378c-.866-1.5-3.032-1.5-3.898 0L2.697 16.126zM12 15.75h.007v.008H12v-.008z"
            />
          </svg>
        </div>
        <p class="error-title">Catalog Unavailable</p>
        <p class="error-msg">{error}</p>
        <button
          class="error-retry-btn"
          onclick={() => window.location.reload()}
        >
          <svg
            width="12"
            height="12"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2.5"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"
            />
          </svg>
          Retry
        </button>
      </div>
    {:else}
      <div class="form-wrap">
        {#if submitError}
          <div class="submit-error">
            <svg width="15" height="15" viewBox="0 0 20 20" fill="#fca5a5">
              <path
                fill-rule="evenodd"
                d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z"
                clip-rule="evenodd"
              />
            </svg>
            <p>{submitError}</p>
          </div>
        {/if}
        <EnrollmentForm
          availableSections={data.availableSections}
          onSubmit={handleEnroll}
          {isLoading}
          mode="new"
        />
      </div>
    {/if}
  </div>
</div>

<style>
  .page {
    font-family: "DM Sans", sans-serif;
    min-height: 100vh;
    background-color: #05101f;
    background-image: radial-gradient(
        ellipse 70% 50% at 15% -5%,
        rgba(14, 60, 120, 0.45) 0%,
        transparent 60%
      ),
      radial-gradient(
        ellipse 50% 40% at 85% 100%,
        rgba(8, 40, 90, 0.3) 0%,
        transparent 60%
      );
    padding: 2.5rem 2rem 6rem;
    position: relative;
    overflow-x: hidden;
  }

  .page::before {
    content: "";
    position: fixed;
    inset: 0;
    background-image: linear-gradient(
        rgba(255, 255, 255, 0.022) 1px,
        transparent 1px
      ),
      linear-gradient(90deg, rgba(255, 255, 255, 0.022) 1px, transparent 1px);
    background-size: 60px 60px;
    pointer-events: none;
    z-index: 0;
  }

  .content {
    position: relative;
    z-index: 1;
    max-width: 760px;
    margin: 0 auto;
  }

  /* ── Back link ───────────────────────────────── */
  .back-link {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    color: rgba(120, 160, 220, 0.55);
    text-decoration: none;
    font-size: 0.74rem;
    font-weight: 600;
    letter-spacing: 0.1em;
    text-transform: uppercase;
    margin-bottom: 2.5rem;
    transition: color 0.2s;
    animation: fadeUp 0.4s cubic-bezier(0.22, 1, 0.36, 1) both;
  }

  .back-link:hover {
    color: rgba(160, 200, 255, 0.85);
  }

  /* ── Header ──────────────────────────────────── */
  .header {
    display: flex;
    align-items: flex-start;
    gap: 1.5rem;
    padding-bottom: 2rem;
    margin-bottom: 2.5rem;
    border-bottom: 1px solid rgba(255, 255, 255, 0.06);
    animation: fadeUp 0.5s 0.05s cubic-bezier(0.22, 1, 0.36, 1) both;
  }

  .header-icon {
    width: 58px;
    height: 58px;
    background: linear-gradient(135deg, #1e4db7, #0d2d7a);
    border-radius: 18px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    box-shadow:
      0 8px 28px rgba(30, 77, 183, 0.4),
      inset 0 1px 0 rgba(255, 255, 255, 0.15);
    position: relative;
    overflow: hidden;
    animation: iconIn 0.6s 0.1s cubic-bezier(0.22, 1, 0.36, 1) both;
  }

  @keyframes iconIn {
    from {
      opacity: 0;
      transform: scale(0.7) rotate(-10deg);
    }
    to {
      opacity: 1;
      transform: scale(1) rotate(0deg);
    }
  }

  .header-icon::after {
    content: "";
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: linear-gradient(
      45deg,
      transparent 40%,
      rgba(255, 255, 255, 0.1) 50%,
      transparent 60%
    );
    animation: sheen 4s 1s ease-in-out infinite;
  }

  @keyframes sheen {
    0%,
    75%,
    100% {
      transform: translateX(-100%);
    }
    40% {
      transform: translateX(100%);
    }
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
    font-family: "DM Serif Display", serif;
    font-style: italic;
    font-size: clamp(1.8rem, 4vw, 2.75rem);
    font-weight: 400;
    color: #ffffff;
    line-height: 1.05;
    letter-spacing: -0.02em;
    margin: 0 0 0.6rem;
  }

  .header-sub {
    font-size: 0.84rem;
    color: rgba(140, 170, 210, 0.55);
    line-height: 1.6;
    margin: 0;
    max-width: 480px;
  }

  /* ── Error state ─────────────────────────────── */
  .error-card {
    background: linear-gradient(
      135deg,
      rgba(180, 28, 28, 0.13),
      rgba(120, 10, 10, 0.18)
    );
    border: 1px solid rgba(248, 113, 113, 0.18);
    border-radius: 24px;
    padding: 3.5rem 2rem;
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
    gap: 1rem;
    box-shadow:
      0 16px 48px rgba(180, 28, 28, 0.15),
      inset 0 1px 0 rgba(255, 255, 255, 0.04);
    animation: fadeUp 0.5s 0.15s cubic-bezier(0.22, 1, 0.36, 1) both;
  }

  .error-icon-wrap {
    width: 52px;
    height: 52px;
    background: rgba(248, 113, 113, 0.12);
    border: 1px solid rgba(248, 113, 113, 0.22);
    border-radius: 14px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 0.5rem;
  }

  .error-title {
    font-family: "DM Serif Display", serif;
    font-style: italic;
    font-size: 1.4rem;
    color: #fca5a5;
    margin: 0 0 0.25rem;
  }

  .error-msg {
    font-size: 0.83rem;
    color: rgba(252, 165, 165, 0.6);
    margin: 0 0 1rem;
    line-height: 1.55;
    max-width: 340px;
  }

  .error-retry-btn {
    display: inline-flex;
    align-items: center;
    gap: 0.45rem;
    background: rgba(248, 113, 113, 0.1);
    border: 1px solid rgba(248, 113, 113, 0.2);
    color: #fca5a5;
    border-radius: 10px;
    padding: 0.7rem 1.4rem;
    font-family: "DM Sans", sans-serif;
    font-size: 0.75rem;
    font-weight: 600;
    letter-spacing: 0.09em;
    text-transform: uppercase;
    cursor: pointer;
    transition: all 0.2s;
  }

  .error-retry-btn:hover {
    background: rgba(248, 113, 113, 0.18);
    transform: translateY(-1px);
  }

  /* ── Submit error inline ─────────────────────── */
  .submit-error {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    background: rgba(180, 28, 28, 0.1);
    border: 1px solid rgba(248, 113, 113, 0.18);
    border-radius: 12px;
    padding: 0.9rem 1.1rem;
    margin-bottom: 1.25rem;
    animation:
      shake 0.4s ease,
      fadeUp 0.3s ease;
  }

  .submit-error p {
    font-size: 0.82rem;
    color: #fca5a5;
    margin: 0;
  }

  @keyframes shake {
    0%,
    100% {
      transform: translateX(0);
    }
    20% {
      transform: translateX(-5px);
    }
    40% {
      transform: translateX(5px);
    }
    60% {
      transform: translateX(-3px);
    }
    80% {
      transform: translateX(3px);
    }
  }

  /* ── Form wrapper ────────────────────────────── */
  .form-wrap {
    animation: fadeUp 0.5s 0.15s cubic-bezier(0.22, 1, 0.36, 1) both;
  }

  @keyframes fadeUp {
    from {
      opacity: 0;
      transform: translateY(16px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }
</style>
