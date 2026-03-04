<script lang="ts">
  import { superForm } from "sveltekit-superforms";
  import { yup } from "sveltekit-superforms/adapters";
  import * as Y from "yup";
  import Button from "./Button.svelte";
  import type { Enrollment, Section } from "$lib/types";

  interface Props {
    enrollment?: Enrollment;
    availableSections?: Section[];
    onSubmit?: (data: any) => void;
    isLoading?: boolean;
  }

  let {
    enrollment,
    availableSections = [],
    onSubmit,
    isLoading = false,
  }: Props = $props();

  const schema = Y.object().shape({
    sectionId: Y.string().required("Section choice is mandatory"),
  });

  // Use a stable reference for initial state to satisfy Svelte 5 linting
  // if enrollment changes later, the $effect handles the sync.
  const initialSectionId = enrollment?.sectionId || "";

  const { form, errors, enhance } = superForm(
    { sectionId: initialSectionId },
    {
      validators: yup(schema),
      SPA: true,
      onUpdate: ({ form }) => {
        if (form.valid && onSubmit) {
          onSubmit(form.data);
        }
      },
    },
  );

  // Sync form if enrollment prop changes (for reactivity)
  $effect(() => {
    const currentId = enrollment?.sectionId;
    if (currentId && $form.sectionId !== currentId) {
      $form.sectionId = currentId;
    }
  });
</script>

<div
  class="glass dark:bg-[#0c1c37]/80 rounded-[40px] shadow-2xl border-white/20 dark:border-white/10 overflow-hidden premium-transition"
>
  <div class="bg-blue-900 p-10 text-white relative overflow-hidden">
    <div
      class="absolute top-0 right-0 w-64 h-64 bg-white/5 rounded-full -mr-32 -mt-32 blur-3xl"
    ></div>
    <div class="relative z-10">
      <h2
        class="text-4xl font-black tracking-tighter uppercase italic leading-none mb-3"
      >
        {enrollment ? "Modify Registration" : "Course Enrollment"}
      </h2>
      <p class="text-blue-200/70 text-sm font-bold uppercase tracking-widest">
        {enrollment
          ? "Update your academic section or time slots"
          : "Secure your spot in the upcoming semester"}
      </p>
    </div>
  </div>

  <form use:enhance class="p-10 space-y-10">
    <!-- Section Selection -->
    <div class="space-y-4">
      <label
        for="sectionId"
        class="block text-[10px] font-black text-gray-400 dark:text-blue-300/40 uppercase tracking-[0.3em]"
      >
        Available Academic Sections <span class="text-red-500">*</span>
      </label>
      <div class="relative group">
        <select
          id="sectionId"
          bind:value={$form.sectionId}
          name="sectionId"
          required
          aria-label="Select course section"
          class="w-full px-6 py-5 rounded-2xl border-2 border-gray-100 dark:border-white/10 bg-gray-50 dark:bg-black/20 text-gray-900 dark:text-white font-black uppercase tracking-widest text-xs focus:outline-none focus:border-blue-900 dark:focus:border-blue-500 transition-all appearance-none disabled:opacity-50"
          disabled={isLoading || availableSections.length === 0}
        >
          <option value="">Search and select a section...</option>
          {#each availableSections as section}
            <option value={section.id}>
              {section.courseCode} — {section.courseName} (Sec {section.sectionNumber})
            </option>
          {/each}
        </select>
        <div
          class="absolute inset-y-0 right-0 flex items-center px-6 pointer-events-none text-gray-400 group-focus-within:text-blue-900"
        >
          <svg
            class="w-5 h-5"
            fill="none"
            stroke="currentColor"
            stroke-width="3"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M19 9l-7 7-7-7"
            ></path>
          </svg>
        </div>
      </div>
      {#if $errors.sectionId?.[0]}
        <div
          class="flex items-center gap-3 text-red-600 animate-in fade-in slide-in-from-top-1"
        >
          <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
            <path
              fill-rule="evenodd"
              d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z"
              clip-rule="evenodd"
            ></path>
          </svg>
          <p class="text-[10px] font-black uppercase tracking-widest">
            {$errors.sectionId[0]}
          </p>
        </div>
      {/if}
    </div>

    <!-- Selected Section Details -->
    {#if $form.sectionId}
      {@const selectedSection = availableSections.find(
        (s) => s.id === $form.sectionId,
      )}
      {#if selectedSection}
        <div
          class="bg-blue-50/50 dark:bg-white/5 border-2 border-dashed border-blue-200 dark:border-white/10 rounded-[32px] p-8 space-y-8 animate-in fade-in zoom-in-95 duration-500"
        >
          <div
            class="flex flex-col md:flex-row md:items-start justify-between gap-6"
          >
            <div>
              <div class="flex items-center gap-3 mb-2">
                <span
                  class="text-[9px] font-black uppercase tracking-widest text-white bg-blue-900 px-3 py-1 rounded-lg"
                >
                  {selectedSection.courseCode}
                </span>
                <span
                  class="text-[9px] text-gray-400 font-black uppercase tracking-widest"
                  >Section {selectedSection.sectionNumber}</span
                >
              </div>
              <h3
                class="font-black text-gray-900 dark:text-blue-200 text-3xl tracking-tighter uppercase italic leading-none"
              >
                {selectedSection.courseName}
              </h3>
            </div>
            <div class="flex md:flex-col items-center md:items-end gap-2">
              <span
                class="text-[9px] font-black text-gray-400 uppercase tracking-widest"
                >Capacity Status</span
              >
              <span
                class={`text-2xl font-black ${selectedSection.enrolledCount >= selectedSection.capacity ? "text-red-600" : "text-blue-900 dark:text-blue-400"}`}
              >
                {selectedSection.capacity - selectedSection.enrolledCount} Seats
                Left
              </span>
            </div>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div
              class="flex items-center gap-5 p-5 bg-white dark:bg-black/20 rounded-[24px] border border-gray-100 dark:border-white/5 shadow-sm"
            >
              <div
                class="w-12 h-12 bg-blue-50 dark:bg-blue-900/20 text-blue-900 dark:text-blue-200 rounded-xl flex items-center justify-center"
              >
                <svg
                  class="w-7 h-7"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2.5"
                  viewBox="0 0 24 24"
                  ><path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
                  ></path></svg
                >
              </div>
              <div>
                <p
                  class="text-[9px] font-black text-gray-400 uppercase tracking-widest leading-none mb-1"
                >
                  Lead Instructor
                </p>
                <p
                  class="text-sm font-black text-gray-900 dark:text-white uppercase"
                >
                  {selectedSection.instructor || "Teaching Staff"}
                </p>
              </div>
            </div>
            <div
              class="flex items-center gap-5 p-5 bg-white dark:bg-black/20 rounded-[24px] border border-gray-100 dark:border-white/5 shadow-sm"
            >
              <div
                class="w-12 h-12 bg-indigo-50 dark:bg-blue-900/20 text-indigo-900 dark:text-indigo-200 rounded-xl flex items-center justify-center"
              >
                <svg
                  class="w-7 h-7"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2.5"
                  viewBox="0 0 24 24"
                  ><path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"
                  ></path><path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"
                  ></path></svg
                >
              </div>
              <div>
                <p
                  class="text-[9px] font-black text-gray-400 uppercase tracking-widest leading-none mb-1"
                >
                  Campus Room
                </p>
                <p
                  class="text-sm font-black text-gray-900 dark:text-white uppercase truncate"
                >
                  {selectedSection.room
                    ? `${selectedSection.room.building} ${selectedSection.room.roomNumber}`
                    : "Online/Hybrid"}
                </p>
              </div>
            </div>
          </div>

          {#if selectedSection.schedules && selectedSection.schedules.length > 0}
            <div class="space-y-4">
              <p
                class="text-[9px] font-black text-blue-900/40 dark:text-blue-300/40 uppercase tracking-[0.4em]"
              >
                Weekly Attendance Schedule
              </p>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                {#each selectedSection.schedules as schedule}
                  <div
                    class="flex items-center justify-between p-5 bg-white/60 dark:bg-black/40 rounded-2xl border border-blue-50 dark:border-white/5"
                  >
                    <div class="flex items-center gap-3">
                      <div class="w-1.5 h-1.5 rounded-full bg-blue-900"></div>
                      <span
                        class="font-black text-xs uppercase text-blue-900 dark:text-blue-400"
                        >{schedule.dayOfWeek}</span
                      >
                    </div>
                    <span
                      class="text-xs font-bold text-gray-600 dark:text-gray-400"
                      >{schedule.startTime} — {schedule.endTime}</span
                    >
                  </div>
                {/each}
              </div>
            </div>
          {/if}

          {#if selectedSection.enrolledCount >= selectedSection.capacity}
            <div
              class="bg-amber-600 text-white p-6 rounded-3xl shadow-2xl animate-in slide-in-from-right-4 duration-700"
            >
              <div class="flex items-start gap-5">
                <div class="bg-white/20 p-3 rounded-xl shadow-inner">
                  <svg
                    class="w-6 h-6"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="3"
                    viewBox="0 0 24 24"
                    ><path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
                    ></path></svg
                  >
                </div>
                <div>
                  <h4
                    class="font-black uppercase italic tracking-tighter text-xl leading-none mb-1"
                  >
                    Section Full
                  </h4>
                  <p
                    class="text-xs text-blue-50 font-bold uppercase tracking-widest opacity-80"
                  >
                    Waitlist Activation Automatic
                  </p>
                </div>
              </div>
            </div>
          {/if}
        </div>
      {/if}
    {/if}

    <!-- Buttons -->
    <div
      class="flex items-center justify-end gap-4 pt-8 border-t border-gray-100 dark:border-white/10"
    >
      <Button
        type="button"
        variant="secondary"
        onclick={() => window.history.back()}
      >
        Discard
      </Button>
      <Button
        type="submit"
        variant="primary"
        {isLoading}
        disabled={!$form.sectionId}
      >
        {enrollment ? "Apply Changes" : "Confirm Enrollment"}
      </Button>
    </div>
  </form>
</div>
