<script lang="ts">
  import { fly } from "svelte/transition";
  import Button from "$lib/components/Button.svelte";
  import Input from "$lib/components/Input.svelte";
  import type { PageData } from "./$types";
  import type { Section } from "$lib/types/section";

  let { data }: { data: PageData } = $props();

  let sections = $state<Section[]>(data.sections || []);
  let searchQuery = $state("");
  let isLoading = $state(false);

  let filteredSections = $derived(
    sections.filter(
      (s) =>
        s.courseCode.toLowerCase().includes(searchQuery.toLowerCase()) ||
        s.courseName.toLowerCase().includes(searchQuery.toLowerCase()),
    ),
  );
</script>

<svelte:head>
  <title>Curriculum Protocols — EMS</title>
</svelte:head>

<div class="space-y-12 animate-in fade-in duration-700">
  <div
    class="flex flex-col md:flex-row md:items-end justify-between gap-6 border-b border-gray-100 dark:border-white/5 pb-8"
  >
    <div>
      <h1
        class="text-5xl font-black text-blue-900 dark:text-white tracking-tighter uppercase italic leading-none mb-4"
      >
        Curriculum Protocols
      </h1>
      <p class="text-gray-500 font-medium max-w-xl">
        Centralized authorization for course sections, faculty assignment, and
        spatial allocation protocols.
      </p>
    </div>
    <Button variant="primary" size="lg">
      <span class="flex items-center gap-2">
        <svg
          class="w-5 h-5"
          fill="none"
          stroke="currentColor"
          stroke-width="3"
          viewBox="0 0 24 24"
          ><path
            stroke-linecap="round"
            stroke-linejoin="round"
            d="M12 4.5v15m7.5-7.5h-15"
          /></svg
        >
        Initialize Section
      </span>
    </Button>
  </div>

  <div
    class="bg-white dark:bg-gray-800 p-6 rounded-3xl border border-gray-200 dark:border-gray-700 shadow-xl flex flex-col lg:flex-row items-center gap-6"
  >
    <div class="flex-1 w-full">
      <Input
        placeholder="Filter by subject code, descriptive title, or instructor identifier..."
        bind:value={searchQuery}
      />
    </div>
    <div class="flex gap-4 w-full lg:w-auto overflow-x-auto pb-2 lg:pb-0">
      {#each ["All Cycles", "Engineering", "Humanities"] as filter}
        <button
          class="px-6 py-3 rounded-2xl bg-gray-50 dark:bg-white/5 border border-gray-100 dark:border-white/10 text-[10px] font-black uppercase tracking-widest text-gray-500 hover:text-blue-900 dark:hover:text-white transition-all whitespace-nowrap"
          >{filter}</button
        >
      {/each}
    </div>
  </div>

  <div
    class="bg-white dark:bg-gray-800 rounded-3xl border border-gray-200 dark:border-gray-700 shadow-xl overflow-hidden transition-all duration-300"
  >
    <div class="overflow-x-auto">
      <table class="w-full text-left border-collapse">
        <thead>
          <tr
            class="bg-blue-900/5 dark:bg-white/5 text-blue-900 dark:text-blue-200 font-black uppercase text-[10px] tracking-[0.3em]"
          >
            <th class="px-10 py-6">Course Token</th>
            <th class="px-10 py-6">Section Metadata</th>
            <th class="px-10 py-6">Load Factor</th>
            <th class="px-10 py-6">Faculty Control</th>
            <th class="px-10 py-6 text-right">Operations</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-50 dark:divide-white/5">
          {#if isLoading}
            {#each Array(5) as _}
              <tr class="animate-pulse">
                <td colspan="5" class="px-10 py-8"
                  ><div
                    class="h-6 bg-gray-100 dark:bg-white/5 rounded-full w-full opacity-50"
                  ></div></td
                >
              </tr>
            {/each}
          {:else if filteredSections.length > 0}
            {#each filteredSections as section, i}
              <tr
                class="group hover:bg-blue-50/50 dark:hover:bg-blue-900/10 transition-all duration-300"
                in:fly={{ x: -20, delay: i * 50 }}
              >
                <td class="px-10 py-6">
                  <span
                    class="text-[11px] font-black text-blue-900 bg-white dark:bg-white/10 dark:text-blue-300 px-3 py-1.5 rounded-xl shadow-lg border border-gray-100 dark:border-white/5 italic"
                  >
                    {section.courseCode}
                  </span>
                </td>
                <td class="px-10 py-6">
                  <p
                    class="font-black text-gray-900 dark:text-white uppercase italic tracking-tighter text-lg leading-none mb-1 group-hover:translate-x-1 transition-transform"
                  >
                    {section.courseName}
                  </p>
                  <p
                    class="text-[10px] font-bold text-gray-400 uppercase tracking-widest"
                  >
                    SEC-{section.sectionNumber} • {section.term}
                    {section.year}
                  </p>
                </td>
                <td class="px-10 py-6">
                  <div class="flex items-center gap-3">
                    <div
                      class="w-24 h-2 bg-gray-100 dark:bg-white/10 rounded-full overflow-hidden shadow-inner"
                    >
                      <div
                        class="h-full bg-blue-900 dark:bg-blue-500 rounded-full"
                        style="width: {(section.enrolledCount /
                          section.capacity) *
                          100}%"
                      ></div>
                    </div>
                    <span
                      class="text-[10px] font-black uppercase tracking-widest text-blue-900 dark:text-blue-300"
                      >{section.enrolledCount}/{section.capacity}</span
                    >
                  </div>
                </td>
                <td class="px-10 py-6">
                  <span
                    class="text-[10px] font-black uppercase tracking-widest text-gray-500 dark:text-gray-400 border border-gray-100 dark:border-white/5 px-4 py-2 rounded-2xl bg-white dark:bg-white/5 shadow-sm"
                  >
                    {section.instructor || "UNASSIGNED"}
                  </span>
                </td>
                <td class="px-10 py-6 text-right">
                  <div
                    class="flex justify-end gap-3 translate-x-4 opacity-0 group-hover:translate-x-0 group-hover:opacity-100 transition-all duration-500"
                  >
                    <button
                      class="w-10 h-10 rounded-xl bg-white dark:bg-white/10 text-gray-400 hover:text-blue-900 dark:hover:text-blue-400 shadow-sm border border-gray-100 dark:border-white/5 flex items-center justify-center transition-all hover:scale-110"
                      aria-label="Edit Profile"
                    >
                      <svg
                        class="w-5 h-5"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2.5"
                        viewBox="0 0 24 24"
                        ><path
                          stroke-linecap="round"
                          stroke-linejoin="round"
                          d="M16.862 4.487l1.687-1.688a1.875 1.875 0 112.652 2.652L10.582 16.07a4.5 4.5 0 01-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 011.13-1.897l8.932-8.931zm0 0L19.5 7.125M18 14v4.75A2.25 2.25 0 0115.75 21H5.25A2.25 2.25 0 013 18.75V8.25A2.25 2.25 0 015.25 6H10"
                        /></svg
                      >
                    </button>
                  </div>
                </td>
              </tr>
            {/each}
          {:else}
            <tr>
              <td colspan="5" class="px-10 py-32 text-center">
                <div class="flex flex-col items-center">
                  <div
                    class="w-24 h-24 bg-gray-50 dark:bg-white/5 rounded-full flex items-center justify-center mb-6"
                  >
                    <svg
                      class="w-10 h-10 text-gray-200"
                      fill="none"
                      stroke="currentColor"
                      stroke-width="2.5"
                      viewBox="0 0 24 24"
                      ><path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        d="M12 6.042A8.967 8.967 0 006 3.75c-1.052 0-2.062.18-3 .512v14.25A8.987 8.987 0 016 18c2.305 0 4.408.867 6 2.292m0-14.25a8.966 8.966 0 016-2.292c1.052 0 2.062.18 3 .512v14.25A8.987 8.987 0 0018 18c-2.305 0-4.408.867-6 2.292m0-14.25v14.25"
                      /></svg
                    >
                  </div>
                  <h3
                    class="text-xl font-black text-gray-900 dark:text-white uppercase tracking-tighter italic"
                  >
                    Protocol Empty
                  </h3>
                  <p
                    class="text-xs font-bold text-gray-400 uppercase tracking-widest mt-2"
                  >
                    Initializing curriculum catalog synchronized with central
                    repository.
                  </p>
                </div>
              </td>
            </tr>
          {/if}
        </tbody>
      </table>
    </div>
  </div>
</div>
