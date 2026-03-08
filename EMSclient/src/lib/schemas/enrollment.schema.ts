import * as yup from "yup";

export const enrollmentStatuses = [
  "PENDING",
  "ENROLLED",
  "DROPPED",
  "CANCELLED"
] as const;

export type EnrollmentStatus = "PENDING" | "ENROLLED" | "DROPPED" | "CANCELLED";

export const enrollmentSchema = yup.object({
  studentId: yup
    .number()
    .typeError("Student ID must be a number.")
    .required("Institutional Student ID is required."),

  sectionId: yup
    .number()
    .typeError("Section ID must be a number.")
    .required("Target Section Identifier is required."),

  term: yup
    .string()
    .required("Academic Term must be specified."),

  status: yup
    .mixed<EnrollmentStatus>()
    .oneOf(
      ["PENDING", "ENROLLED", "DROPPED"],
      "Invalid enrollment status protocol.",
    )
    .default("PENDING")
    .required()
});

export type EnrollmentSchema = yup.InferType<typeof enrollmentSchema>;