import axios from 'axios';
import { BabyProduct } from '@/types/product.types';
import api from './api-base';


// Baby Product API Endpoints
export const babyProductAPI = {
  // Get all baby products
  getAll: async (): Promise<BabyProduct[]> => {
    const response = await api.get<BabyProduct[]>('/api/baby-products');
    return response.data;
  },

  // Get a single baby product by ID
  getById: async (id: number): Promise<BabyProduct> => {
    const response = await api.get<BabyProduct>(`/api/baby-products/${id}`);
    return response.data;
  },

  // Create a new baby product
  create: async (product: Partial<BabyProduct>): Promise<BabyProduct> => {
    const response = await api.post<BabyProduct>('/api/baby-products', product);
    return response.data;
  },

  // Update a baby product
  update: async (id: number, product: Partial<BabyProduct>): Promise<BabyProduct> => {
    const response = await api.put<BabyProduct>(`/api/baby-products/${id}`, product);
    return response.data;
  },

  // Delete a baby product
  delete: async (id: number): Promise<void> => {
    await api.delete(`/api/baby-products/${id}`);
  },
};

export default api;

