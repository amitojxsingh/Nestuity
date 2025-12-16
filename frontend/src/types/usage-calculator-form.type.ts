export interface UsageCalculatorForm {
    id: Number;
    dob: String;
    weight: Number;
    diaperSize: String;
    dailyUsage: Number;
    diapersPerBox?: Number;
    boxesAtHome?: Number;
}