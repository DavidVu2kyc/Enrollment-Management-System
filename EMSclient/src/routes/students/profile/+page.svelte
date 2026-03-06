<script lang="ts">
  import { userStore } from "$lib/stores/user.svelte";
  import { fly, fade } from "svelte/transition";

  let { data } = $props();

  const parts = (data.profile?.name ?? "").trim().split(/\s+/);
  let firstName  = $state(parts[0] ?? "");
  let lastName   = $state(parts.slice(1).join(" "));
  let isUpdating = $state(false);
  let feedback   = $state<{ msg: string; type: "success" | "error" } | null>(null);

  const initials = $derived(
    `${firstName?.[0] ?? ""}${lastName?.[0] ?? ""}`.toUpperCase() || "?"
  );

  const handleSubmit = async (e: SubmitEvent) => {
    e.preventDefault();
    isUpdating = true;
    feedback = null;
    const name = `${firstName} ${lastName}`.trim();
    try {
      const { apiClient } = await import("$lib/api/client");
      await apiClient.put(`/students/${data.profile.id}`, { name });
      const current = userStore.current;
      if (current) userStore.update({ ...current, name });
      feedback = { msg: "Profile updated successfully.", type: "success" };
    } catch (err: any) {
      feedback = { msg: err?.message ?? "Failed to update profile.", type: "error" };
    } finally {
      isUpdating = false;
    }
  };
</script>

<div class="page" in:fade={{ duration: 300 }}>
  <div class="card" in:fly={{ y: 20, duration: 400 }}>

    <div class="profile-header">
      <div class="avatar">{initials}</div>
      <div>
        <h2>{firstName} {lastName || "—"}</h2>
        <p>{data.profile?.email ?? ""}</p>
      </div>
    </div>

    <div class="divider"></div>

    {#if feedback}
      <div class="alert {feedback.type}" in:fly={{ y: -8, duration: 200 }} out:fade={{ duration: 150 }}>
        {#if feedback.type === "success"}
          <svg viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/></svg>
        {:else}
          <svg viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clip-rule="evenodd"/></svg>
        {/if}
        {feedback.msg}
      </div>
    {/if}

    <form onsubmit={handleSubmit}>
      <div class="field">
        <label>Email</label>
        <div class="input-locked">
          <svg viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z" clip-rule="evenodd"/></svg>
          {data.profile?.email ?? "—"}
        </div>
      </div>

      <div class="row">
        <div class="field">
          <label for="firstName">First Name</label>
          <input id="firstName" type="text" bind:value={firstName} placeholder="First name" required />
        </div>
        <div class="field">
          <label for="lastName">Last Name</label>
          <input id="lastName" type="text" bind:value={lastName} placeholder="Last name" />
        </div>
      </div>

      <button type="submit" disabled={isUpdating}>
        {#if isUpdating}<span class="spinner"></span> Saving…{:else}Save Changes{/if}
      </button>
    </form>

  </div>
</div>

<style>
  @import url('https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap');

  :global(body) {
    margin: 0;
    font-family: 'Plus Jakarta Sans', sans-serif;
    background: #0f172a;
    color: #e2e8f0;
  }

  .page {
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 2rem;
  }

  .card {
    width: 100%;
    max-width: 460px;
    background: #1e293b;
    border: 1px solid rgba(148,163,184,.1);
    border-radius: 16px;
    padding: 2rem;
    display: flex;
    flex-direction: column;
    gap: 1.4rem;
    box-shadow: 0 20px 60px rgba(0,0,0,.4);
  }

  .profile-header { display: flex; align-items: center; gap: 1rem; }

  .avatar {
    width: 50px; height: 50px; border-radius: 12px;
    background: #2563eb; color: #fff;
    display: flex; align-items: center; justify-content: center;
    font-size: 1.05rem; font-weight: 700; flex-shrink: 0;
  }

  .profile-header h2 { margin: 0 0 .2rem; font-size: .95rem; font-weight: 700; color: #f1f5f9; }
  .profile-header p  { margin: 0; font-size: .75rem; color: #64748b; }

  .divider { height: 1px; background: rgba(148,163,184,.08); }

  .alert {
    display: flex; align-items: center; gap: .5rem;
    padding: .6rem .85rem; border-radius: 8px;
    font-size: .8rem; font-weight: 500;
  }
  .alert svg { width: 14px; height: 14px; flex-shrink: 0; }
  .alert.success { background: rgba(34,197,94,.1); border: 1px solid rgba(34,197,94,.2); color: #4ade80; }
  .alert.error   { background: rgba(239,68,68,.1);  border: 1px solid rgba(239,68,68,.2);  color: #f87171; }

  form { display: flex; flex-direction: column; gap: .95rem; }

  .row { display: grid; grid-template-columns: 1fr 1fr; gap: .8rem; }

  .field { display: flex; flex-direction: column; gap: .32rem; }

  label {
    font-size: .68rem; font-weight: 600;
    letter-spacing: .06em; text-transform: uppercase; color: #64748b;
  }

  input, .input-locked {
    padding: .58rem .82rem; border-radius: 8px;
    font-size: .855rem; font-family: 'Plus Jakarta Sans', sans-serif;
    background: #0f172a; border: 1px solid rgba(148,163,184,.12);
    color: #e2e8f0; outline: none; width: 100%; box-sizing: border-box;
    transition: border-color .15s, box-shadow .15s;
  }
  input::placeholder { color: #334155; }
  input:focus { border-color: #3b82f6; box-shadow: 0 0 0 3px rgba(59,130,246,.15); }

  .input-locked {
    display: flex; align-items: center; gap: .45rem;
    color: #475569; cursor: not-allowed;
  }
  .input-locked svg { width: 13px; height: 13px; opacity: .5; flex-shrink: 0; }

  button {
    height: 41px; border: none; border-radius: 8px;
    background: #2563eb; color: #fff;
    font-family: 'Plus Jakarta Sans', sans-serif;
    font-size: .855rem; font-weight: 600; cursor: pointer;
    display: flex; align-items: center; justify-content: center; gap: .45rem;
    transition: background .15s, box-shadow .15s, transform .1s;
    box-shadow: 0 2px 10px rgba(37,99,235,.35);
  }
  button:hover:not(:disabled) { background: #1d4ed8; box-shadow: 0 4px 16px rgba(37,99,235,.45); }
  button:active:not(:disabled) { transform: scale(.99); }
  button:disabled { opacity: .5; cursor: wait; }

  .spinner {
    width: 13px; height: 13px; border-radius: 50%;
    border: 2px solid rgba(255,255,255,.3); border-top-color: #fff;
    animation: spin .7s linear infinite;
  }
  @keyframes spin { to { transform: rotate(360deg); } }

  @media (max-width: 460px) {
    .row { grid-template-columns: 1fr; }
    .card { padding: 1.5rem; }
  }
</style>