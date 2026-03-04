<script lang="ts">
  import { fade } from "svelte/transition";
  import Button from "./Button.svelte";
  import Modal from "./Modal.svelte";
  import type { Enrollment } from "$lib/types";

  interface Props {
    enrollment: Enrollment;
    onDelete?: (id: string) => void;
    onEnroll?: (id: string) => void;
  }

  let { enrollment, onDelete, onEnroll }: Props = $props();
  let showDeleteModal = $state(false);
  let isDeleting = $state(false);

  const handleDeleteClick = () => {
    showDeleteModal = true;
  };

  const handleConfirmDelete = async () => {
    isDeleting = true;
    try {
      if (onDelete) {
        await onDelete(enrollment.id);
      }
    } finally {
      isDeleting = false;
      showDeleteModal = false;
    }
  };

  const handleEnroll = async () => {
    if (onEnroll) {
      await onEnroll(enrollment.id);
    }
  };
</script>

<div
  class="bg-white dark:bg-white/5 rounded-[32px] shadow-xl hover:shadow-2xl transition-all duration-500 border border-gray-100 dark:border-white/10 relative overflow-hidden group p-8"
  transition:fade
>
  <!-- Decorative Accent -->
  <div
    class="absolute top-0 right-0 w-32 h-32 bg-blue-900/5 dark:bg-blue-500/5 rounded-full -mr-16 -mt-16 group-hover:scale-150 transition-transform duration-700"
  ></div>

  <div class="flex flex-col gap-8 relative z-10">
    <div class="flex flex-col md:flex-row md:items-start justify-between gap-6">
      <div class="flex-1 space-y-3">
        <div class="flex items-center gap-3">
          <span
            class="text-[10px] font-black tracking-[0.2em] uppercase text-white bg-blue-900 px-3 py-1 rounded-lg shadow-lg rotate-[-1deg] group-hover:rotate-0 transition-transform"
          >
            {enrollment.section?.courseCode}
          </span>
          <span
            class="text-[10px] font-black uppercase tracking-widest text-gray-400"
            >Section {enrollment.section?.sectionNumber}</span
          >
        </div>
        <h3
          class="text-3xl font-black text-blue-900 dark:text-white tracking-tighter uppercase italic leading-none"
        >
          {enrollment.section?.courseName || "General Subject"}
        </h3>
      </div>

      <div class="flex flex-wrap md:flex-col items-start md:items-end gap-3">
        {#if enrollment.status === "ENROLLED" || enrollment.isEnrolled}
          <div
            class="inline-flex items-center px-4 py-1.5 rounded-2xl bg-green-500/10 text-green-700 dark:text-green-400 border border-green-500/20 text-[10px] font-black uppercase tracking-widest"
          >
            <span class="w-2 h-2 rounded-full bg-green-500 mr-2 animate-pulse"
            ></span>
            Confirmed
          </div>
        {:else}
          <div
            class="inline-flex items-center px-4 py-1.5 rounded-2xl bg-amber-500/10 text-amber-700 dark:text-amber-400 border border-amber-500/20 text-[10px] font-black uppercase tracking-widest"
          >
            <span class="w-2 h-2 rounded-full bg-amber-500 mr-2"></span>
            Pending
          </div>
        {/if}

        {#if enrollment.scheduledConflict}
          <div
            class="inline-flex items-center px-4 py-1.5 rounded-2xl bg-red-600 text-white shadow-lg text-[10px] font-black uppercase tracking-widest"
          >
            <svg
              class="w-3.5 h-3.5 mr-2"
              fill="currentColor"
              viewBox="0 0 20 20"
              ><path
                fill-rule="evenodd"
                d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z"
                clip-rule="evenodd"
              ></path></svg
            >
            Conflict Detected
          </div>
        {/if}
      </div>
    </div>

    <!-- Details Grid -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
      {#if enrollment.section?.schedules && enrollment.section.schedules.length > 0}
        <div
          class="bg-gray-50 dark:bg-white/5 rounded-3xl p-5 border border-gray-100 dark:border-white/5"
        >
          <p
            class="text-[9px] font-black uppercase tracking-[0.3em] text-blue-900/40 dark:text-blue-300/40 mb-4"
          >
            Standard Schedule
          </p>
          <div class="space-y-3">
            {#each enrollment.section.schedules as schedule}
              <div class="flex items-center justify-between">
                <span
                  class="text-xs font-black uppercase text-blue-900 dark:text-blue-400"
                  >{schedule.dayOfWeek}</span
                >
                <span class="text-xs font-bold text-gray-500"
                  >{schedule.startTime} — {schedule.endTime}</span
                >
              </div>
            {/each}
          </div>
        </div>
      {/if}

      <div class="space-y-4">
        <div
          class="flex items-center gap-4 p-4 bg-gray-50 dark:bg-white/5 rounded-2xl border border-gray-100 dark:border-white/5"
        >
          <div
            class="w-10 h-10 bg-white dark:bg-white/10 text-blue-900 dark:text-blue-200 rounded-lg flex items-center justify-center shadow-sm"
          >
            <svg
              class="w-6 h-6"
              fill="none"
              stroke="currentColor"
              stroke-width="2.5"
              viewBox="0 0 24 24"
              ><path
                stroke-linecap="round"
                stroke-linejoin="round"
                d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"
              /></svg
            >
          </div>
          <div>
            <p
              class="text-[9px] font-black text-gray-400 uppercase tracking-widest leading-none mb-1"
            >
              Campus Room
            </p>
            <p
              class="text-xs font-bold text-gray-900 dark:text-white uppercase truncate"
            >
              {enrollment.section?.room
                ? `${enrollment.section.room.building} ${enrollment.section.room.roomNumber}`
                : "Online Hall"}
            </p>
          </div>
        </div>
        <div
          class="flex items-center gap-4 p-4 bg-gray-50 dark:bg-white/5 rounded-2xl border border-gray-100 dark:border-white/5"
        >
          <div
            class="w-10 h-10 bg-white dark:bg-white/10 text-indigo-900 dark:text-indigo-200 rounded-lg flex items-center justify-center shadow-sm"
          >
            <svg
              class="w-6 h-6"
              fill="none"
              stroke="currentColor"
              stroke-width="2.5"
              viewBox="0 0 24 24"
              ><path
                stroke-linecap="round"
                stroke-linejoin="round"
                d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
              /></svg
            >
          </div>
          <div>
            <p
              class="text-[9px] font-black text-gray-400 uppercase tracking-widest leading-none mb-1"
            >
              Semester Term
            </p>
            <p
              class="text-xs font-bold text-gray-900 dark:text-white uppercase"
            >
              {enrollment.section?.term || "FALL"}
              {enrollment.section?.year || 2026}
            </p>
          </div>
        </div>
      </div>
    </div>

    <!-- Actions -->
    <div
      class="flex items-center gap-3 pt-6 border-t border-gray-100 dark:border-white/5 mt-auto"
    >
      {#if !enrollment.isEnrolled && enrollment.status !== "ENROLLED"}
        <div class="flex-1">
          <Button
            variant="success"
            size="md"
            onclick={handleEnroll}
            style="width: 100%"
          >
            Confirm Registration
          </Button>
        </div>
      {/if}

      <div
        class={!enrollment.isEnrolled && enrollment.status !== "ENROLLED"
          ? "flex-1"
          : "w-full"}
      >
        <Button
          variant="secondary"
          size="md"
          onclick={handleDeleteClick}
          style="width: 100%"
        >
          Drop Course
        </Button>
      </div>
    </div>
  </div>
</div>

<!-- Modal confirmed / drop -->
<Modal
  isOpen={showDeleteModal}
  title="Academic Withdrawal"
  type="confirm"
  confirmText="Withdraw Permanently"
  cancelText="Cancel Action"
  onConfirm={handleConfirmDelete}
  onCancel={() => (showDeleteModal = false)}
>
  <div class="space-y-6">
    <p class="text-sm font-medium text-gray-500 leading-relaxed">
      You are about to withdraw from <span
        class="text-blue-900 dark:text-white font-black uppercase italic tracking-tighter"
        >{enrollment.section?.courseName}</span
      >. This action is immediate and may affect your graduation schedule.
    </p>
    <div
      class="bg-red-50 dark:bg-red-500/10 p-6 rounded-2xl border border-red-100 dark:border-red-500/20"
    >
      <div class="flex items-center gap-3 mb-2">
        <div class="w-1.5 h-1.5 rounded-full bg-red-600"></div>
        <span
          class="text-xs font-black uppercase tracking-widest text-red-700 dark:text-red-400"
          >Withdrawal Impact</span
        >
      </div>
      <p class="text-xs font-bold text-red-900 dark:text-red-200">
        This will remove your reservation for {enrollment.section?.courseCode} -
        Section {enrollment.section?.sectionNumber}.
      </p>
    </div>
  </div>
</Modal>
