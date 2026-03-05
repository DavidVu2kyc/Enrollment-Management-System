import type { Room } from "./room";

// Schedule
export interface Schedule {
	id: string;
	sectionId: string;
	dayOfWeek: string; // MONDAY, TUESDAY, etc.
	startTime: string; // HH:mm format
	endTime: string; // HH:mm format
	roomId?: string;
	room?: Room;
}
