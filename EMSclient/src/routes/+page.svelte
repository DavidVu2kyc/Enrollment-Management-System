<!-- HOMEPAGE (ENROLLMENT LIST ) -->

<script lang="ts">
  import { onMount } from "svelte";
  import { userStore } from "$lib/stores/user.svelte";
  import { enrollmentsStore } from "$lib/stores/enrollments.svelte";
  import { fly, fade } from "svelte/transition";
  import EnrollmentList from "$lib/components/EnrollmentList.svelte";

  let isLoading = $state(true);
  
  let dashboardStats = $state({
    enrolledUnits: 0,
    activeCourses: 0,
    enrollmentPhase: "Enlistment Open",
    capacityStatus: 85,
  });

  
  onMount(async () => {
    try {
      const [enrollRes] = await Promise.all([
        fetch("/api/enrollments"),
        new Promise((r) => setTimeout(r, 800)),
      ]);
      if (enrollRes.ok) {
        const result = await enrollRes.json();
        const rawData = result.data || [];
        enrollmentsStore.set(rawData);
        dashboardStats.activeCourses = rawData.length;
        dashboardStats.enrolledUnits = rawData.reduce(
          (acc: number, curr: any) => acc + (curr.units || 0), 0
        );
      }
    } catch (err) {
      console.error("System synchronization failed.", err);
    } finally {
      isLoading = false;
    }
  });

  const cards = $derived([
    {
      label: "Academic Load",
      value: `${dashboardStats.enrolledUnits}`,
      unit: "Units",
      icon: "M12 6.042A8.967 8.967 0 006 3.75c-1.052 0-2.062.18-3 .512v14.25A8.987 8.987 0 016 18c2.305 0 4.408.867 6 2.292m0-14.25a8.966 8.966 0 016-2.292c1.052 0 2.062.18 3 .512v14.25A8.987 8.987 0 0118 18c-2.305 0-4.408.867-6 2.292m0-14.25v14.25",
      accent: "#3b82f6",
      accentBg: "rgba(59,130,246,0.08)",
      accentBorder: "rgba(59,130,246,0.2)",
      // trend: "+2 from last sem",
      trendUp: true,
    },
    {
      label: "Active Enlistments",
      value: `${dashboardStats.activeCourses}`,
      unit: "Courses",
      icon: "M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10",
      accent: "#10b981",
      accentBg: "rgba(16,185,129,0.08)",
      accentBorder: "rgba(16,185,129,0.2)",
      // trend: "All confirmed",
      trendUp: true,
    },
    {
      label: "Phase Status",
      value: dashboardStats.enrollmentPhase,
      unit: "",
      icon: "M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z",
      accent: "#f59e0b",
      accentBg: "rgba(245,158,11,0.08)",
      accentBorder: "rgba(245,158,11,0.2)",
      // trend: "Closes in 3 days",
      trendUp: false,
    },
    {
      label: "Seat Capacity",
      value: `${dashboardStats.capacityStatus}%`,
      unit: "Filled",
      icon: "M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z",
      accent: "#a78bfa",
      accentBg: "rgba(167,139,250,0.08)",
      accentBorder: "rgba(167,139,250,0.2)",
      // trend: "High demand",
      trendUp: false,
    },
  ]);

  // Get current time greeting
  const greeting = (() => {
    const h = new Date().getHours();
    if (h < 12) return "Good morning";
    if (h < 18) return "Good afternoon";
    return "Good evening";
  })();

  // Current date
  const dateStr = new Date().toLocaleDateString("en-US", {
    weekday: "long", month: "long", day: "numeric", year: "numeric"
  });
</script>

<svelte:head>
  <title>Dashboard — EMS</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin="anonymous" />
  <link href="https://fonts.googleapis.com/css2?family=Syne:wght@400;600;700;800&family=Geist+Mono:wght@400;500&display=swap" rel="stylesheet" />
</svelte:head>

<style>
  /* ── TOKENS ── */
  :root {
    --page-bg:     #040d1a;
    --surface-1:   rgba(12,28,55,0.6);
    --surface-2:   rgba(255,255,255,0.03);
    --border-1:    rgba(255,255,255,0.06);
    --border-2:    rgba(255,255,255,0.04);
    --text-hi:     #e2ecff;
    --text-mid:    rgba(148,185,255,0.6);
    --text-lo:     rgba(96,130,200,0.45);
    --blue:        #3b82f6;
    --blue-glow:   rgba(59,130,246,0.15);
    --font:        'Syne', sans-serif;
    --mono:        'Geist Mono', monospace;
  }

  /* ── PAGE WRAPPER ── */
  .page {
    min-height: 100vh;
    background: var(--page-bg);
    font-family: var(--font);
    padding: 2.5rem 2rem 5rem;
    position: relative;
    overflow-x: hidden;
  }

  /* Background atmosphere */
  .page::before {
    content: '';
    position: fixed;
    width: 700px; height: 700px;
    top: -200px; right: -200px;
    background: radial-gradient(circle, rgba(29,78,216,0.07) 0%, transparent 65%);
    pointer-events: none;
  }

  .page::after {
    content: '';
    position: fixed;
    width: 500px; height: 500px;
    bottom: -150px; left: -100px;
    background: radial-gradient(circle, rgba(29,78,216,0.05) 0%, transparent 65%);
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
    margin-bottom: 3rem;
  }

  @media (min-width: 900px) {
    .hero {
      flex-direction: row;
      align-items: flex-end;
      justify-content: space-between;
    }
  }

  .hero-left {}

  .hero-eyebrow {
    display: flex;
    align-items: center;
    gap: 0.6rem;
    margin-bottom: 0.75rem;
  }

  .eyebrow-dot {
    width: 6px; height: 6px;
    border-radius: 50%;
    background: #22c55e;
    box-shadow: 0 0 8px rgba(34,197,94,0.8);
  }

  .eyebrow-text {
    font-size: 0.6rem;
    font-weight: 700;
    letter-spacing: 0.38em;
    text-transform: uppercase;
    color: var(--text-lo);
  }

  .hero-title {
    font-size: clamp(2.2rem, 4vw, 3.6rem);
    font-weight: 800;
    letter-spacing: -0.03em;
    color: var(--text-hi);
    line-height: 1.05;
    margin: 0 0 0.75rem;
  }

  .hero-title span {
    color: transparent;
    -webkit-text-stroke: 1.5px rgba(96,165,250,0.45);
  }

  .hero-sub {
    font-size: 0.85rem;
    font-weight: 400;
    color: var(--text-mid);
    line-height: 1.6;
  }

  .hero-sub strong {
    color: var(--text-hi);
    font-weight: 700;
  }

  /* Hero right: system status widget */
  .status-widget {
    background: rgba(12,28,55,0.7);
    border: 1px solid var(--border-1);
    border-radius: 20px;
    padding: 1.25rem 1.5rem;
    backdrop-filter: blur(16px);
    min-width: 240px;
    flex-shrink: 0;
    box-shadow: inset 0 1px 0 rgba(255,255,255,0.04);
  }

  .status-label {
    font-size: 0.55rem;
    font-weight: 700;
    letter-spacing: 0.35em;
    text-transform: uppercase;
    color: var(--text-lo);
    margin-bottom: 0.85rem;
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }

  .status-label::before {
    content: '';
    display: block;
    width: 14px; height: 1px;
    background: var(--text-lo);
  }

  .status-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 0.6rem;
  }

  .status-name {
    font-size: 0.65rem;
    font-weight: 600;
    letter-spacing: 0.08em;
    text-transform: uppercase;
    color: var(--text-mid);
  }

  .status-pct {
    font-family: var(--mono);
    font-size: 0.65rem;
    font-weight: 500;
    color: var(--blue);
  }

  .progress-track {
    height: 3px;
    background: rgba(255,255,255,0.05);
    border-radius: 99px;
    overflow: hidden;
    margin-bottom: 0.85rem;
  }

  .progress-fill {
    height: 100%;
    border-radius: 99px;
    background: linear-gradient(90deg, #1d4ed8, #3b82f6);
    box-shadow: 0 0 8px rgba(59,130,246,0.6);
    transition: width 1.2s cubic-bezier(0.22, 1, 0.36, 1);
  }

  .status-date {
    font-size: 0.6rem;
    font-weight: 500;
    color: var(--text-lo);
    font-family: var(--mono);
  }

  /* ── STATS GRID ── */
  .stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
    gap: 1rem;
    margin-bottom: 3rem;
  }

  .stat-card {
    background: var(--surface-1);
    border: 1px solid var(--border-1);
    border-radius: 20px;
    padding: 1.5rem;
    display: flex;
    flex-direction: column;
    gap: 1rem;
    backdrop-filter: blur(16px);
    position: relative;
    overflow: hidden;
    transition: transform 0.25s ease, border-color 0.25s ease, box-shadow 0.25s ease;
    cursor: default;
  }

  /* Accent glow in top-left corner */
  .stat-card::before {
    content: '';
    position: absolute;
    top: 0; left: 0;
    width: 120px; height: 120px;
    border-radius: 50%;
    background: var(--card-accent-bg, transparent);
    transform: translate(-40%, -40%);
    pointer-events: none;
    transition: opacity 0.3s;
    opacity: 0.6;
  }

  .stat-card:hover {
    transform: translateY(-3px);
    box-shadow: 0 16px 40px rgba(0,0,0,0.35);
  }

  .stat-card:hover::before { opacity: 1; }

  .stat-top {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
  }

  .stat-icon {
    width: 42px; height: 42px;
    border-radius: 13px;
    display: flex; align-items: center; justify-content: center;
    flex-shrink: 0;
    transition: transform 0.3s cubic-bezier(0.34,1.56,0.64,1);
  }

  .stat-card:hover .stat-icon {
    transform: scale(1.1) rotate(-6deg);
  }

  .stat-badge {
    font-size: 0.55rem;
    font-weight: 700;
    letter-spacing: 0.2em;
    text-transform: uppercase;
    padding: 0.25rem 0.6rem;
    border-radius: 99px;
    background: rgba(255,255,255,0.04);
    border: 1px solid rgba(255,255,255,0.07);
    color: var(--text-lo);
    white-space: nowrap;
  }

  .stat-bottom {}

  .stat-label {
    font-size: 0.6rem;
    font-weight: 700;
    letter-spacing: 0.2em;
    text-transform: uppercase;
    color: var(--text-lo);
    margin-bottom: 0.35rem;
  }

  .stat-value-row {
    display: flex;
    align-items: baseline;
    gap: 0.4rem;
    margin-bottom: 0.5rem;
  }

  .stat-value {
    font-size: 2rem;
    font-weight: 800;
    letter-spacing: -0.04em;
    color: var(--text-hi);
    line-height: 1;
  }

  .stat-unit {
    font-size: 0.7rem;
    font-weight: 600;
    letter-spacing: 0.1em;
    text-transform: uppercase;
    color: var(--text-lo);
  }

  .stat-trend {
    display: flex;
    align-items: center;
    gap: 0.3rem;
    font-size: 0.62rem;
    font-weight: 600;
    color: var(--text-lo);
  }

  .trend-dot {
    width: 5px; height: 5px;
    border-radius: 50%;
    flex-shrink: 0;
  }

  /* ── SECTION HEADER ── */
  .section-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 1.5rem;
    gap: 1rem;
  }

  .section-title-wrap {
    display: flex;
    align-items: center;
    gap: 0.75rem;
  }

  .section-pip {
    width: 3px;
    height: 22px;
    border-radius: 99px;
    background: linear-gradient(180deg, #3b82f6, #1e3a8a);
    box-shadow: 0 0 10px rgba(59,130,246,0.5);
    flex-shrink: 0;
  }

  .section-title {
    font-size: 1.1rem;
    font-weight: 800;
    letter-spacing: -0.02em;
    color: var(--text-hi);
  }

  .section-count {
    font-size: 0.58rem;
    font-weight: 700;
    letter-spacing: 0.2em;
    text-transform: uppercase;
    color: var(--text-lo);
    background: rgba(255,255,255,0.04);
    border: 1px solid var(--border-1);
    border-radius: 99px;
    padding: 0.2rem 0.6rem;
    font-family: var(--mono);
  }

  .section-action {
    display: flex;
    align-items: center;
    gap: 0.4rem;
    padding: 0.55rem 1.1rem;
    background: rgba(29,78,216,0.1);
    border: 1px solid rgba(29,78,216,0.22);
    border-radius: 12px;
    font-family: var(--font);
    font-size: 0.65rem;
    font-weight: 700;
    letter-spacing: 0.14em;
    text-transform: uppercase;
    color: #60a5fa;
    text-decoration: none;
    cursor: pointer;
    transition: all 0.2s ease;
  }

  .section-action:hover {
    background: rgba(29,78,216,0.2);
    border-color: rgba(59,130,246,0.4);
    transform: translateY(-1px);
    box-shadow: 0 4px 16px rgba(29,78,216,0.25);
  }

  /* ── ENROLLMENT PANEL ── */
  .enrollment-panel {
    background: var(--surface-1);
    border: 1px solid var(--border-1);
    border-radius: 24px;
    overflow: hidden;
    backdrop-filter: blur(16px);
    box-shadow: inset 0 1px 0 rgba(255,255,255,0.03);
  }

  /* Loading skeleton */
  .skeleton-rows {
    display: flex;
    flex-direction: column;
    gap: 0;
  }

  .skeleton-row {
    display: flex;
    align-items: center;
    gap: 1rem;
    padding: 1.1rem 1.5rem;
    border-bottom: 1px solid var(--border-2);
    animation: shimmer 1.6s ease-in-out infinite;
  }

  .skeleton-row:last-child { border-bottom: none; }

  .sk-circle {
    width: 36px; height: 36px;
    border-radius: 10px;
    background: rgba(255,255,255,0.04);
    flex-shrink: 0;
  }

  .sk-lines { flex: 1; display: flex; flex-direction: column; gap: 0.4rem; }

  .sk-line {
    height: 8px;
    border-radius: 4px;
    background: rgba(255,255,255,0.04);
  }

  .sk-line.short { width: 40%; }

  @keyframes shimmer {
    0%,100% { opacity: 0.6; }
    50%      { opacity: 1;   }
  }
</style>

<div class="page" in:fade={{ duration: 400 }}>
  <div class="inner">

    <!-- ── HERO ── -->
    <header class="hero" in:fly={{ y: 16, duration: 500, delay: 60 }}>
      <div class="hero-left">
        <h1 class="hero-title">
          {greeting}<br>
          <!-- <span>{userStore.current?.name?.split(" ")[0] ?? "Scholar"}</span>. -->
        </h1>
      </div>

    </header>

    <!-- ── STATS ── -->
    <section class="stats-grid">
      {#each cards as card, i}
        <div
          class="stat-card"
          style="--card-accent-bg: {card.accentBg}; border-color: {isLoading ? 'var(--border-1)' : card.accentBorder};"
          in:fly={{ y: 18, duration: 440, delay: 260 + i * 80 }}
        >
          <div class="stat-top">
            <div class="stat-icon" style="background:{card.accentBg}; border:1px solid {card.accentBorder};">
              <svg width="18" height="18" fill="none" stroke={card.accent} stroke-width="2" stroke-linecap="round" stroke-linejoin="round" viewBox="0 0 24 24">
                <path d={card.icon} />
              </svg>
            </div>
            <!-- <span class="stat-badge">{card.trendUp ? "↑" : "↓"}&nbsp;{card.trend}</span> -->
          </div>

          <div class="stat-bottom">
            <div class="stat-label">{card.label}</div>
            <div class="stat-value-row">
              <span class="stat-value" style="color:{isLoading ? 'rgba(255,255,255,0.1)' : 'var(--text-hi)'}">
                {isLoading ? "—" : card.value}
              </span>
              {#if card.unit}
                <span class="stat-unit">{card.unit}</span>
              {/if}
            </div>
            <!-- <div class="stat-trend">
              <span class="trend-dot" style="background:{card.trendUp ? '#22c55e' : '#f59e0b'}; box-shadow:0 0 6px {card.trendUp ? 'rgba(34,197,94,0.6)' : 'rgba(245,158,11,0.6)'}"></span>
              {card.trend}
            </div> -->
          </div>
        </div>
      {/each}
    </section>

    <!-- ── ENROLLMENT LEDGER ── -->
    <section in:fly={{ y: 18, duration: 440, delay: 580 }}>
      <div class="section-head">
        <div class="section-title-wrap">
          <div class="section-pip"></div>
          <h2 class="section-title">Active Enrollments</h2>
          <span class="section-count">{isLoading ? "…" : dashboardStats.activeCourses}</span>
        </div>
        <a href="/enrollments" class="section-action">
          Manage Records
          <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
            <path d="M13 7l5 5m0 0l-5 5m5-5H6" />
          </svg>
        </a>
      </div>

      <div class="enrollment-panel">
        {#if isLoading}
          <div class="skeleton-rows">
            {#each Array(5) as _}
              <div class="skeleton-row">
                <div class="sk-circle"></div>
                <div class="sk-lines">
                  <div class="sk-line"></div>
                  <div class="sk-line short"></div>
                </div>
              </div>
            {/each}
          </div>
        {:else}
          <EnrollmentList
            {isLoading}
            onDeleteEnrollment={() => {}}
            onEnrollCourse={() => {}}
          />
        {/if}
      </div>
    </section>

  </div>
</div>