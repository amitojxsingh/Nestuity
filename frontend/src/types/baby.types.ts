export interface User {
    id: number;
    email: string;
    phoneNumber: string;
    firstName: string;
    lastName: string;
    isActive: boolean;
    preferences: UserPreferences;
    createdAt: string;
    updatedAt: string;
    babies: Array<Baby>;
    inventory: Inventory[];
}

export interface UserPreferences {
    id: number;
    currency: string;
    emailNotificationsEnabled: boolean;
    smsNotificationsEnabled: boolean;
}

export interface Baby {
    id: number;
    user: User;
    dob: string;
    name: string;
    weight: number;
    diaperSize: string;
    diapersPerBox: number;
    dailyUsage: number;
}

export interface DiaperUsage {
    remainingSupply: number;
    daysLeft: number;
    recommendedPurchase: number;
    message: string;
}

export interface Inventory {
    id: number;
    userId: number;
    supplyName: string;
    totalSingleQuantity: number;
    totalUnitQuantity: number;
    unitConversion: number;
    unitName: string;
    preferredSupplyMin: number;
}