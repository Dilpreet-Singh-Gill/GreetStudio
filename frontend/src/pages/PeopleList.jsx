import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Search, Plus, Edit2, Trash2, Upload } from 'lucide-react';
import { personService } from '../services/personService';
import PersonFormModal from '../components/PersonFormModal';

export default function PeopleList() {
  const [people, setPeople] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedPerson, setSelectedPerson] = useState(null);

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

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold text-slate-100">People Management</h1>
          <p className="text-slate-400 mt-1">Manage birthdays and details</p>
        </div>
        <div className="flex space-x-3">
          <button className="bg-slate-800 hover:bg-slate-700 text-slate-200 px-4 py-2 rounded-lg flex items-center transition-colors">
            <Upload className="w-4 h-4 mr-2" />
            Import Excel
          </button>
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
                      <div className="w-10 h-10 rounded-full bg-indigo-500/20 text-indigo-400 flex items-center justify-center font-bold">
                        {person.name.charAt(0)}
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
    </div>
  );
}
