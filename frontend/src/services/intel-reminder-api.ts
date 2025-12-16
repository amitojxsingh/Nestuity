import axios from 'axios';
import { BabyReminder } from '@/types/reminder.types'; // create this DTO type to match backend

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const babyReminderAPI = {
  /** Create baseline reminders for a baby (system-generated from JSON) */
  createBaseline: async (babyId: number): Promise<void> => {
    await api.post(`/api/reminders/${babyId}`);
  },

  /** Get a single reminder by ID */
  getById: async (id: number): Promise<BabyReminder> => {
    const response = await api.get<BabyReminder>(`/api/reminders/${id}`);
    return response.data;
  },

  /** Get all reminders for a baby */
  getByBabyId: async (babyId: number): Promise<BabyReminder[]> => {
    const response = await api.get<BabyReminder[]>(`/api/reminders/baby/${babyId}`);
    return response.data;
  },

  /** Get upcoming reminders for baby — optional daysAhead (0=today, 7=week, 30=month) */
  getUpcoming: async (babyId: number, daysAhead?: number): Promise<BabyReminder[]> => {
    const params = daysAhead ? { daysAhead } : {};
    const response = await api.get<BabyReminder[]>(`/api/reminders/baby/${babyId}/upcoming`, { params });
    return response.data;
  },

  /** Get reminders by category (medical / overdue) */
  getByCategory: async (babyId: number, type: 'medical' | 'overdue'): Promise<BabyReminder[]> => {
    const response = await api.get<BabyReminder[]>(`/api/reminders/baby/${babyId}/reminders`, { params: { type } });
    return response.data;
  },

  /** Create a custom task reminder for a baby */
  createTask: async (babyId: number, reminder: Partial<BabyReminder>): Promise<BabyReminder> => {
    const response = await api.post<BabyReminder>(`/api/reminders/baby/${babyId}/task`, reminder);
    return response.data;
  },

  /** Update an existing reminder (partial update allowed) */
  update: async (id: number, reminder: Partial<BabyReminder>): Promise<BabyReminder> => {
    const response = await api.put<BabyReminder>(`/api/reminders/${id}`, reminder);
    return response.data;
  },

  /** Mark a reminder as completed (normal completion) */
  complete: async (id: number): Promise<void> => {
    await api.put(`/api/reminders/${id}/complete`);
  },

  /** Mark a TASK reminder as completed (special delete-style op) */
  completeTask: async (id: number): Promise<void> => {
    await api.delete(`/api/reminders/${id}/task-complete`);
  },

  /** Permanently delete a reminder (only for TASK type) */
  delete: async (id: number): Promise<void> => {
    await api.delete(`/api/reminders/${id}`);
  },

  /** Get the current milestone reminder for a baby */
  getCurrentMilestone: async (babyId: number): Promise<BabyReminder | null> => {
    try {
      const response = await api.get<BabyReminder>(`/api/reminders/baby/${babyId}/current`);
      return response.data;
    } catch (error: any) {
      if (error.response?.status === 404) return null;
      throw error;
    }
  },
};

export default api;
