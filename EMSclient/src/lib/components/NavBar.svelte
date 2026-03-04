<script lang="ts">
  import { userStore } from "$lib/stores/user.svelte";
  import { base } from "$app/paths";
  import { page } from "$app/state";
  import { fade, slide } from "svelte/transition";

  let isMenuOpen = $state(false);

  const isActive = (path: string) => {
    if (path === "/")
      return page.url.pathname === "/" || page.url.pathname === "/enrollments";
    return page.url.pathname.startsWith(path);
  };

  const menuItems = $derived([
    { label: "Dashboard", path: "/", roles: ["STUDENT", "ADMIN"] },
    { label: "Admin Center", path: "/admin", roles: ["ADMIN"] },
    { label: "Students", path: "/admin/students", roles: ["ADMIN"] },
    { label: "Sections", path: "/admin/sections", roles: ["ADMIN"] },
    { label: "Profile", path: "/profile", roles: ["STUDENT", "ADMIN"] },
  ]);

  const filteredItems = $derived(
    menuItems.filter((item) =>
      item.roles.includes(userStore.current?.role ?? "STUDENT"),
    ),
  );
</script>

<nav class="sticky top-0 z-[100] w-full px-6 py-4">
  <div class="max-w-7xl mx-auto">
    <div
      class="glass dark:bg-[#0c1c37]/80 rounded-[28px] border-white/20 dark:border-white/10 px-8 py-3 flex items-center justify-between shadow-2xl backdrop-blur-3xl transition-all duration-500"
    >
      <!-- Brand -->
      <a href="/" class="flex items-center gap-3 group">
        <div
          class="w-10 h-10 bg-blue-900 rounded-xl flex items-center justify-center text-white shadow-lg group-hover:rotate-6 transition-transform duration-300"
        >
          <svg
            class="w-6 h-6"
            fill="none"
            stroke="currentColor"
            stroke-width="2.5"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M12 6.042A8.967 8.967 0 006 3.75c-1.052 0-2.062.18-3 .512v14.25A8.987 8.987 0 016 18c2.305 0 4.408.867 6 2.292m0-14.25a8.966 8.966 0 016-2.292c1.052 0 2.062.18 3 .512v14.25A8.987 8.987 0 0018 18c-2.305 0-4.408.867-6 2.292m0-14.25v14.25"
            />
          </svg>
        </div>
        <div>
          <span
            class="text-xl font-black text-blue-900 dark:text-white tracking-tighter uppercase italic leading-none"
            >EMS</span
          >
          <p
            class="text-[8px] font-bold text-blue-900/40 dark:text-blue-400/40 uppercase tracking-[0.3em] leading-none"
          >
            Institutional
          </p>
        </div>
      </a>

      <!-- Desktop Nav -->
      <div class="hidden md:flex items-center gap-2">
        {#each filteredItems as item}
          <a
            href={item.path}
            class="px-5 py-2 rounded-[14px] text-[11px] font-black uppercase tracking-widest transition-all duration-300 {isActive(
              item.path,
            )
              ? 'bg-blue-900 text-white shadow-xl scale-105'
              : 'text-blue-900/60 dark:text-blue-300/60 hover:text-blue-900 dark:hover:text-white hover:bg-white/10'}"
          >
            {item.label}
          </a>
        {/each}

        <div class="w-px h-6 bg-blue-900/10 dark:bg-white/10 mx-4"></div>

        <div class="flex items-center gap-3">
          <div class="text-right hidden xl:block">
            <p
              class="text-[10px] font-black text-blue-900 dark:text-white uppercase tracking-wider"
            >
              {userStore.current?.name ?? "User"}
            </p>
            <p
              class="text-[8px] font-bold text-blue-900/40 dark:text-blue-300/40 uppercase tracking-widest italic"
            >
              {userStore.current?.role}
            </p>
          </div>
          <div
            class="w-10 h-10 rounded-full border-2 border-blue-900/20 dark:border-white/20 p-0.5"
          >
            <div
              class="w-full h-full rounded-full bg-blue-900 flex items-center justify-center text-[10px] font-black text-white italic"
            >
              {userStore.current?.name?.charAt(0) ?? "U"}
            </div>
          </div>
        </div>
      </div>

      <!-- Mobile Menu Button -->
      <button
        onclick={() => (isMenuOpen = !isMenuOpen)}
        class="md:hidden w-10 h-10 flex items-center justify-center text-blue-900 dark:text-white hover:bg-white/10 rounded-xl transition-colors"
      >
        <svg
          class="w-6 h-6"
          fill="none"
          stroke="currentColor"
          stroke-width="2.5"
          viewBox="0 0 24 24"
        >
          {#if isMenuOpen}
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M6 18L18 6M6 6l12 12"
            />
          {:else}
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M3.75 6.75h16.5M3.75 12h16.5m-16.5 5.25h16.5"
            />
          {/if}
        </svg>
      </button>
    </div>

    <!-- Mobile Menu Overlay -->
    {#if isMenuOpen}
      <div
        transition:slide
        class="md:hidden mt-4 glass dark:bg-[#0c1c37]/95 rounded-[32px] border-white/20 dark:border-white/10 p-6 shadow-3xl flex flex-col gap-3"
      >
        {#each filteredItems as item}
          <a
            href={item.path}
            onclick={() => (isMenuOpen = false)}
            class="px-6 py-4 rounded-2xl text-[12px] font-black uppercase tracking-widest transition-all {isActive(
              item.path,
            )
              ? 'bg-blue-900 text-white'
              : 'text-blue-900/60 dark:text-blue-300/60 hover:bg-white/5'}"
          >
            {item.label}
          </a>
        {/each}

        <div class="h-px bg-white/10 my-2"></div>

        <div class="flex items-center gap-4 px-4 py-2">
          <div
            class="w-12 h-12 rounded-full bg-blue-900 flex items-center justify-center text-xs font-black text-white italic"
          >
            {userStore.current?.name?.charAt(0) ?? "U"}
          </div>
          <div>
            <p
              class="text-xs font-black text-blue-900 dark:text-white uppercase italic"
            >
              {userStore.current?.name}
            </p>
            <p
              class="text-[9px] font-bold text-blue-900/40 dark:text-blue-300/40 uppercase tracking-widest"
            >
              {userStore.current?.role}
            </p>
          </div>
        </div>
      </div>
    {/if}
  </div>
</nav>
