export interface UsageSuggestion {
    size: string;
    weightRange: string;
    avgUsage: string;
    ageRange: string;
    perBox: string;
    weightMin?: number;      // in kg
    weightMax?: number;      // in kg
    ageMinMonths?: number;
    ageMaxMonths?: number;
}

export const suggestionTable: UsageSuggestion[] = [
    { size: "NB", weightRange: "Up to 4.54 kg",   avgUsage: "10-12", ageRange: "0-1.5 mo", perBox: "76", weightMin: 0,    weightMax: 4.54, ageMinMonths: 0, ageMaxMonths: 1.5 },
    { size: "1",  weightRange: "3.63-6.35 kg",    avgUsage: "8-10",  ageRange: "1-4 mo",   perBox: "82", weightMin: 3.63, weightMax: 6.35, ageMinMonths: 1, ageMaxMonths: 4 },
    { size: "2",  weightRange: "5.44-8.16 kg",    avgUsage: "8-9",   ageRange: "3-8 mo",   perBox: "74", weightMin: 5.44, weightMax: 8.16, ageMinMonths: 3, ageMaxMonths: 8 },
    { size: "3",  weightRange: "7.26-12.70 kg",   avgUsage: "6-7",   ageRange: "5-24 mo",  perBox: "66", weightMin: 7.26, weightMax: 12.7, ageMinMonths: 5, ageMaxMonths: 24 },
    { size: "4",  weightRange: "9.98-16.78 kg",   avgUsage: "5-7",   ageRange: "18-36 mo", perBox: "58", weightMin: 9.98, weightMax: 16.78, ageMinMonths: 18, ageMaxMonths: 36 },
    { size: "5",  weightRange: "12.25+ kg",       avgUsage: "3-5",   ageRange: "2-3 yr",   perBox: "48", weightMin: 12.25, weightMax: Infinity, ageMinMonths: 24, ageMaxMonths: 36 },
    { size: "6",  weightRange: "12.25+ kg",       avgUsage: "3-5",   ageRange: "2.5-3 yr", perBox: "42", weightMin: 12.25, weightMax: Infinity, ageMinMonths: 30, ageMaxMonths: Infinity },
];