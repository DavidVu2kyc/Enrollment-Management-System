import type { User } from '$lib/types';

class UserStore {
	#user = $state<User | null>(null);

	get current() {
		return this.#user;
	}

	get isAuthenticated() {
		return this.#user !== null;
	}

	get isAdmin() {
		return this.#user?.role === 'ADMIN';
	}

	get isStudent() {
		return this.#user?.role === 'STUDENT';
	}

	login(user: User) {
		this.#user = user;
	}

	logout() {
		this.#user = null;
	}

	update(user: User) {
		this.#user = user;
	}

	setUser(user: User | null) {
		this.#user = user;
	}
}

export const userStore = new UserStore();
