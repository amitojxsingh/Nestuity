import api from './api-base';

import { UsageCalculatorForm } from '@/types/usage-calculator-form.type';

// Usage Calculator API Endpoints
export const usageCalculatorApi = {
  // Update usage calculation
  update: async ( form: UsageCalculatorForm ): Promise<String> => {
    const response = await api.post<String>('/api/usage-calculator/edit', form);
    return response.data;
  },
};

export default api;

