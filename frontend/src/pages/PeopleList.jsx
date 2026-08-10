import { useState, useEffect, useRef } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Search, Plus, Edit2, Trash2, Upload, Camera, Sparkles, Loader2, X } from 'lucide-react';
import { personService } from '../services/personService';
import { posterService } from '../services/posterService';
import PersonFormModal from '../components/PersonFormModal';

export default function PeopleList() {
  const [people, setPeople] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedPerson, setSelectedPerson] = useState(null);
  const [uploadingPhotoId, setUploadingPhotoId] = useState(null);
  const [generatingId, setGeneratingId] = useState(null);
  const [generatedPoster, setGeneratedPoster] = useState(null);

  useEffect(() => {
    fetchPeople();
  }, [page, search]);

  const fetchPeople = async () => {
    try {
      setLoading(true);
      const res = await personService.getAllPeople(page, 10, search);
      setPeople(res.content);
      setTotalPages(res.totalPages);
    } catch (error) {
      console.error('Error fetching people:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this person?')) {
      try {
        await personService.deletePerson(id);
        fetchPeople();
      } catch (error) {
        console.error('Error deleting person:', error);
      }
    }
  };

  const handleEdit = (person) => {
    setSelectedPerson(person);
    setIsModalOpen(true);
  };

  const handleAdd = () => {
    setSelectedPerson(null);
    setIsModalOpen(true);
  };

  const handleFileUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    try {
      setLoading(true);
      await personService.uploadExcel(file);
      alert('Excel file imported successfully!');
      fetchPeople();
    } catch (error) {
      console.error('Error uploading Excel:', error);
      alert(error.response?.data?.message || 'Failed to upload Excel file.');
    } finally {
      setLoading(false);
      e.target.value = '';
    }
  };

  const handlePhotoUpload = async (personId, e) => {
    const file = e.target.files[0];
    if (!file) return;

    try {
      setUploadingPhotoId(personId);
      await personService.uploadPhoto(personId, file);
      fetchPeople();
    } catch (error) {
      console.error('Error uploading photo:', error);
      alert(error.response?.data?.message || 'Failed to upload photo.');
    } finally {
      setUploadingPhotoId(null);
      e.target.value = '';
    }
  };

  const handleGeneratePoster = async (personId) => {
    try {
      setGeneratingId(personId);
      const res = await posterService.generatePoster(personId);
      setGeneratedPoster(res);
    } catch (error) {
      console.error('Error generating poster:', error);
      alert(error.response?.data?.message || 'Failed to generate poster.');
    } finally {
      setGeneratingId(null);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold text-slate-100">People Management</h1>
          <p className="text-slate-400 mt-1">Manage birthdays and details</p>
        </div>
        <div className="flex space-x-3">
          <label className="bg-slate-800 hover:bg-slate-700 text-slate-200 px-4 py-2 rounded-lg flex items-center transition-colors cursor-pointer">
            <Upload className="w-4 h-4 mr-2" />
            Import Excel
            <input 
              type="file" 
              accept=".xlsx" 
              className="hidden" 
              onChange={handleFileUpload} 
            />
          </label>
          <button onClick={handleAdd} className="bg-primary-600 hover:bg-primary-500 text-white px-4 py-2 rounded-lg flex items-center transition-colors shadow-lg shadow-primary-500/25">
            <Plus className="w-4 h-4 mr-2" />
            Add Person
          </button>
        </div>
      </div>

      <div className="glass-panel p-4 flex items-center">
        <Search className="w-5 h-5 text-slate-500 mr-3" />
        <input 
          type="text" 
          placeholder="Search by name, email, or department..." 
          className="bg-transparent border-none outline-none focus:ring-0 text-slate-200 w-full placeholder-slate-500"
          value={search}
          onChange={(e) => {
            setSearch(e.target.value);
            setPage(0);
          }}
        />
      </div>

      <div className="glass-panel overflow-hidden">
        <table className="w-full text-left border-collapse">
          <thead>
            <tr className="bg-slate-900/50 text-slate-400 border-b border-slate-800">
              <th className="p-4 font-medium">Name</th>
              <th className="p-4 font-medium">Date of Birth</th>
              <th className="p-4 font-medium">Department</th>
              <th className="p-4 font-medium text-right">Actions</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan="4" className="p-8 text-center text-slate-400">Loading...</td>
              </tr>
            ) : people.length === 0 ? (
              <tr>
                <td colSpan="4" className="p-8 text-center text-slate-400">No people found.</td>
              </tr>
            ) : (
              people.map((person) => (
                <tr key={person.id} className="border-b border-slate-800/50 hover:bg-slate-800/20 transition-colors">
                  <td className="p-4">
                    <div className="flex items-center space-x-3">
                      <div className="relative group">
                        {person.photoUrl ? (
                          <img
                            src={person.photoUrl}
                            alt={person.name}
                            className="w-10 h-10 rounded-full object-cover border-2 border-slate-700"
                          />
                        ) : (
                          <div className="w-10 h-10 rounded-full bg-indigo-500/20 text-indigo-400 flex items-center justify-center font-bold">
                            {person.name.charAt(0)}
                          </div>
                        )}
                        <label className="absolute inset-0 rounded-full bg-black/50 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center cursor-pointer">
                          {uploadingPhotoId === person.id ? (
                            <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                          ) : (
                            <Camera className="w-4 h-4 text-white" />
                          )}
                          <input
                            type="file"
                            accept="image/*"
                            className="hidden"
                            onChange={(e) => handlePhotoUpload(person.id, e)}
                            disabled={uploadingPhotoId === person.id}
                          />
                        </label>
                      </div>
                      <div>
                        <div className="font-medium text-slate-200">{person.name}</div>
                        <div className="text-sm text-slate-500">{person.email}</div>
                      </div>
                    </div>
                  </td>
                  <td className="p-4 text-slate-300">{person.dob}</td>
                  <td className="p-4 text-slate-300">{person.department || '-'}</td>
                  <td className="p-4 text-right">
                    <button 
                      onClick={() => handleGeneratePoster(person.id)} 
                      disabled={generatingId === person.id}
                      className="px-3 py-1.5 mr-2 bg-gradient-to-r from-violet-500 to-fuchsia-500 hover:from-violet-400 hover:to-fuchsia-400 text-white text-sm font-medium rounded-lg transition-all shadow-lg shadow-violet-500/25 disabled:opacity-50 inline-flex items-center"
                      title="Generate Birthday Poster"
                    >
                      {generatingId === person.id ? (
                        <Loader2 className="w-4 h-4 animate-spin mr-1.5" />
                      ) : (
                        <Sparkles className="w-4 h-4 mr-1.5" />
                      )}
                      {generatingId === person.id ? 'Generating...' : 'Generate'}
                    </button>
                    <button onClick={() => handleEdit(person)} className="p-2 text-slate-400 hover:text-primary-400 transition-colors">
                      <Edit2 className="w-4 h-4" />
                    </button>
                    <button onClick={() => handleDelete(person.id)} className="p-2 text-slate-400 hover:text-red-400 transition-colors ml-2">
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>

        {totalPages > 1 && (
          <div className="p-4 border-t border-slate-800 flex justify-between items-center">
            <button 
              disabled={page === 0} 
              onClick={() => setPage(p => p - 1)}
              className="px-3 py-1 bg-slate-800 rounded disabled:opacity-50 text-slate-300"
            >
              Previous
            </button>
            <span className="text-slate-400 text-sm">Page {page + 1} of {totalPages}</span>
            <button 
              disabled={page === totalPages - 1} 
              onClick={() => setPage(p => p + 1)}
              className="px-3 py-1 bg-slate-800 rounded disabled:opacity-50 text-slate-300"
            >
              Next
            </button>
          </div>
        )}
      </div>

      <AnimatePresence>
        {isModalOpen && (
          <PersonFormModal 
            isOpen={isModalOpen} 
            onClose={() => setIsModalOpen(false)} 
            person={selectedPerson}
            onSuccess={fetchPeople}
          />
        )}
      </AnimatePresence>

      <AnimatePresence>
        {generatedPoster && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 bg-black/80 backdrop-blur-sm flex items-center justify-center z-50 p-4"
            onClick={() => setGeneratedPoster(null)}
          >
            <motion.div
              initial={{ scale: 0.9, opacity: 0, y: 20 }}
              animate={{ scale: 1, opacity: 1, y: 0 }}
              exit={{ scale: 0.9, opacity: 0, y: 20 }}
              transition={{ type: 'spring', damping: 25, stiffness: 300 }}
              className="glass-panel p-6 max-w-2xl w-full"
              onClick={(e) => e.stopPropagation()}
            >
              <div className="flex justify-between items-center mb-4">
                <h2 className="text-2xl font-bold bg-gradient-to-r from-violet-400 to-fuchsia-400 bg-clip-text text-transparent flex items-center">
                  <Sparkles className="w-6 h-6 mr-2 text-violet-400" />
                  Poster Generated!
                </h2>
                <button onClick={() => setGeneratedPoster(null)} className="text-slate-400 hover:text-white transition-colors">
                  <X className="w-6 h-6" />
                </button>
              </div>
              
              <div className="flex flex-col md:flex-row gap-6">
                <div className="md:w-1/2 rounded-xl overflow-hidden shadow-2xl border border-slate-700">
                  <img src={generatedPoster.posterUrl} alt="Generated Poster" className="w-full h-auto" />
                </div>
                <div className="md:w-1/2 flex flex-col justify-center">
                  <h3 className="text-lg font-semibold text-slate-200 mb-2">AI Generated Wish:</h3>
                  <div className="bg-slate-900/50 p-4 rounded-lg border border-slate-700/50 relative">
                    <div className="absolute -left-2 -top-2 text-3xl text-primary-500/30">"</div>
                    <p className="text-slate-300 text-lg italic relative z-10">
                      {generatedPoster.wishText}
                    </p>
                  </div>
                  
                  <a 
                    href={generatedPoster.posterUrl}
                    download={`Poster-${generatedPoster.personName}.png`}
                    target="_blank"
                    rel="noreferrer"
                    className="mt-6 w-full py-3 bg-primary-600 hover:bg-primary-500 text-white rounded-lg font-medium transition-colors text-center shadow-lg shadow-primary-500/20"
                  >
                    Download Poster
                  </a>
                </div>
              </div>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}

