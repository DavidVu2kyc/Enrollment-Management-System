<script lang="ts">
  import NavBar from "$lib/components/NavBar.svelte";
  import { page } from "$app/stores";
  import { browser } from "$app/environment";
  import { onMount } from "svelte";

  let Toaster: any = null;

  // Load toaster only in browser
  onMount(async () => {
    if (browser) {
      const mod = await import("svelte-sonner");
      Toaster = mod.Toaster;
    }
  });

  $: showNavBar =
    !$page.url.pathname.startsWith("/login") &&
    !$page.url.pathname.startsWith("/register");
</script>

{#if Toaster}
  <svelte:component this={Toaster} position="top-right" richColors />
{/if}

{#if showNavBar}
  <NavBar />
{/if}

<main>
  <slot />
</main>
