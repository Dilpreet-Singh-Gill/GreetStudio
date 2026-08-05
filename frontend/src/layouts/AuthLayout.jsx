import { Outlet } from 'react-router-dom';

export default function AuthLayout() {
  return (
    <div className="min-h-screen flex items-center justify-center p-4">
      <div className="absolute inset-0 bg-slate-950 overflow-hidden -z-10">
        <div className="absolute -top-40 -right-40 w-96 h-96 bg-primary-600 rounded-full blur-[100px] opacity-20"></div>
        <div className="absolute -bottom-40 -left-40 w-96 h-96 bg-indigo-600 rounded-full blur-[100px] opacity-20"></div>
      </div>
      <Outlet />
    </div>
  );
}
