// Modal types
export interface ModalState {
	isOpen: boolean;
	title: string;
	message: string;
	type: 'confirm' | 'alert' | 'success' | 'error';
	onConfirm?: () => void;
	onCancel?: () => void;
}
