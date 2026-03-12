<script lang="ts">
  import NavBar from "$lib/components/NavBar.svelte";
  import { userStore } from "$lib/stores/user.svelte";
  import { themeStore } from "$lib/stores/theme.svelte";
  import { page } from "$app/stores";
  import { browser } from "$app/environment";
  import { onMount } from "svelte";
  import "../app.css";

  let Toaster: any = null;

  // Load toaster only in browser
  let { data, children } = $props();

  // Load toaster only in browser
  onMount(async () => {
    if (browser) {
      const mod = await import("svelte-sonner");
      Toaster = mod.Toaster;
    }
  });

  // Sync user state from server data to store
  $effect(() => {
    if (data?.user) {
      userStore.setUser(data.user);
    } else {
      userStore.logout();
    }
  });

  $effect(() => {
    if (browser) {
      if (themeStore.isDark) {
        document.documentElement.classList.add('dark');
      } else {
        document.documentElement.classList.remove('dark');
      }
    }
  });

  let showNavBar = $derived(userStore.isAuthenticated &&
    !$page.url.pathname.startsWith("/login") &&
    !$page.url.pathname.startsWith("/register"));
</script>

{#if Toaster}
  <svelte:component this={Toaster} position="top-right" richColors />
{/if}

{#if showNavBar}
  <NavBar />
{/if}

<main>
  {@render children()}
</main>
