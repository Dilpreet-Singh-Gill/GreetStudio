import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Users, Gift, Calendar, Image as ImageIcon, CheckCircle, X, Sparkles, Cake, PartyPopper } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

const StatCard = ({ title, value, icon: Icon, color, delay }) => (
  <motion.div
    initial={{ opacity: 0, y: 20 }}
    animate={{ opacity: 1, y: 0 }}
    transition={{ duration: 0.5, delay }}
    className="glass-panel p-6 relative overflow-hidden group"
  >
    <div className={`absolute -right-6 -top-6 w-24 h-24 rounded-full ${color} opacity-10 group-hover:scale-150 transition-transform duration-500`} />
    <div className="flex items-center justify-between mb-4 relative">
      <h3 className="text-slate-400 font-medium">{title}</h3>
      <div className={`p-2 rounded-lg ${color} bg-opacity-20`}>
        <Icon className={`w-5 h-5 ${color.replace('bg-', 'text-')}`} />
      </div>
    </div>
    <p className="text-3xl font-bold text-slate-100">{value}</p>
  </motion.div>
);

/* ── Confetti particle ────────────────────────────────── */
const ConfettiPiece = ({ delay, color, left }) => (
  <motion.div
    initial={{ y: -10, opacity: 1, rotate: 0, scale: 1 }}
    animate={{
      y: [0, -30, 200],
      opacity: [1, 1, 0],
      rotate: [0, 180, 360 + Math.random() * 360],
      scale: [1, 1.2, 0.6],
      x: [0, (Math.random() - 0.5) * 80],
    }}
    transition={{
      duration: 2.5 + Math.random() * 1.5,
      delay: delay,
      repeat: Infinity,
      repeatDelay: Math.random() * 3,
    }}
    style={{
      position: 'absolute',
      left: `${left}%`,
      top: '-5px',
      width: `${6 + Math.random() * 6}px`,
      height: `${6 + Math.random() * 6}px`,
      borderRadius: Math.random() > 0.5 ? '50%' : '2px',
      backgroundColor: color,
      zIndex: 0,
    }}
  />
);

const confettiColors = ['#f472b6', '#a78bfa', '#34d399', '#fbbf24', '#60a5fa', '#f87171', '#e879f9', '#2dd4bf'];

/* ── Birthday Notification Banner ─────────────────────── */
const BirthdayNotificationBanner = ({ notifications, onDismiss, onGeneratePoster }) => {
  if (!notifications || notifications.length === 0) return null;

  return (
    <AnimatePresence>
      <motion.div
        initial={{ opacity: 0, y: -30, scale: 0.95 }}
        animate={{ opacity: 1, y: 0, scale: 1 }}
        exit={{ opacity: 0, y: -30, scale: 0.95 }}
        transition={{ type: 'spring', stiffness: 200, damping: 20 }}
        style={{
          position: 'relative',
          overflow: 'hidden',
          borderRadius: '16px',
          border: '1px solid rgba(139, 92, 246, 0.4)',
          background: 'linear-gradient(135deg, rgba(99, 102, 241, 0.15) 0%, rgba(139, 92, 246, 0.12) 50%, rgba(236, 72, 153, 0.1) 100%)',
          backdropFilter: 'blur(12px)',
          padding: '24px',
          marginBottom: '24px',
          boxShadow: '0 0 40px rgba(99, 102, 241, 0.1), inset 0 1px 0 rgba(255, 255, 255, 0.05)',
        }}
      >
        {/* Confetti particles */}
        {Array.from({ length: 25 }).map((_, i) => (
          <ConfettiPiece
            key={i}
            delay={Math.random() * 2}
            color={confettiColors[i % confettiColors.length]}
            left={Math.random() * 100}
          />
        ))}

        {/* Glow effect */}
        <div style={{
          position: 'absolute',
          top: '-50%',
          left: '50%',
          transform: 'translateX(-50%)',
          width: '80%',
          height: '100%',
          background: 'radial-gradient(ellipse, rgba(139, 92, 246, 0.15) 0%, transparent 70%)',
          pointerEvents: 'none',
        }} />

        {/* Dismiss button */}
        <button
          onClick={onDismiss}
          style={{
            position: 'absolute',
            top: '12px',
            right: '12px',
            background: 'rgba(255, 255, 255, 0.05)',
            border: '1px solid rgba(255, 255, 255, 0.1)',
            borderRadius: '8px',
            padding: '6px',
            cursor: 'pointer',
            zIndex: 10,
            color: '#94a3b8',
            transition: 'all 0.2s',
          }}
          onMouseEnter={e => { e.target.style.background = 'rgba(255,255,255,0.1)'; e.target.style.color = '#e2e8f0'; }}
          onMouseLeave={e => { e.target.style.background = 'rgba(255,255,255,0.05)'; e.target.style.color = '#94a3b8'; }}
        >
          <X className="w-4 h-4" />
        </button>

        {/* Header */}
        <div style={{ position: 'relative', zIndex: 1, display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '16px' }}>
          <motion.div
            animate={{ rotate: [0, -10, 10, -10, 0], scale: [1, 1.1, 1] }}
            transition={{ duration: 1.5, repeat: Infinity, repeatDelay: 3 }}
            style={{
              background: 'linear-gradient(135deg, #6366f1, #ec4899)',
              borderRadius: '12px',
              padding: '10px',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
            }}
          >
            <Cake className="w-6 h-6 text-white" />
          </motion.div>
          <div>
            <h3 style={{ color: '#f1f5f9', fontSize: '18px', fontWeight: 700, margin: 0, display: 'flex', alignItems: 'center', gap: '8px' }}>
              🎉 Birthday Alert!
              <motion.span
                animate={{ opacity: [0.5, 1, 0.5] }}
                transition={{ duration: 2, repeat: Infinity }}
                style={{ fontSize: '13px', fontWeight: 500, color: '#a78bfa', background: 'rgba(167, 139, 250, 0.15)', padding: '2px 10px', borderRadius: '20px' }}
              >
                Today
              </motion.span>
            </h3>
            <p style={{ color: '#94a3b8', fontSize: '14px', margin: '2px 0 0' }}>
              {notifications.length === 1
                ? `${notifications[0].name} has a birthday today!`
                : `${notifications.length} people have birthdays today!`}
            </p>
          </div>
        </div>

        {/* Birthday person cards */}
        <div style={{ position: 'relative', zIndex: 1, display: 'flex', flexWrap: 'wrap', gap: '12px' }}>
          {notifications.map((person, index) => (
            <motion.div
              key={person.id}
              initial={{ opacity: 0, scale: 0.8 }}
              animate={{ opacity: 1, scale: 1 }}
              transition={{ delay: 0.2 + index * 0.1, type: 'spring' }}
              style={{
                flex: '1 1 calc(50% - 6px)',
                minWidth: '280px',
                background: 'rgba(15, 23, 42, 0.6)',
                border: '1px solid rgba(99, 102, 241, 0.2)',
                borderRadius: '12px',
                padding: '16px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                gap: '12px',
              }}
            >
              <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                {person.photoUrl ? (
                  <img
                    src={person.photoUrl}
                    alt={person.name}
                    style={{
                      width: '44px',
                      height: '44px',
                      borderRadius: '50%',
                      objectFit: 'cover',
                      border: '2px solid rgba(139, 92, 246, 0.5)',
                    }}
                  />
                ) : (
                  <div style={{
                    width: '44px',
                    height: '44px',
                    borderRadius: '50%',
                    background: 'linear-gradient(135deg, #6366f1, #8b5cf6)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    color: 'white',
                    fontWeight: 700,
                    fontSize: '16px',
                  }}>
                    {person.name?.charAt(0)?.toUpperCase() || '?'}
                  </div>
                )}
                <div>
                  <p style={{ color: '#e2e8f0', fontWeight: 600, fontSize: '15px', margin: 0 }}>
                    {person.name}
                  </p>
                  <p style={{ color: '#64748b', fontSize: '13px', margin: '2px 0 0' }}>
                    {person.department}{person.relationship ? ` • ${person.relationship}` : ''}
                  </p>
                </div>
              </div>
              <button
                onClick={() => onGeneratePoster(person.id)}
                style={{
                  background: 'linear-gradient(135deg, #6366f1, #8b5cf6)',
                  color: 'white',
                  border: 'none',
                  borderRadius: '8px',
                  padding: '8px 16px',
                  fontSize: '13px',
                  fontWeight: 600,
                  cursor: 'pointer',
                  whiteSpace: 'nowrap',
                  transition: 'all 0.2s',
                  boxShadow: '0 2px 8px rgba(99, 102, 241, 0.3)',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '6px',
                }}
                onMouseEnter={e => { e.target.style.transform = 'translateY(-1px)'; e.target.style.boxShadow = '0 4px 12px rgba(99, 102, 241, 0.5)'; }}
                onMouseLeave={e => { e.target.style.transform = 'translateY(0)'; e.target.style.boxShadow = '0 2px 8px rgba(99, 102, 241, 0.3)'; }}
              >
                <Sparkles className="w-4 h-4" />
                Generate Poster
              </button>
            </motion.div>
          ))}
        </div>
      </motion.div>
    </AnimatePresence>
  );
};

export default function Dashboard() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [showBirthdayBanner, setShowBirthdayBanner] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    import('../services/dashboardService').then(({ dashboardService }) => {
      dashboardService.getDashboardStats()
        .then(data => {
          setStats(data);
          setLoading(false);
        })
        .catch(err => {
          console.error("Failed to load dashboard stats", err);
          setLoading(false);
        });
    });
  }, []);

  const handleGeneratePoster = (personId) => {
    import('../services/posterService').then(({ posterService }) => {
      posterService.generatePoster(personId)
        .then(() => {
          navigate('/dashboard/history');
        })
        .catch(err => {
          console.error("Failed to generate poster", err);
          alert("Failed to generate poster. Please try again.");
        });
    });
  };

  if (loading || !stats) {
    return (
      <div className="flex items-center justify-center h-full min-h-[500px]">
        <div className="w-10 h-10 border-4 border-primary-500 border-t-transparent rounded-full animate-spin"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Birthday Notification Banner */}
      {showBirthdayBanner && (
        <BirthdayNotificationBanner
          notifications={stats.birthdayNotifications}
          onDismiss={() => setShowBirthdayBanner(false)}
          onGeneratePoster={handleGeneratePoster}
        />
      )}

      <header className="mb-8">
        <h1 className="text-3xl font-bold text-slate-100">Overview</h1>
        <p className="text-slate-400 mt-1">Here's what's happening today</p>
      </header>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatCard 
          title="Total People" 
          value={stats.totalPeople} 
          icon={Users} 
          color="bg-primary-500" 
          delay={0.1}
        />
        <StatCard 
          title="Birthdays Today" 
          value={stats.birthdaysToday} 
          icon={Calendar} 
          color="bg-emerald-500" 
          delay={0.2}
        />
        <StatCard 
          title="Active Templates" 
          value={stats.activeTemplates} 
          icon={ImageIcon} 
          color="bg-indigo-500" 
          delay={0.3}
        />
        <StatCard 
          title="Posters Generated" 
          value={stats.postersGenerated} 
          icon={CheckCircle} 
          color="bg-violet-500" 
          delay={0.4}
        />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 mt-8">
        <motion.div 
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.5 }}
          className="lg:col-span-2 glass-panel p-6"
        >
          <h2 className="text-xl font-bold text-slate-100 mb-4">Upcoming Birthdays</h2>
          <div className="space-y-4">
            {stats.upcomingBirthdays?.length === 0 ? (
              <p className="text-slate-400">No upcoming birthdays.</p>
            ) : (
              stats.upcomingBirthdays.map((b) => (
                <div key={b.id} className="flex items-center justify-between p-4 rounded-lg bg-slate-800/30 border border-slate-700/50">
                  <div className="flex items-center space-x-4">
                    <div className="w-10 h-10 rounded-full bg-slate-700 flex items-center justify-center font-bold text-slate-300">
                      {b.initials}
                    </div>
                    <div>
                      <p className="font-medium text-slate-200">{b.name}</p>
                      <p className="text-sm text-slate-400">{b.department}</p>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className="text-emerald-400 font-medium">{b.daysUntilText}</p>
                    <p className="text-sm text-slate-500">{b.dob}</p>
                  </div>
                </div>
              ))
            )}
          </div>
        </motion.div>

        <motion.div 
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.6 }}
          className="glass-panel p-6"
        >
          <h2 className="text-xl font-bold text-slate-100 mb-4">Recent Posters</h2>
          <div className="space-y-4">
            {stats.recentPosters?.length === 0 ? (
              <p className="text-slate-400">No recent posters.</p>
            ) : (
              stats.recentPosters.map((p) => (
                <div key={p.id} className="flex items-center space-x-3">
                  <div className="w-12 h-16 bg-slate-800 rounded flex-shrink-0 border border-slate-700 overflow-hidden">
                    {p.posterUrl ? <img src={p.posterUrl} className="w-full h-full object-cover" alt="" /> : null}
                  </div>
                  <div>
                    <p className="text-sm font-medium text-slate-300 truncate w-32">{p.personName}</p>
                    <p className="text-xs text-slate-500">{p.timeAgoText}</p>
                  </div>
                </div>
              ))
            )}
          </div>
        </motion.div>
      </div>
    </div>
  );
}

