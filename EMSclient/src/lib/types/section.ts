import type { Room } from "./room";
import type { Schedule } from "./schedule";

// Section
export interface Section {
	id: string;
	courseId: string;
	courseName: string;
	courseCode: string;
	term: string;
	year: number;
	sectionNumber: string;
	capacity: number;
	enrolledCount: number;
	schedules: Schedule[];
	roomId?: string;
	room?: Room;
	instructor?: string;
}