
---

# 🎓 Enrollment Management System (EMS)

## Project Overview

The **Orange and Bronze University Enrollment Management System** is a full-stack application designed to modernize the manual enrollment process. The system focuses on solving real-world registration bottlenecks: schedule conflicts, overbooked sections, and degree requirement tracking.

---

## 🛠️ Tech Stack & Requirements

### **Backend (Spring Boot)**

* **Language:** Java with Spring Data JPA.
* **Database:** PostgreSQL managed via **Flyway** migrations.
* **API Design:** **OpenAPI 3.0** (Contract-first development highly recommended).
* **Testing:** * **Unit Tests:** JUnit 5 with `@ParameterizedTest`.
* **Integration Tests:** **TestContainers** for real database interaction.


* **Authentication:** Role-based access (STUDENT and ADMIN).

### **Frontend (SvelteKit)**

* **Reactivity:** **Svelte 5 Runes** (`$state`, `$derived`, `$effect`, `$props`).
* **State Management:** Svelte Stores (writable) and `$bindable` props.
* **Form Handling:** **SvelteKit SuperForms** with **Yup** validation.
* **Styling:** Tailwind CSS with Dark Mode support.
* **Rendering:** Hybrid SSR (Server-Side Rendering) using `load()` functions.

---

## 🏛️ Domain Model & Business Logic

1. **Conflict Validation:** A student cannot enroll in two sections if their schedules overlap.
2. **Capacity Management:** Sections have a hard limit; once reached, enrollment is blocked.
3. **Degree Tracking:** A Degree consists of specific Courses; students must complete these to graduate.
4. **EAF (Enrollment Assessment Form):** The final output containing the student's validated schedule.
---
## 🚀 Key Features to Implement

| Feature | Description |
| --- | --- |
| **Auth System** | Redirect logic for STUDENT vs ADMIN roles. |
| **Enlistment Engine** | Logic to prevent "Overbooked" sections and schedule overlaps. |
| **Admin Dashboard** | View student lists by section/subject and manage capacity. |
| **Reactive UI** | Real-time status filters (All/Enrolled/Pending) using Svelte Runes. |
| **Theming** | Seamless Dark/Light mode toggle via Tailwind. |

---

## 🧪 Testing Standards

### **Backend Unit Test Example**

```java
@ParameterizedTest
@ValueSource(strings = {"08:00-09:30", "10:00-11:30"})
void shouldDetectScheduleConflict(String newSchedule) {
    // Assert conflict logic
}

```

### **Integration Testing**

Use **TestContainers** to spin up a PostgreSQL container during the `mvn verify` phase to ensure your Flyway migrations and JPA queries work in a production-like environment.

---

## 📝 Submission Checklist

* [ ] Valid `create_schema.sql` or Flyway migration files.
* [ ] OpenAPI documentation accessible via Swagger UI.
* [ ] All Runes used correctly in Svelte 5.
* [ ] Integration tests passing with TestContainers.
* [ ] Responsive UI built with Tailwind.

---
