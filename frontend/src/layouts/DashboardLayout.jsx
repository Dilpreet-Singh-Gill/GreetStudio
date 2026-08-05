import { Outlet } from 'react-router-dom';
import Sidebar from '../components/Sidebar';

export default function DashboardLayout() {
  return (
    <div className="min-h-screen flex bg-slate-950">
      <Sidebar />
      <div className="flex-1 flex flex-col p-8 overflow-y-auto ml-64">
        <Outlet />
      </div>
    </div>
  );
}
