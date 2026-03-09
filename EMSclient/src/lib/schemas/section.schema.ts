import * as Y from "yup";

export type Status = "PENDING" | "ENROLLED" | "DROPPED";

export const enrollmentSchema = Y.object({
  sectionId: Y.number()
    .typeError("Section choice is mandatory")
    .required("Section choice is mandatory"),

  status: Y.mixed<Status>()
    .oneOf(["PENDING", "ENROLLED", "DROPPED"])
    .default("PENDING"),
});

export type EnrollmentFormSchema = Y.InferType<typeof enrollmentSchema>;