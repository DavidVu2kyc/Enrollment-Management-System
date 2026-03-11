<script lang="ts">
  import { fade, scale } from "svelte/transition";
  import type { SectionResponse } from "$lib/server/section";

  interface Props {
    selectedSections: SectionResponse[];
    show: boolean;
    onClose: () => void;
    onConfirm: () => void;
    isSubmitting?: boolean;
  }

  let { selectedSections, show, onClose, onConfirm, isSubmitting = false }: Props = $props();

  const formatTime = (time: string | null) => {
    if (!time) return "TBA";
    return time.split(':').slice(0, 2).join(':');
  };

  const calculateTotalCredits = () => {
    // Assuming each course has a credit value, if not, just show count
    return selectedSections.length;
  };
</script>

{#if show}
  <button 
    class="modal-backdrop" 
    transition:fade 
    onclick={onClose}
    aria-label="Close modal"
  ></button>
  <div class="modal-container" transition:scale={{ start: 0.95 }}>
    <div class="modal-content">
      <div class="header">
        <h2>Confirm Registration</h2>
        <p>Review your selected sections before finalizing enrollment.</p>
        <button class="close-btn" onclick={onClose}>×</button>
      </div>

      <div class="section-list">
        {#each selectedSections as section}
          <div class="section-item">
            <div class="item-info">
              <span class="course-code">{section.course?.code}</span>
              <span class="course-title">{section.course?.title}</span>
              <div class="section-details">
                <span class="tag">{section.sectionCode}</span>
                <span class="schedule">
                  {section.schedule?.dayOfWeek} | {formatTime(section.schedule?.startTime ?? null)} - {formatTime(section.schedule?.endTime ?? null)}
                </span>
              </div>
            </div>
          </div>
        {/each}
      </div>

      <div class="summary">
        <div class="summary-item">
          <span>Total Sections:</span>
          <span class="value">{selectedSections.length}</span>
        </div>
      </div>

      <div class="actions">
        <button class="cancel-btn" onclick={onClose} disabled={isSubmitting}>
          Cancel
        </button>
        <button class="confirm-btn" onclick={onConfirm} disabled={isSubmitting}>
          {#if isSubmitting}
            <span class="spinner"></span>
            Processing...
          {:else}
            Confirm Registration
          {/if}
        </button>
      </div>
    </div>
  </div>
{/if}

<style>
  .modal-backdrop {
    position: fixed;
    inset: 0;
    background: rgba(15, 23, 42, 0.8);
    backdrop-filter: blur(8px);
    z-index: 2000;
  }

  .modal-container {
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 90%;
    max-width: 500px;
    background: #1e293b;
    border-radius: 20px;
    border: 1px solid rgba(148, 163, 184, 0.1);
    box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
    z-index: 2001;
    overflow: hidden;
  }

  .modal-content {
    padding: 2rem;
  }

  .header {
    margin-bottom: 1.5rem;
    position: relative;
  }

  .header h2 {
    margin: 0;
    font-size: 1.5rem;
    color: #f8fafc;
  }

  .header p {
    margin: 0.5rem 0 0;
    color: #94a3b8;
    font-size: 0.9rem;
  }

  .close-btn {
    position: absolute;
    top: -0.5rem;
    right: -0.5rem;
    background: none;
    border: none;
    color: #94a3b8;
    font-size: 1.5rem;
    cursor: pointer;
  }

  .section-list {
    max-height: 300px;
    overflow-y: auto;
    margin-bottom: 1.5rem;
    border: 1px solid rgba(148, 163, 184, 0.1);
    border-radius: 12px;
    background: rgba(15, 23, 42, 0.5);
  }

  .section-item {
    padding: 1rem;
    border-bottom: 1px solid rgba(148, 163, 184, 0.1);
  }

  .section-item:last-child {
    border-bottom: none;
  }

  .course-code {
    display: block;
    font-size: 0.75rem;
    font-weight: 700;
    color: #3b82f6;
    text-transform: uppercase;
    margin-bottom: 0.25rem;
  }

  .course-title {
    display: block;
    font-weight: 600;
    color: #e2e8f0;
    margin-bottom: 0.5rem;
  }

  .section-details {
    display: flex;
    align-items: center;
    gap: 0.75rem;
  }

  .tag {
    font-size: 0.7rem;
    background: rgba(148, 163, 184, 0.1);
    padding: 0.2rem 0.5rem;
    border-radius: 4px;
    color: #94a3b8;
  }

  .schedule {
    font-size: 0.75rem;
    color: #64748b;
  }

  .summary {
    padding: 1rem;
    background: rgba(59, 130, 246, 0.1);
    border-radius: 12px;
    margin-bottom: 1.5rem;
  }

  .summary-item {
    display: flex;
    justify-content: space-between;
    font-weight: 600;
  }

  .summary-item .value {
    color: #3b82f6;
  }

  .actions {
    display: flex;
    gap: 1rem;
  }

  .cancel-btn {
    flex: 1;
    padding: 0.75rem;
    border-radius: 10px;
    background: transparent;
    border: 1px solid rgba(148, 163, 184, 0.2);
    color: #e2e8f0;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s;
  }

  .cancel-btn:hover {
    background: rgba(148, 163, 184, 0.1);
  }

  .confirm-btn {
    flex: 2;
    padding: 0.75rem;
    border-radius: 10px;
    background: #2563eb;
    border: none;
    color: white;
    font-weight: 700;
    cursor: pointer;
    transition: all 0.2s;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
  }

  .confirm-btn:hover {
    background: #1d4ed8;
    transform: translateY(-1px);
  }

  .confirm-btn:disabled {
    opacity: 0.7;
    cursor: not-allowed;
  }

  .spinner {
    width: 16px;
    height: 16px;
    border: 2px solid rgba(255, 255, 255, 0.3);
    border-top-color: white;
    border-radius: 50%;
    animation: spin 1s linear infinite;
  }

  @keyframes spin {
    to { transform: rotate(360deg); }
  }
</style>
