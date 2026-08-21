import api from './api';

export const dashboardService = {
  getDashboardStats: async () => {
    const response = await api.get('/api/dashboard/stats');
    return response.data;
  }
};
