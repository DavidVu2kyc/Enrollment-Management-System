<script lang="ts">
  import { userStore } from "$lib/stores/user.svelte";
  import { fly, fade } from "svelte/transition";

  let { data } = $props();

  let firstName = $state(data.profile?.firstName ?? "");
  let lastName = $state(data.profile?.lastName ?? "");

  let isUpdating = $state(false);
  let feedback = $state<{ msg: string; type: "success" | "error" } | null>(null);

  const initials = $derived(
    `${firstName?.[0] ?? ""}${lastName?.[0] ?? ""}`.toUpperCase() || "?"
  );

  const isDirty = $derived(
    firstName !== data.profile?.firstName ||
    lastName !== data.profile?.lastName
  );

  const handleSubmit = async (e: SubmitEvent) => {
    e.preventDefault();

    if (!isDirty) return;

    isUpdating = true;
    feedback = null;

    try {
      const { apiClient } = await import("$lib/api/client");

      await apiClient.put(`/students/${data.profile.studentId}`, {
        firstName,
        lastName
      });

      const current = userStore.current;
      if (current) userStore.update({ ...current, firstName, lastName });

      feedback = { msg: "Profile updated successfully.", type: "success" };

      setTimeout(() => (feedback = null), 3000);
    } catch (err: any) {
      feedback = {
        msg: err?.message ?? "Failed to update profile.",
        type: "error"
      };
    } finally {
      isUpdating = false;
    }
  };
</script>

<div class="page" in:fade={{ duration: 300 }}>
  <div class="card" in:fly={{ y: 20, duration: 400 }}>

    <!-- PROFILE HEADER -->

    <div class="profile-header">
      <div class="avatar">{initials}</div>

      <div class="profile-meta">
        <h2>{firstName} {lastName}</h2>

        <div class="meta">
          <span>ID {data.profile?.studentNumber}</span>

          {#if data.profile?.degree}
            <span>{data.profile.degree.name}</span>
          {/if}
        </div>
      </div>
    </div>

    <div class="divider"></div>

    <!-- ALERT -->

    {#if feedback}
      <div class="alert {feedback.type}" in:fly={{ y: -8 }} out:fade>
        {feedback.msg}
      </div>
    {/if}

    <!-- FORM -->

    <form onsubmit={handleSubmit}>

      <h3 class="section-title">Personal Information</h3>

      <div class="field">
        <label>Student Number</label>
        <div class="input-locked">
          {data.profile?.studentNumber}
        </div>
      </div>

      <div class="row">

        <div class="field">
          <label>First Name</label>
          <input
            bind:value={firstName}
            placeholder="First name"
            autocomplete="given-name"
            required
          />
        </div>

        <div class="field">
          <label>Last Name</label>
          <input
            bind:value={lastName}
            placeholder="Last name"
            autocomplete="family-name"
          />
        </div>

      </div>

      <button type="submit" disabled={!isDirty || isUpdating}>
        {#if isUpdating}
          <span class="spinner"></span> Saving...
        {:else}
          Save Changes
        {/if}
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
    max-width: 480px;
    background: #1e293b;
    border-radius: 16px;
    padding: 2rem;
    display: flex;
    flex-direction: column;
    gap: 1.4rem;
    border: 1px solid rgba(148,163,184,.1);
    box-shadow: 0 20px 60px rgba(0,0,0,.45);
  }

  /* HEADER */

  .profile-header {
    display: flex;
    align-items: center;
    gap: 1rem;
  }

  .avatar {
    width: 52px;
    height: 52px;
    border-radius: 12px;
    background: linear-gradient(135deg,#2563eb,#3b82f6);
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 700;
    font-size: 1rem;
    color: white;
  }

  .profile-meta {
    display: flex;
    flex-direction: column;
  }

  .profile-meta h2 {
    margin: 0;
    font-size: 1rem;
    font-weight: 700;
  }

  .meta {
    display: flex;
    gap: .5rem;
    margin-top: .3rem;
  }

  .meta span {
    font-size: .7rem;
    color: #94a3b8;
    background: rgba(148,163,184,.1);
    padding: .25rem .5rem;
    border-radius: 6px;
  }

  /* DIVIDER */

  .divider {
    height: 1px;
    background: rgba(148,163,184,.08);
  }

  /* ALERT */

  .alert {
    padding: .6rem .8rem;
    border-radius: 8px;
    font-size: .8rem;
    font-weight: 500;
  }

  .alert.success {
    background: rgba(34,197,94,.1);
    border: 1px solid rgba(34,197,94,.2);
    color: #4ade80;
  }

  .alert.error {
    background: rgba(239,68,68,.1);
    border: 1px solid rgba(239,68,68,.2);
    color: #f87171;
  }

  /* FORM */

  form {
    display: flex;
    flex-direction: column;
    gap: 1rem;
  }

  .section-title {
    font-size: .75rem;
    font-weight: 600;
    color: #94a3b8;
    text-transform: uppercase;
    letter-spacing: .05em;
  }

  .row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: .8rem;
  }

  .field {
    display: flex;
    flex-direction: column;
    gap: .35rem;
  }

  label {
    font-size: .65rem;
    text-transform: uppercase;
    letter-spacing: .06em;
    font-weight: 600;
    color: #64748b;
  }

  input,
  .input-locked {
    padding: .6rem .8rem;
    border-radius: 8px;
    background: #0f172a;
    border: 1px solid rgba(148,163,184,.15);
    color: #e2e8f0;
    font-size: .85rem;
  }

  input:focus {
    outline: none;
    border-color: #3b82f6;
    box-shadow: 0 0 0 3px rgba(59,130,246,.15);
  }

  .input-locked {
    color: #64748b;
    cursor: not-allowed;
  }

  /* BUTTON */

  button {
    height: 42px;
    border-radius: 8px;
    border: none;
    background: #2563eb;
    color: white;
    font-weight: 600;
    font-size: .85rem;
    cursor: pointer;
    display: flex;
    justify-content: center;
    align-items: center;
    gap: .4rem;
    transition: all .15s;
  }

  button:hover:not(:disabled) {
    background: #1d4ed8;
  }

  button:disabled {
    opacity: .5;
    cursor: not-allowed;
  }

  /* SPINNER */

  .spinner {
    width: 14px;
    height: 14px;
    border-radius: 50%;
    border: 2px solid rgba(255,255,255,.3);
    border-top-color: white;
    animation: spin .7s linear infinite;
  }

  @keyframes spin {
    to { transform: rotate(360deg); }
  }

  /* MOBILE */

  @media (max-width: 480px) {
    .row {
      grid-template-columns: 1fr;
    }

    .card {
      padding: 1.5rem;
    }
  }

</style>