import axios from 'axios';
import { Baby, DiaperUsage, Inventory } from '../types/baby.types';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const diaperApi = {
    // Get baby diaper usage
    getDiaperUsage: async (id: number): Promise<DiaperUsage> => {
        const response = await api.get<DiaperUsage>(`/api/babies/${id}/diaper-usage`);
        return response.data;
    },

    // Update baby diaper usage 
    updateDiaperUsage: async (id: number, newUsage: Record<string, number>): Promise<number> => {
        const response = await api.put<number>(`/api/babies/${id}/diaper-usage`, newUsage);
        return response.data;
    },
    // Get all inventory items for a user
    getUserInventory: async (userId: number): Promise<Inventory[]> => {
        const response = await api.get<Inventory[]>(`/api/inventory/user/${userId}`);
        return response.data;
    },
    // --- INDIVIDUAL DIAPERS ---
    // Get remaining diapers for a parent
    getRemainingDiapers: async (id: number): Promise<number> => {
        const response = await api.get<number>(`/api/inventory/user/${id}/diapers/single-quantity`);
        return response.data;
    },

    updateRemainingDiapers: async (
        id: number,
        totalSingleQuantity: number
    ): Promise<number> => {
        const response = await api.put<number>(
            `/api/inventory/user/${id}/diapers/single-quantity`,
            { totalSingleQuantity }
        );
        return response.data;
    },

    // --- DIAPER BOXES ---
    // Get remaining diaper boxes for a parent
    getRemainingDiaperBoxes: async (id: number): Promise<number> => {
        const response = await api.get<number>(`/api/inventory/user/${id}/diapers/unit-quantity`);
        return response.data;
    },

    updateRemainingDiaperBoxes: async (
        id: number,
        totalUnitQuantity: number
    ): Promise<number> => {
        const response = await api.put<number>(
            `/api/inventory/user/${id}/diapers/unit-quantity`,
            { totalUnitQuantity }
        );
        return response.data;
    },
}

export const babyApi = {
    // Get all babies
    getAll: async (): Promise<Baby[]> => {
        const response = await api.get<Baby[]>('/api/babies');
        return response.data;
    },
    
    // Get a single baby by baby ID
    getByBabyId: async (id: number): Promise<Baby> => {
        const response = await api.get<Baby>(`/api/babies/${id}`);
        return response.data;
    },

    // Get a single baby by user ID
    getByUserId: async (id: number): Promise<Baby[]> => {
        const response = await api.get<Baby[]>(`/api/babies/user/${id}`);
        return response.data;
    },
    
    // Create a new baby
    create: async (baby: Partial<Baby>): Promise<Baby> => {
        const response = await api.post<Baby>('/api/babies', baby);
        return response.data;
    },
    
    // Update a baby
    update: async (id: number, baby: Partial<Baby>): Promise<Baby> => {
        const response = await api.put<Baby>(`/api/babies/${id}`, baby);
        return response.data;
    },
    
    // Delete a baby
    delete: async (id: number): Promise<void> => {
        await api.delete(`/api/babies/${id}`);
    },
}

export default api;