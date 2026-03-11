<script lang="ts">
  import { userStore } from "$lib/stores/user.svelte";
  import { page } from "$app/state";
  import { slide } from "svelte/transition";
  import { goto } from "$app/navigation";
  import { logout } from "$lib/auth";

  async function handleLogout() {
    console.log("Logout function called");
    try {
      // debugger
      const res = await logout(fetch);
      console.log("Logout response: " + res); 
      
      if (!res.ok) {
        console.error("Logout failed");
        return;
      }

      userStore.logout();
      await goto("/login");
    } catch (error) {
      console.error("Logout request failed:", error);
    }
  }

  let isMenuOpen = $state(false);
  let scrolled = $state(false);

  const isActive = (path: string) => {
    if (path === "/")
      return page.url.pathname === "/" || page.url.pathname === "/enrollments";
    return page.url.pathname.startsWith(path);
  };

  const menuItems = $derived([
    {
      label: "Dashboard",
      path: "/",
      roles: ["STUDENT", "ADMIN"],
      icon: "M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6",
    },
    {
      label: "Admin",
      path: "/admin",
      roles: ["ADMIN"],
      icon: "M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z M15 12a3 3 0 11-6 0 3 3 0 016 0z",
    },
    {
      label: "Students",
      path: "/admin/students",
      roles: ["ADMIN"],
      icon: "M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z",
    },
    {
      label: "Sections",
      path: "/admin/sections",
      roles: ["ADMIN"],
      icon: "M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10",
    },
    {
      label: "Profile",
      path: "/students/profile",
      roles: ["STUDENT", "ADMIN"],
      icon: "M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z",
    },
  ]);

  const filteredItems = $derived(
    menuItems.filter((item) =>
      item.roles.includes(userStore.current?.role ?? "STUDENT"),
    ),
  );

  $effect(() => {
    const fn = () => {
      scrolled = window.scrollY > 10;
    };
    window.addEventListener("scroll", fn, { passive: true });
    return () => window.removeEventListener("scroll", fn);
  });
</script>

<svelte:head>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link
    rel="preconnect"
    href="https://fonts.gstatic.com"
    crossorigin="anonymous"
  />
  <link
    href="https://fonts.googleapis.com/css2?family=Syne:wght@600;700;800&display=swap"
    rel="stylesheet"
  />
</svelte:head>

<nav class="nav-root" class:scrolled>
  <div class="pill">
    <a href="/" class="brand">
      <div class="brand-icon">
        <svg
          width="18"
          height="18"
          fill="none"
          stroke="white"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
          viewBox="0 0 24 24"
        >
          <path
            d="M12 6.042A8.967 8.967 0 006 3.75c-1.052 0-2.062.18-3 .512v14.25A8.987 8.987 0 016 18c2.305 0 4.408.867 6 2.292m0-14.25a8.966 8.966 0 016-2.292c1.052 0 2.062.18 3 .512v14.25A8.987 8.987 0 0118 18c-2.305 0-4.408.867-6 2.292m0-14.25v14.25"
          />
        </svg>
      </div>
      <div>
        <span class="brand-name">EMS</span>
        <span class="brand-sub">Institutional</span>
      </div>
    </a>

    <div class="centre">
      {#each filteredItems as item}
        <a href={item.path} class="nl" class:active={isActive(item.path)}>
          <svg
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <path d={item.icon} />
          </svg>
          {item.label}
        </a>
      {/each}
    </div>

    <div class="right">
      <div class="sep"></div>
      <div class="chip">
        <div class="ucopy">
          <span class="uname">{userStore.current?.username ?? "Guest"}</span>
          <span class="urole">{userStore.current?.role ?? ""}</span>
        </div>
        <div class="av">
          {(userStore.current?.username ?? "U")[0].toUpperCase()}
        </div>
      </div>
      <button class="logout-btn" onclick={handleLogout} aria-label="Logout">
        <svg
          width="14"
          height="14"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2.5"
          stroke-linecap="round"
          stroke-linejoin="round"
        >
          <path
            d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4M16 17l5-5-5-5M21 12H9"
          />
        </svg>
      </button>
    </div>

    <button
      class="tog"
      onclick={() => (isMenuOpen = !isMenuOpen)}
      aria-label="Toggle menu"
    >
      <svg
        width="16"
        height="16"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="2.5"
        stroke-linecap="round"
        stroke-linejoin="round"
      >
        {#if isMenuOpen}
          <path d="M6 18L18 6M6 6l12 12" />
        {:else}
          <path d="M3.75 6.75h16.5M3.75 12h16.5m-16.5 5.25h16.5" />
        {/if}
      </svg>
    </button>
  </div>

  {#if isMenuOpen}
    <div class="mmenu" transition:slide={{ duration: 180 }}>
      {#each filteredItems as item}
        <a
          href={item.path}
          class="ml"
          class:active={isActive(item.path)}
          onclick={() => (isMenuOpen = false)}
        >
          <svg
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <path d={item.icon} />
          </svg>
          <span class="ml-txt">{item.label}</span>
          {#if isActive(item.path)}<span class="adot"></span>{/if}
        </a>
      {/each}

      <div class="msep"></div>
      <!-- write your logout button here -->

      <button class="ml mlogout" onclick={handleLogout}>
        <svg
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
        >
          <path
            d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4M16 17l5-5-5-5M21 12H9"
          />
        </svg>
        <span class="ml-txt">Logout</span>
      </button>
    </div>
  {/if}
</nav>

<style>
  :global(:root) {
    --nf: "Syne", sans-serif;
    --nbg: #0a0f1a;
    --nbgs: #060b14;
    --nbr: rgba(255, 255, 255, 0.07);
    --nact: #1d4ed8;
    --ntxt: rgba(148, 185, 255, 0.5);
    --ntxth: #e2ecff;
  }

  .nav-root {
    position: sticky;
    top: 0;
    z-index: 100;
    width: 100%;
    padding: 14px 20px;
    font-family: var(--nf);
    transition: padding 0.3s ease;
  }
  .nav-root.scrolled {
    padding: 8px 20px;
  }

  .pill {
    max-width: 1280px;
    margin: 0 auto;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 6px;
    background: var(--nbg);
    border: 1px solid var(--nbr);
    border-radius: 20px;
    padding: 6px 8px 6px 14px;
    box-shadow:
      0 2px 30px rgba(0, 0, 0, 0.5),
      inset 0 1px 0 rgba(255, 255, 255, 0.04);
    transition:
      background 0.3s,
      box-shadow 0.3s,
      border-color 0.3s;
  }
  .scrolled .pill {
    background: var(--nbgs);
    border-color: rgba(255, 255, 255, 0.09);
    box-shadow:
      0 8px 48px rgba(0, 0, 0, 0.65),
      inset 0 1px 0 rgba(255, 255, 255, 0.05),
      0 0 60px rgba(29, 78, 216, 0.05);
  }

  /* Brand */
  .brand {
    display: flex;
    align-items: center;
    gap: 10px;
    text-decoration: none;
    flex-shrink: 0;
  }
  .brand-icon {
    width: 36px;
    height: 36px;
    background: linear-gradient(135deg, #1d4ed8, #1e3a8a);
    border-radius: 11px;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 0 16px rgba(29, 78, 216, 0.4);
    flex-shrink: 0;
    transition:
      transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1),
      box-shadow 0.3s;
  }
  .brand:hover .brand-icon {
    transform: rotate(8deg) scale(1.06);
    box-shadow: 0 0 26px rgba(29, 78, 216, 0.65);
  }
  .brand-name {
    display: block;
    font-size: 0.95rem;
    font-weight: 800;
    text-transform: uppercase;
    letter-spacing: 0.01em;
    color: var(--ntxth);
  }
  .brand-sub {
    display: block;
    font-size: 0.46rem;
    font-weight: 700;
    letter-spacing: 0.4em;
    text-transform: uppercase;
    color: rgba(96, 165, 250, 0.38);
    margin-top: 2px;
  }

  /* Centre links */
  .centre {
    display: none;
    align-items: center;
    gap: 2px;
    flex: 1;
    justify-content: center;
  }
  @media (min-width: 768px) {
    .centre {
      display: flex;
    }
  }

  .nl {
    display: flex;
    align-items: center;
    gap: 5px;
    padding: 7px 13px;
    border-radius: 12px;
    font-size: 0.6rem;
    font-weight: 700;
    letter-spacing: 0.13em;
    text-transform: uppercase;
    text-decoration: none;
    color: var(--ntxt);
    white-space: nowrap;
    position: relative;
    transition:
      color 0.18s,
      background 0.18s;
  }
  .nl svg {
    width: 12px;
    height: 12px;
    flex-shrink: 0;
    opacity: 0.5;
    transition: opacity 0.18s;
  }
  .nl:hover {
    color: var(--ntxth);
    background: rgba(255, 255, 255, 0.06);
  }
  .nl:hover svg {
    opacity: 0.9;
  }
  .nl.active {
    color: #fff;
    background: var(--nact);
    box-shadow:
      0 2px 14px rgba(29, 78, 216, 0.45),
      inset 0 1px 0 rgba(255, 255, 255, 0.14);
  }
  .nl.active svg {
    opacity: 1;
  }
  .nl.active::after {
    content: "";
    position: absolute;
    bottom: -1px;
    left: 50%;
    transform: translateX(-50%);
    width: 4px;
    height: 4px;
    border-radius: 50%;
    background: rgba(147, 197, 253, 0.9);
    box-shadow: 0 0 6px rgba(147, 197, 253, 1);
  }

  /* Right */
  .right {
    display: none;
    align-items: center;
    gap: 8px;
    flex-shrink: 0;
  }
  @media (min-width: 768px) {
    .right {
      display: flex;
    }
  }
  .sep {
    width: 1px;
    height: 20px;
    background: rgba(255, 255, 255, 0.07);
  }

  .chip {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 4px 5px 4px 10px;
    background: rgba(255, 255, 255, 0.03);
    border: 1px solid rgba(255, 255, 255, 0.07);
    border-radius: 99px;
    cursor: default;
    transition:
      background 0.18s,
      border-color 0.18s;
  }
  .chip:hover {
    background: rgba(255, 255, 255, 0.055);
    border-color: rgba(255, 255, 255, 0.11);
  }

  .ucopy {
    display: none;
    flex-direction: column;
    align-items: flex-end;
    gap: 1px;
  }
  @media (min-width: 1100px) {
    .ucopy {
      display: flex;
    }
  }
  .uname {
    font-size: 0.62rem;
    font-weight: 700;
    letter-spacing: 0.08em;
    text-transform: uppercase;
    color: var(--ntxth);
    white-space: nowrap;
    max-width: 120px;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  .urole {
    font-size: 0.46rem;
    font-weight: 600;
    letter-spacing: 0.22em;
    text-transform: uppercase;
    color: rgba(96, 165, 250, 0.4);
    white-space: nowrap;
  }

  .av {
    width: 28px;
    height: 28px;
    border-radius: 50%;
    background: linear-gradient(135deg, #1d4ed8, #1e3a8a);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 0.6rem;
    font-weight: 800;
    color: #fff;
    flex-shrink: 0;
    position: relative;
    box-shadow: 0 0 10px rgba(29, 78, 216, 0.4);
  }
  .av::after {
    content: "";
    position: absolute;
    bottom: -1px;
    right: -1px;
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: #22c55e;
    border: 2px solid #0a0f1a;
    box-shadow: 0 0 5px rgba(34, 197, 94, 0.8);
  }

  .logout-btn {
    display: flex;
    width: 34px;
    height: 34px;
    align-items: center;
    justify-content: center;
    background: rgba(239, 68, 68, 0.08);
    border: 1px solid rgba(239, 68, 68, 0.18);
    border-radius: 10px;
    color: rgba(239, 68, 68, 0.6);
    cursor: pointer;
    transition:
      background 0.18s,
      border-color 0.18s,
      color 0.18s,
      transform 0.18s;
  }
  .logout-btn:hover {
    background: rgba(239, 68, 68, 0.16);
    border-color: rgba(239, 68, 68, 0.38);
    color: #f87171;
    transform: translateX(1px);
  }

  /* Mobile */
  .tog {
    display: flex;
    width: 34px;
    height: 34px;
    align-items: center;
    justify-content: center;
    background: rgba(255, 255, 255, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.08);
    border-radius: 10px;
    color: var(--ntxth);
    cursor: pointer;
    transition:
      background 0.18s,
      border-color 0.18s;
  }
  .tog:hover {
    background: rgba(255, 255, 255, 0.09);
    border-color: rgba(255, 255, 255, 0.13);
  }
  @media (min-width: 768px) {
    .tog {
      display: none;
    }
  }

  .mmenu {
    max-width: 1280px;
    margin: 6px auto 0;
    background: #070c17;
    border: 1px solid rgba(255, 255, 255, 0.08);
    border-radius: 18px;
    padding: 8px;
    display: flex;
    flex-direction: column;
    gap: 2px;
    box-shadow:
      0 20px 56px rgba(0, 0, 0, 0.65),
      inset 0 1px 0 rgba(255, 255, 255, 0.04);
  }
  .ml {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 11px 14px;
    border-radius: 12px;
    font-size: 0.65rem;
    font-weight: 700;
    letter-spacing: 0.13em;
    text-transform: uppercase;
    text-decoration: none;
    color: var(--ntxt);
    transition:
      color 0.16s,
      background 0.16s;
  }
  .ml svg {
    width: 14px;
    height: 14px;
    flex-shrink: 0;
    opacity: 0.4;
    transition: opacity 0.16s;
  }
  .ml:hover {
    color: var(--ntxth);
    background: rgba(255, 255, 255, 0.05);
  }
  .ml.active {
    color: #fff;
    background: rgba(29, 78, 216, 0.22);
    border: 1px solid rgba(29, 78, 216, 0.28);
  }
  .ml-txt {
    flex: 1;
  }
  .adot {
    width: 5px;
    height: 5px;
    border-radius: 50%;
    background: var(--nact);
    box-shadow: 0 0 7px rgba(59, 130, 246, 0.9);
    margin-left: auto;
  }
  .msep {
    height: 1px;
    background: rgba(255, 255, 255, 0.06);
    margin: 4px 6px;
  }
  .mlogout {
    width: 100%;
    background: none;
    border: none;
    cursor: pointer;
    color: rgba(239, 68, 68, 0.55);
  }
  .mlogout:hover {
    color: #f87171;
    background: rgba(239, 68, 68, 0.08);
  }
</style>
