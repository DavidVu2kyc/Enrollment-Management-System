type Theme = 'light' | 'dark';

class ThemeStore {
	#theme = $state<Theme>('light');

	constructor() {
		if (typeof window !== 'undefined') {
			const saved = localStorage.getItem('theme') as Theme;
			const preferred = window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
			this.#theme = saved || preferred;
			this.apply();
		}
	}

	get current() {
		return this.#theme;
	}

	get isDark() {
		return this.#theme === 'dark';
	}

	toggle() {
		this.#theme = this.#theme === 'light' ? 'dark' : 'light';
		this.apply();
	}

	set(theme: Theme) {
		this.#theme = theme;
		this.apply();
	}

	private apply() {
		if (typeof window !== 'undefined') {
			localStorage.setItem('theme', this.#theme);
			if (this.#theme === 'dark') {
				document.documentElement.classList.add('dark');
			} else {
				document.documentElement.classList.remove('dark');
			}
		}
	}
}

export const themeStore = new ThemeStore();
