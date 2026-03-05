<script lang="ts">
  import type { Snippet } from "svelte";

  interface Props {
    variant?: "primary" | "secondary" | "danger" | "success" | "outline";
    size?: "sm" | "md" | "lg" | "xl";
    disabled?: boolean;
    type?: "button" | "submit" | "reset";
    isLoading?: boolean;
    href?: string;
    children?: Snippet;
    onclick?: (e: MouseEvent) => void;
    [key: string]: any;
  }

  let {
    variant = "primary",
    size = "md",
    disabled = false,
    type = "button",
    isLoading = false,
    href,
    children,
    onclick,
    ...rest
  }: Props = $props();

  const baseStyles =
    "inline-flex items-center justify-center font-black uppercase tracking-widest italic transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed focus:outline-none focus:ring-2 focus:ring-offset-2 active:scale-95 cursor-pointer select-none";

  const variantStyles = {
    primary:
      "bg-blue-900 text-white hover:bg-black dark:hover:bg-blue-800 shadow-xl hover:shadow-2xl border border-transparent",
    secondary:
      "bg-gray-100 dark:bg-white/5 text-gray-500 hover:text-blue-900 dark:hover:text-white border border-gray-200 dark:border-white/10 shadow-sm",
    danger: "bg-red-600 text-white hover:bg-black shadow-lg",
    success: "bg-green-700 text-white hover:bg-black shadow-lg",
    outline:
      "bg-transparent border-2 border-blue-900 text-blue-900 dark:border-blue-400 dark:text-blue-400 hover:bg-blue-900 hover:text-white dark:hover:bg-blue-400 dark:hover:text-blue-900",
  };

  const sizeStyles = {
    sm: "px-4 py-2 text-[10px] rounded-xl",
    md: "px-6 py-3 text-xs rounded-2xl",
    lg: "px-10 py-4 text-sm rounded-2xl",
    xl: "px-12 py-5 text-base rounded-[24px]",
  };

  let buttonClass = $derived(
    `${baseStyles} ${variantStyles[variant]} ${sizeStyles[size]}`,
  );

  const handleAction = (e: MouseEvent) => {
    if (disabled || isLoading) {
      e.preventDefault();
      e.stopPropagation();
      return;
    }
    if (onclick) {
      onclick(e);
    }
  };
</script>

{#if href}
  <a
    {href}
    class={buttonClass}
    aria-disabled={disabled || isLoading}
    onclick={handleAction}
    {...rest}
  >
    {#if isLoading}
      <span
        class="animate-spin mr-3 h-4 w-4 border-3 border-white/30 border-t-white rounded-full"
      ></span>
      Synchronizing
    {:else if children}
      {@render children()}
    {/if}
  </a>
{:else}
  <button
    {type}
    {disabled}
    class={buttonClass}
    onclick={handleAction}
    {...rest}
  >
    {#if isLoading}
      <span
        class="animate-spin mr-3 h-4 w-4 border-3 border-white/30 border-t-white rounded-full"
      ></span>
      Synchronizing
    {:else if children}
      {@render children()}
    {/if}
  </button>
{/if}
