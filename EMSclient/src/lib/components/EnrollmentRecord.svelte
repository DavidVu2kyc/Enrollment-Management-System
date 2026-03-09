<script lang="ts">
    import { fade, fly, scale, slide } from "svelte/transition";
    import { cubicOut } from "svelte/easing";
    import type { Enrollment } from "$lib/types/enrollment";

    interface Props {
      enrollment: Enrollment;
      onDelete?: (enrollmentId: number) => void;
      onEnroll?: (enrollmentId: number) => void;
      onShow?: (enrollmentId: number) => void;
    }

    let { enrollment, onDelete, onEnroll, onShow }: Props = $props();
    let showDeleteModal = $state(false);
    let isDeleting = $state(false);
    let isConfirming = $state(false);
    let hovered = $state(false);

    // Disappear + toast state
    let isDisappearing = $state(false);
    let showToast = $state(false);
    let toastMessage = $state("");
    let toastTimeout: ReturnType<typeof setTimeout> | null = null;

    const isEnrolled = $derived(enrollment.status === "ENROLLED");

    const handleDeleteClick = () => { showDeleteModal = true; };

    const handleConfirmDelete = async () => {
      isDeleting = true;
      try {
        if (onDelete) await onDelete(enrollment.enrollmentId);
      } finally {
        isDeleting = false;
        showDeleteModal = false;
      }
    };

    const triggerToast = (msg: string) => {
      toastMessage = msg;
      showToast = true;
      if (toastTimeout) clearTimeout(toastTimeout);
      toastTimeout = setTimeout(() => { showToast = false; }, 3200);
    };

    const handleEnroll = async () => {
      isConfirming = true;
      isDisappearing = true;

      // Wait for disappear animation
      await new Promise(r => setTimeout(r, 480));

      try {
        if (onEnroll) await onEnroll(enrollment.enrollmentId);
      } finally {
        // Brief pause before reappear
        await new Promise(r => setTimeout(r, 320));
        isDisappearing = false;
        isConfirming = false;

        // Show success toast
        const title = enrollment.section?.course?.title ?? "Course";
        triggerToast(`✓ ${title} confirmed!`);
      }
    };

    const credits = enrollment.section?.course?.units ?? enrollment.units ?? 0;
    const faculty = enrollment.section?.facultyName ?? null;
    const schedule = enrollment.section?.schedule ?? null;
    const room = enrollment.section?.room ?? null;

    const formattedSchedule = $derived.by(() => {
      if (!schedule) return null;
      const start = schedule.startTime?.slice(0, 5) ?? "";
      const end = schedule.endTime?.slice(0, 5) ?? "";
      return `${schedule.dayOfWeek} ${start} - ${end}`.trim();
    });
  </script>

  <!-- ── TOAST ── -->
  {#if showToast}
    <div
      class="toast"
      in:fly={{ y: 16, duration: 300, easing: cubicOut }}
      out:fly={{ y: -10, duration: 220 }}
      role="status"
      aria-live="polite"
    >
      <div class="toast-icon">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
          <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7"/>
        </svg>
      </div>
      <span class="toast-msg">{toastMessage}</span>
      <button class="toast-close" onclick={() => showToast = false} aria-label="Dismiss">
        <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
          <path stroke-linecap="round" d="M6 18L18 6M6 6l12 12"/>
        </svg>
      </button>
    </div>
  {/if}

  <!-- svelte-ignore a11y_no_static_element_interactions -->
  <div
    class="card"
    class:enrolled={isEnrolled}
    class:pending={!isEnrolled}
    class:is-hovered={hovered}
    class:is-disappearing={isDisappearing}
    role="article"
    aria-label="{enrollment.section?.course?.title} enrollment record"
    onmouseenter={() => hovered = true}
    onmouseleave={() => hovered = false}
    in:fly={{ y: 12, duration: 380, easing: cubicOut }}
  >
    <!-- Status stripe -->
    <div class="status-stripe" class:stripe-enrolled={isEnrolled} class:stripe-pending={!isEnrolled}></div>

    <!-- Card body -->
    <div class="card-inner">

      <!-- Top row: course info + status badge -->
      <div class="card-top">
        <!-- svelte-ignore a11y_click_events_have_key_events -->
        <svelte:element this={onShow ? 'button' : 'div'}
          class="course-info"
          class:clickable={!!onShow}
          onclick={() => onShow && onShow(enrollment.enrollmentId)}
          type={onShow ? "button" : undefined}
        >
        <div class="course-meta">
            <span class="course-code">{enrollment.section?.course?.code ?? "—"}</span>
            <span class="meta-sep">·</span>
            <span class="section-code">§ {enrollment.section?.sectionCode ?? "—"}</span>
            {#if credits}
              <span class="meta-sep">·</span>
              <span class="credits">{credits} {credits === 1 ? "unit" : "units"}</span>
            {/if}
          </div>

          <h3 class="course-title">
            {enrollment.section?.course?.title ?? "General Subject"}
          </h3>

          {#if enrollment.section?.course?.description}
            <p class="course-desc">{enrollment.section.course.description}</p>
          {/if}
        </svelte:element>

        <!-- Status badge -->
        <div class="badge-wrap">
          {#if isEnrolled}
            <span class="badge badge-enrolled" in:scale={{ duration: 250 }}>
              <span class="badge-dot"></span>
              Confirmed
            </span>
          {:else}
            <span class="badge badge-pending" in:scale={{ duration: 250 }}>
              <span class="badge-dot"></span>
              Pending
            </span>
          {/if}
        </div>
      </div>

      <!-- Details row: instructor / schedule / room -->
      {#if faculty || formattedSchedule || room}
        <div class="details-row">
          {#if faculty}
            <div class="detail-chip">
              <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2M12 11a4 4 0 100-8 4 4 0 000 8z"/>
              </svg>
              <span>{faculty}</span>
            </div>
          {/if}
          {#if formattedSchedule}
            <div class="detail-chip">
              <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="3" y="4" width="18" height="18" rx="2"/><path stroke-linecap="round" d="M16 2v4M8 2v4M3 10h18"/>
              </svg>
              <span>{formattedSchedule}</span>
            </div>
          {/if}
          {#if room}
            <div class="detail-chip">
              <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"/>
              </svg>
              <span>{room.building} {room.roomNumber}</span>
            </div>
          {/if}
        </div>
      {/if}

      <!-- Actions -->
      <div class="actions">
        {#if !isEnrolled}
          <button
            class="action-btn btn-confirm"
            onclick={handleEnroll}
            disabled={isConfirming}
            aria-busy={isConfirming}
          >
            {#if isConfirming}
              <span class="btn-spinner"></span>
              Confirming…
            {:else}
              <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7"/>
              </svg>
              Confirm Registration
            {/if}
          </button>
        {/if}

        <!-- integrate api drop course -->
        <button
          class="action-btn btn-drop"
          class:btn-drop-solo={isEnrolled}
          onclick={handleDeleteClick}
          disabled={isDeleting}
          aria-label="Drop {enrollment.section?.course?.title}"
        >
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/>
          </svg>
          Drop Course
        </button>
      </div>
    </div>
  </div>

  <!-- ── WITHDRAWAL MODAL ── -->
  {#if showDeleteModal}
    <div
      class="modal-backdrop"
      in:fade={{ duration: 180 }}
      out:fade={{ duration: 150 }}
      role="dialog"
      aria-modal="true"
      aria-labelledby="modal-title"
    >
      <div
        class="modal"
        in:fly={{ y: 20, duration: 280, easing: cubicOut }}
        out:fly={{ y: 10, duration: 180 }}
      >
        <!-- Modal header -->
        <div class="modal-header">
          <div class="modal-icon">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v4m0 4h.01M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z"/>
            </svg>
          </div>
          <div>
            <h2 id="modal-title" class="modal-title">Drop Course?</h2>
            <p class="modal-subtitle">This action cannot be undone</p>
          </div>
        </div>

        <!-- Course summary -->
        <div class="modal-course">
          <div class="modal-course-code">{enrollment.section?.course?.code} · § {enrollment.section?.sectionCode}</div>
          <div class="modal-course-name">{enrollment.section?.course?.title}</div>
        </div>

        <!-- Warning -->
        <div class="modal-warning">
          <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="flex-shrink:0; margin-top:1px">
            <circle cx="12" cy="12" r="10"/><path stroke-linecap="round" d="M12 8v4m0 4h.01"/>
          </svg>
          <p>Dropping this course will remove your seat reservation and may affect your academic standing.</p>
        </div>

        <!-- Actions -->
        <div class="modal-actions">
          <button
            class="modal-btn modal-cancel"
            onclick={() => showDeleteModal = false}
            disabled={isDeleting}
          >
            Keep Course
          </button>
          <button
            class="modal-btn modal-confirm"
            onclick={handleConfirmDelete}
            disabled={isDeleting}
            aria-busy={isDeleting}
          >
            {#if isDeleting}
              <span class="btn-spinner spinner-red"></span>
              Withdrawing…
            {:else}
              Drop Course
            {/if}
          </button>
        </div>
      </div>
    </div>
  {/if}

  <style>
    /* ── TOKENS ── */
    :root {
      --c-surface:    rgba(10, 20, 44, 0.92);
      --c-border:     rgba(255, 255, 255, 0.07);
      --c-border-hi:  rgba(255, 255, 255, 0.12);
      --c-text-1:     #eef2ff;
      --c-text-2:     rgba(160, 190, 235, 0.65);
      --c-text-3:     rgba(100, 140, 205, 0.45);
      --c-blue:       #3d6ff8;
      --c-blue-lo:    rgba(61, 111, 248, 0.1);
      --c-blue-mid:   rgba(61, 111, 248, 0.22);
      --c-green:      #34d399;
      --c-green-lo:   rgba(52, 211, 153, 0.1);
      --c-amber:      #fbbf24;
      --c-amber-lo:   rgba(251, 191, 36, 0.1);
      --c-red:        #f87171;
      --c-red-lo:     rgba(248, 113, 113, 0.1);
      --c-red-mid:    rgba(248, 113, 113, 0.2);
      --ease-spring:  cubic-bezier(0.34, 1.4, 0.64, 1);
      --ease-out:     cubic-bezier(0.22, 1, 0.36, 1);
      --font:         "Outfit", sans-serif;
      --font-mono:    "JetBrains Mono", monospace;
    }

    /* ── CARD ── */
    .card {
      font-family: var(--font);
      position: relative;
      background: var(--c-surface);
      border: 1px solid var(--c-border);
      border-radius: 20px;
      overflow: hidden;
      backdrop-filter: blur(16px);
      transition:
        border-color 0.25s ease,
        box-shadow 0.25s ease,
        transform 0.25s var(--ease-out),
        opacity 0.42s var(--ease-out),
        scale 0.42s var(--ease-out),
        filter 0.42s var(--ease-out);
      box-shadow: 0 4px 24px rgba(0, 0, 0, 0.3);
    }

    /* ── DISAPPEAR EFFECT ── */
    .card.is-disappearing {
      opacity: 0;
      scale: 0.96;
      filter: blur(4px);
      pointer-events: none;
    }

    .card:hover:not(.is-disappearing) {
      border-color: var(--c-border-hi);
      box-shadow: 0 8px 40px rgba(0, 0, 0, 0.45);
      transform: translateY(-2px);
    }

    .card.enrolled { --stripe-color: var(--c-green); }
    .card.pending  { --stripe-color: var(--c-amber); }

    /* Left status stripe */
    .status-stripe {
      position: absolute;
      left: 0;
      top: 0;
      bottom: 0;
      width: 3px;
      transition: width 0.25s var(--ease-spring);
    }

    .stripe-enrolled { background: linear-gradient(180deg, var(--c-green), rgba(52,211,153,0.3)); }
    .stripe-pending  { background: linear-gradient(180deg, var(--c-amber), rgba(251,191,36,0.3)); }

    .card:hover .status-stripe { width: 4px; }

    .card-inner {
      padding: 1.4rem 1.5rem 1.4rem 1.75rem;
      display: flex;
      flex-direction: column;
      gap: 1rem;
    }

    /* ── TOP ROW ── */
    .card-top {
      display: flex;
      align-items: flex-start;
      justify-content: space-between;
      gap: 1rem;
    }

    .course-info {
      flex: 1;
      min-width: 0;
      /* Reset button styles for when it's a <button> */
      appearance: none;
      background: none;
      border: none;
      padding: 0;
      text-align: left;
      font: inherit;
      color: inherit;
      display: block;
      width: 100%;
    }

    .course-info.clickable {
      cursor: pointer;
      border-radius: 10px;
      transition: opacity 0.15s;
    }

    .course-info.clickable:hover { opacity: 0.8; }

    .course-meta {
      display: flex;
      align-items: center;
      gap: 0.4rem;
      margin-bottom: 0.5rem;
      flex-wrap: wrap;
    }

    .course-code {
      font-family: var(--font-mono);
      font-size: 0.62rem;
      font-weight: 500;
      color: var(--c-blue);
      background: var(--c-blue-lo);
      border: 1px solid var(--c-blue-mid);
      padding: 0.2rem 0.55rem;
      border-radius: 6px;
      letter-spacing: 0.05em;
    }

    .section-code,
    .credits {
      font-family: var(--font-mono);
      font-size: 0.6rem;
      font-weight: 500;
      color: var(--c-text-3);
      letter-spacing: 0.05em;
    }

    .meta-sep {
      color: var(--c-text-3);
      font-size: 0.7rem;
      opacity: 0.5;
    }

    .course-title {
      font-size: 1.05rem;
      font-weight: 700;
      color: var(--c-text-1);
      letter-spacing: -0.025em;
      line-height: 1.2;
      margin: 0;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .course-desc {
      font-size: 0.75rem;
      font-weight: 300;
      color: var(--c-text-2);
      line-height: 1.55;
      margin: 0.3rem 0 0;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    /* ── BADGE ── */
    .badge-wrap { flex-shrink: 0; }

    .badge {
      display: inline-flex;
      align-items: center;
      gap: 0.4rem;
      font-size: 0.58rem;
      font-weight: 700;
      letter-spacing: 0.15em;
      text-transform: uppercase;
      padding: 0.3rem 0.75rem;
      border-radius: 99px;
    }

    .badge-dot {
      width: 5px;
      height: 5px;
      border-radius: 50%;
      flex-shrink: 0;
    }

    .badge-enrolled {
      background: var(--c-green-lo);
      color: var(--c-green);
      border: 1px solid rgba(52, 211, 153, 0.2);
    }

    .badge-enrolled .badge-dot {
      background: var(--c-green);
      box-shadow: 0 0 6px var(--c-green);
      animation: blink 2s ease-in-out infinite;
    }

    .badge-pending {
      background: var(--c-amber-lo);
      color: var(--c-amber);
      border: 1px solid rgba(251, 191, 36, 0.2);
    }

    .badge-pending .badge-dot { background: var(--c-amber); }

    /* ── DETAIL CHIPS ── */
    .details-row {
      display: flex;
      flex-wrap: wrap;
      gap: 0.5rem;
    }

    .detail-chip {
      display: inline-flex;
      align-items: center;
      gap: 0.4rem;
      font-size: 0.65rem;
      font-weight: 400;
      color: var(--c-text-2);
      background: rgba(255, 255, 255, 0.03);
      border: 1px solid var(--c-border);
      padding: 0.3rem 0.7rem;
      border-radius: 8px;
      letter-spacing: 0.02em;
    }

    .detail-chip svg { opacity: 0.6; flex-shrink: 0; }

    /* ── ACTIONS ── */
    .actions {
      display: flex;
      gap: 0.6rem;
      padding-top: 0.9rem;
      border-top: 1px solid var(--c-border);
    }

    .action-btn {
      flex: 1;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      gap: 0.5rem;
      border: none;
      border-radius: 11px;
      padding: 0.65rem 1rem;
      font-family: var(--font);
      font-size: 0.73rem;
      font-weight: 600;
      letter-spacing: 0.04em;
      cursor: pointer;
      transition: all 0.2s var(--ease-out);
      white-space: nowrap;
    }

    .action-btn:disabled {
      opacity: 0.55;
      cursor: not-allowed;
    }

    .btn-confirm {
      background: var(--c-blue);
      color: #fff;
      box-shadow: 0 3px 14px rgba(61, 111, 248, 0.3);
    }

    .btn-confirm:not(:disabled):hover {
      background: #5580fa;
      box-shadow: 0 5px 22px rgba(61, 111, 248, 0.45);
      transform: translateY(-1px);
    }

    .btn-drop {
      background: rgba(255, 255, 255, 0.04);
      border: 1px solid var(--c-border);
      color: var(--c-text-2);
    }

    .btn-drop:not(:disabled):hover {
      background: var(--c-red-lo);
      border-color: var(--c-red-mid);
      color: var(--c-red);
    }

    .btn-drop-solo {
      max-width: 160px;
      margin-left: auto;
    }

    /* Spinner */
    .btn-spinner {
      width: 12px;
      height: 12px;
      border: 2px solid rgba(255,255,255,0.25);
      border-top-color: #fff;
      border-radius: 50%;
      animation: spin 0.65s linear infinite;
      flex-shrink: 0;
    }

    .spinner-red {
      border-color: rgba(248,113,113,0.25);
      border-top-color: var(--c-red);
    }

    /* ── TOAST ── */
    .toast {
      position: fixed;
      bottom: 1.5rem;
      right: 1.5rem;
      z-index: 200;
      display: inline-flex;
      align-items: center;
      gap: 0.75rem;
      background: #0d1f3c;
      border: 1px solid rgba(52, 211, 153, 0.28);
      border-radius: 14px;
      padding: 0.75rem 1rem 0.75rem 0.85rem;
      box-shadow:
        0 8px 32px rgba(0, 0, 0, 0.5),
        0 0 0 1px rgba(52, 211, 153, 0.06);
      font-family: var(--font);
      max-width: 340px;
    }

    .toast-icon {
      width: 28px;
      height: 28px;
      border-radius: 8px;
      background: rgba(52, 211, 153, 0.14);
      border: 1px solid rgba(52, 211, 153, 0.25);
      color: var(--c-green);
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
    }

    .toast-msg {
      font-size: 0.78rem;
      font-weight: 600;
      color: var(--c-text-1);
      letter-spacing: 0.01em;
      flex: 1;
    }

    .toast-close {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 22px;
      height: 22px;
      border-radius: 6px;
      border: none;
      background: transparent;
      color: var(--c-text-3);
      cursor: pointer;
      flex-shrink: 0;
      transition: background 0.15s, color 0.15s;
      padding: 0;
    }

    .toast-close:hover {
      background: rgba(255, 255, 255, 0.06);
      color: var(--c-text-2);
    }

    /* ── MODAL ── */
    .modal-backdrop {
      position: fixed;
      inset: 0;
      background: rgba(4, 10, 24, 0.8);
      backdrop-filter: blur(8px);
      z-index: 100;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 1.5rem;
    }

    .modal {
      background: #0a1628;
      border: 1px solid var(--c-border-hi);
      border-radius: 22px;
      width: 100%;
      max-width: 420px;
      padding: 1.75rem;
      box-shadow: 0 24px 80px rgba(0, 0, 0, 0.6);
      font-family: var(--font);
    }

    .modal-header {
      display: flex;
      align-items: flex-start;
      gap: 1rem;
      margin-bottom: 1.5rem;
    }

    .modal-icon {
      width: 42px;
      height: 42px;
      border-radius: 12px;
      background: var(--c-red-lo);
      border: 1px solid var(--c-red-mid);
      color: var(--c-red);
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
    }

    .modal-title {
      font-size: 1rem;
      font-weight: 700;
      color: var(--c-text-1);
      margin: 0 0 0.2rem;
      letter-spacing: -0.02em;
    }

    .modal-subtitle {
      font-size: 0.72rem;
      color: var(--c-text-3);
      margin: 0;
      font-weight: 400;
    }

    .modal-course {
      background: rgba(255, 255, 255, 0.03);
      border: 1px solid var(--c-border);
      border-radius: 13px;
      padding: 0.9rem 1.1rem;
      margin-bottom: 1rem;
    }

    .modal-course-code {
      font-family: var(--font-mono);
      font-size: 0.6rem;
      color: var(--c-blue);
      letter-spacing: 0.08em;
      margin-bottom: 0.3rem;
    }

    .modal-course-name {
      font-size: 0.9rem;
      font-weight: 600;
      color: var(--c-text-1);
      letter-spacing: -0.01em;
    }

    .modal-warning {
      display: flex;
      align-items: flex-start;
      gap: 0.6rem;
      background: var(--c-red-lo);
      border: 1px solid var(--c-red-mid);
      border-radius: 11px;
      padding: 0.85rem 1rem;
      margin-bottom: 1.5rem;
      color: rgba(248, 113, 113, 0.8);
    }

    .modal-warning p {
      font-size: 0.75rem;
      line-height: 1.55;
      margin: 0;
      color: rgba(248, 160, 160, 0.85);
    }

    .modal-actions {
      display: flex;
      gap: 0.6rem;
    }

    .modal-btn {
      flex: 1;
      padding: 0.75rem 1rem;
      border-radius: 12px;
      font-family: var(--font);
      font-size: 0.78rem;
      font-weight: 600;
      letter-spacing: 0.03em;
      cursor: pointer;
      transition: all 0.2s ease;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      gap: 0.5rem;
    }

    .modal-btn:disabled { opacity: 0.5; cursor: not-allowed; }

    .modal-cancel {
      background: rgba(255,255,255,0.05);
      border: 1px solid var(--c-border);
      color: var(--c-text-2);
    }

    .modal-cancel:not(:disabled):hover {
      background: rgba(255,255,255,0.08);
      color: var(--c-text-1);
    }

    .modal-confirm {
      background: rgba(220, 38, 38, 0.85);
      border: 1px solid rgba(248, 113, 113, 0.3);
      color: #fff;
      box-shadow: 0 4px 16px rgba(220, 38, 38, 0.3);
    }

    .modal-confirm:not(:disabled):hover {
      background: rgb(220, 38, 38);
      box-shadow: 0 6px 24px rgba(220, 38, 38, 0.45);
      transform: translateY(-1px);
    }

    /* ── KEYFRAMES ── */
    @keyframes spin {
      to { transform: rotate(360deg); }
    }

    @keyframes blink {
      0%, 100% { opacity: 1; }
      50%       { opacity: 0.4; }
    }
  </style>