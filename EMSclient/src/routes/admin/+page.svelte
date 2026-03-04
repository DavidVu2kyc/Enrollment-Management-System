<script lang="ts">
  import { onMount } from "svelte";
  import { fly } from "svelte/transition";
  import Button from "$lib/components/Button.svelte";

  let stats = $state({
    totalStudents: 1240,
    totalSections: 48,
    activeEnrollments: 3850,
    pendingApprovals: 12,
  });

  let isLoading = $state(true);

  onMount(async () => {
    setTimeout(() => {
      isLoading = false;
    }, 600);
  });

  const cards = [
    {
      label: "Live Student Body",
      value: stats.totalStudents,
      color: "text-blue-900 dark:text-blue-400",
      icon: `<svg class="w-6 h-6" fill="none" stroke="currentColor" stroke-width="2.5" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M15 19.128a9.38 9.38 0 002.625.372 9.337 9.337 0 004.121-.952 4.125 4.125 0 00-2.533-5.226 10.081 10.081 0 00-4.762-.146 4.125 4.125 0 00-2.533 5.226 9.317 9.317 0 001.082 1.954zm0 0l-2.012 2.012M12 18.25V21M12 3v3m0 12h.01M5.121 18.879l1.061-1.061M18.879 5.121l-1.061 1.061M3 12h3m12 0h3M5.121 5.121l1.061 1.061M18.879 18.879l-1.061-1.061"></path></svg>`,
    },
    {
      label: "Active Sessions",
      value: stats.totalSections,
      color: "text-indigo-900 dark:text-indigo-400",
      icon: `<svg class="w-6 h-6" fill="none" stroke="currentColor" stroke-width="2.5" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M3.75 6A2.25 2.25 0 016 3.75h2.25A2.25 2.25 0 0110.5 6v2.25a2.25 2.25 0 01-2.25 2.25H6a2.25 2.25 0 01-2.25-2.25V6zM3.75 15.75A2.25 2.25 0 016 13.5h2.25a2.25 2.25 0 012.25 2.25V18a2.25 2.25 0 01-2.25 2.25H6A2.25 2.25 0 013.75 18v-2.25zM13.5 6a2.25 2.25 0 012.25-2.25H18A2.25 2.25 0 0120.25 6v2.25A2.25 2.25 0 0118 10.5h-2.25a2.25 2.25 0 01-2.25-2.25V6zM13.5 15.75a2.25 2.25 0 012.25-2.25H18a2.25 2.25 0 012.25 2.25V18A2.25 2.25 0 0118 20.25h-2.25A2.25 2.25 0 0113.5 18v-2.25z"></path></svg>`,
    },
    {
      label: "Enrollment Yield",
      value: stats.activeEnrollments,
      color: "text-emerald-900 dark:text-emerald-400",
      icon: `<svg class="w-6 h-6" fill="none" stroke="currentColor" stroke-width="2.5" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M2.25 18L9 11.25l4.5 4.5L21.75 7.5M21.75 7.5V12m0-4.5H17.25"></path></svg>`,
    },
    {
      label: "Approval Queue",
      value: stats.pendingApprovals,
      color: "text-amber-900 dark:text-amber-400",
      icon: `<svg class="w-6 h-6" fill="none" stroke="currentColor" stroke-width="2.5" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M12 6.042A8.967 8.967 0 006 3.75c-1.052 0-2.062.18-3 .512v14.25A8.987 8.987 0 016 18c2.305 0 4.408.867 6 2.292m0-14.25a8.966 8.966 0 016-2.292c1.052 0 2.062.18 3 .512v14.25A8.987 8.987 0 0018 18c-2.305 0-4.408.867-6 2.292m0-14.25v14.25"></path></svg>`,
    },
  ];
</script>

<div class="space-y-12 animate-in fade-in duration-700">
  <div
    class="flex flex-col md:flex-row md:items-end justify-between gap-6 border-b border-gray-100 dark:border-white/5 pb-8"
  >
    <div>
      <h1
        class="text-5xl font-black text-blue-900 dark:text-white tracking-tighter uppercase italic leading-none mb-4"
      >
        Command Center
      </h1>
      <p class="text-gray-500 font-medium max-w-xl">
        A unified perspective on institutional performance, system throughput,
        and enrollment velocity for the FY 2026 academic period.
      </p>
    </div>
    <div
      class="bg-blue-900 text-white px-8 py-4 rounded-[24px] shadow-2xl relative overflow-hidden group"
    >
      <div
        class="absolute inset-0 bg-white/10 opacity-0 group-hover:opacity-100 transition-opacity"
      ></div>
      <p
        class="text-[9px] font-black uppercase tracking-[0.4em] text-blue-300/60 mb-1"
      >
        Active Cycle
      </p>
      <p class="text-lg font-black uppercase italic tracking-tighter italic">
        FALL TERM 2026
      </p>
    </div>
  </div>

  {#if isLoading}
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      {#each Array(4) as _}
        <div
          class="h-40 bg-gray-100 dark:bg-white/5 rounded-[40px] animate-pulse"
        ></div>
      {/each}
    </div>
  {:else}
    <!-- Stats Grid -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      {#each cards as card, i}
        <div
          class="glass dark:bg-white/5 p-8 rounded-[40px] border-white/40 dark:border-white/10 shadow-2xl relative overflow-hidden group hover:scale-[1.02] transition-all duration-500"
          in:fly={{ y: 20, delay: i * 80 }}
        >
          <div
            class="absolute -top-[50%] -left-[10%] w-[30%] h-[200%] bg-blue-500/5 rotate-12 blur-3xl pointer-events-none group-hover:translate-x-full transition-transform duration-1000"
          ></div>

          <div class="relative z-10">
            <div
              class={`w-14 h-14 rounded-2xl bg-white dark:bg-white/10 ${card.color} flex items-center justify-center mb-6 shadow-xl border border-gray-50 dark:border-white/5`}
            >
              {@html card.icon}
            </div>
            <p
              class="text-[10px] font-black text-gray-400 dark:text-blue-300/40 uppercase tracking-[0.3em] mb-2"
            >
              {card.label}
            </p>
            <h3
              class={`text-4xl font-black ${card.color} dark:text-white tracking-tighter leading-none italic`}
            >
              {card.value.toLocaleString()}
            </h3>
          </div>
        </div>
      {/each}
    </div>

    <!-- System Health & Critical Action -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      <div
        class="lg:col-span-2 glass dark:bg-white/5 rounded-[48px] border-white/40 dark:border-white/10 shadow-2xl overflow-hidden"
      >
        <div
          class="px-10 py-8 border-b border-gray-100 dark:border-white/5 flex items-center justify-between"
        >
          <h2
            class="font-black text-blue-900 dark:text-white uppercase tracking-tighter italic text-xl"
          >
            Core Infrastructure
          </h2>
          <span
            class="px-5 py-2 bg-emerald-50 dark:bg-emerald-500/10 text-emerald-700 dark:text-emerald-400 rounded-full text-[10px] font-black uppercase tracking-[0.2em] border border-emerald-100 dark:border-emerald-500/20 flex items-center gap-3"
          >
            <span class="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"
            ></span>
            Peak Status
          </span>
        </div>
        <div class="p-10">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            {#each [{ name: "Kernel Engine", detail: "v1.44 Stable Cluster", health: "Normal", icon: "M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" }, { name: "Protocol Mesh", detail: "99.98% Latency Compliance", health: "Stable", icon: "M13 10V3L4 14h7v7l9-11h-7z" }] as sys}
              <div
                class="flex items-center justify-between p-6 bg-gray-50/50 dark:bg-white/5 rounded-[32px] border border-gray-100 dark:border-white/5"
              >
                <div class="flex items-center gap-5">
                  <div
                    class="w-14 h-14 bg-white dark:bg-white/10 rounded-2xl flex items-center justify-center text-blue-900 dark:text-blue-200 shadow-xl"
                  >
                    <svg
                      class="w-6 h-6"
                      fill="none"
                      stroke="currentColor"
                      stroke-width="2.5"
                      viewBox="0 0 24 24"
                      ><path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        d={sys.icon}
                      ></path></svg
                    >
                  </div>
                  <div>
                    <p
                      class="text-sm font-black text-gray-900 dark:text-white uppercase tracking-tighter"
                    >
                      {sys.name}
                    </p>
                    <p
                      class="text-[10px] font-bold text-gray-400 dark:text-gray-500 uppercase"
                    >
                      {sys.detail}
                    </p>
                  </div>
                </div>
              </div>
            {/each}
          </div>
        </div>
      </div>

      <div
        class="bg-blue-900 dark:bg-blue-950 rounded-[48px] shadow-2xl p-10 text-white relative overflow-hidden flex flex-col group"
      >
        <div
          class="absolute inset-0 bg-white/5 transform translate-x-full group-hover:translate-x-0 transition-transform duration-700 ease-in-out"
        ></div>
        <div
          class="absolute top-0 right-0 w-48 h-48 bg-white/5 rounded-full -mr-24 -mt-24 blur-3xl"
        ></div>

        <div class="relative z-10 flex flex-col h-full">
          <div
            class="w-12 h-12 bg-white/10 rounded-xl flex items-center justify-center mb-6"
          >
            <svg
              class="w-6 h-6"
              fill="none"
              stroke="currentColor"
              stroke-width="2.5"
              viewBox="0 0 24 24"
              ><path
                stroke-linecap="round"
                stroke-linejoin="round"
                d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
              /></svg
            >
          </div>
          <h3
            class="text-2xl font-black uppercase italic tracking-tighter mb-4 leading-none"
          >
            Administrative Action
          </h3>
          <p class="text-blue-200/80 text-sm font-medium leading-relaxed mb-10">
            Institutional enrollment window automatically terminates in <span
              class="text-white font-black underline">72 hours</span
            >. All pending overflows must be authorized prior to
            synchronization.
          </p>

          <div class="mt-auto">
            <Button variant="glass" size="xl" style="width: 100%">
              <span class="text-blue-900">Broadcast Protocol</span>
            </Button>
          </div>
        </div>
      </div>
    </div>
  {/if}
</div>
