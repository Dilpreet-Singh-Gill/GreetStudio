import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { Home, Users, Image as ImageIcon, Settings, LogOut, Clock, LayoutDashboard } from 'lucide-react';
import { motion } from 'framer-motion';

const navItems = [
  { path: '/dashboard', label: 'Dashboard', icon: Home },
  { path: '/dashboard/people', label: 'People', icon: Users },
  { path: '/dashboard/templates', label: 'Templates', icon: ImageIcon },
  { path: '/dashboard/history', label: 'History', icon: Clock },
  { path: '/dashboard/settings', label: 'Settings', icon: Settings },
];

export default function Sidebar() {
  const location = useLocation();

  return (
    <div className="w-64 glass-panel border-y-0 border-l-0 rounded-none fixed left-0 top-0 h-full flex flex-col">
      <div className="p-6">
        <h1 className="text-2xl font-bold bg-gradient-to-r from-primary-500 to-indigo-400 bg-clip-text text-transparent">
          GreetStudio
        </h1>
      </div>
      
      <nav className="flex-1 px-4 mt-6 space-y-2">
        {navItems.map((item) => {
          const isActive = location.pathname === item.path;
          return (
            <Link key={item.path} to={item.path} className="relative block">
              {isActive && (
                <motion.div 
                  layoutId="sidebar-active" 
                  className="absolute inset-0 bg-primary-600/20 rounded-lg border border-primary-500/30" 
                  transition={{ type: "spring", stiffness: 300, damping: 30 }}
                />
              )}
              <div className={`relative flex items-center px-4 py-3 rounded-lg transition-colors ${isActive ? 'text-primary-400 font-medium' : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/50'}`}>
                <item.icon className="w-5 h-5 mr-3" />
                {item.label}
              </div>
            </Link>
          );
        })}
      </nav>

      <div className="p-4 mt-auto">
        <button className="flex items-center w-full px-4 py-3 text-red-400 transition-colors rounded-lg hover:bg-red-500/10">
          <LogOut className="w-5 h-5 mr-3" />
          Logout
        </button>
      </div>
    </div>
  );
}
