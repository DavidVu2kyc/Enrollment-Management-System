<script lang="ts">
  import favicon from "$lib/assets/favicon.svg";
  import NavBar from "$lib/components/NavBar.svelte";
  import { userStore } from "$lib/stores/user.svelte";
  import type { LayoutData } from "./$types";
  import "../app.css";

  let { children, data }: { children: any; data: LayoutData } = $props();

  // Initialize user store from server data
  $effect(() => {
    if (data.user) {
      userStore.login(data.user);
    }
  });
</script>

<svelte:head>
  <link rel="icon" href={favicon} />
  <title>EMS — Enrollment Management System</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link
    rel="preconnect"
    href="https://fonts.gstatic.com"
    crossorigin="anonymous"
  />
  <link
    href="https://fonts.googleapis.com/css2?family=DM+Serif+Display:ital@0;1&family=DM+Sans:wght@300;400;500;600;700;800&family=DM+Mono&display=swap"
    rel="stylesheet"
  />
</svelte:head>

<div
  class="min-h-screen transition-colors duration-500 relative selection:bg-blue-500 selection:text-white"
>
  <NavBar />
  <main
    class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 relative z-10 animate-in fade-in slide-in-from-bottom-2 duration-700"
  >
    {@render children()}
  </main>
</div>
