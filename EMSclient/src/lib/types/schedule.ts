import type { Room } from "./room";

// Schedule
export interface Schedule {
	scheduleId: number;
	sectionId: string;
	dayOfWeek: string; // MONDAY, TUESDAY, etc.
	startTime: string; // HH:mm format
	endTime: string; // HH:mm format
	roomId?: string;
	room?: Room;
}
