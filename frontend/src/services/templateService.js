import api from './api';

export const templateService = {
  uploadTemplate: async (file, name, textColor = '#FFFFFF', boundingBoxes = '') => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('name', name);
    formData.append('textColor', textColor);
    if (boundingBoxes) {
      formData.append('boundingBoxes', boundingBoxes);
    }
    const response = await api.post('/api/templates', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return response.data;
  },

  getAllTemplates: async (page = 0, size = 12) => {
    const response = await api.get('/api/templates', { params: { page, size } });
    return response.data;
  },

  deleteTemplate: async (id) => {
    const response = await api.delete(`/api/templates/${id}`);
    return response.data;
  },
};
