<script lang="ts">
  import { userStore } from "$lib/stores/user.svelte";
  import { page } from "$app/state";
  import { fade, slide } from "svelte/transition";

  let { children } = $props();

  const navItems = [
    {
      label: "Control Center",
      href: "/admin",
      icon: `<svg class="w-5 h-5" fill="none" stroke="currentColor" stroke-width="2.5" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M3.75 6A2.25 2.25 0 016 3.75h2.25A2.25 2.25 0 0110.5 6v2.25a2.25 2.25 0 01-2.25 2.25H6a2.25 2.25 0 01-2.25-2.25V6zM3.75 15.75A2.25 2.25 0 016 13.5h2.25a2.25 2.25 0 012.25 2.25V18a2.25 2.25 0 01-2.25 2.25H6A2.25 2.25 0 013.75 18v-2.25zM13.5 6a2.25 2.25 0 012.25-2.25H18A2.25 2.25 0 0120.25 6v2.25A2.25 2.25 0 0118 10.5h-2.25a2.25 2.25 0 01-2.25-2.25V6zM13.5 15.75a2.25 2.25 0 012.25-2.25H18a2.25 2.25 0 012.25 2.25V18A2.25 2.25 0 0118 20.25h-2.25A2.25 2.25 0 0113.5 18v-2.25z"></path></svg>`,
    },
    {
      label: "Institutional Registry",
      href: "/admin/students",
      icon: `<svg class="w-5 h-5" fill="none" stroke="currentColor" stroke-width="2.5" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M15 19.128a9.38 9.38 0 002.625.372 9.337 9.337 0 004.121-.952 4.125 4.125 0 00-2.533-5.226 10.081 10.081 0 00-4.762-.146 4.125 4.125 0 00-2.533 5.226 9.317 9.317 0 001.082 1.954zm0 0l-2.012 2.012M12 18.25V21M12 3v3m0 12h.01M5.121 18.879l1.061-1.061M18.879 5.121l-1.061 1.061M3 12h3m12 0h3M5.121 5.121l1.061 1.061M18.879 18.879l-1.061-1.061"></path></svg>`,
    },
    {
      label: "Course Protocols",
      href: "/admin/sections",
      icon: `<svg class="w-5 h-5" fill="none" stroke="currentColor" stroke-width="2.5" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M12 6.042A8.967 8.967 0 006 3.75c-1.052 0-2.062.18-3 .512v14.25A8.987 8.987 0 016 18c2.305 0 4.408.867 6 2.292m0-14.25a8.966 8.966 0 016-2.292c1.052 0 2.062.18 3 .512v14.25A8.987 8.987 0 0018 18c-2.305 0-4.408.867-6 2.292m0-14.25v14.25"></path></svg>`,
    },
  ];

  let currentPath = $derived(page.url.pathname);
</script>

<div
  class="flex flex-col lg:flex-row gap-12 min-h-[calc(100vh-200px)] animate-in fade-in duration-700"
>
  <!-- Admin Sidebar -->
  <aside class="w-full lg:w-80 flex-shrink-0">
    <div
      class="bg-white dark:bg-gray-800 rounded-3xl shadow-xl border border-gray-200 dark:border-gray-700 overflow-hidden sticky top-32 transition-all duration-300"
    >
      <div class="p-10 bg-blue-900 text-white relative overflow-hidden">
        <div class="absolute inset-0 bg-white/5 opacity-20"></div>
        <div class="relative z-10 flex items-center gap-5">
          <div
            class="w-14 h-14 bg-white/10 rounded-2xl flex items-center justify-center backdrop-blur-md shadow-inner border border-white/20"
          >
            <svg
              class="w-8 h-8"
              fill="none"
              stroke="currentColor"
              stroke-width="2.5"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"
              ></path>
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"
              ></path>
            </svg>
          </div>
          <div class="flex flex-col">
            <h2
              class="text-xl font-black tracking-tighter uppercase italic leading-none"
            >
              Master Console
            </h2>
            <p
              class="text-[9px] text-blue-300/80 font-black uppercase tracking-[0.3em] mt-1"
            >
              Institutional Intelligence
            </p>
          </div>
        </div>
      </div>

      <nav class="p-4 py-8">
        <ul class="space-y-2">
          {#each navItems as item}
            <li>
              <a
                href={item.href}
                class={`flex items-center gap-4 px-6 py-4 rounded-[24px] text-[11px] font-black uppercase tracking-[0.2em] italic transition-all duration-500 ${
                  currentPath === item.href
                    ? "bg-blue-900 text-white shadow-2xl scale-[1.05] translate-x-2"
                    : "text-gray-400 hover:text-blue-900 dark:hover:text-white dark:hover:bg-white/5"
                }`}
              >
                <span class="flex-shrink-0">
                  {@html item.icon}
                </span>
                {item.label}
              </a>
            </li>
          {/each}
        </ul>
      </nav>

      <div class="px-6 pb-10">
        <div
          class="bg-blue-900/5 dark:bg-white/5 rounded-[32px] p-6 border-2 border-dashed border-blue-900/5 dark:border-white/5"
        >
          <p
            class="text-[9px] font-black text-blue-900/40 dark:text-blue-300/20 uppercase tracking-[0.4em] mb-3"
          >
            Synchronization Status
          </p>
          <div class="flex items-center gap-3">
            <div
              class="w-2.5 h-2.5 rounded-full bg-emerald-500 animate-pulse shadow-[0_0_10px_rgba(16,185,129,0.5)]"
            ></div>
            <span
              class="text-[10px] font-black uppercase tracking-widest text-blue-900 dark:text-blue-300"
              >Protocol Integrated</span
            >
          </div>
        </div>
      </div>
    </div>
  </aside>

  <!-- Admin Content -->
  <main class="flex-1 w-full relative z-10" transition:fade>
    {@render children()}
  </main>
</div>
