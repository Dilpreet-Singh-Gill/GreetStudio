import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import AuthLayout from './layouts/AuthLayout';
import DashboardLayout from './layouts/DashboardLayout';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import PeopleList from './pages/PeopleList';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/auth" element={<AuthLayout />}>
          <Route path="login" element={<Login />} />
          <Route path="register" element={<Register />} />
        </Route>
        
        <Route path="/dashboard" element={<DashboardLayout />}>
          <Route index element={<Dashboard />} />
          <Route path="people" element={<PeopleList />} />
          {/* Templates, Settings will go here later */}
        </Route>

        <Route path="*" element={<Navigate to="/auth/login" />} />
      </Routes>
    </Router>
  );
}

export default App;
