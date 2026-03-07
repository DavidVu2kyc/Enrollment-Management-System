<script lang="ts">
  import { onMount, tick } from "svelte";
  import { enrollmentsStore } from "$lib/stores/enrollments.svelte";
  import EnrollmentList from "$lib/components/EnrollmentList.svelte";
  import { fly, fade, scale } from "svelte/transition";
  import type { PageData } from "./$types";

  let { data }: { data: PageData } = $props();

  let isLoading = $state(true);
  let filterStatus = $state("ALL");
  let searchQuery = $state("");
  let toast = $state<{ msg: string; type: "success" | "error" } | null>(null);
  let toastTimer: ReturnType<typeof setTimeout>;

  const showToast = (msg: string, type: "success" | "error" = "success") => {
    clearTimeout(toastTimer);
    toast = { msg, type };
    toastTimer = setTimeout(() => (toast = null), 4000);
  };

  onMount(() => {
    // Populate store from SSR data
    if (data.enrollments) {
      enrollmentsStore.set(data.enrollments);
    }
    isLoading = false;
  });

  const handleDelete = async (id: string) => {
    try {
      const formData = new FormData();
      formData.append("id", id);

      const resp = await fetch("?/delete", {
        method: "POST",
        body: formData,
      });

      if (!resp.ok) throw new Error();

      enrollmentsStore.remove(id);
      showToast("Enrollment successfully removed.", "success");
    } catch {
      showToast("Failed to remove enrollment. Try again.", "error");
    }
  };

  const filters = [
    { key: "ALL", label: "All", icon: "M4 6h16M4 10h16M4 14h16M4 18h16" },
    {
      key: "ENROLLED",
      label: "Confirmed",
      icon: "M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z",
    },
    {
      key: "PENDING",
      label: "Pending",
      icon: "M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z",
    },
  ];

  // Counts (approximate – real filtering happens in EnrollmentList)
  const allCount = $derived(enrollmentsStore.all?.length ?? 0);
</script>

<svelte:head>
  <title>Enrollments — EMS</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link
    rel="preconnect"
    href="https://fonts.gstatic.com"
    crossorigin="anonymous"
  />
  <link
    href="https://fonts.googleapis.com/css2?family=Syne:wght@400;600;700;800&family=Geist+Mono:wght@400;500&display=swap"
    rel="stylesheet"
  />
</svelte:head>

<div class="page" in:fade={{ duration: 350 }}>
  <div class="inner">
    <!-- ── HERO ── -->
    <header class="hero" in:fly={{ y: 14, duration: 480, delay: 50 }}>
      <div>
        <div class="hero-eyebrow">
          <!-- <span class="live-dot"></span> -->
          <!-- <span class="eyebrow-text">Enrollment Management · Live</span> -->
        </div>
        <h1 class="page-title">
          My <em>Enrollments</em>
        </h1>
        <p class="page-sub">
          View, manage, and track all your active course enlistments for the
          current academic period.
        </p>
      </div>
    </header>

    <!-- ── TOOLBAR ── -->
    <div class="toolbar" in:fly={{ y: 12, duration: 440, delay: 220 }}>
      <!-- Filter tabs -->
      <div class="filter-tabs">
        {#each filters as f}
          <button
            class="tab-btn"
            class:active={filterStatus === f.key}
            onclick={() => (filterStatus = f.key)}
          >
            <svg
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <path d={f.icon} />
            </svg>
            {f.label}
            {#if f.key === "ALL"}
              <span class="tab-count">{allCount}</span>
            {/if}
          </button>
        {/each}
      </div>

      <!-- Search -->
      <div class="search-wrap">
        <svg
          class="search-icon"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
        >
          <circle cx="11" cy="11" r="8" /><path d="m21 21-4.35-4.35" />
        </svg>
        <input
          class="search-input"
          type="text"
          placeholder="Search courses…"
          bind:value={searchQuery}
        />
      </div>
    </div>

    <!-- ── PANEL ── -->
    <div class="panel" in:fly={{ y: 16, duration: 440, delay: 300 }}>
      <!-- Panel header -->
      <div class="panel-bar">
        <div class="panel-bar-left">
          <div class="panel-pip"></div>
          <span class="panel-title">Enrollment Ledger</span>
          <span class="panel-count">{isLoading ? "…" : allCount} records</span>
        </div>
        <span class="panel-meta">A.Y. 2025–2026 · Sem 2</span>
      </div>

      <!-- Content -->
      {#if isLoading}
        <div class="skeleton-rows">
          {#each Array(6) as _}
            <div class="skeleton-row">
              <div class="sk-block sk-sq"></div>
              <div class="sk-col">
                <div class="sk-block sk-ln m"></div>
                <div class="sk-block sk-ln s"></div>
              </div>
              <div class="sk-block sk-chip"></div>
            </div>
          {/each}
        </div>
      {:else}
      <!-- show a list of enrollment form postgresql -->
        <EnrollmentList
          {isLoading}
          onDeleteEnrollment={handleDelete}
          onEnrollCourse={() => {}}
        />
      {/if}
    </div>
  </div>
</div>

<!-- ── TOAST ── -->
{#if toast}
  <div class="toast-wrap">
    <div
      class="toast {toast.type}"
      in:fly={{ y: 12, duration: 300 }}
      out:fly={{ y: 12, duration: 200 }}
      style="position:relative; overflow:hidden;"
    >
      <div class="toast-icon">
        {#if toast.type === "success"}
          <svg
            width="14"
            height="14"
            viewBox="0 0 24 24"
            fill="none"
            stroke="#10b981"
            stroke-width="2.5"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <path d="M20 6L9 17l-5-5" />
          </svg>
        {:else}
          <svg
            width="14"
            height="14"
            viewBox="0 0 24 24"
            fill="none"
            stroke="#ef4444"
            stroke-width="2.5"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <circle cx="12" cy="12" r="10" /><path d="M12 8v4m0 4h.01" />
          </svg>
        {/if}
      </div>
      <div class="toast-body">
        <div class="toast-type">
          {toast.type === "success" ? "Success" : "Error"}
        </div>
        <div class="toast-msg">{toast.msg}</div>
      </div>
      <div class="toast-progress"></div>
    </div>
  </div>
{/if}

<style>
  :root {
    --page-bg: #040d1a;
    --surface-1: rgba(12, 28, 55, 0.6);
    --border-1: rgba(255, 255, 255, 0.06);
    --border-2: rgba(255, 255, 255, 0.04);
    --text-hi: #e2ecff;
    --text-mid: rgba(148, 185, 255, 0.6);
    --text-lo: rgba(96, 130, 200, 0.45);
    --blue: #3b82f6;
    --font: "Syne", sans-serif;
    --mono: "Geist Mono", monospace;
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
    content: "";
    position: fixed;
    width: 600px;
    height: 600px;
    top: -150px;
    right: -150px;
    background: radial-gradient(
      circle,
      rgba(29, 78, 216, 0.07) 0%,
      transparent 65%
    );
    pointer-events: none;
  }

  .inner {
    max-width: 1280px;
    margin: 0 auto;
    position: relative;
    z-index: 1;
  }

  /* ── HERO ── */
  .hero {
    display: flex;
    flex-direction: column;
    gap: 2rem;
    margin-bottom: 2.5rem;
  }

  @media (min-width: 860px) {
    .hero {
      flex-direction: row;
      align-items: flex-end;
      justify-content: space-between;
    }
  }

  .hero-eyebrow {
    display: flex;
    align-items: center;
    gap: 0.55rem;
    margin-bottom: 0.7rem;
  }

  .live-dot {
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: #22c55e;
    box-shadow: 0 0 8px rgba(34, 197, 94, 0.8);
    animation: blink 2s ease-in-out infinite;
  }

  @keyframes blink {
    0%,
    100% {
      opacity: 1;
    }
    50% {
      opacity: 0.4;
    }
  }

  .eyebrow-text {
    font-size: 0.58rem;
    font-weight: 700;
    letter-spacing: 0.38em;
    text-transform: uppercase;
    color: var(--text-lo);
  }

  .page-title {
    font-size: clamp(2rem, 4vw, 3.2rem);
    font-weight: 800;
    letter-spacing: -0.03em;
    color: var(--text-hi);
    line-height: 1.05;
    margin: 0 0 0.7rem;
  }

  .page-title em {
    font-style: normal;
    color: transparent;
    -webkit-text-stroke: 1.5px rgba(96, 165, 250, 0.45);
  }

  .page-sub {
    font-size: 0.82rem;
    color: var(--text-mid);
    font-weight: 400;
    line-height: 1.6;
    max-width: 440px;
  }

  /* Hero right: gateway status */
  .gateway-card {
    background: var(--surface-1);
    border: 1px solid var(--border-1);
    border-radius: 18px;
    padding: 1.1rem 1.4rem;
    backdrop-filter: blur(16px);
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
    min-width: 220px;
    flex-shrink: 0;
    box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.04);
  }

  .gateway-label {
    font-size: 0.55rem;
    font-weight: 700;
    letter-spacing: 0.35em;
    text-transform: uppercase;
    color: var(--text-lo);
    display: flex;
    align-items: center;
    gap: 0.4rem;
  }

  .gateway-label::before {
    content: "";
    width: 12px;
    height: 1px;
    background: currentColor;
  }

  .gateway-rows {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
  }

  .gateway-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 1rem;
  }

  .gw-name {
    font-size: 0.62rem;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.08em;
    color: var(--text-mid);
  }

  .gw-status {
    display: flex;
    align-items: center;
    gap: 0.35rem;
    font-family: var(--mono);
    font-size: 0.6rem;
    color: #22c55e;
  }

  .gw-dot {
    width: 5px;
    height: 5px;
    border-radius: 50%;
    background: currentColor;
    box-shadow: 0 0 6px currentColor;
  }

  .gw-status.warn {
    color: #f59e0b;
  }
  .gw-status.off {
    color: #ef4444;
  }

  /* ── TOOLBAR ── */
  .toolbar {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    justify-content: space-between;
    gap: 1rem;
    margin-bottom: 1.5rem;
  }

  /* Filter tabs */
  .filter-tabs {
    display: flex;
    align-items: center;
    background: rgba(12, 28, 55, 0.7);
    border: 1px solid var(--border-1);
    border-radius: 14px;
    padding: 4px;
    gap: 2px;
    backdrop-filter: blur(12px);
  }

  .tab-btn {
    display: flex;
    align-items: center;
    gap: 0.4rem;
    padding: 0.5rem 1rem;
    border-radius: 10px;
    font-family: var(--font);
    font-size: 0.62rem;
    font-weight: 700;
    letter-spacing: 0.14em;
    text-transform: uppercase;
    color: var(--text-lo);
    background: none;
    border: none;
    cursor: pointer;
    transition: all 0.2s ease;
    white-space: nowrap;
  }

  .tab-btn svg {
    width: 12px;
    height: 12px;
    opacity: 0.5;
    transition: opacity 0.2s;
    flex-shrink: 0;
  }

  .tab-btn:hover {
    color: var(--text-mid);
    background: rgba(255, 255, 255, 0.04);
  }

  .tab-btn.active {
    background: #1d4ed8;
    color: #fff;
    box-shadow:
      0 2px 14px rgba(29, 78, 216, 0.45),
      inset 0 1px 0 rgba(255, 255, 255, 0.12);
  }

  .tab-btn.active svg {
    opacity: 1;
  }

  /* Count pill on tab */
  .tab-count {
    font-family: var(--mono);
    font-size: 0.55rem;
    background: rgba(255, 255, 255, 0.12);
    border-radius: 99px;
    padding: 0.1rem 0.4rem;
    line-height: 1.4;
  }

  /* Search */
  .search-wrap {
    position: relative;
    flex: 1;
    max-width: 300px;
  }

  .search-icon {
    position: absolute;
    left: 0.85rem;
    top: 50%;
    transform: translateY(-50%);
    width: 14px;
    height: 14px;
    color: var(--text-lo);
    pointer-events: none;
  }

  .search-input {
    width: 100%;
    background: rgba(12, 28, 55, 0.7);
    border: 1px solid var(--border-1);
    border-radius: 12px;
    padding: 0.6rem 1rem 0.6rem 2.4rem;
    font-family: var(--font);
    font-size: 0.78rem;
    font-weight: 500;
    color: var(--text-hi);
    outline: none;
    backdrop-filter: blur(12px);
    transition: all 0.2s ease;
  }

  .search-input::placeholder {
    color: var(--text-lo);
  }

  .search-input:focus {
    border-color: rgba(59, 130, 246, 0.4);
    background: rgba(29, 78, 216, 0.07);
    box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.08);
  }

  /* ── MAIN PANEL ── */
  .panel {
    background: var(--surface-1);
    border: 1px solid var(--border-1);
    border-radius: 24px;
    overflow: hidden;
    backdrop-filter: blur(16px);
    box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.03);
  }

  /* Panel header bar */
  .panel-bar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 1rem 1.5rem;
    border-bottom: 1px solid var(--border-2);
    gap: 1rem;
  }

  .panel-bar-left {
    display: flex;
    align-items: center;
    gap: 0.65rem;
  }

  .panel-pip {
    width: 3px;
    height: 18px;
    border-radius: 99px;
    background: linear-gradient(180deg, #3b82f6, #1e3a8a);
    box-shadow: 0 0 8px rgba(59, 130, 246, 0.5);
    flex-shrink: 0;
  }

  .panel-title {
    font-size: 0.72rem;
    font-weight: 700;
    letter-spacing: 0.12em;
    text-transform: uppercase;
    color: var(--text-hi);
  }

  .panel-count {
    font-family: var(--mono);
    font-size: 0.58rem;
    font-weight: 500;
    color: var(--text-lo);
    background: rgba(255, 255, 255, 0.04);
    border: 1px solid var(--border-1);
    border-radius: 99px;
    padding: 0.18rem 0.55rem;
  }

  .panel-meta {
    font-family: var(--mono);
    font-size: 0.58rem;
    color: var(--text-lo);
    letter-spacing: 0.06em;
  }

  /* Skeleton */
  .skeleton-rows {
    display: flex;
    flex-direction: column;
  }

  .skeleton-row {
    display: flex;
    align-items: center;
    gap: 1rem;
    padding: 1.1rem 1.5rem;
    border-bottom: 1px solid var(--border-2);
    animation: shimmer 1.6s ease-in-out infinite;
  }

  .skeleton-row:last-child {
    border-bottom: none;
  }

  .sk-block {
    border-radius: 8px;
    background: rgba(255, 255, 255, 0.04);
    flex-shrink: 0;
  }

  .sk-sq {
    width: 38px;
    height: 38px;
    border-radius: 11px;
  }
  .sk-col {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 0.4rem;
  }
  .sk-ln {
    height: 8px;
    border-radius: 4px;
  }
  .sk-ln.s {
    width: 38%;
  }
  .sk-ln.m {
    width: 62%;
  }
  .sk-chip {
    width: 70px;
    height: 24px;
    border-radius: 99px;
  }

  @keyframes shimmer {
    0%,
    100% {
      opacity: 0.55;
    }
    50% {
      opacity: 0.9;
    }
  }

  /* ── TOAST ── */
  .toast-wrap {
    position: fixed;
    bottom: 2rem;
    right: 2rem;
    z-index: 200;
  }

  .toast {
    display: flex;
    align-items: center;
    gap: 0.85rem;
    background: rgba(5, 14, 28, 0.97);
    border: 1px solid var(--border-1);
    border-radius: 16px;
    padding: 1rem 1.4rem;
    min-width: 300px;
    max-width: 420px;
    backdrop-filter: blur(24px);
    box-shadow:
      0 16px 48px rgba(0, 0, 0, 0.55),
      inset 0 1px 0 rgba(255, 255, 255, 0.04);
  }

  .toast.success {
    border-color: rgba(16, 185, 129, 0.25);
  }
  .toast.error {
    border-color: rgba(239, 68, 68, 0.25);
  }

  .toast-icon {
    width: 30px;
    height: 30px;
    border-radius: 9px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }

  .toast.success .toast-icon {
    background: rgba(16, 185, 129, 0.12);
  }
  .toast.error .toast-icon {
    background: rgba(239, 68, 68, 0.12);
  }

  .toast-type {
    font-size: 0.55rem;
    font-weight: 700;
    letter-spacing: 0.25em;
    text-transform: uppercase;
    margin-bottom: 0.2rem;
  }

  .toast.success .toast-type {
    color: #10b981;
  }
  .toast.error .toast-type {
    color: #ef4444;
  }

  .toast-msg {
    font-size: 0.78rem;
    font-weight: 500;
    color: var(--text-hi);
    line-height: 1.4;
  }

  /* Progress bar on toast */
  .toast-progress {
    position: absolute;
    bottom: 0;
    left: 0;
    height: 2px;
    border-radius: 0 0 16px 16px;
    animation: drain 4s linear forwards;
  }

  .toast.success .toast-progress {
    background: #10b981;
    box-shadow: 0 0 6px rgba(16, 185, 129, 0.5);
  }
  .toast.error .toast-progress {
    background: #ef4444;
    box-shadow: 0 0 6px rgba(239, 68, 68, 0.5);
  }

  @keyframes drain {
    from {
      width: 100%;
    }
    to {
      width: 0%;
    }
  }
</style>
