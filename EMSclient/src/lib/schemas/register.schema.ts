import * as yup from 'yup';

export const registerSchema = yup.object().shape({
    username: yup.string().min(3, 'Username must be at least 3 characters.').max(100, 'Username cannot exceed 100 characters.').required('Username is mandatory.'),
    password: yup.string().min(6, 'Security standard requires at least 6 characters.').required('Access credential is required.'),
    firstName: yup.string().required('First name is required.'),
    lastName: yup.string().required('Last name is required.'),
    studentNumber: yup.string().required('Student number is required.'),
    degreeId: yup.number().required('Degree must be selected.')
});

export type RegisterSchema = typeof registerSchema;
