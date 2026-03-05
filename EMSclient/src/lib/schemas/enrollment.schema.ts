import * as yup from 'yup';

export const enrollmentSchema = yup.object().shape({
    studentId: yup.string().required('Institutional Student ID is required.'),
    sectionId: yup.string().required('Target Section Identifier is required.'),
    term: yup.string().required('Academic Term must be specified.'),
    status: yup.string().oneOf(['PENDING', 'ENROLLED', 'DROPPED'], 'Invalid enrollment status protocol.').default('PENDING')
});

export type EnrollmentSchema = typeof enrollmentSchema;
    