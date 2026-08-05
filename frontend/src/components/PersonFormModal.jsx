import { useForm } from 'react-hook-form';
import { motion } from 'framer-motion';
import { X } from 'lucide-react';
import { personService } from '../services/personService';

export default function PersonFormModal({ isOpen, onClose, person, onSuccess }) {
  const { register, handleSubmit, formState: { errors } } = useForm({
    defaultValues: person || {}
  });

  const onSubmit = async (data) => {
    try {
      if (person) {
        await personService.updatePerson(person.id, data);
      } else {
        await personService.createPerson(data);
      }
      onSuccess();
      onClose();
    } catch (error) {
      console.error('Error saving person:', error);
      alert('Failed to save person.');
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm">
      <motion.div
        initial={{ opacity: 0, scale: 0.95 }}
        animate={{ opacity: 1, scale: 1 }}
        exit={{ opacity: 0, scale: 0.95 }}
        className="w-full max-w-lg glass-panel overflow-hidden flex flex-col max-h-[90vh]"
      >
        <div className="flex justify-between items-center p-6 border-b border-slate-800">
          <h2 className="text-xl font-bold text-slate-100">
            {person ? 'Edit Person' : 'Add Person'}
          </h2>
          <button onClick={onClose} className="text-slate-400 hover:text-slate-200">
            <X className="w-5 h-5" />
          </button>
        </div>

        <div className="p-6 overflow-y-auto">
          <form id="person-form" onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-slate-300 mb-1">Full Name</label>
              <input 
                {...register('name', { required: 'Name is required' })}
                type="text" 
                className="w-full bg-slate-800/50 border border-slate-700 rounded-lg py-2 px-4 text-slate-200 focus:outline-none focus:border-primary-500"
              />
              {errors.name && <p className="text-red-400 text-sm mt-1">{errors.name.message}</p>}
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-300 mb-1">Email</label>
              <input 
                {...register('email', { required: 'Email is required' })}
                type="email" 
                className="w-full bg-slate-800/50 border border-slate-700 rounded-lg py-2 px-4 text-slate-200 focus:outline-none focus:border-primary-500"
              />
              {errors.email && <p className="text-red-400 text-sm mt-1">{errors.email.message}</p>}
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-300 mb-1">Date of Birth</label>
              <input 
                {...register('dob', { required: 'Date of Birth is required' })}
                type="date" 
                className="w-full bg-slate-800/50 border border-slate-700 rounded-lg py-2 px-4 text-slate-200 focus:outline-none focus:border-primary-500 [color-scheme:dark]"
              />
              {errors.dob && <p className="text-red-400 text-sm mt-1">{errors.dob.message}</p>}
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1">Department</label>
                <input 
                  {...register('department')}
                  type="text" 
                  className="w-full bg-slate-800/50 border border-slate-700 rounded-lg py-2 px-4 text-slate-200 focus:outline-none focus:border-primary-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1">Designation</label>
                <input 
                  {...register('designation')}
                  type="text" 
                  className="w-full bg-slate-800/50 border border-slate-700 rounded-lg py-2 px-4 text-slate-200 focus:outline-none focus:border-primary-500"
                />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-300 mb-1">Relationship</label>
              <select 
                {...register('relationship')}
                className="w-full bg-slate-800/50 border border-slate-700 rounded-lg py-2 px-4 text-slate-200 focus:outline-none focus:border-primary-500"
              >
                <option value="">Select...</option>
                <option value="Colleague">Colleague</option>
                <option value="Manager">Manager</option>
                <option value="Employee">Employee</option>
                <option value="Friend">Friend</option>
                <option value="Family">Family</option>
              </select>
            </div>
          </form>
        </div>

        <div className="p-6 border-t border-slate-800 flex justify-end space-x-3 bg-slate-900/50">
          <button onClick={onClose} className="px-4 py-2 text-slate-300 hover:text-white transition-colors">
            Cancel
          </button>
          <button form="person-form" type="submit" className="bg-primary-600 hover:bg-primary-500 text-white px-6 py-2 rounded-lg transition-colors">
            {person ? 'Save Changes' : 'Create Person'}
          </button>
        </div>
      </motion.div>
    </div>
  );
}
