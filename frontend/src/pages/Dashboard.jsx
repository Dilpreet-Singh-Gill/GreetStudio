import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { Users, Gift, Calendar, Image as ImageIcon, CheckCircle } from 'lucide-react';

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

export default function Dashboard() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

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

  if (loading || !stats) {
    return (
      <div className="flex items-center justify-center h-full min-h-[500px]">
        <div className="w-10 h-10 border-4 border-primary-500 border-t-transparent rounded-full animate-spin"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
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
