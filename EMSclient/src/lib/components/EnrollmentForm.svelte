<!-- src/lib/components/EnrollmentForm.svelte -->
<script lang="ts">
  import { superForm } from "sveltekit-superforms";
  import { yup } from "sveltekit-superforms/adapters";
  import {
    enrollmentSchema,
    type Status,
  } from "$lib/schemas/enrollment.schema";

  import type { EnrollmentResponse } from "$lib/types/enrollment";
  import type { SectionResponse } from "$lib/server/section";

  interface Props {
    enrollment?: EnrollmentResponse | null; 
    availableSections?: SectionResponse[]; 
    onSubmit?: (data: { sectionId: number; status: Status }) => void;
    onSectionChange?: (sectionId: number) => void;
    isLoading?: boolean;
    mode?: "new" | "edit";
  }

  let {
    enrollment,
    availableSections = [],
    onSubmit,
    onSectionChange,
    isLoading = false,
    mode = "new",
  }: Props = $props();

  const initialSectionId = enrollment?.sectionId ?? undefined;
  const initialStatus: Status = (enrollment?.status as Status) ?? "";

  // ✅ superForm declared BEFORE $derived that uses $form
  const { form, errors, enhance } = superForm(
    { sectionId: initialSectionId, status: initialStatus },
    {
      validators: yup(enrollmentSchema),
      SPA: true,
      onUpdate: ({ form }) => {
        if (form.valid && onSubmit) {
          onSubmit(form.data as { sectionId: number; status: Status });
        }
      },
    },
  );

  // ✅ $derived now safely after superForm (uses $form)
  const selectedSection = $derived(
    availableSections.find((s) => s.sectionId === Number($form.sectionId)) ??
      null,
  );

  // Sync form when enrollment prop changes (SSR data arrives after mount)
  $effect(() => {
    if (enrollment) {
      $form.sectionId = enrollment.section.sectionId;
      $form.status = enrollment.status as Status;
    }
  });

  // ✅ Notifies parent page to fire GET /api/sections/{id}
  const handleSectionChange = (e: Event) => {
    const id = Number((e.currentTarget as HTMLSelectElement).value);
    if (id && onSectionChange)
         onSectionChange(id);
  };

  const statusMeta: Record<
    Status,
    { label: string; color: string; dot: string }
  > = {
    PENDING: {
      label: "Pending",
      color: "rgba(251,191,36,0.15)",
      dot: "#fbbf24",
    },
    ENROLLED: {
      label: "Enrolled",
      color: "rgba(52,199,130,0.15)",
      dot: "#34c782",
    },
    DROPPED: {
      label: "Dropped",
      color: "rgba(248,113,113,0.15)",
      dot: "#f87171",
    },
  };

  const statuses = Object.entries(statusMeta) as [
    Status,
    { label: string; color: string; dot: string },
  ][];
</script>

<div class="form-shell">
  <!-- ── Header ─────────────────────────────────────────── -->
  <div class="form-header">
    <div class="form-header-inner">
      <div>
        <p class="form-eyebrow">Academic Registration</p>
        <h2 class="form-title">
          {mode === "edit" ? "Modify Registration" : "Course Enrollment"}
        </h2>
        <p class="form-subtitle">
          {mode === "edit"
            ? "Update the section assignment or enrollment status for this record."
            : "Select a section to secure your place in the upcoming academic cycle."}
        </p>
      </div>
      <div class="mode-badge">
        <span class="mode-badge-dot"></span>
        {mode === "edit" ? "Editing" : "New"}
      </div>
    </div>
  </div>

  <form use:enhance>
    <div class="form-body">
      <!-- ── Section select ──────────────────────────────── -->
      <div class="field-group">
        <label for="sectionId" class="field-label">
          Academic Section <span class="req">*</span>
        </label>
        <div class="select-wrap">
        <!-- showw section available -->
          <select
            id="sectionId"
            name="sectionId"
            bind:value={$form.sectionId}
            onchange={handleSectionChange}
            required 
            aria-label="Select course section"
            disabled={isLoading || availableSections.length === 0}
          >
            <option value="">Select a section…</option>
            {#each availableSections as section}
              <option value={section.sectionId}>
                {section.course?.code} — {section.course?.title} · {section.sectionCode}
              </option>
            {/each}
          </select>
          
          <span class="select-caret">
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
                d="M19 9l-7 7-7-7"
              />
            </svg>
          </span>
        </div>

        {#if $errors.sectionId?.[0]}
          <div class="field-error">{$errors.sectionId[0]}</div>
        {/if}
        {#if availableSections.length === 0} 
          <div class="empty-hint">
            No sections available in this catalog period
          </div>
        {/if}
      </div>

      <!-- ── Status pills (edit only) ────────────────────── -->
      {#if mode === "edit"}
        <div class="field-group">
          <!-- svelte-ignore a11y_label_has_associated_control -->
          <label class="field-label"
            >Enrollment Status <span class="req">*</span></label
          >
          <div class="status-pills">
            {#each statuses as [val, meta]}
              <button
                type="button"
                class="status-pill"
                class:active={$form.status === val}
                style={$form.status === val
                  ? `background:${meta.color}; border-color:${meta.dot}33; color:${meta.dot};`
                  : ""}
                onclick={() => ($form.status = val)}
              >
                <!-- ✅ onclick not on:click (Svelte 5 runes) -->
                <span class="status-pill-dot" style={`background:${meta.dot};`}
                ></span>
                {meta.label}
              </button>
            {/each}
          </div>
          <input type="hidden" name="status" bind:value={$form.status} />
        </div>
      {/if}

      <!-- ── Section preview (instant, from availableSections) ── -->
      {#if selectedSection}
        {@const seatsLeft =
          selectedSection.maxSeats - selectedSection.enrolledCount}
        {@const seatClass =
          seatsLeft <= 0
            ? "seats-full"
            : seatsLeft <= 3
              ? "seats-warn"
              : "seats-ok"}

        <div class="section-preview">
          <div class="preview-top">
            <div>
              <div class="preview-code-row">
                <span class="preview-code">{selectedSection.course?.code}</span>
                <span class="preview-section-num"
                  >Section {selectedSection.sectionCode}</span
                >
              </div>
              <h3 class="preview-course-name">
                {selectedSection.course?.title}
              </h3>
            </div>
            <div class="preview-seats">
              <p class="preview-seats-label">Seats Left</p>
              <p class="preview-seats-count {seatClass}">
                {seatsLeft <= 0 ? "Full" : seatsLeft}
              </p>
            </div>
          </div>

          <div class="preview-meta">
            <div class="preview-meta-cell">
              <div class="meta-icon">
                <svg
                  width="16"
                  height="16"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
                  />
                </svg>
              </div>
              <div>
                <p class="meta-label">Instructor</p>
                <p class="meta-value">{selectedSection.facultyName || "TBA"}</p>
              </div>
            </div>
            <div class="preview-meta-cell">
              <div class="meta-icon">
                <svg
                  width="16"
                  height="16"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M17.657 16.657L13.414 20.9a2 2 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0zM15 11a3 3 0 11-6 0 3 3 0 016 0z"
                  />
                </svg>
              </div>
              <div>
                <p class="meta-label">Room</p>
                <p class="meta-value">
                  {selectedSection.room
                    ? `${selectedSection.room.building} ${selectedSection.room.roomNumber}`
                    : "Online / Hybrid"}
                </p>
              </div>
            </div>
            <div class="preview-meta-cell">
              <div class="meta-icon">
                <svg
                  width="16"
                  height="16"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z"
                  />
                </svg>
              </div>
              <div>
                <p class="meta-label">Enrolled / Capacity</p>
                <p class="meta-value">
                  {selectedSection.enrolledCount} / {selectedSection.maxSeats}
                </p>
              </div>
            </div>
            <div class="preview-meta-cell">
              <div class="meta-icon">
                <svg
                  width="16"
                  height="16"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"
                  />
                </svg>
              </div>
              <div>
                <p class="meta-label">Waitlist</p>
                <p class="meta-value">
                  {seatsLeft <= 0 ? "Active" : "Not Required"}
                </p>
              </div>
            </div>
          </div>

          {#if seatsLeft <= 0}
            <div class="full-warning">
              <svg
                width="15"
                height="15"
                viewBox="0 0 24 24"
                fill="none"
                stroke="rgba(251,191,36,0.8)"
                stroke-width="2"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  d="M12 9v2m0 4h.01M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z"
                />
              </svg>
              <p>
                This section is full — enrolling will activate the waitlist
                automatically.
              </p>
            </div>
          {/if}
        </div>
      {/if}
    </div>

    <div class="divider"></div>

    <!-- ── Footer ─────────────────────────────────────────── -->
    <div class="form-footer">
      <button
        type="button"
        class="btn-discard"
        onclick={() => window.history.back()}
      >
        <!-- ✅ onclick not on:click -->
        Discard
      </button>
      <button
        type="submit"
        class="btn-submit"
        disabled={isLoading || !$form.sectionId}
      >
        {#if isLoading}
          <span class="btn-spinner"></span>
          {mode === "edit" ? "Saving…" : "Enrolling…"}
        {:else if mode === "edit"}
          <svg
            width="13"
            height="13"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2.5"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M5 13l4 4L19 7"
            />
          </svg>
          Apply Changes
        {:else}
          <svg
            width="13"
            height="13"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2.5"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M12 4.5v15m7.5-7.5h-15"
            />
          </svg>
          Confirm Enrollment
        {/if}
      </button>
    </div>
  </form>
</div>

<style>
  :root {
    --panel-bg: rgba(10, 22, 45, 0.85);
    --panel-border: rgba(255, 255, 255, 0.07);
    --input-bg: rgba(255, 255, 255, 0.04);
    --input-border: rgba(255, 255, 255, 0.08);
    --text-primary: #ffffff;
    --text-muted: rgba(140, 170, 210, 0.55);
    --text-label: rgba(80, 140, 255, 0.65);
    --font-serif: "DM Serif Display", serif;
    --font-sans: "DM Sans", sans-serif;
  }

  .form-shell {
    font-family: var(--font-sans);
    background: var(--panel-bg);
    border: 1px solid var(--panel-border);
    border-radius: 28px;
    overflow: hidden;
    box-shadow:
      0 24px 64px rgba(0, 0, 0, 0.5),
      0 1px 0 rgba(255, 255, 255, 0.06) inset;
    backdrop-filter: blur(20px);
  }

  .form-header {
    position: relative;
    padding: 2.5rem 2.5rem 2rem;
    border-bottom: 1px solid var(--panel-border);
    overflow: hidden;
  }

  .form-header::before {
    content: "";
    position: absolute;
    inset: 0;
    background: radial-gradient(
        ellipse 80% 120% at -10% 50%,
        rgba(30, 77, 183, 0.22) 0%,
        transparent 65%
      ),
      radial-gradient(
        ellipse 50% 80% at 110% 50%,
        rgba(14, 60, 120, 0.15) 0%,
        transparent 65%
      );
    pointer-events: none;
  }

  .form-header-inner {
    position: relative;
    z-index: 1;
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 1rem;
  }

  .form-eyebrow {
    font-size: 0.6rem;
    font-weight: 700;
    letter-spacing: 0.25em;
    text-transform: uppercase;
    color: var(--text-label);
    margin-bottom: 0.55rem;
  }

  .form-title {
    font-family: var(--font-serif);
    font-style: italic;
    font-size: clamp(1.5rem, 3vw, 2rem);
    font-weight: 400;
    color: var(--text-primary);
    line-height: 1.05;
    letter-spacing: -0.02em;
    margin: 0;
  }

  .form-subtitle {
    font-size: 0.78rem;
    color: var(--text-muted);
    margin: 0.5rem 0 0;
    line-height: 1.6;
  }

  .mode-badge {
    display: inline-flex;
    align-items: center;
    gap: 0.4rem;
    background: rgba(59, 111, 212, 0.12);
    border: 1px solid rgba(59, 111, 212, 0.22);
    border-radius: 10px;
    padding: 0.4rem 0.85rem;
    font-size: 0.62rem;
    font-weight: 700;
    letter-spacing: 0.12em;
    text-transform: uppercase;
    color: rgba(100, 160, 255, 0.8);
    white-space: nowrap;
    flex-shrink: 0;
  }

  .mode-badge-dot {
    width: 5px;
    height: 5px;
    border-radius: 50%;
    background: rgba(100, 160, 255, 0.7);
    animation: pulse-dot 2s ease-in-out infinite;
  }

  .form-body {
    padding: 2.5rem;
    display: flex;
    flex-direction: column;
    gap: 2rem;
  }

  .field-group {
    display: flex;
    flex-direction: column;
    gap: 0.6rem;
  }

  .field-label {
    font-size: 0.6rem;
    font-weight: 700;
    letter-spacing: 0.22em;
    text-transform: uppercase;
    color: var(--text-label);
  }

  .field-label .req {
    color: rgba(248, 113, 113, 0.7);
    margin-left: 0.25rem;
  }

  .select-wrap {
    position: relative;
  }

  .select-wrap select {
    width: 100%;
    appearance: none;
    background: var(--input-bg);
    border: 1px solid var(--input-border);
    border-radius: 14px;
    padding: 1rem 3rem 1rem 1.25rem;
    font-family: var(--font-sans);
    font-size: 0.85rem;
    font-weight: 500;
    color: var(--text-primary);
    cursor: pointer;
    outline: none;
    transition:
      border-color 0.2s,
      background 0.2s,
      box-shadow 0.2s;
  }

  .select-wrap select:focus {
    border-color: rgba(59, 111, 212, 0.6);
    background: rgba(59, 111, 212, 0.06);
    box-shadow: 0 0 0 3px rgba(59, 111, 212, 0.12);
  }

  .select-wrap select:disabled {
    opacity: 0.4;
    cursor: not-allowed;
  }
  .select-wrap select option {
    background: #0c1c35;
    color: #ffffff;
  }

  .select-caret {
    position: absolute;
    right: 1rem;
    top: 50%;
    transform: translateY(-50%);
    pointer-events: none;
    color: rgba(140, 170, 210, 0.4);
    transition: color 0.2s;
  }

  .select-wrap:focus-within .select-caret {
    color: rgba(100, 160, 255, 0.7);
  }

  .status-pills {
    display: flex;
    gap: 0.6rem;
    flex-wrap: wrap;
  }

  .status-pill {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.6rem 1.1rem;
    border-radius: 10px;
    font-size: 0.7rem;
    font-weight: 700;
    letter-spacing: 0.1em;
    text-transform: uppercase;
    border: 1px solid transparent;
    cursor: pointer;
    transition: all 0.18s;
    background: var(--input-bg);
    border-color: var(--input-border);
    color: var(--text-muted);
    font-family: var(--font-sans);
  }

  .status-pill:hover {
    border-color: rgba(255, 255, 255, 0.15);
    color: rgba(200, 220, 255, 0.7);
  }
  .status-pill.active {
    color: var(--text-primary);
  }
  .status-pill-dot {
    width: 6px;
    height: 6px;
    border-radius: 50%;
    flex-shrink: 0;
  }

  .field-error {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    font-size: 0.7rem;
    font-weight: 600;
    color: rgba(248, 113, 113, 0.8);
    animation: fadeUp 0.2s ease both;
  }

  .empty-hint {
    display: flex;
    align-items: center;
    gap: 0.6rem;
    padding: 0.75rem 1rem;
    background: rgba(255, 255, 255, 0.02);
    border: 1px dashed rgba(255, 255, 255, 0.06);
    border-radius: 12px;
    font-size: 0.72rem;
    color: rgba(140, 170, 210, 0.35);
  }

  /* ── Section preview ──────────────────── */
  .section-preview {
    background: rgba(59, 111, 212, 0.05);
    border: 1px solid rgba(59, 111, 212, 0.15);
    border-radius: 20px;
    overflow: hidden;
    animation: fadeUp 0.35s cubic-bezier(0.22, 1, 0.36, 1) both;
  }

  .preview-top {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 1rem;
    padding: 1.5rem 1.75rem 1.25rem;
    border-bottom: 1px solid rgba(59, 111, 212, 0.1);
  }

  .preview-code-row {
    display: flex;
    align-items: center;
    gap: 0.6rem;
    margin-bottom: 0.5rem;
  }

  .preview-code {
    background: rgba(59, 111, 212, 0.2);
    border: 1px solid rgba(59, 111, 212, 0.3);
    border-radius: 6px;
    padding: 0.2rem 0.6rem;
    font-size: 0.62rem;
    font-weight: 700;
    letter-spacing: 0.12em;
    text-transform: uppercase;
    color: rgba(120, 170, 255, 0.9);
  }

  .preview-section-num {
    font-size: 0.62rem;
    font-weight: 600;
    color: var(--text-muted);
    letter-spacing: 0.08em;
  }

  .preview-course-name {
    font-family: var(--font-serif);
    font-style: italic;
    font-size: 1.35rem;
    color: var(--text-primary);
    line-height: 1.1;
    letter-spacing: -0.01em;
    margin: 0;
  }

  .preview-seats {
    text-align: right;
    flex-shrink: 0;
  }
  .preview-seats-label {
    font-size: 0.58rem;
    font-weight: 700;
    letter-spacing: 0.18em;
    text-transform: uppercase;
    color: var(--text-muted);
    margin-bottom: 0.25rem;
  }
  .preview-seats-count {
    font-family: var(--font-serif);
    font-size: 1.8rem;
    line-height: 1;
    font-weight: 400;
  }

  .seats-ok {
    color: #34c782;
  }
  .seats-warn {
    color: #fbbf24;
  }
  .seats-full {
    color: #f87171;
  }

  .preview-meta {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 0;
  }

  .preview-meta-cell {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    padding: 1rem 1.75rem;
    border-right: 1px solid rgba(255, 255, 255, 0.04);
    border-bottom: 1px solid rgba(255, 255, 255, 0.04);
  }

  .preview-meta-cell:nth-child(even) {
    border-right: none;
  }
  .preview-meta-cell:nth-last-child(-n + 2) {
    border-bottom: none;
  }

  .meta-icon {
    width: 34px;
    height: 34px;
    border-radius: 10px;
    flex-shrink: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(59, 111, 212, 0.1);
    color: rgba(100, 160, 255, 0.7);
  }

  .meta-label {
    font-size: 0.58rem;
    font-weight: 700;
    letter-spacing: 0.16em;
    text-transform: uppercase;
    color: var(--text-muted);
    margin-bottom: 0.2rem;
  }
  .meta-value {
    font-size: 0.8rem;
    font-weight: 600;
    color: var(--text-primary);
    line-height: 1.2;
  }

  .full-warning {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    background: rgba(251, 191, 36, 0.08);
    border: 1px solid rgba(251, 191, 36, 0.2);
    border-radius: 12px;
    padding: 0.9rem 1.1rem;
    margin: 0 1.75rem 1.5rem;
    animation: fadeUp 0.3s ease both;
  }

  .full-warning p {
    font-size: 0.75rem;
    font-weight: 600;
    color: rgba(251, 191, 36, 0.85);
    margin: 0;
  }

  /* ── Footer ───────────────────────────── */
  .form-footer {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    gap: 0.75rem;
    padding: 1.5rem 2.5rem;
    border-top: 1px solid var(--panel-border);
  }

  .btn-discard {
    display: inline-flex;
    align-items: center;
    gap: 0.4rem;
    padding: 0.75rem 1.4rem;
    background: transparent;
    border: 1px solid rgba(255, 255, 255, 0.08);
    border-radius: 12px;
    font-family: var(--font-sans);
    font-size: 0.75rem;
    font-weight: 600;
    letter-spacing: 0.08em;
    text-transform: uppercase;
    color: var(--text-muted);
    cursor: pointer;
    transition: all 0.2s;
  }

  .btn-discard:hover {
    border-color: rgba(255, 255, 255, 0.15);
    color: rgba(200, 220, 255, 0.7);
  }

  .btn-submit {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.8rem 1.75rem;
    background: linear-gradient(135deg, #1e4db7, #0d2d7a);
    border: 1px solid rgba(59, 111, 212, 0.35);
    border-radius: 12px;
    font-family: var(--font-sans);
    font-size: 0.75rem;
    font-weight: 700;
    letter-spacing: 0.1em;
    text-transform: uppercase;
    color: #fff;
    cursor: pointer;
    transition: all 0.2s;
    box-shadow:
      0 4px 16px rgba(30, 77, 183, 0.35),
      inset 0 1px 0 rgba(255, 255, 255, 0.12);
  }

  .btn-submit:hover:not(:disabled) {
    background: linear-gradient(135deg, #2457cc, #1339a0);
    transform: translateY(-1px);
    box-shadow:
      0 6px 24px rgba(30, 77, 183, 0.45),
      inset 0 1px 0 rgba(255, 255, 255, 0.15);
  }

  .btn-submit:disabled {
    opacity: 0.4;
    cursor: not-allowed;
    transform: none;
  }

  .btn-spinner {
    width: 13px;
    height: 13px;
    border: 2px solid rgba(255, 255, 255, 0.3);
    border-top-color: white;
    border-radius: 50%;
    animation: spin 0.65s linear infinite;
  }

  .divider {
    height: 1px;
    background: var(--panel-border);
    margin: 0;
  }

  @keyframes spin {
    to {
      transform: rotate(360deg);
    }
  }
  @keyframes pulse-dot {
    0%,
    100% {
      opacity: 1;
      transform: scale(1);
    }
    50% {
      opacity: 0.4;
      transform: scale(0.7);
    }
  }
  @keyframes fadeUp {
    from {
      opacity: 0;
      transform: translateY(8px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }
</style>
