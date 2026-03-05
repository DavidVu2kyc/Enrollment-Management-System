<script lang="ts">
  import { superValidate } from 'sveltekit-superforms';
  import { loginSchema } from '$lib/schemas/login.schema';
  import { enhance } from '$app/forms';
  import type { PageData, Actions } from './$types';
  export let data: PageData;
  const form = data.form;
</script>

<form use:enhance={async ({ result }: any) => {
  if (result.type === 'success') {
    // login succeeded, redirect handled by server action
  }
}}>
  <div>
    <label for="username">Username</label>
    <input id="username" name="username" type="text" bind:value={form.data.username} required />
    {#if form.errors?.username}
      <p class="error">{form.errors.username}</p>
    {/if}
  </div>
  <div>
    <label for="password">Password</label>
    <input id="password" name="password" type="password" bind:value={form.data.password} required />
    {#if form.errors?.password}
      <p class="error">{form.errors.password}</p>
    {/if}
  </div>
  {#if form.message}
    <p class="message">{form.message}</p>
  {/if}
  <button type="submit">Login</button>
</form>