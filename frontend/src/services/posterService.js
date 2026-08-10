import api from './api';

export const posterService = {
  generatePoster: async (personId, templateId = null) => {
    const params = templateId ? { templateId } : {};
    const response = await api.post(`/api/poster/generate/${personId}`, null, { params });
    return response.data;
  },

  getPosterHistory: async (page = 0, size = 12) => {
    const response = await api.get('/api/poster/history', { params: { page, size } });
    return response.data;
  }
};
