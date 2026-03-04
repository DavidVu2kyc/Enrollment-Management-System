<script lang="ts">
  interface Props {
    type?: string;
    label?: string;
    placeholder?: string;
    value?: string;
    error?: string;
    required?: boolean;
    id?: string;
    name?: string;
    [key: string]: any;
  }

  let {
    type = "text",
    label,
    placeholder,
    value = $bindable(""),
    error,
    required = false,
    id,
    name,
    ...rest
  }: Props = $props();

  const baseStyles =
    "w-full px-6 py-5 rounded-[20px] border-2 border-gray-100 dark:border-white/10 bg-gray-50 dark:bg-black/20 text-gray-900 dark:text-white font-black uppercase tracking-widest text-[10px] placeholder-gray-400 dark:placeholder-gray-600 transition-all duration-300 focus:outline-none focus:bg-white dark:focus:bg-blue-900/10 disabled:opacity-50 disabled:cursor-not-allowed";

  const errorStyles = $derived(
    error
      ? "border-red-500 bg-red-50 dark:bg-red-950/20 focus:border-red-600"
      : "focus:border-blue-900 dark:focus:border-blue-500 shadow-sm focus:shadow-xl",
  );

  let inputClass = $derived(`${baseStyles} ${errorStyles}`);
</script>

<div class="w-full group/input">
  {#if label}
    <label
      for={id}
      class="block text-[10px] font-black text-gray-400 dark:text-blue-300/30 mb-2 uppercase tracking-[0.3em] transition-colors group-focus-within/input:text-blue-900 dark:group-focus-within/input:text-blue-400"
    >
      {label}
      {#if required}
        <span class="text-red-500 ml-1 opacity-50">*</span>
      {/if}
    </label>
  {/if}

  <div class="relative">
    <input
      {id}
      {name}
      {type}
      {placeholder}
      {required}
      bind:value
      class={inputClass}
      {...rest}
    />

    <!-- Focus Underline / Glow -->
    <div
      class="absolute inset-0 rounded-[20px] pointer-events-none border-2 border-blue-900 opacity-0 group-focus-within/input:opacity-10 dark:border-blue-500 transition-opacity"
    ></div>
  </div>

  {#if error}
    <p
      class="mt-2 text-[10px] font-black text-red-600 dark:text-red-400 uppercase tracking-widest flex items-center gap-2 animate-in slide-in-from-top-1"
    >
      <svg class="w-3.5 h-3.5" fill="currentColor" viewBox="0 0 20 20">
        <path
          fill-rule="evenodd"
          d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z"
          clip-rule="evenodd"
        />
      </svg>
      {error}
    </p>
  {/if}
</div>
