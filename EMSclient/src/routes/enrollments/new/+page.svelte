<!-- src/routes/enrollments/new/+page.svelte -->
<script lang="ts">
  import EnrollmentForm from "$lib/components/EnrollmentForm.svelte";
  import { goto } from "$app/navigation";
  import { deserialize } from "$app/forms";
  import type { ActionResult } from "@sveltejs/kit";
  import type { PageData } from "./$types";

  let { data }: { data: PageData } = $props();

  let isLoading = $state(false);
  let submitError = $state<string | null>(null);

  const handleEnroll = async (formData: {
    sectionId?: number;
    status?: string;
  }) => {

    isLoading = true;
    submitError = null;

    try {
      const body = new FormData();
      if (formData.sectionId)
        body.append("sectionId", String(formData.sectionId));

     const response = await fetch("?/enroll", { method: "POST", body }); //call default enroll 
      const result: ActionResult = deserialize(await response.text());

      if (result.type === "failure") {
        submitError = (result.data as any)?.message ?? "Failed to enroll";
        return;
      }
      if (result.type === "error") {
        submitError = result.error?.message ?? "An unexpected error occurred";
        return;
      }

      goto("/");
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

    <div class="form-wrap">
      {#if submitError}
        <div class="submit-error" role="alert">
          <svg width="15" height="15" viewBox="0 0 20 20" fill="#fca5a5">
            <path
              fill-rule="evenodd"
              d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z"
              clip-rule="evenodd"
            />
          </svg>
          <p>{submitError}</p>
          <button
            class="dismiss-btn"
            onclick={() => (submitError = null)}
            aria-label="Dismiss">✕</button
          >
        </div>
      {/if}

      <EnrollmentForm
        availableSections={data.availableSections}
        onSubmit={handleEnroll}
        {isLoading}
        mode="new"
      />
    </div>
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
    flex-shrink: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow:
      0 8px 28px rgba(30, 77, 183, 0.4),
      inset 0 1px 0 rgba(255, 255, 255, 0.15);
    position: relative;
    overflow: hidden;
    animation: iconIn 0.6s 0.1s cubic-bezier(0.22, 1, 0.36, 1) both;
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
    color: #fff;
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
    flex: 1;
  }

  .dismiss-btn {
    background: none;
    border: none;
    color: rgba(252, 165, 165, 0.5);
    cursor: pointer;
    font-size: 0.75rem;
    padding: 0;
    transition: color 0.15s;
  }
  .dismiss-btn:hover {
    color: #fca5a5;
  }

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
  @keyframes iconIn {
    from {
      opacity: 0;
      transform: scale(0.7) rotate(-10deg);
    }
    to {
      opacity: 1;
      transform: scale(1) rotate(0);
    }
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
</style>
