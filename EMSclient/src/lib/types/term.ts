
export interface Term {
    termId: number ;
    name : string ;
    year: number
    semester: 'FIRST' | 'SECOND' | 'SUMMER' ;
    isActive: boolean ;
}