<script lang="ts">
  import { filterStore } from "$lib/stores/filters.svelte";
  import { enrollmentsStore } from "$lib/stores/enrollments.svelte";
  import EnrollmentRecord from "./EnrollmentRecord.svelte";

  interface Props {
    onDeleteEnrollment?: (enrollmentId: number) => void;
    onEnrollCourse?: (enrollmentId: number) => void;
    onShowEnrollment?: (enrollmentId: number) => void;
    isLoading?: boolean;
  }

  let {
    onDeleteEnrollment,
    onEnrollCourse,
    onShowEnrollment,
    isLoading = false,
  }: Props = $props();

  let search = $state("");

  $effect(() => {
    filterStore.setQuery(search);
  });

  
  const filterOptions = [
    { label: "All", type: "all" },
    { label: "Enrolled", type: "enrolled" },
    { label: "Pending", type: "pending" },
    { label: "Dropped",  type: "dropped"  },
  ];
</script>

<svelte:head>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link
    rel="preconnect"
    href="https://fonts.gstatic.com"
    crossorigin="anonymous"
  />
  <link
    href="https://fonts.googleapis.com/css2?family=DM+Serif+Display:ital@0;1&family=DM+Sans:ital,wght@0,300;0,400;0,500;0,600;1,400&display=swap"
    rel="stylesheet"
  />
</svelte:head>

<div class="page">
  <div class="content">
    <!-- Header -->
    <div class="header">
      <div>
        <p class="header-eyebrow">Academic Dashboard</p>
        <h1>My Academic Path</h1>
        <p class="header-sub">
          Manage your active enrollments, resolve schedule conflicts, and plan
          your current semester.
        </p>
      </div>
      <a href="/enrollments/new" class="enroll-btn">
        <svg
          width="14"
          height="14"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="3"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            d="M12 4v16m8-8H4"
          />
        </svg>
        Enroll New Course
      </a>
    </div>

    <!-- Controls -->
    <div class="controls">
      <div class="search-wrap">
        <svg
          class="search-icon"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
        >
          <circle cx="11" cy="11" r="8" /><path
            stroke-linecap="round"
            d="M21 21l-4.35-4.35"
          />
        </svg>
        <input
          class="search-input"
          type="text"
          placeholder="Filter by course code or subject description…"
          bind:value={search}
        />
      </div>

      <div class="filter-pills">
        {#each filterOptions as opt}
          <button
            class="filter-pill {filterStore.type === opt.type ? 'active' : ''}"
            onclick={() => filterStore.setType(opt.type as any)}
          >
            {opt.label}
          </button>
        {/each}
      </div>
    </div>

    <!-- List -->
    {#if isLoading}
      <div class="loading-wrap">
        <div class="spinner-ring"></div>
        <p class="loading-text">Synchronizing Records</p>
      </div>
    {:else if (filterStore.searchedAndFiltered ?? []).length > 0}
      <div class="records-grid">
        {#each filterStore.searchedAndFiltered as enrollment (enrollment.enrollmentId)}
        <!-- Child component  -->
          <EnrollmentRecord
            enrollment={enrollment as any}
            onDelete={onDeleteEnrollment}
            onEnroll={onEnrollCourse}
            onShow={onShowEnrollment}
          />
        {/each}
      </div>
    {:else}
      <div class="empty-state">
        <div class="empty-icon-wrap">
          <svg
            width="32"
            height="32"
            fill="none"
            stroke="rgba(80,130,220,0.5)"
            stroke-width="1.8"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"
            />
          </svg>
        </div>
      </div>
    {/if}
  </div>
</div>

<style>
  .page {
    font-family: "DM Sans", sans-serif;
    min-height: 100vh;
    background-color: 
#05101f;
    background-image: radial-gradient(
        ellipse 70% 50% at 10% -5%,
        rgba(14, 60, 120, 0.45) 0%,
        transparent 60%
      ),
      radial-gradient(
        ellipse 50% 40% at 90% 100%,
        rgba(8, 40, 90, 0.35) 0%,
        transparent 60%
      );
    padding: 2.5rem 2rem 5rem;
    position: relative;
    overflow-x: hidden;
  }

  /* Grid texture */
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
    max-width: 1280px;
    margin: 0 auto;
  }

  /* ── Header ─────────────────────────────────────── */
  .header {
    display: flex;
    flex-direction: column;
    gap: 1.25rem;
    padding-bottom: 2rem;
    margin-bottom: 2.5rem;
    border-bottom: 1px solid rgba(255, 255, 255, 0.06);
    animation: fadeUp 0.55s cubic-bezier(0.22, 1, 0.36, 1) both;
  }

  @media (min-width: 768px) {
    .header {
      flex-direction: row;
      align-items: flex-end;
      justify-content: space-between;
    }
  }

  .header-eyebrow {
    font-size: 0.65rem;
    font-weight: 700;
    letter-spacing: 0.22em;
    text-transform: uppercase;
    color: rgba(80, 140, 255, 0.7);
    margin-bottom: 0.6rem;
  }

  h1 {
    font-family: "DM Serif Display", serif;
    font-style: italic;
    font-size: clamp(2rem, 4vw, 3.25rem);
    font-weight: 400;
    color: 
#ffffff;
    line-height: 1.05;
    letter-spacing: -0.02em;
    margin: 0 0 0.6rem;
  }

  .header-sub {
    font-size: 0.85rem;
    font-weight: 400;
    color: rgba(160, 185, 220, 0.6);
    max-width: 420px;
    line-height: 1.6;
    margin: 0;
  }

  .enroll-btn {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    background: linear-gradient(135deg, 
#1e4db7, 
#0f3298);
    color: white;
    text-decoration: none;
    border: none;
    border-radius: 12px;
    padding: 0.85rem 1.5rem;
    font-family: "DM Sans", sans-serif;
    font-size: 0.82rem;
    font-weight: 600;
    letter-spacing: 0.04em;
    cursor: pointer;
    transition: all 0.25s ease;
    box-shadow: 0 4px 20px rgba(30, 77, 183, 0.35);
    white-space: nowrap;
    flex-shrink: 0;
  }

  .enroll-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 32px rgba(30, 77, 183, 0.5);
  }

  /* ── Stats ───────────────────────────────────────── */
  .stats-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 1rem;
    margin-bottom: 2rem;
    animation: fadeUp 0.55s 0.1s cubic-bezier(0.22, 1, 0.36, 1) both;
  }

  @media (max-width: 600px) {
    .stats-grid {
      grid-template-columns: 1fr;
    }
  }

  .stat-card {
    background: linear-gradient(
      145deg,
      rgba(12, 28, 55, 0.9) 0%,
      rgba(7, 18, 38, 0.95) 100%
    );
    border: 1px solid rgba(255, 255, 255, 0.07);
    border-radius: 20px;
    padding: 1.5rem 1.75rem;
    position: relative;
    overflow: hidden;
    transition:
      border-color 0.2s,
      transform 0.2s;
    box-shadow:
      0 8px 32px rgba(0, 0, 0, 0.35),
      inset 0 1px 0 rgba(255, 255, 255, 0.05);
  }

  .stat-card:hover {
    border-color: rgba(60, 120, 255, 0.2);
    transform: translateY(-2px);
  }

  .stat-card::after {
    content: attr(data-emoji);
    position: absolute;
    right: -8px;
    bottom: -8px;
    font-size: 3.5rem;
    opacity: 0.07;
    pointer-events: none;
    transition:
      opacity 0.3s,
      transform 0.3s;
  }

  .stat-card:hover::after {
    opacity: 0.12;
    transform: scale(1.2);
  }

  .stat-label {
    font-size: 0.62rem;
    font-weight: 700;
    letter-spacing: 0.2em;
    text-transform: uppercase;
    color: rgba(120, 160, 210, 0.6);
    margin-bottom: 0.5rem;
  }

  .stat-num {
    font-family: "DM Serif Display", serif;
    font-size: 3rem;
    color: 
#ffffff;
    line-height: 1;
    letter-spacing: -0.03em;
  }

  .stat-num.confirmed {
    color: 
#4ade80;
  }
  .stat-num.pending {
    color: 
#fbbf24;
  }

  /* ── Controls ────────────────────────────────────── */
  .controls {
    background: linear-gradient(
      145deg,
      rgba(12, 28, 55, 0.9),
      rgba(7, 18, 38, 0.95)
    );
    border: 1px solid rgba(255, 255, 255, 0.07);
    border-radius: 20px;
    padding: 1.25rem 1.5rem;
    display: flex;
    flex-direction: column;
    gap: 1rem;
    margin-bottom: 2rem;
    box-shadow:
      0 8px 32px rgba(0, 0, 0, 0.35),
      inset 0 1px 0 rgba(255, 255, 255, 0.05);
    animation: fadeUp 0.55s 0.2s cubic-bezier(0.22, 1, 0.36, 1) both;
  }

  @media (min-width: 768px) {
    .controls {
      flex-direction: row;
      align-items: center;
    }
  }

  .search-wrap {
    flex: 1;
    position: relative;
  }

  .search-icon {
    position: absolute;
    left: 1rem;
    top: 50%;
    transform: translateY(-50%);
    color: rgba(100, 140, 200, 0.45);
    pointer-events: none;
    width: 16px;
    height: 16px;
  }

  .search-input {
    width: 100%;
    background: rgba(255, 255, 255, 0.04);
    border: 1px solid rgba(255, 255, 255, 0.08);
    border-radius: 12px;
    padding: 0.8rem 1rem 0.8rem 2.75rem;
    color: 
#e8f0ff;
    font-family: "DM Sans", sans-serif;
    font-size: 0.875rem;
    outline: none;
    transition: all 0.2s ease;
    box-sizing: border-box;
  }

  .search-input::placeholder {
    color: rgba(120, 150, 200, 0.3);
  }

  .search-input:focus {
    border-color: rgba(80, 140, 255, 0.4);
    background: rgba(30, 70, 160, 0.1);
    box-shadow: 0 0 0 3px rgba(60, 120, 255, 0.08);
  }

  .filter-pills {
    display: flex;
    background: rgba(0, 0, 0, 0.25);
    border: 1px solid rgba(255, 255, 255, 0.05);
    border-radius: 12px;
    padding: 4px;
    gap: 2px;
    overflow-x: auto;
  }

  .filter-pill {
    padding: 0.55rem 1.2rem;
    border-radius: 9px;
    border: none;
    background: transparent;
    font-family: "DM Sans", sans-serif;
    font-size: 0.72rem;
    font-weight: 700;
    letter-spacing: 0.1em;
    text-transform: uppercase;
    color: rgba(140, 170, 220, 0.5);
    cursor: pointer;
    transition: all 0.2s ease;
    white-space: nowrap;
  }

  .filter-pill:hover {
    color: rgba(200, 220, 255, 0.8);
  }

  .filter-pill.active {
    background: linear-gradient(135deg, 
#1e4db7, 
#0f3298);
    color: white;
    box-shadow: 0 2px 12px rgba(30, 77, 183, 0.4);
  }

  /* ── Records grid ────────────────────────────────── */
  .records-grid {
    display: grid;
    grid-template-columns: 1fr;
    gap: 1rem;
    animation: fadeUp 0.55s 0.3s cubic-bezier(0.22, 1, 0.36, 1) both;
  }

  @media (min-width: 1200px) {
    .records-grid {
      grid-template-columns: 1fr 1fr;
    }
  }

  /* ── Loading ─────────────────────────────────────── */
  .loading-wrap {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 8rem 0;
    gap: 1.5rem;
  }

  .spinner-ring {
    width: 52px;
    height: 52px;
    border: 3px solid rgba(30, 77, 183, 0.15);
    border-top-color: 
#3b6fd4;
    border-radius: 50%;
    animation: spin 0.75s linear infinite;
  }

  .loading-text {
    font-size: 0.62rem;
    font-weight: 700;
    letter-spacing: 0.28em;
    text-transform: uppercase;
    color: rgba(80, 130, 200, 0.4);
  }

  @keyframes spin {
    to {
      transform: rotate(360deg);
    }
  }

  /* ── Empty state ─────────────────────────────────── */
  .empty-state {
    background: linear-gradient(
      145deg,
      rgba(12, 28, 55, 0.7),
      rgba(7, 18, 38, 0.8)
    );
    border: 1px dashed rgba(255, 255, 255, 0.08);
    border-radius: 24px;
    padding: 5rem 2rem;
    text-align: center;
    animation: fadeUp 0.4s cubic-bezier(0.22, 1, 0.36, 1) both;
  }

  .empty-icon-wrap {
    width: 80px;
    height: 80px;
    background: rgba(30, 77, 183, 0.12);
    border: 1px solid rgba(60, 120, 255, 0.15);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 1.75rem;
  }

  .empty-title {
    font-family: "DM Serif Display", serif;
    font-style: italic;
    font-size: 1.75rem;
    color: 
#ffffff;
    margin: 0 0 0.5rem;
    letter-spacing: -0.02em;
  }

  .empty-sub {
    font-size: 0.84rem;
    color: rgba(140, 170, 210, 0.5);
    max-width: 320px;
    margin: 0 auto 2rem;
    line-height: 1.6;
  }

  .reset-btn {
    background: rgba(255, 255, 255, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.08);
    color: rgba(160, 190, 230, 0.7);
    border-radius: 10px;
    padding: 0.7rem 1.5rem;
    font-family: "DM Sans", sans-serif;
    font-size: 0.75rem;
    font-weight: 700;
    letter-spacing: 0.12em;
    text-transform: uppercase;
    cursor: pointer;
    transition: all 0.2s;
  }

  .reset-btn:hover {
    background: rgba(30, 77, 183, 0.15);
    border-color: rgba(60, 120, 255, 0.25);
    color: #fff;
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