import api from './api';

export const personService = {
  getAllPeople: async (page = 0, size = 10, search = '') => {
    const response = await api.get('/api/people', { params: { page, size, search } });
    return response.data;
  },

  getPersonById: async (id) => {
    const response = await api.get(`/api/people/${id}`);
    return response.data;
  },

  createPerson: async (data) => {
    const response = await api.post('/api/people', data);
    return response.data;
  },

  updatePerson: async (id, data) => {
    const response = await api.put(`/api/people/${id}`, data);
    return response.data;
  },

  deletePerson: async (id) => {
    const response = await api.delete(`/api/people/${id}`);
    return response.data;
  },

  uploadExcel: async (file) => {
    const formData = new FormData();
    formData.append('file', file);
    const response = await api.post('/api/people/upload-excel', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  },

  uploadPhoto: async (personId, file) => {
    const formData = new FormData();
    formData.append('file', file);
    const response = await api.post(`/api/media/upload-photo/${personId}`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return response.data;
  },
};
