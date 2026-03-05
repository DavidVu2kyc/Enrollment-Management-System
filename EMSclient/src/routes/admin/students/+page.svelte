<script lang="ts">
  import { onMount } from "svelte";
  import { fly } from "svelte/transition";
  import Button from "$lib/components/Button.svelte";
  import Input from "$lib/components/Input.svelte";
  import type { PageData } from "./$types";

  let { data }: { data: PageData } = $props();

  let students = $state(data.students || []);
  let searchQuery = $state("");
  let isLoading = $state(false);

  let filteredStudents = $derived(
    students.filter(
      (s) =>
        `${s.firstName} ${s.lastName}`
          .toLowerCase()
          .includes(searchQuery.toLowerCase()) ||
        s.studentNumber.includes(searchQuery) ||
        s.email?.toLowerCase().includes(searchQuery.toLowerCase()),
    ),
  );

  const terminateStudent = async (id: number) => {
    if (
      !confirm(
        "Are you sure you want to terminate this student identity? This action is IRREVERSIBLE.",
      )
    )
      return;

    try {
      const formData = new FormData();
      formData.append("id", id.toString());

      const resp = await fetch("?/delete", {
        method: "POST",
        body: formData,
      });

      if (!resp.ok) throw new Error("Synchronization failure");

      students = students.filter((s) => s.id !== id);
    } catch (err) {
      alert("Failed to terminate identity record.");
    }
  };
</script>

<div class="space-y-12 animate-in fade-in duration-700">
  <div
    class="flex flex-col md:flex-row md:items-end justify-between gap-6 border-b border-gray-100 dark:border-white/5 pb-8"
  >
    <div>
      <h1
        class="text-5xl font-black text-blue-900 dark:text-white tracking-tighter uppercase italic leading-none mb-4"
      >
        Student Registry
      </h1>
      <p class="text-gray-500 font-medium max-w-xl">
        Comprehensive lifecycle management for institutional members, from
        authentication to academic clearance.
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
            d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z"
          /></svg
        >
        Register Student
      </span>
    </Button>
  </div>

  <!-- Perspective Filter -->
  <div
    class="bg-white dark:bg-gray-800 p-6 rounded-3xl border border-gray-200 dark:border-gray-700 shadow-xl flex flex-col lg:flex-row items-center gap-6"
  >
    <div class="flex-1 w-full">
      <Input
        placeholder="Filter by full name, institutional ID, or encrypted email..."
        bind:value={searchQuery}
      />
    </div>
    <div class="flex gap-4 w-full lg:w-auto overflow-x-auto pb-2 lg:pb-0">
      {#each ["All Disciplines", "Active Status", "On Leave"] as filter}
        <button
          class="px-6 py-3 rounded-2xl bg-gray-50 dark:bg-white/5 border border-gray-100 dark:border-white/10 text-[10px] font-black uppercase tracking-widest text-gray-500 hover:text-blue-900 dark:hover:text-white transition-all whitespace-nowrap"
          >{filter}</button
        >
      {/each}
    </div>
  </div>

  <!-- Ledger / Table -->
  <div
    class="bg-white dark:bg-gray-800 rounded-3xl border border-gray-200 dark:border-gray-700 shadow-xl overflow-hidden transition-all duration-300"
  >
    <div class="overflow-x-auto">
      <table class="w-full text-left border-collapse">
        <thead>
          <tr
            class="bg-blue-900/5 dark:bg-white/5 text-blue-900 dark:text-blue-200 font-black uppercase text-[10px] tracking-[0.3em]"
          >
            <th class="px-10 py-6">ID Token</th>
            <th class="px-10 py-6">Identity Profile</th>
            <th class="px-10 py-6">Program Cluster</th>
            <th class="px-10 py-6">Lifecycle Status</th>
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
          {:else if filteredStudents.length > 0}
            {#each filteredStudents as student, i}
              <tr
                class="group hover:bg-blue-50/50 dark:hover:bg-blue-900/10 transition-all duration-300"
                in:fly={{ x: -20, delay: i * 50 }}
              >
                <td class="px-10 py-6">
                  <span
                    class="text-[11px] font-black text-blue-900 bg-white dark:bg-white/10 dark:text-blue-300 px-3 py-1.5 rounded-xl shadow-lg border border-gray-100 dark:border-white/5 italic"
                  >
                    {student.studentNumber}
                  </span>
                </td>
                <td class="px-10 py-6">
                  <p
                    class="font-black text-gray-900 dark:text-white uppercase italic tracking-tighter text-lg leading-none mb-1 group-hover:translate-x-1 transition-transform"
                  >
                    {student.firstName}
                    {student.lastName}
                  </p>
                  <p
                    class="text-[10px] font-bold text-gray-400 uppercase tracking-widest"
                  >
                    {student.email || "N/A"}
                  </p>
                </td>
                <td class="px-10 py-6">
                  <div class="flex flex-col">
                    <span
                      class="font-black text-blue-900 dark:text-blue-400 text-xs uppercase tracking-widest"
                      >{student.degree?.code || "GEN"}</span
                    >
                    <span
                      class="text-[9px] font-bold text-gray-400 uppercase tracking-widest"
                      >Level {student.yearLevel} Academic</span
                    >
                  </div>
                </td>
                <td class="px-10 py-6">
                  <span
                    class={`px-4 py-1.5 rounded-full text-[9px] font-black uppercase tracking-[0.2em] border shadow-sm ${
                      (student.status || "Active") === "Active"
                        ? "bg-emerald-50 text-emerald-700 border-emerald-100 dark:bg-emerald-500/10 dark:text-emerald-400 dark:border-emerald-500/20"
                        : "bg-amber-50 text-amber-700 border-amber-100 dark:bg-amber-500/10 dark:text-amber-400 dark:border-amber-500/20"
                    }`}
                  >
                    {student.status || "Active"}
                  </span>
                </td>
                <td class="px-10 py-6 text-right">
                  <div
                    class="flex justify-end gap-3 translate-x-4 opacity-0 group-hover:translate-x-0 group-hover:opacity-100 transition-all duration-500"
                  >
                    <button
                      class="w-10 h-10 rounded-xl bg-white dark:bg-white/10 text-gray-400 hover:text-blue-900 dark:hover:text-blue-400 shadow-sm border border-gray-100 dark:border-white/5 flex items-center justify-center transition-all hover:scale-110"
                      aria-label="Edit Entity Profile"
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
                    <button
                      onclick={() => terminateStudent(student.id)}
                      class="w-10 h-10 rounded-xl bg-white dark:bg-white/10 text-gray-400 hover:text-red-600 shadow-sm border border-gray-100 dark:border-white/5 flex items-center justify-center transition-all hover:scale-110"
                      aria-label="Terminate Identity"
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
                          d="M14.74 9l-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 01-2.244 2.077H8.084a2.25 2.25 0 01-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 00-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 013.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 00-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 00-7.5 0"
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
                        d="M15.75 6a3.75 3.75 0 11-7.5 0 3.75 3.75 0 017.5 0zM4.501 20.118a7.5 7.5 0 0114.998 0A17.933 17.933 0 0112 21.75c-2.676 0-5.216-.584-7.499-1.632z"
                      /></svg
                    >
                  </div>
                  <h3
                    class="text-xl font-black text-gray-900 dark:text-white uppercase tracking-tighter italic"
                  >
                    Identity Void
                  </h3>
                  <p
                    class="text-xs font-bold text-gray-400 uppercase tracking-widest mt-2"
                  >
                    Zero matches identified within synchronization scope.
                  </p>
                </div>
              </td>
            </tr>
          {/if}
        </tbody>
      </table>
    </div>

    <!-- Pager -->
    <div
      class="px-10 py-8 bg-gray-50/50 dark:bg-white/5 border-t border-gray-100 dark:border-white/5 flex flex-col md:flex-row justify-between items-center gap-6"
    >
      <p
        class="text-[10px] font-black text-gray-400 uppercase tracking-[0.3em]"
      >
        Inventory Resolution: {filteredStudents.length} of {students.length} Active
        Entities
      </p>
      <div class="flex gap-2">
        {#each ["1", "2", "3"] as page}
          <button
            class="w-12 h-12 rounded-xl bg-white dark:bg-white/10 border border-gray-100 dark:border-white/10 text-[10px] font-black italic flex items-center justify-center text-gray-500 hover:text-blue-900 dark:hover:text-white shadow-sm transition-all hover:scale-110"
            >{page}</button
          >
        {/each}
        <button
          class="w-12 h-12 rounded-xl bg-blue-900 text-white shadow-xl flex items-center justify-center font-black"
          >»</button
        >
      </div>
    </div>
  </div>
</div>
