<script lang="ts">
  import { fade, fly, scale } from "svelte/transition";
  import type { PageData } from "./$types";
  import type { SectionResponse } from "$lib/server/section";
  import RegistrationSummaryModal from "$lib/components/RegistrationSummaryModal.svelte";

  let { data } = $props<{ data: PageData }>();
  let sections = $derived(data.sections ?? []);

  // Modal state
  let selectedSection = $state<SectionResponse | null>(null);
  let isLoadingDetails = $state(false);
  let showModal = $state(false);

  // Multi-selection state
  let selectedSectionIds = $state<number[]>([]);
  let showSummaryModal = $state(false);
  let isSubmittingBulk = $state(false);
  let overlappingSection = $state<SectionResponse | null>(null);

  const formatTime = (time: string | null) => {
    if (!time) return "TBA";
    return time.split(':').slice(0, 2).join(':');
  };

  const parseTime = (timeStr: string) => {
    const [hours, minutes] = timeStr.split(':').map(Number);
    return hours * 60 + minutes;
  };

  const checkOverlap = (s1: SectionResponse, s2: SectionResponse) => {
    if (!s1.schedule || !s2.schedule) return false;
    if (s1.schedule.dayOfWeek !== s2.schedule.dayOfWeek) return false;

    const start1 = parseTime(s1.schedule.startTime);
    const end1 = parseTime(s1.schedule.endTime);
    const start2 = parseTime(s2.schedule.startTime);
    const end2 = parseTime(s2.schedule.endTime);

    return start1 < end2 && end1 > start2;
  };

  const toggleSectionSelection = (section: SectionResponse, e: Event) => {
    e.stopPropagation(); // Don't open details modal
    
    if (selectedSectionIds.includes(section.sectionId)) {
      selectedSectionIds = selectedSectionIds.filter(id => id !== section.sectionId);
    } else {
      // Check for overlap with already selected sections
      const selected = sections.filter((s: SectionResponse) => selectedSectionIds.includes(s.sectionId));
      const conflict = selected.find((s: SectionResponse) => checkOverlap(s, section));
      
      if (conflict) {
        alert(`Schedule conflict: "${section.course?.code}" overlaps with "${conflict.course?.code}" on ${section.schedule?.dayOfWeek}`);
        return;
      }
      
      selectedSectionIds = [...selectedSectionIds, section.sectionId];
    }
  };

  const openDetails = async (sectionId: number) => {
    isLoadingDetails = true;
    showModal = true;
    
    try {
      const resp = await fetch(`/api/sections/${sectionId}`);
      if (!resp.ok) throw new Error("Failed to fetch details");
      selectedSection = await resp.json();
    } catch (err) {
      console.error(err);
      // Fallback: use data from the list
      selectedSection = sections.find((s: SectionResponse) => s.sectionId === sectionId) || null;
    } finally {
      isLoadingDetails = false;
    }
  };

  const closeModal = () => {
    showModal = false;
    setTimeout(() => {
      if (!showModal) selectedSection = null;
    }, 300);
  };

  const handleBulkEnroll = async () => {
    if (selectedSectionIds.length === 0) return;
    isSubmittingBulk = true;
    
    try {
      debugger

      let studentID = data.user?.studentId || null
      console.log("Enrolling student", studentID, "in sections", selectedSectionIds);
      const resp = await fetch('/api/enrollments', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          studentId: data.user?.studentId || null, // Assuming studentId is available in page data
          sectionIds: selectedSectionIds
        })
      });
      debugger
      if (!resp.ok) {
        const err = await resp.json();
        throw new Error(err.message || "Failed to enroll");
      }

      alert(`Successfully enrolled in ${selectedSectionIds.length} sections!`);
      selectedSectionIds = [];
      showSummaryModal = false;
      // Optionally redirect to dashboard or refresh data
      window.location.href = '/';
    } catch (err: any) {
      alert(err.message);
    } finally {
      isSubmittingBulk = false;
    }
  };

  let selectedSectionsList = $derived(sections.filter((s: SectionResponse) => selectedSectionIds.includes(s.sectionId)));
</script>

<div class="page" in:fade={{ duration: 300 }}>
  {#if showModal}
    <!-- MODAL OVERLAY -->
    <button 
      class="modal-overlay" 
      onclick={closeModal} 
      in:fade={{ duration: 200 }} 
      out:fade={{ duration: 200 }}
      aria-label="Close details"
    ></button>

    <!-- MODAL CONTENT -->
    <div 
      class="modal-container" 
      in:scale={{ start: 0.95, duration: 300 }} 
      out:scale={{ start: 0.95, duration: 200 }}
    >
      {#if isLoadingDetails}
        <div class="modal-loading">
          <div class="spinner"></div>
          <p>Fetching section details...</p>
        </div>
      {:else if selectedSection}
        <div class="modal-content">
          <button class="close-btn" onclick={closeModal}>✕</button>
          
          <div class="modal-header">
            <span class="course-code">{selectedSection.course?.code}</span>
            <h2>{selectedSection.course?.title}</h2>
            <div class="section-tag">{selectedSection.sectionCode}</div>
          </div>

          <div class="divider"></div>

          <div class="modal-body">
            <div class="detail-section">
              <h4>Class Information</h4>
              <div class="detail-grid">
                <div class="detail-item">
                  <span class="detail-label">Faculty</span>
                  <p>{selectedSection.facultyName || "TBA"}</p>
                </div>
                <div class="detail-item">
                  <span class="detail-label">Room</span>
                  <p>
                    {#if selectedSection.room}
                      {selectedSection.room.building} {selectedSection.room.roomNumber}
                    {:else}
                      To be assigned
                    {/if}
                  </p>
                </div>
                <div class="detail-item">
                  <span class="detail-label">Schedule</span>
                  <p>
                    {selectedSection.schedule?.dayOfWeek ?? "TBA"} | 
                    {formatTime(selectedSection.schedule?.startTime ?? null)} - {formatTime(selectedSection.schedule?.endTime ?? null)}
                  </p>
                </div>
                <div class="detail-item">
                  <span class="detail-label">Term</span>
                  <p>{selectedSection.term?.name || "Active Term"}</p>
                </div>
              </div>
            </div>

            <div class="detail-section">
              <h4>Enrollment Status</h4>
              <div class="status-box">
                <div class="capacity-info">
                  <div class="capacity-bar-large">
                    <div 
                        class="progress" 
                        class:full={selectedSection.availableSeats === 0}
                        style="width: {(selectedSection.enrolledCount / selectedSection.maxSeats) * 100}%"
                    ></div>
                  </div>
                  <div class="capacity-labels">
                    <span>{selectedSection.enrolledCount} Enrolled</span>
                    <span>{selectedSection.availableSeats} Available</span>
                    <span>{selectedSection.maxSeats} Total Capacity</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="modal-footer">
            <a 
              href="/enrollments/new?sectionId={selectedSection.sectionId}" 
              class="primary-btn"
              class:disabled={selectedSection.availableSeats === 0}
            >
              {#if selectedSection.availableSeats === 0}
                Section Full
              {:else}
                Enroll in this Section
              {/if}
            </a>
          </div>
        </div>
      {/if}
    </div>
  {/if}

  <div class="header" in:fly={{ y: -20, duration: 400 }}>
    <h1>Available Sections</h1>
    <p>Browse and find the perfect time for your courses</p>
  </div>

  {#if sections.length === 0}
    <div class="empty-state" in:fade={{ delay: 200 }}>
      <div class="icon">🔍</div>
      <h3>No sections found</h3>
      <p>Try checking back later or contacting the administration.</p>
    </div>
  {:else}
    <div class="grid">
      {#each sections as section, i (section.sectionId)}
        <div 
          class="card" 
          role="button"
          tabindex="0"
          class:selected={selectedSectionIds.includes(section.sectionId)}
          in:fly={{ y: 20, duration: 400, delay: i * 50 }}
          onclick={() => openDetails(section.sectionId)}
          onkeydown={(e) => e.key === 'Enter' && openDetails(section.sectionId)}
        >
          <button 
            type="button"
            class="selection-checkbox" 
            onclick={(e) => toggleSectionSelection(section, e)}
            aria-label={selectedSectionIds.includes(section.sectionId) ? "Deselect section" : "Select section"}
          >
             <div class="checkbox-inner" class:checked={selectedSectionIds.includes(section.sectionId)}>
               {#if selectedSectionIds.includes(section.sectionId)}
                 <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="4">
                   <path d="M5 13l4 4L19 7" />
                 </svg>
               {/if}
             </div>
          </button>

          <div class="card-header">
            <div class="course-info">
              <span class="course-code">{section.course?.code}</span>
              <h3>{section.course?.title}</h3>
            </div>
            <div class="section-tag">{section.sectionCode}</div>
          </div>

          <div class="card-body">
            <div class="stat-row">
              <span class="icon">👨‍🏫</span>
              <div class="stat">
                <span class="stat-label">Faculty</span>
                <span>{section.facultyName || "To be assigned"}</span>
              </div>
            </div>

            <div class="stat-row">
              <span class="icon">📅</span>
              <div class="stat">
                <span class="stat-label">Schedule</span>
                <span>
                  {section.schedule?.dayOfWeek ?? "TBA"} | 
                  {formatTime(section.schedule?.startTime)} - {formatTime(section.schedule?.endTime)}
                </span>
              </div>
            </div>

            <div class="stat-row">
              <span class="icon">🏛️</span>
              <div class="stat">
                <span class="stat-label">Room</span>
                <span>
                  {#if section.room}
                    {section.room.building} {section.room.roomNumber}
                  {:else}
                    TBA
                  {/if}
                </span>
              </div>
            </div>
          </div>

          <div class="card-footer">
            <div class="availability">
              <div class="capacity-bar">
                <div 
                  class="progress" 
                  class:full={section.availableSeats === 0}
                  class:warning={section.availableSeats > 0 && section.availableSeats < 5}
                  style="width: {(section.enrolledCount / section.maxSeats) * 100}%"
                ></div>
              </div>
              <div class="capacity-text">
                <span class="available">{section.availableSeats} seats left</span>
                <span class="total">of {section.maxSeats}</span>
              </div>
            </div>
          </div>
        </div>
      {/each}
    </div>
  {/if}

  {#if selectedSectionIds.length > 0}
    <div class="selection-bar" transition:fly={{ y: 50, duration: 300 }}>
       <div class="selection-info">
          <span class="count">{selectedSectionIds.length}</span>
          <span class="label">sections selected</span>
       </div>
       <div class="selection-actions">
          <button class="clear-btn" onclick={() => selectedSectionIds = []}>Clear all</button>
          <button class="enroll-btn" onclick={() => showSummaryModal = true}>
             Review & Confirm Registration
          </button>
       </div>
    </div>
  {/if}

  <RegistrationSummaryModal 
    show={showSummaryModal} 
    selectedSections={selectedSectionsList}
    onClose={() => showSummaryModal = false}
    onConfirm={handleBulkEnroll}
    isSubmitting={isSubmittingBulk}
  />
</div>

<style>
  @import url('https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap');

  /* MODAL */
  .modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(15, 23, 42, 0.8);
    backdrop-filter: blur(8px);
    z-index: 1000;
    border: none;
    cursor: default;
  }

  .modal-container {
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 90%;
    max-width: 540px;
    background: #1e293b;
    border-radius: 24px;
    border: 1px solid rgba(148, 163, 184, 0.1);
    box-shadow: 0 40px 100px rgba(0, 0, 0, 0.6);
    z-index: 1001;
    overflow: hidden;
  }

  .modal-loading {
    padding: 4rem 2rem;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 1.5rem;
    color: #94a3b8;
  }

  .modal-content {
    position: relative;
    padding: 2.5rem;
  }

  .close-btn {
    position: absolute;
    top: 1.5rem;
    right: 1.5rem;
    background: rgba(148, 163, 184, 0.1);
    border: none;
    width: 36px;
    height: 36px;
    border-radius: 50%;
    color: white;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s;
  }

  .close-btn:hover {
    background: rgba(148, 163, 184, 0.2);
    transform: rotate(90deg);
  }

  .modal-header {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
    margin-bottom: 2rem;
  }

  .modal-header h2 {
    margin: 0;
    font-size: 1.75rem;
    background: linear-gradient(135deg, #fff 0%, #94a3b8 100%);
    background-clip: text;
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }

  .divider {
    height: 1px;
    background: rgba(148, 163, 184, 0.1);
    margin: 0 -2.5rem 2rem -2.5rem;
  }

  .modal-body {
    display: flex;
    flex-direction: column;
    gap: 2rem;
  }

  .detail-section h4 {
    margin: 0 0 1rem 0;
    font-size: 0.75rem;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    color: #64748b;
  }

  .detail-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 1.5rem;
  }

  .detail-item .detail-label {
    display: block;
    font-size: 0.65rem;
    text-transform: uppercase;
    color: #94a3b8;
    margin-bottom: 0.25rem;
  }

  .detail-item p {
    margin: 0;
    font-weight: 600;
    color: #e2e8f0;
  }

  .status-box {
    background: #0f172a;
    padding: 1.5rem;
    border-radius: 16px;
    border: 1px solid rgba(148, 163, 184, 0.1);
  }

  .capacity-bar-large {
    height: 8px;
    background: #1e293b;
    border-radius: 10px;
    overflow: hidden;
    margin-bottom: 1rem;
  }

  .capacity-labels {
    display: flex;
    justify-content: space-between;
    font-size: 0.75rem;
    font-weight: 600;
    color: #94a3b8;
  }

  .modal-footer {
    margin-top: 2.5rem;
  }

  .primary-btn {
    width: 100%;
    height: 48px;
    background: #2563eb;
    color: white;
    text-decoration: none;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 700;
    transition: all 0.2s;
  }

  .primary-btn:hover:not(.disabled) {
    background: #1d4ed8;
    transform: translateY(-2px);
  }

  .primary-btn.disabled {
    background: rgba(148, 163, 184, 0.1);
    color: #64748b;
    pointer-events: none;
  }

  /* MAIN PAGE STYLES */
  .page {
    min-height: 100vh;
    padding: 2.5rem 2rem;
    background: var(--page-bg);
    color: var(--text-hi);
    font-family: var(--font);
  }

  .header {
    margin-bottom: 3rem;
    text-align: center;
  }

  .header h1 {
    font-size: 2.5rem;
    font-weight: 800;
    margin: 0;
    background: linear-gradient(135deg, #fff 0%, #94a3b8 100%);
    background-clip: text;
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }

  .header p {
    color: #94a3b8;
    font-size: 1.1rem;
    margin-top: 0.5rem;
  }

  .grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
    gap: 1.5rem;
  }

  .card {
    background: var(--surface-1);
    border: 1px solid var(--border-1);
    border-radius: var(--radius-md);
    padding: 1.5rem;
    display: flex;
    flex-direction: column;
    gap: 1.25rem;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
    overflow: hidden;
    cursor: pointer;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
    text-align: left;
    outline: none;
  }

  .card:hover {
    transform: translateY(-5px);
    border-color: rgba(59, 130, 246, 0.4);
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: 1rem;
  }

  .course-info {
    display: flex;
    flex-direction: column;
    gap: 0.25rem;
  }

  .course-code {
    font-size: 0.75rem;
    font-weight: 700;
    color: #3b82f6;
    text-transform: uppercase;
    letter-spacing: 0.05em;
  }

  .card-header h3 {
    margin: 0;
    font-size: 1.1rem;
    font-weight: 700;
    line-height: 1.4;
    color: var(--text-hi);
  }

  .section-tag {
    background: rgba(148, 163, 184, 0.1);
    padding: 0.4rem 0.75rem;
    border-radius: 8px;
    font-size: 0.75rem;
    font-weight: 600;
    color: #94a3b8;
    white-space: nowrap;
  }

  .card-body {
    display: flex;
    flex-direction: column;
    gap: 1rem;
  }

  .stat-row {
    display: flex;
    align-items: center;
    gap: 0.85rem;
  }

  .stat {
    display: flex;
    flex-direction: column;
    gap: 0.1rem;
  }

  .stat .stat-label {
    font-size: 0.65rem;
    text-transform: uppercase;
    font-weight: 600;
    color: var(--text-lo);
  }

  .stat span {
    font-size: 0.85rem;
    color: var(--text-hi);
  }

  .card-footer {
    margin-top: auto;
    padding-top: 1rem;
    border-top: 1px solid rgba(148, 163, 184, 0.08);
  }

  .capacity-bar {
    height: 4px;
    background: #0f172a;
    border-radius: 10px;
    overflow: hidden;
    margin-bottom: 0.5rem;
  }

  .progress {
    height: 100%;
    background: #22c55e;
    transition: width 0.5s ease;
  }

  .progress.warning { background: #eab308; }
  .progress.full { background: #ef4444; }

  .capacity-text {
    display: flex;
    justify-content: space-between;
    font-size: 0.7rem;
    font-weight: 600;
  }

  .available { color: #22c55e; }
  .total { color: #64748b; }

  .spinner {
    width: 24px;
    height: 24px;
    border: 3px solid rgba(59, 130, 246, 0.3);
    border-top-color: #3b82f6;
    border-radius: 50%;
    animation: spin 1s linear infinite;
  }

  .card.selected {
    border-color: #3b82f6;
    background: rgba(59, 130, 246, 0.05);
    box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2);
  }

  .selection-checkbox {
    position: absolute;
    top: 1.25rem;
    right: 1.25rem;
    z-index: 10;
    padding: 0.5rem;
    margin: -0.5rem;
    background: none;
    border: none;
    cursor: pointer;
  }

  .checkbox-inner {
    width: 22px;
    height: 22px;
    border: 2px solid rgba(148, 163, 184, 0.3);
    border-radius: 6px;
    background: rgba(15, 23, 42, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s;
    color: white;
  }

  .checkbox-inner.checked {
    background: var(--blue);
    border-color: var(--blue);
  }

  .checkbox-inner svg {
    width: 14px;
    height: 14px;
  }

  .selection-bar {
    position: fixed;
    bottom: 2rem;
    left: 50%;
    transform: translateX(-50%);
    background: var(--surface-2);
    border: 1px solid var(--border-1);
    border-radius: 100px;
    padding: 0.75rem 1.5rem;
    display: flex;
    align-items: center;
    gap: 2rem;
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.4);
    z-index: 1500;
    backdrop-filter: blur(12px);
  }

  .selection-info {
    display: flex;
    align-items: center;
    gap: 0.75rem;
  }

  .selection-info .count {
    background: #3b82f6;
    color: white;
    width: 28px;
    height: 28px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 0.85rem;
    font-weight: 700;
  }

  .selection-info .label {
    font-size: 0.9rem;
    font-weight: 600;
    color: #e2e8f0;
  }

  .selection-actions {
    display: flex;
    align-items: center;
    gap: 1rem;
  }

  .clear-btn {
    background: none;
    border: none;
    color: #94a3b8;
    font-size: 0.85rem;
    font-weight: 600;
    cursor: pointer;
    transition: color 0.2s;
  }

  .clear-btn:hover {
    color: #f87171;
  }

  .enroll-btn {
    background: var(--blue);
    color: white;
    border: none;
    padding: 0.6rem 1.25rem;
    border-radius: 50px;
    font-size: 0.85rem;
    font-weight: 700;
    cursor: pointer;
    transition: all 0.2s;
  }

  .enroll-btn:hover {
    background: var(--blue);
    opacity: 0.9;
    transform: scale(1.02);
  }

  @keyframes spin {
    to { transform: rotate(360deg); }
  }

  @media (max-width: 640px) {
    .modal-content { padding: 1.5rem; }
    .detail-grid { grid-template-columns: 1fr; gap: 1rem; }
    .modal-header h2 { font-size: 1.25rem; }
    .selection-bar {
       width: calc(100% - 2rem);
       gap: 1rem;
       flex-direction: column;
       padding: 1rem;
       border-radius: 20px;
    }
  }
</style>
