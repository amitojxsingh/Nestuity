// User API Endpoints
import api from "@/services/api-base";
import {LoginRequest, NewUser, UserPreferences} from "@/types/user.types";

export const userAPI = {
    // POST a new user
    createUser: async (newUser: NewUser) => {
        const response = await api.post('/api/users', newUser);
        return response.data;
    },
    // POST a login request
    login: async (loginRequest: LoginRequest) => {
        const response = await api.post('/api/users/login', loginRequest);
        return response.data;
    },
    // GET a user by ID
    getUserById: async (userId: number) => {
        const response = await api.get(`/api/users/${userId}`);
        return response.data; // This will return the UserResponse DTO from your backend
    },
    // PUT (partial update) a user by ID
    updateUser: async (userId: number, partialUser: Partial<NewUser>) => {
        const response = await api.put(`/api/users/${userId}`, partialUser);
        return response.data; // Returns updated UserResponse DTO
    },
    updateUserPreferences: async (userId: number, preferences: UserPreferences) => {
        const response = await api.put(`/api/users/${userId}/preferences`, preferences);
        return response.data;
    }
}