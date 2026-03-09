import type { Course } from "./enrollment";
import type { Room } from "./room";
import type { Schedule } from "./schedule";
import type { Term } from "./term";

// Section
export interface Section {
	sectionId: number;
	sectionCode: string;
	course?:Course;
	term?:Term;
	room?:Room ;
	schedule?:Schedule;
	facultyName :string;
	maxSeats:number;
	enrolledCount: number;
}
