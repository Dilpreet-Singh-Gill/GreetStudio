import { motion } from 'framer-motion';
import { Users, Calendar, Image as ImageIcon, CheckCircle } from 'lucide-react';

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
  return (
    <div className="space-y-6">
      <header className="mb-8">
        <h1 className="text-3xl font-bold text-slate-100">Overview</h1>
        <p className="text-slate-400 mt-1">Here's what's happening today</p>
      </header>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatCard 
          title="Total People" 
          value="1,248" 
          icon={Users} 
          color="bg-primary-500" 
          delay={0.1}
        />
        <StatCard 
          title="Birthdays Today" 
          value="12" 
          icon={Calendar} 
          color="bg-emerald-500" 
          delay={0.2}
        />
        <StatCard 
          title="Active Templates" 
          value="8" 
          icon={ImageIcon} 
          color="bg-indigo-500" 
          delay={0.3}
        />
        <StatCard 
          title="Posters Generated" 
          value="4,821" 
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
            {[1, 2, 3].map((i) => (
              <div key={i} className="flex items-center justify-between p-4 rounded-lg bg-slate-800/30 border border-slate-700/50">
                <div className="flex items-center space-x-4">
                  <div className="w-10 h-10 rounded-full bg-slate-700 flex items-center justify-center font-bold text-slate-300">
                    JD
                  </div>
                  <div>
                    <p className="font-medium text-slate-200">John Doe {i}</p>
                    <p className="text-sm text-slate-400">Engineering Dept</p>
                  </div>
                </div>
                <div className="text-right">
                  <p className="text-emerald-400 font-medium">Tomorrow</p>
                  <p className="text-sm text-slate-500">Aug {15 + i}</p>
                </div>
              </div>
            ))}
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
            {[1, 2, 3].map((i) => (
              <div key={i} className="flex items-center space-x-3">
                <div className="w-12 h-16 bg-slate-800 rounded flex-shrink-0 border border-slate-700"></div>
                <div>
                  <p className="text-sm font-medium text-slate-300">Poster #{1040 + i}</p>
                  <p className="text-xs text-slate-500">Generated 2h ago</p>
                </div>
              </div>
            ))}
          </div>
        </motion.div>
      </div>
    </div>
  );
}
