import { Outlet, Navigate } from 'react-router-dom';
import Sidebar from '../components/Sidebar';
import { authService } from '../services/authService';

export default function DashboardLayout() {
  if (!authService.isAuthenticated()) {
    return <Navigate to="/auth/login" replace />;
  }

  return (
    <div className="min-h-screen flex bg-slate-950">
      <Sidebar />
      <div className="flex-1 flex flex-col p-8 overflow-y-auto ml-64">
        <Outlet />
      </div>
    </div>
  );
}
