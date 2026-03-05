import * as yup from 'yup';

export const loginSchema = yup.object().shape({
    username: yup.string().required('Identity is mandatory.'),
    password: yup.string().required('Access credential is required.')
});

export type LoginSchema = typeof loginSchema;
