import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Plus, Trash2, Eye, Loader2, Image as ImageIcon } from 'lucide-react';
import { templateService } from '../services/templateService';
import TemplateUploadModal from '../components/TemplateUploadModal';

export default function TemplateGallery() {
  const [templates, setTemplates] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [previewUrl, setPreviewUrl] = useState(null);
  const [deletingId, setDeletingId] = useState(null);

  useEffect(() => {
    fetchTemplates();
  }, [page]);

  const fetchTemplates = async () => {
    try {
      setLoading(true);
      const res = await templateService.getAllTemplates(page, 12);
      setTemplates(res.content);
      setTotalPages(res.totalPages);
    } catch (error) {
      console.error('Error fetching templates:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this template? This action cannot be undone.')) return;

    try {
      setDeletingId(id);
      await templateService.deleteTemplate(id);
      fetchTemplates();
    } catch (error) {
      console.error('Error deleting template:', error);
      alert(error.response?.data?.message || 'Failed to delete template.');
    } finally {
      setDeletingId(null);
    }
  };

  const containerVariants = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: { staggerChildren: 0.08 },
    },
  };

  const cardVariants = {
    hidden: { opacity: 0, y: 30, scale: 0.95 },
    visible: { opacity: 1, y: 0, scale: 1, transition: { duration: 0.4, ease: 'easeOut' } },
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold text-slate-100">Template Gallery</h1>
          <p className="text-slate-400 mt-1">
            Upload and manage birthday poster backgrounds
          </p>
        </div>
        <button
          onClick={() => setIsModalOpen(true)}
          className="bg-gradient-to-r from-primary-600 to-indigo-600 hover:from-primary-500 hover:to-indigo-500 text-white px-5 py-2.5 rounded-lg flex items-center transition-all duration-300 shadow-lg shadow-primary-500/25 font-medium"
        >
          <Plus className="w-5 h-5 mr-2" />
          Upload Template
        </button>
      </div>

      {/* Gallery Grid */}
      {loading ? (
        <div className="flex items-center justify-center py-20">
          <Loader2 className="w-8 h-8 text-primary-400 animate-spin" />
        </div>
      ) : templates.length === 0 ? (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="glass-panel p-16 text-center"
        >
          <div className="w-20 h-20 mx-auto mb-4 rounded-2xl bg-gradient-to-br from-primary-500/20 to-indigo-500/20 flex items-center justify-center">
            <ImageIcon className="w-10 h-10 text-primary-400" />
          </div>
          <h3 className="text-xl font-semibold text-slate-200 mb-2">
            No templates yet
          </h3>
          <p className="text-slate-400 mb-6 max-w-sm mx-auto">
            Upload your first birthday poster background template to get started.
          </p>
          <button
            onClick={() => setIsModalOpen(true)}
            className="bg-primary-600 hover:bg-primary-500 text-white px-5 py-2.5 rounded-lg inline-flex items-center transition-colors"
          >
            <Plus className="w-4 h-4 mr-2" />
            Upload Template
          </button>
        </motion.div>
      ) : (
        <motion.div
          variants={containerVariants}
          initial="hidden"
          animate="visible"
          className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-5"
        >
          {templates.map((template) => (
            <motion.div
              key={template.id}
              variants={cardVariants}
              layout
              className="group glass-panel p-0 overflow-hidden"
            >
              {/* Image */}
              <div className="relative aspect-[4/3] overflow-hidden bg-slate-900">
                <img
                  src={template.templateUrl}
                  alt={template.name}
                  className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110"
                  loading="lazy"
                />

                {/* Hover Overlay */}
                <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-black/30 to-transparent opacity-0 group-hover:opacity-100 transition-all duration-300 flex items-end justify-between p-4">
                  <button
                    onClick={() => setPreviewUrl(template.templateUrl)}
                    className="p-2.5 rounded-lg bg-white/10 backdrop-blur-sm text-white hover:bg-white/20 transition-colors"
                    title="Preview"
                  >
                    <Eye className="w-4 h-4" />
                  </button>
                  <button
                    onClick={() => handleDelete(template.id)}
                    disabled={deletingId === template.id}
                    className="p-2.5 rounded-lg bg-red-500/20 backdrop-blur-sm text-red-300 hover:bg-red-500/40 transition-colors disabled:opacity-50"
                    title="Delete"
                  >
                    {deletingId === template.id ? (
                      <Loader2 className="w-4 h-4 animate-spin" />
                    ) : (
                      <Trash2 className="w-4 h-4" />
                    )}
                  </button>
                </div>
              </div>

              {/* Info */}
              <div className="p-4">
                <h3 className="font-semibold text-slate-200 truncate">
                  {template.name}
                </h3>
                <div className="flex items-center mt-2 space-x-2">
                  <div
                    className="w-4 h-4 rounded-full border border-slate-600"
                    style={{ backgroundColor: template.textColor || '#FFFFFF' }}
                  />
                  <span className="text-xs text-slate-500 font-mono">
                    {template.textColor || '#FFFFFF'}
                  </span>
                </div>
              </div>
            </motion.div>
          ))}
        </motion.div>
      )}

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="glass-panel p-4 flex justify-between items-center">
          <button
            disabled={page === 0}
            onClick={() => setPage((p) => p - 1)}
            className="px-4 py-2 bg-slate-800 rounded-lg disabled:opacity-50 text-slate-300 hover:bg-slate-700 transition-colors"
          >
            Previous
          </button>
          <span className="text-slate-400 text-sm">
            Page {page + 1} of {totalPages}
          </span>
          <button
            disabled={page === totalPages - 1}
            onClick={() => setPage((p) => p + 1)}
            className="px-4 py-2 bg-slate-800 rounded-lg disabled:opacity-50 text-slate-300 hover:bg-slate-700 transition-colors"
          >
            Next
          </button>
        </div>
      )}

      {/* Upload Modal */}
      <AnimatePresence>
        {isModalOpen && (
          <TemplateUploadModal
            isOpen={isModalOpen}
            onClose={() => setIsModalOpen(false)}
            onSuccess={fetchTemplates}
          />
        )}
      </AnimatePresence>

      {/* Full-screen Preview */}
      <AnimatePresence>
        {previewUrl && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 bg-black/80 backdrop-blur-md flex items-center justify-center z-50 cursor-pointer p-8"
            onClick={() => setPreviewUrl(null)}
          >
            <motion.img
              initial={{ scale: 0.8, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0.8, opacity: 0 }}
              transition={{ type: 'spring', stiffness: 300, damping: 25 }}
              src={previewUrl}
              alt="Template preview"
              className="max-w-full max-h-full object-contain rounded-xl shadow-2xl"
            />
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}
