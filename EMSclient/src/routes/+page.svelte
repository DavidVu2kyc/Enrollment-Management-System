
<!-- Home page ( Enrollment List ) -->
<script lang="ts">
  import { enrollmentsStore } from "$lib/stores/enrollments.svelte";
  import EnrollmentList from "$lib/components/EnrollmentList.svelte";
  import { onMount } from "svelte";

  let isLoading = $state(true);
  let error = $state<string | null>(null);
  let toastMessage = $state<string | null>(null);
  let toastType = $state<"success" | "error">("success");
  let toastTimer: ReturnType<typeof setTimeout>;

  const showToast = (msg: string, type: "success" | "error" = "success") => {
    clearTimeout(toastTimer);
    toastMessage = msg;
    toastType = type;
    toastTimer = setTimeout(() => (toastMessage = null), 3500);
  };

  onMount(async () => {
    try {
      const response = await fetch("/api/enrollments");
      if (!response.ok) throw new Error("Synchronization failure with institutional ledger.");
      const result = await response.json();
      enrollmentsStore.set(result.data || []);
    } catch (err) {
      error = err instanceof Error ? err.message : "An unexpected protocol error occurred.";
    } finally {
      isLoading = false;
    }
  });

  const handleDeleteEnrollment = async (id: string) => {
    try {
      const response = await fetch(`/api/enrollments/${id}`, { method: "DELETE" });
      if (!response.ok) throw new Error();
      enrollmentsStore.remove(id);
      showToast("Enrollment removed successfully.", "success");
    } catch {
      showToast("Failed to remove enrollment. Please try again.", "error");
    }
  };

  const handleEnrollCourse = async (id: string) => {
    try {
      const response = await fetch(`/api/enrollments/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ isEnrolled: true, status: "ENROLLED" }),
      });
      if (!response.ok) throw new Error();
      const result = await response.json();
      enrollmentsStore.update(id, result.data);
      showToast("Successfully enrolled in course.", "success");
    } catch {
      showToast("Failed to enroll in course. Please try again.", "error");
    }
  };
</script>

<svelte:head>
  <title>Pathway Dashboard — EMS</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin="anonymous" />
  <link href="https://fonts.googleapis.com/css2?family=DM+Serif+Display:ital@0;1&family=DM+Sans:wght@300;400;500;600&display=swap" rel="stylesheet" />
</svelte:head>

<style>
  :global(body) {
    font-family: 'DM Sans', sans-serif;
    background-color: #05101f;
  }

  /* ── Error Banner ─────────────────────────────── */
  .error-banner {
    display: flex;
    flex-direction: column;
    gap: 1rem;
    background: linear-gradient(135deg, rgba(180, 28, 28, 0.15), rgba(120, 10, 10, 0.2));
    border: 1px solid rgba(248, 113, 113, 0.18);
    border-radius: 20px;
    padding: 1.5rem 1.75rem;
    margin-bottom: 1.5rem;
    box-shadow: 0 8px 32px rgba(180, 28, 28, 0.15), inset 0 1px 0 rgba(255,255,255,0.04);
    animation: slideDown 0.45s cubic-bezier(0.22, 1, 0.36, 1) both;
  }

  @media (min-width: 640px) {
    .error-banner {
      flex-direction: row;
      align-items: center;
      justify-content: space-between;
    }
  }

  @keyframes slideDown {
    from { opacity: 0; transform: translateY(-14px); }
    to   { opacity: 1; transform: translateY(0); }
  }

  .error-body {
    display: flex;
    align-items: flex-start;
    gap: 1rem;
  }

  .error-icon {
    width: 44px; height: 44px;
    background: rgba(248, 113, 113, 0.12);
    border: 1px solid rgba(248, 113, 113, 0.22);
    border-radius: 12px;
    display: flex; align-items: center; justify-content: center;
    flex-shrink: 0;
  }

  .error-title {
    font-family: 'DM Serif Display', serif;
    font-style: italic;
    font-size: 1.1rem;
    color: #fca5a5;
    margin: 0 0 0.25rem;
  }

  .error-msg {
    font-size: 0.82rem;
    color: rgba(252, 165, 165, 0.65);
    margin: 0;
    line-height: 1.5;
  }

  .retry-btn {
    display: inline-flex;
    align-items: center;
    gap: 0.45rem;
    background: rgba(248, 113, 113, 0.1);
    border: 1px solid rgba(248, 113, 113, 0.2);
    color: #fca5a5;
    border-radius: 10px;
    padding: 0.65rem 1.25rem;
    font-family: 'DM Sans', sans-serif;
    font-size: 0.74rem;
    font-weight: 600;
    letter-spacing: 0.09em;
    text-transform: uppercase;
    cursor: pointer;
    transition: all 0.2s ease;
    white-space: nowrap;
    flex-shrink: 0;
  }

  .retry-btn:hover {
    background: rgba(248, 113, 113, 0.18);
    border-color: rgba(248, 113, 113, 0.32);
    transform: translateY(-1px);
  }

  /* ── Toast ───────────────────────────────────── */
  .toast-wrap {
    position: fixed;
    bottom: 2rem;
    left: 50%;
    transform: translateX(-50%);
    z-index: 9999;
    pointer-events: none;
    animation: toastIn 0.35s cubic-bezier(0.22, 1, 0.36, 1) both;
  }

  @keyframes toastIn {
    from { opacity: 0; transform: translateX(-50%) translateY(14px) scale(0.96); }
    to   { opacity: 1; transform: translateX(-50%) translateY(0) scale(1); }
  }

  .toast {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    padding: 0.85rem 1.4rem;
    border-radius: 14px;
    font-size: 0.82rem;
    font-weight: 500;
    backdrop-filter: blur(24px);
    box-shadow: 0 16px 48px rgba(0,0,0,0.55);
    white-space: nowrap;
  }

  .toast.success {
    background: linear-gradient(135deg, rgba(10, 40, 18, 0.97), rgba(5, 25, 10, 0.98));
    border: 1px solid rgba(74, 222, 128, 0.22);
    color: #86efac;
  }

  .toast.error {
    background: linear-gradient(135deg, rgba(50, 12, 12, 0.97), rgba(30, 5, 5, 0.98));
    border: 1px solid rgba(248, 113, 113, 0.22);
    color: #fca5a5;
  }

  .toast-dot {
    width: 7px; height: 7px;
    border-radius: 50%;
    flex-shrink: 0;
    animation: pulse 1.8s ease-in-out infinite;
  }

  .toast.success .toast-dot { background: #4ade80; box-shadow: 0 0 8px rgba(74, 222, 128, 0.7); }
  .toast.error   .toast-dot { background: #f87171; box-shadow: 0 0 8px rgba(248, 113, 113, 0.7); }

  @keyframes pulse {
    0%, 100% { opacity: 1; transform: scale(1); }
    50% { opacity: 0.6; transform: scale(0.85); }
  }
</style>

{#if error}
  <div class="error-banner">
    <div class="error-body">
      <div class="error-icon">
        <svg width="20" height="20" fill="none" stroke="#fca5a5" stroke-width="2" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v3.75m-9.303 3.376c-.866 1.5.217 3.374 1.948 3.374h14.71c1.73 0 2.813-1.874 1.948-3.374L13.949 3.378c-.866-1.5-3.032-1.5-3.898 0L2.697 16.126zM12 15.75h.007v.008H12v-.008z"/>
        </svg>
      </div>
      <div>
        <p class="error-title">Sync Failure</p>
        <p class="error-msg">{error}</p>
      </div>
    </div>
    <button class="retry-btn" onclick={() => window.location.reload()}>
      <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
        <path stroke-linecap="round" stroke-linejoin="round" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"/>
      </svg>
      Retry Sync
    </button>
  </div>
{/if}

<EnrollmentList
  {isLoading}
  onDeleteEnrollment={handleDeleteEnrollment}
  onEnrollCourse={handleEnrollCourse}
/>

{#if toastMessage}
  <div class="toast-wrap">
    <div class="toast {toastType}">
      <span class="toast-dot"></span>
      {toastMessage}
    </div>
  </div>
{/if}