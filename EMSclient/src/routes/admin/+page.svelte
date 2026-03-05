<script lang="ts">
  import { onMount } from "svelte";
  import { fly, fade } from "svelte/transition";
  import Button from "$lib/components/Button.svelte";

  let stats = $state({
    totalStudents: 1542,
    totalSections: 64,
    activeEnrollments: 4102,
    pendingApprovals: 9,
    serverLoad: 42,
  });

  let isLoading = $state(true);
  let liveTimeline = $state([
    {
      event: "New Enlistment",
      user: "ST-2024-012",
      time: "Just now",
      impact: "Low",
    },
    {
      event: "Section Finalized",
      user: "Admin-SYS",
      time: "2m ago",
      impact: "Critical",
    },
    {
      event: "Database Sync",
      user: "System",
      time: "5m ago",
      impact: "Nominal",
    },
  ]);

  onMount(() => {
    setTimeout(() => {
      isLoading = false;
    }, 800);

    // Simulate real-time ticks
    const interval = setInterval(() => {
      stats.serverLoad = Math.floor(Math.random() * 15) + 35;
    }, 3000);
    return () => clearInterval(interval);
  });

  const cards = $derived([
    {
      label: "Live Student Body",
      value: stats.totalStudents,
      delta: "+12%",
      color: "text-blue-400",
      icon: "M15 19.128a9.38 9.38 0 002.625.372 9.337 9.337 0 004.121-.952 4.125 4.125 0 00-2.533-5.226 10.081 10.081 0 00-4.762-.146 4.125 4.125 0 00-2.533 5.226 9.317 9.317 0 001.082 1.954zm0 0l-2.012 2.012",
    },
    {
      label: "Active Sessions",
      value: stats.totalSections,
      delta: "Stable",
      color: "text-indigo-400",
      icon: "M3.75 6A2.25 2.25 0 016 3.75h2.25A2.25 2.25 0 0110.5 6v2.25a2.25 2.25 0 01-2.25 2.25H6a2.25 2.25 0 01-2.25-2.25V6z",
    },
    {
      label: "System Throughput",
      value: stats.activeEnrollments,
      delta: "+450",
      color: "text-emerald-400",
      icon: "M2.25 18L9 11.25l4.5 4.5L21.75 7.5",
    },
    {
      label: "Approval Queue",
      value: stats.pendingApprovals,
      delta: "Action Required",
      color: "text-rose-400",
      icon: "M12 6v6m0 0v6m0-6h6m-6 0H6",
    },
  ]);
</script>

<svelte:head>
  <title>Admin Command Center &mdash; EMS</title>
</svelte:head>

<div class="admin-hub" in:fade={{ duration: 800 }}>
  <!-- Hero -->
  <header class="command-header">
    <div>
      <span
        class="text-[10px] font-black text-blue-500 uppercase tracking-widest block mb-2"
        >Institutional Node v4.0</span
      >
      <h1>Command Center</h1>
    </div>
    <div class="status-badge">
      <div class="pulse"></div>
      System Nominal
    </div>
  </header>

  {#if isLoading}
    <div class="grid grid-cols-4 gap-6 mb-12">
      {#each Array(4) as _}
        <div class="h-40 bg-slate-800/40 rounded-[32px] animate-pulse"></div>
      {/each}
    </div>
  {:else}
    <!-- Metrics -->
    <section class="metrics-grid">
      {#each cards as card, i}
        <div class="metric-tile" in:fly={{ y: 20, delay: i * 100 }}>
          <p class="metric-label">{card.label}</p>
          <p class="metric-value {card.color}">{card.value.toLocaleString()}</p>
          <p class="metric-delta">{card.delta}</p>
        </div>
      {/each}
    </section>

    <!-- Intelligence Feed -->
    <section class="intel-layout">
      <div class="intel-card" in:fly={{ y: 20, delay: 500 }}>
        <div class="intel-header">
          <h3>Real-time Nexus Feed</h3>
          <span
            class="text-[10px] font-mono text-slate-500 uppercase tracking-widest"
            >Active Threads: 124</span
          >
        </div>
        <div class="intel-body">
          {#each liveTimeline as row}
            <div class="event-row">
              <span class="event-name">{row.event}</span>
              <span class="event-user">{row.user}</span>
              <span class="event-time">{row.time}</span>
              <span class="impact-tag {row.impact.toLowerCase()}"
                >{row.impact}</span
              >
            </div>
          {/each}
        </div>
      </div>

      <div class="flex flex-col gap-6">
        <div
          class="intel-card p-8 bg-blue-600/10 border-blue-500/20"
          in:fly={{ x: 20, delay: 600 }}
        >
          <h4
            class="text-[10px] font-black text-blue-400 uppercase tracking-[0.2em] mb-4"
          >
            Core Compute
          </h4>
          <div class="flex items-center justify-between mb-4">
            <span class="text-3xl font-black text-white"
              >{stats.serverLoad}%</span
            >
            <span class="text-[10px] font-bold text-blue-400">NOMINAL LOAD</span
            >
          </div>
          <div class="w-full h-2 bg-blue-900/40 rounded-full overflow-hidden">
            <div
              class="h-full bg-blue-500 transition-all duration-1000"
              style="width: {stats.serverLoad}%"
            ></div>
          </div>
        </div>

        <div
          class="intel-card p-8 group cursor-pointer hover:bg-white/5 transition-colors"
          in:fly={{ x: 20, delay: 700 }}
        >
          <h4
            class="text-[10px] font-black text-slate-500 uppercase tracking-[0.2em] mb-4"
          >
            Administrative Protocol
          </h4>
          <p class="text-sm text-slate-300 leading-relaxed">
            The institutional enrollment window is currently <strong
              >ACTIVE</strong
            >. Critical overflows are scheduled for processing at 23:59 UTC.
          </p>
          <div class="mt-8 flex justify-end">
            <Button variant="outline" size="sm">Broadcast System Alert</Button>
          </div>
        </div>
      </div>
    </section>
  {/if}
</div>

<style>
  .admin-hub {
    padding: 3rem 0;
  }

  /* ───── Strategic Header ───── */
  .command-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 4rem;
    padding-bottom: 2rem;
    border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  }

  .command-header h1 {
    font-family: "DM Serif Display", serif;
    font-size: 4rem;
    color: #f8fafc;
    letter-spacing: -0.04em;
    font-style: italic;
    margin: 0;
  }

  .status-badge {
    background: rgba(16, 185, 129, 0.1);
    border: 1px solid rgba(16, 185, 129, 0.2);
    color: #10b981;
    padding: 0.5rem 1.25rem;
    border-radius: 99px;
    font-size: 0.7rem;
    font-weight: 800;
    text-transform: uppercase;
    letter-spacing: 0.15em;
    display: flex;
    align-items: center;
    gap: 0.75rem;
  }

  .pulse {
    width: 6px;
    height: 6px;
    background: #10b981;
    border-radius: 50%;
    box-shadow: 0 0 10px #10b981;
    animation: flash 2s infinite;
  }

  @keyframes flash {
    0%,
    100% {
      opacity: 1;
      transform: scale(1);
    }
    50% {
      opacity: 0.4;
      transform: scale(0.8);
    }
  }

  /* ───── Metrics ───── */
  .metrics-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
    gap: 1.5rem;
    margin-bottom: 4rem;
  }

  .metric-tile {
    background: rgba(15, 23, 42, 0.4);
    border: 1px solid rgba(255, 255, 255, 0.05);
    border-radius: 32px;
    padding: 2rem;
    position: relative;
    overflow: hidden;
  }

  .metric-tile::before {
    content: "";
    position: absolute;
    top: 0;
    right: 0;
    width: 100px;
    height: 100px;
    background: radial-gradient(
      circle at top right,
      rgba(255, 255, 255, 0.03),
      transparent
    );
  }

  .metric-label {
    font-size: 0.7rem;
    font-weight: 700;
    text-transform: uppercase;
    letter-spacing: 0.15em;
    color: #64748b;
    margin-bottom: 0.75rem;
  }
  .metric-value {
    font-size: 2.25rem;
    font-weight: 800;
    color: #f1f5f9;
    letter-spacing: -0.02em;
  }
  .metric-delta {
    font-size: 0.65rem;
    font-weight: 700;
    color: #94a3b8;
    margin-top: 0.5rem;
  }

  /* ───── Intelligence Feed ───── */
  .intel-layout {
    display: grid;
    grid-template-columns: 2fr 1fr;
    gap: 2rem;
  }

  @media (max-width: 1024px) {
    .intel-layout {
      grid-template-columns: 1fr;
    }
  }

  .intel-card {
    background: rgba(30, 41, 59, 0.3);
    border: 1px solid rgba(255, 255, 255, 0.05);
    border-radius: 32px;
    overflow: hidden;
  }

  .intel-header {
    padding: 1.5rem 2rem;
    background: rgba(255, 255, 255, 0.02);
    border-bottom: 1px solid rgba(255, 255, 255, 0.05);
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .intel-header h3 {
    font-size: 0.85rem;
    font-weight: 800;
    text-transform: uppercase;
    letter-spacing: 0.15em;
    color: #e2e8f0;
    margin: 0;
  }

  .event-row {
    padding: 1.25rem 2rem;
    display: grid;
    grid-template-columns: 1fr 1fr 1fr 100px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.03);
    align-items: center;
  }

  .event-row:last-child {
    border: none;
  }

  .event-name {
    font-size: 0.85rem;
    font-weight: 700;
    color: #f1f5f9;
  }
  .event-user {
    font-size: 0.75rem;
    color: #94a3b8;
    font-family: "DM Mono", monospace;
  }
  .event-time {
    font-size: 0.75rem;
    color: #64748b;
  }

  .impact-tag {
    font-size: 0.6rem;
    font-weight: 800;
    text-transform: uppercase;
    padding: 0.25rem 0.6rem;
    border-radius: 6px;
    text-align: center;
  }
  .impact-tag.critical {
    background: rgba(239, 68, 68, 0.1);
    color: #f87171;
  }
  .impact-tag.low {
    background: rgba(59, 130, 246, 0.1);
    color: #60a5fa;
  }
  .impact-tag.nominal {
    background: rgba(148, 163, 184, 0.1);
    color: #94a3b8;
  }
</style>
