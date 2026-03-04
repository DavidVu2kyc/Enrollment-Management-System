// See https://svelte.dev/docs/kit/types#app.d.ts
// for information about these interfaces
// global Typescript definitions
import type { User } from '$lib/types';

declare global {
	namespace App {
		interface Locals {
			user?: User;
			token?: string;
		}
		// interface Error {}
		// interface PageData {}
		// interface PageState {}
		// interface Platform {}
	}
}

export { };

