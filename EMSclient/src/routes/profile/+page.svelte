<script lang="ts">
  import { userStore } from "$lib/stores/user.svelte";
  import { fly } from "svelte/transition";
  import { onMount } from "svelte";

  let firstName = $state("");
  let lastName = $state("");
  let isUpdating = $state(false);
  let feedback = $state<{ msg: string; type: "success" | "error" } | null>(
    null,
  );

  // Load current student profile
  onMount(async () => {
    try {
      const res = await fetch("/api/students/me");
      if (res.ok) {
        debugger
        const data = await res.json();
        const parts = (data.name ?? "").trim().split(/\s+/);
        firstName = parts[0] ?? "";
        lastName = parts.slice(1).join(" ");

        // Update store with all required fields
        const current = userStore.current;
        if (current) {
          userStore.update({ ...current, name: data.name });
        }
      }
    } catch (e) {
      console.error("Failed to load profile", e);
    }
  });

  const handleSubmit = async (e: SubmitEvent) => {
    e.preventDefault();
    isUpdating = true;
    feedback = null;
    const name = `${firstName} ${lastName}`.trim();

    try {
      // Get ID then update
      const meRes = await fetch("/api/students/me");
      if (!meRes.ok) throw new Error("Unable to fetch user ID");
      const me = await meRes.json();

      const response = await fetch(`/api/students/${me.id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name }),
      });

      if (!response.ok) throw new Error("Update failed");

      const current = userStore.current;
      if (current) {
        userStore.update({ ...current, name });
      }

      feedback = { msg: "Profile updated successfully.", type: "success" };
    } catch (err) {
      feedback = {
        msg: "Failed to update profile. Please try again.",
        type: "error",
      };
    } finally {
      isUpdating = false;
    }
  };
</script>

<main class="container" in:fly={{ y: 20, duration: 600 }}>
  <div class="card">
    <header class="header"><h1>Profile</h1></header>

    {#if feedback}
      <p class="message {feedback.type}">{feedback.msg}</p>
    {/if}

    <form onsubmit={handleSubmit}>
      <div class="field">
        <label for="firstName">First Name</label>
        <input id="firstName" bind:value={firstName} required />
      </div>

      <div class="field">
        <label for="lastName">Last Name</label>
        <input id="lastName" bind:value={lastName} required />
      </div>

      <button type="submit" disabled={isUpdating}>
        {#if isUpdating}<span class="loader"></span>{:else}Update Profile{/if}
      </button>
    </form>
  </div>
</main>

<style>
  :global(body) {
    margin: 0;
    font-family: "DM Sans", sans-serif;
    background: #020617;
    color: white;
  }
  .container {
    height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .card {
    width: 360px;
    padding: 2.5rem;
    border-radius: 20px;
    background: rgba(15, 23, 42, 0.7);
    backdrop-filter: blur(20px);
    border: 1px solid rgba(255, 255, 255, 0.08);
  }
  .header {
    text-align: center;
    margin-bottom: 2rem;
  }
  .field {
    margin-bottom: 1.4rem;
  }
  label {
    font-size: 0.7rem;
    text-transform: uppercase;
    letter-spacing: 0.08em;
    opacity: 0.7;
    margin-bottom: 0.4rem;
    display: block;
  }
  input {
    width: 100%;
    padding: 0.5rem;
    border: 1px solid rgba(255, 255, 255, 0.08);
    border-radius: 8px;
    background: rgba(255, 255, 255, 0.03);
    color: white;
  }
  .message {
    margin-bottom: 1rem;
    text-align: center;
  }
  .message.success {
    color: #10b981;
  }
  .message.error {
    color: #f87171;
  }
  button {
    width: 100%;
    height: 44px;
    border: none;
    border-radius: 10px;
    background: #2563eb;
    color: white;
    font-weight: 600;
    cursor: pointer;
  }
  button:disabled {
    opacity: 0.7;
    cursor: wait;
  }
  .loader {
    width: 18px;
    height: 18px;
    border: 2px solid rgba(255, 255, 255, 0.3);
    border-top: 2px solid white;
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
  }
  @keyframes spin {
    to {
      transform: rotate(360deg);
    }
  }
</style>
