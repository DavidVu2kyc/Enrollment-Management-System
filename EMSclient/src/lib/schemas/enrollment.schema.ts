import * as yup from "yup";

export const enrollmentStatuses = [
  "PENDING",
  "ENROLLED",
  "DROPPED",
] as const;

export type EnrollmentStatus = "PENDING" | "ENROLLED" | "DROPPED" ;

export const enrollmentSchema = yup.object({
  studentId: yup
    .number()
    .optional(),

  sectionId: yup
    .number()
    .typeError("Section ID must be a number.")
    .required("Target Section Identifier is required."),

  term: yup
    .string()
    .optional(),

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