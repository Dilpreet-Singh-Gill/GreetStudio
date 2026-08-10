import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Image as ImageIcon, Loader2, CalendarDays, ExternalLink } from 'lucide-react';
import { posterService } from '../services/posterService';

export default function PosterHistory() {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [previewUrl, setPreviewUrl] = useState(null);

  useEffect(() => {
    fetchHistory();
  }, [page]);

  const fetchHistory = async () => {
    try {
      setLoading(true);
      const res = await posterService.getPosterHistory(page, 12);
      setHistory(res.content);
      setTotalPages(res.totalPages);
    } catch (error) {
      console.error('Error fetching poster history:', error);
    } finally {
      setLoading(false);
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
          <h1 className="text-3xl font-bold text-slate-100">Poster History</h1>
          <p className="text-slate-400 mt-1">
            View all generated birthday posters and wishes
          </p>
        </div>
      </div>

      {/* Gallery Grid */}
      {loading ? (
        <div className="flex items-center justify-center py-20">
          <Loader2 className="w-8 h-8 text-primary-400 animate-spin" />
        </div>
      ) : history.length === 0 ? (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="glass-panel p-16 text-center"
        >
          <div className="w-20 h-20 mx-auto mb-4 rounded-2xl bg-gradient-to-br from-primary-500/20 to-indigo-500/20 flex items-center justify-center">
            <ImageIcon className="w-10 h-10 text-primary-400" />
          </div>
          <h3 className="text-xl font-semibold text-slate-200 mb-2">
            No posters generated yet
          </h3>
          <p className="text-slate-400 max-w-sm mx-auto">
            Head over to the People section and click the magic wand icon to generate your first birthday poster!
          </p>
        </motion.div>
      ) : (
        <motion.div
          variants={containerVariants}
          initial="hidden"
          animate="visible"
          className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6"
        >
          {history.map((item) => (
            <motion.div
              key={item.id}
              variants={cardVariants}
              layout
              className="group glass-panel p-0 overflow-hidden flex flex-col"
            >
              {/* Image */}
              <div 
                className="relative aspect-[3/4] overflow-hidden bg-slate-900 cursor-pointer"
                onClick={() => setPreviewUrl(item.posterUrl)}
              >
                {item.posterUrl ? (
                  <img
                    src={item.posterUrl}
                    alt={`Poster for ${item.personName}`}
                    className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105"
                    loading="lazy"
                  />
                ) : (
                  <div className="flex items-center justify-center h-full text-slate-500">
                    Image failed to generate
                  </div>
                )}
                
                {/* Status Badge */}
                <div className="absolute top-3 right-3">
                  <span className={`px-2 py-1 text-xs font-bold rounded-md backdrop-blur-md ${
                    item.status === 'SUCCESS' ? 'bg-emerald-500/20 text-emerald-300' : 'bg-red-500/20 text-red-300'
                  }`}>
                    {item.status}
                  </span>
                </div>
                
                {/* Hover overlay with expand icon */}
                <div className="absolute inset-0 bg-black/40 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center pointer-events-none">
                  <div className="w-10 h-10 rounded-full bg-white/20 backdrop-blur-md flex items-center justify-center text-white">
                    <ExternalLink className="w-5 h-5" />
                  </div>
                </div>
              </div>

              {/* Info */}
              <div className="p-4 flex-1 flex flex-col">
                <div className="flex justify-between items-start mb-2">
                  <h3 className="font-semibold text-slate-200 truncate pr-2">
                    {item.personName}
                  </h3>
                </div>
                <div className="flex items-center text-xs text-slate-500 mb-3">
                  <CalendarDays className="w-3 h-3 mr-1" />
                  {new Date(item.createdAt).toLocaleDateString(undefined, { 
                    year: 'numeric', month: 'short', day: 'numeric' 
                  })}
                </div>
                
                {/* AI Wish Preview */}
                <div className="mt-auto bg-slate-900/50 p-3 rounded-lg border border-slate-700/50">
                  <p className="text-xs text-slate-400 line-clamp-3 italic leading-relaxed">
                    "{item.wishText}"
                  </p>
                </div>
              </div>
            </motion.div>
          ))}
        </motion.div>
      )}

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="glass-panel p-4 flex justify-between items-center mt-6">
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

      {/* Full-screen Preview */}
      <AnimatePresence>
        {previewUrl && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 bg-black/80 backdrop-blur-md flex items-center justify-center z-50 cursor-pointer p-4 md:p-12"
            onClick={() => setPreviewUrl(null)}
          >
            <motion.img
              initial={{ scale: 0.8, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0.8, opacity: 0 }}
              transition={{ type: 'spring', stiffness: 300, damping: 25 }}
              src={previewUrl}
              alt="Generated poster preview"
              className="max-w-full max-h-full object-contain rounded-xl shadow-[0_0_50px_rgba(0,0,0,0.5)]"
            />
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}
