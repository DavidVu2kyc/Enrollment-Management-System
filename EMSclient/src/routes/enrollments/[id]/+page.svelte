<!-- src/routes/enrollments/[id]/+page.svelte -->
<script lang="ts">
  import { deserialize } from "$app/forms";
  import { goto } from "$app/navigation";
  import { page } from "$app/stores";
  import type { ActionResult } from "@sveltejs/kit";

  import EnrollmentForm from "$lib/components/EnrollmentForm.svelte";
  import { enrollmentsStore } from "$lib/stores/enrollments.svelte";
  import { apiClient } from "$lib/api/client"; // ✅ added — needed for handleSectionGet
  import type { PageData } from "./$types";
  import type { EnrollmentResponse } from "$lib/types/enrollment";
  import type { SectionResponse } from "$lib/server/section";

  let { data }: { data: PageData } = $props();

  const enrollmentId = $page.params.id!;
  const isNew = enrollmentId === "new";

  let isLoading = $state(false);
  let submitError = $state<string | null>(null);

  // ── Section preview state (lives here, not in EnrollmentForm) ─
  let selectedSection = $state<SectionResponse | null>(null); // ✅ correct location
  let sectionFetching = $state(false);
  let sectionFetchErr = $state<string | null>(null);

  // ─────────────────────────────────────────────────────────────
  // handleSectionGet — called by EnrollmentForm's onSectionChange
  // fires GET /api/sections/{sectionId} for the full server record
  // ─────────────────────────────────────────────────────────────

  const handleSectionGet = async (sectionId: number) => {
    // ✅ was empty / missing
    if (!sectionId) {
      selectedSection = null;
      return;
    }

    sectionFetching = true;
    sectionFetchErr = null;

    try {
      selectedSection = await apiClient.get<SectionResponse>(
        `/sections/${sectionId}`,
      );
    } catch (err: any) {
      sectionFetchErr = err?.message ?? "Failed to load section";
      selectedSection = null;
    } finally {
      sectionFetching = false;
    }
  };

  // ─────────────────────────────────────────────────────────────
  // handleUpdate — called by EnrollmentForm's onSubmit
  // ─────────────────────────────────────────────────────────────
  const handleUpdate = async (formData: {
    sectionId?: number;
    status?: string;
  }) => {
    isLoading = true;
    submitError = null;

    try {
      const body = new FormData();
      body.append("enrollmentId", enrollmentId);
      if (formData.sectionId)
        body.append("sectionId", String(formData.sectionId));

      // [id]/+page.svelte — handleUpdate
      const response = await fetch("?/save", { method: "POST", body }); // ← was "?/default"
      const result: ActionResult = deserialize(await response.text()); // ✅ not .json()

      if (result.type === "failure") {
        submitError =
          (result.data as any)?.message ?? "Failed to update enrollment";
        return;
      }
      if (result.type === "error") {
        submitError = result.error?.message ?? "An unexpected error occurred";
        return;
      }

      // Sync store so list page re-renders immediately
      if (result.type === "success" && result.data?.enrollment) {
        const updated = result.data.enrollment as EnrollmentResponse;
        isNew
          ? enrollmentsStore.add(updated)
          : enrollmentsStore.update(updated.enrollmentId, updated);
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
  <title>{isNew ? "New" : "Edit"} Registration — EMS</title>
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
      Back to Enrollments
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
            d="M16.862 4.487l1.687-1.688a1.875 1.875 0 112.652 2.652L10.582 16.07a4.5 4.5 0 01-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 011.13-1.897l8.932-8.931zm0 0L19.5 7.125M18 14v4.75A2.25 2.25 0 0115.75 21H5.25A2.25 2.25 0 013 18.75V8.25A2.25 2.25 0 015.25 6H10"
          />
        </svg>
      </div>
      <div>
        <p class="eyebrow">Academic Registration</p>
        <h1>{isNew ? "New Enrollment" : "Edit Enrollment"}</h1>
        <p class="header-sub">
          {isNew
            ? "Select a section to secure your place in the upcoming academic cycle."
            : "Modify an existing course enrollment. Changes are reflected immediately."}
        </p>
        {#if !isNew && data.enrollment}
          <div class="id-badge">
            <div class="id-badge-dot"></div>
            ID: {enrollmentId}
          </div>
        {/if}
      </div>
    </div>

    <!-- ── Live section preview (fetched from server) ─────── -->
    {#if sectionFetching}
      <div class="section-status">
        <div class="preview-spinner"></div>
        <span>Loading section details…</span>
      </div>
    {:else if sectionFetchErr}
      <div class="section-status section-status--error">
        <svg
          width="13"
          height="13"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
        >
          <circle cx="12" cy="12" r="10" />
          <path stroke-linecap="round" d="M12 8v4m0 4h.01" />
        </svg>
        {sectionFetchErr}
      </div>
    {:else if selectedSection}
      {@const seats = selectedSection.maxSeats - selectedSection.enrolledCount}
      <div class="section-panel" class:section-panel--full={seats <= 0}>
        <div class="sp-top">
          <div>
            <div class="sp-codes">
              <span class="sp-code">{selectedSection.course?.code}</span>
              <span class="sp-sec">§ {selectedSection.sectionCode}</span>
            </div>
            <p class="sp-title">{selectedSection.course?.title}</p>
          </div>
          <div
            class="sp-seats"
            class:seats-ok={seats > 3}
            class:seats-warn={seats > 0 && seats <= 3}
            class:seats-full={seats <= 0}
          >
            <span class="seats-num">{seats <= 0 ? "Full" : seats}</span>
            <span class="seats-lbl">seats left</span>
          </div>
        </div>
        <div class="sp-chips">
          {#if selectedSection.facultyName}
            <span class="sp-chip">👤 {selectedSection.facultyName}</span>
          {/if}
          {#if selectedSection.room}
            <span class="sp-chip"
              >📍 {selectedSection.room.building}
              {selectedSection.room.roomNumber}</span
            >
          {/if}
          <span class="sp-chip"
            >🪑 {selectedSection.enrolledCount} / {selectedSection.maxSeats}</span
          >
        </div>
        {#if seats <= 0}
          <div class="sp-warning">
            ⚠ Section is full — enrolling will place you on the waitlist.
          </div>
        {/if}
      </div>
    {/if}

    <!-- ── Form ──────────────────────────────────────────── -->
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
        enrollment={data.enrollment}
        onSubmit={handleUpdate}
        onSectionChange={handleSectionGet}
        {isLoading}
        mode={isNew ? "new" : "edit"}
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
    margin-bottom: 2rem;
    border-bottom: 1px solid rgba(255, 255, 255, 0.06);
    animation: fadeUp 0.5s 0.05s cubic-bezier(0.22, 1, 0.36, 1) both;
  }

  .header-icon {
    width: 58px;
    height: 58px;
    background: linear-gradient(135deg, #1a6b4a, #0d3d2b);
    border-radius: 18px;
    flex-shrink: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow:
      0 8px 28px rgba(26, 107, 74, 0.4),
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
    color: rgba(52, 199, 130, 0.6);
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

  .id-badge {
    display: inline-flex;
    align-items: center;
    gap: 0.45rem;
    margin-top: 0.75rem;
    background: rgba(52, 199, 130, 0.08);
    border: 1px solid rgba(52, 199, 130, 0.18);
    border-radius: 8px;
    padding: 0.3rem 0.75rem;
    font-size: 0.7rem;
    font-weight: 600;
    letter-spacing: 0.1em;
    text-transform: uppercase;
    color: rgba(52, 199, 130, 0.7);
  }

  .id-badge-dot {
    width: 5px;
    height: 5px;
    background: rgba(52, 199, 130, 0.6);
    border-radius: 50%;
  }

  /* ── Section status / preview ─────────── */
  .section-status {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    background: rgba(59, 111, 212, 0.05);
    border: 1px solid rgba(59, 111, 212, 0.15);
    border-radius: 14px;
    padding: 0.9rem 1.1rem;
    margin-bottom: 1.25rem;
    font-size: 0.78rem;
    font-weight: 500;
    color: rgba(140, 170, 210, 0.5);
    animation: fadeUp 0.3s cubic-bezier(0.22, 1, 0.36, 1) both;
  }

  .section-status--error {
    background: rgba(248, 113, 113, 0.06);
    border-color: rgba(248, 113, 113, 0.2);
    color: rgba(252, 165, 165, 0.75);
  }

  .preview-spinner {
    width: 16px;
    height: 16px;
    flex-shrink: 0;
    border: 2px solid rgba(59, 111, 212, 0.2);
    border-top-color: rgba(100, 160, 255, 0.7);
    border-radius: 50%;
    animation: spin 0.65s linear infinite;
  }

  .section-panel {
    background: rgba(59, 111, 212, 0.05);
    border: 1px solid rgba(59, 111, 212, 0.18);
    border-radius: 18px;
    margin-bottom: 1.5rem;
    overflow: hidden;
    animation: fadeUp 0.3s cubic-bezier(0.22, 1, 0.36, 1) both;
  }

  .section-panel--full {
    border-color: rgba(251, 191, 36, 0.25);
    background: rgba(251, 191, 36, 0.04);
  }

  .sp-top {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 1rem;
    padding: 1.25rem 1.5rem 1rem;
    border-bottom: 1px solid rgba(59, 111, 212, 0.1);
  }

  .sp-codes {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    margin-bottom: 0.35rem;
  }

  .sp-code {
    font-size: 0.6rem;
    font-weight: 700;
    letter-spacing: 0.12em;
    text-transform: uppercase;
    color: rgba(100, 160, 255, 0.85);
    background: rgba(59, 111, 212, 0.15);
    border: 1px solid rgba(59, 111, 212, 0.25);
    border-radius: 5px;
    padding: 0.15rem 0.5rem;
  }

  .sp-sec {
    font-size: 0.6rem;
    font-weight: 600;
    color: rgba(140, 170, 210, 0.5);
  }

  .sp-title {
    font-family: "DM Serif Display", serif;
    font-style: italic;
    font-size: 1.1rem;
    color: #fff;
    margin: 0;
    line-height: 1.2;
  }

  .sp-seats {
    text-align: right;
    flex-shrink: 0;
  }

  .seats-num {
    display: block;
    font-family: "DM Serif Display", serif;
    font-size: 1.8rem;
    line-height: 1;
  }

  .seats-lbl {
    font-size: 0.58rem;
    font-weight: 700;
    letter-spacing: 0.15em;
    text-transform: uppercase;
    opacity: 0.5;
  }

  .seats-ok .seats-num {
    color: #34c782;
  }
  .seats-warn .seats-num {
    color: #fbbf24;
  }
  .seats-full .seats-num {
    color: #f87171;
  }

  .sp-chips {
    display: flex;
    flex-wrap: wrap;
    gap: 0.5rem;
    padding: 0.9rem 1.5rem;
  }

  .sp-chip {
    display: inline-flex;
    align-items: center;
    gap: 0.4rem;
    background: rgba(255, 255, 255, 0.04);
    border: 1px solid rgba(255, 255, 255, 0.07);
    border-radius: 7px;
    padding: 0.3rem 0.7rem;
    font-size: 0.68rem;
    color: rgba(160, 185, 220, 0.6);
  }

  .sp-warning {
    margin: 0 1.25rem 1rem;
    padding: 0.65rem 0.9rem;
    background: rgba(251, 191, 36, 0.08);
    border: 1px solid rgba(251, 191, 36, 0.2);
    border-radius: 9px;
    font-size: 0.72rem;
    font-weight: 600;
    color: rgba(251, 191, 36, 0.8);
  }

  /* ── Submit error ─────────────────────── */
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
  @keyframes spin {
    to {
      transform: rotate(360deg);
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
