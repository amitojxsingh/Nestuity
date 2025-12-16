export interface NewUser {
    firstName: string;
    lastName: string;
    email: string;
    password?: string;
    phoneNumber?: string;
    authProvider?: "credentials" | "google";
    providerId?: string;
}
export interface LoginRequest {
    email: string;
    password: string;
}
export interface User {
    id: string;
    firstName: string;
    lastName: string;
    email: string;
    phoneNumber?: string;
    isActive: boolean;
    createdAt: Date;
    updatedAt: Date;
    preferences?: UserPreferences;
    babies?: Baby[];
}
export interface UserPreferences {
    currency: string;
    timezone: string;
    emailNotificationsEnabled: boolean;
    smsNotificationsEnabled: boolean;
}
export interface Baby {
    id: number;
    name: string;
    dob: string;
    weight: number;
    diaperSize: string;
    dailyUsage: number;
    diapersPerBox: number;
}
