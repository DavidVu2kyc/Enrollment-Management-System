<script lang="ts">
  import { fade, fly } from "svelte/transition";
  import type { Snippet } from "svelte";

  interface Props {
    isOpen: boolean;
    title?: string;
    type?: "confirm" | "alert" | "success" | "error";
    onConfirm?: () => void;
    onCancel?: () => void;
    confirmText?: string;
    cancelText?: string;
    children?: Snippet;
  }

  let {
    isOpen = false,
    title,
    type = "alert",
    onConfirm,
    onCancel,
    confirmText = "Confirm",
    cancelText = "Cancel",
    children,
  }: Props = $props();

  const colorMap = {
    confirm: "bg-amber-100 text-amber-900 border-amber-200",
    alert: "bg-blue-100 text-blue-900 border-blue-200",
    success: "bg-green-100 text-green-900 border-green-200",
    error: "bg-red-100 text-red-900 border-red-200",
  };

  const iconMap = {
    confirm: `<svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"></path></svg>`,
    alert: `<svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>`,
    success: `<svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path></svg>`,
    error: `<svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>`,
  };
</script>

{#if isOpen}
  <!-- svelte-ignore a11y_click_events_have_key_events -->
  <!-- svelte-ignore a11y_no_static_element_interactions -->
  <div
    class="fixed inset-0 bg-blue-900/60 backdrop-blur-sm flex items-center justify-center z-[100] px-4"
    transition:fade={{ duration: 200 }}
    onclick={onCancel}
  >
    <div
      class="bg-white dark:bg-gray-800 rounded-2xl shadow-2xl max-w-md w-full overflow-hidden border border-white/20"
      transition:fly={{ y: 20, duration: 400, opacity: 0 }}
      onclick={(e) => e.stopPropagation()}
    >
      <!-- Header/Icon area -->
      <div class="p-6 pb-0 flex items-center gap-4">
        <div
          class={`flex-shrink-0 w-12 h-12 rounded-xl flex items-center justify-center border ${colorMap[type]}`}
        >
          {@html iconMap[type]}
        </div>
        <div>
          {#if title}
            <h3
              class="text-xl font-black text-gray-900 dark:text-white tracking-tight"
            >
              {title}
            </h3>
          {/if}
        </div>
      </div>

      <!-- Content Area -->
      <div class="p-6">
        <div class="text-gray-600 dark:text-gray-300 font-medium">
          {#if children}
            {@render children()}
          {/if}
        </div>
      </div>

      <!-- Footer/Actions -->
      <div
        class="bg-gray-50 dark:bg-gray-900/50 p-6 flex gap-3 justify-end border-t border-gray-100 dark:border-gray-700"
      >
        {#if type === "confirm" || type === "alert"}
          <button
            type="button"
            class="px-5 py-2.5 text-sm font-bold text-gray-700 bg-white border-2 border-gray-200 rounded-xl hover:bg-gray-50 transition-all active:scale-95"
            onclick={onCancel}
          >
            {cancelText}
          </button>
        {/if}

        <button
          type="button"
          class={`px-5 py-2.5 text-sm font-bold text-white rounded-xl transition-all shadow-lg active:scale-95 ${
            type === "error"
              ? "bg-red-600 hover:bg-red-700"
              : type === "success"
                ? "bg-green-700 hover:bg-green-800"
                : type === "confirm"
                  ? "bg-blue-900 hover:bg-black"
                  : "bg-blue-900 hover:bg-black"
          }`}
          onclick={onConfirm || onCancel}
        >
          {confirmText}
        </button>
      </div>
    </div>
  </div>
{/if}
