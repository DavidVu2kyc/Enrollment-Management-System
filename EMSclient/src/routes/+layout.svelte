<script lang="ts">
  import NavBar from "$lib/components/NavBar.svelte";
  import { page } from '$app/stores';

  // Hide NavBar on auth pages (e.g., /login, /register)
  $: showNavBar = !$page.url.pathname.startsWith('/login') && !$page.url.pathname.startsWith('/register');
</script>

{#if showNavBar}
  <NavBar />
{/if}
<main>
  <slot />
</main>