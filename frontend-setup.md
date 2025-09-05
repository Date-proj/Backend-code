# Frontend-Backend Connection Setup

## 1. Install Dependencies

```bash
npm install axios react-router-dom
```

## 2. Axios Configuration

Create `src/utils/api.js`:

```javascript
import axios from 'axios';

// Set base URL for your Spring Boot backend
const API_BASE_URL = 'http://ec2-13-48-25-176.eu-north-1.compute.amazonaws.com:8080';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add request interceptor to include JWT token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Add response interceptor to handle errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expired or invalid
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
```

## 3. Login Component

Create `src/components/Login.jsx`:

```javascript
import React, { useState } from 'react';
import api from '../utils/api';
import { useNavigate } from 'react-router-dom';

const Login = () => {
  const [credentials, setCredentials] = useState({
    userName: '',
    password: ''
  });
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const response = await api.post('/api/auth/login', credentials);
      const { token, forcePasswordChange } = response.data;
      
      localStorage.setItem('token', token);
      
      if (forcePasswordChange) {
        navigate('/change-password');
      } else {
        // Get user profile to determine role and redirect
        const profileResponse = await api.get('/api/user/profile');
        const { role } = profileResponse.data;
        
        switch (role) {
          case 'SUPER_ADMIN':
            navigate('/super-admin/dashboard');
            break;
          case 'ADMIN':
            navigate('/admin/dashboard');
            break;
          case 'EMPLOYEE':
            navigate('/employee/dashboard');
            break;
          case 'FARMER':
            navigate('/farmer/dashboard');
            break;
          default:
            navigate('/dashboard');
        }
      }
    } catch (error) {
      console.error('Login failed:', error);
      alert('Login failed. Please check your credentials.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <form onSubmit={handleLogin}>
        <h2>Login</h2>
        <input
          type="email"
          placeholder="Email"
          value={credentials.userName}
          onChange={(e) => setCredentials({...credentials, userName: e.target.value})}
          required
        />
        <input
          type="password"
          placeholder="Password"
          value={credentials.password}
          onChange={(e) => setCredentials({...credentials, password: e.target.value})}
          required
        />
        <button type="submit" disabled={loading}>
          {loading ? 'Logging in...' : 'Login'}
        </button>
      </form>
    </div>
  );
};

export default Login;
```

## 4. App.js with Routing

Update your `src/App.js`:

```javascript
import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './components/Login';
import SuperAdminDashboard from './components/SuperAdminDashboard';
import AdminDashboard from './components/AdminDashboard';
import EmployeeDashboard from './components/EmployeeDashboard';
import FarmerDashboard from './components/FarmerDashboard';
import ChangePassword from './components/ChangePassword';
import Registration from './components/Registration';

function App() {
  return (
    <Router>
      <div className="App">
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Registration />} />
          <Route path="/change-password" element={<ChangePassword />} />
          <Route path="/super-admin/dashboard" element={<SuperAdminDashboard />} />
          <Route path="/admin/dashboard" element={<AdminDashboard />} />
          <Route path="/employee/dashboard" element={<EmployeeDashboard />} />
          <Route path="/farmer/dashboard" element={<FarmerDashboard />} />
          <Route path="/" element={<Navigate to="/login" replace />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
```

## 5. Registration Component

Create `src/components/Registration.jsx`:

```javascript
import React, { useState } from 'react';
import api from '../utils/api';
import { useNavigate } from 'react-router-dom';

const Registration = () => {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    phoneNumber: '',
    password: '',
    dateOfBirth: '',
    gender: '',
    country: '',
    state: '',
    pinCode: '',
    timeZone: ''
  });
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      await api.post('/api/auth/register', formData);
      alert('Registration successful - waiting for approval');
      navigate('/login');
    } catch (error) {
      console.error('Registration failed:', error);
      alert('Registration failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="registration-container">
      <form onSubmit={handleSubmit}>
        <h2>Register</h2>
        <input
          type="text"
          placeholder="First Name"
          value={formData.firstName}
          onChange={(e) => setFormData({...formData, firstName: e.target.value})}
          required
        />
        <input
          type="text"
          placeholder="Last Name"
          value={formData.lastName}
          onChange={(e) => setFormData({...formData, lastName: e.target.value})}
          required
        />
        <input
          type="email"
          placeholder="Email"
          value={formData.email}
          onChange={(e) => setFormData({...formData, email: e.target.value})}
          required
        />
        <input
          type="tel"
          placeholder="Phone Number"
          value={formData.phoneNumber}
          onChange={(e) => setFormData({...formData, phoneNumber: e.target.value})}
          required
        />
        <input
          type="password"
          placeholder="Password"
          value={formData.password}
          onChange={(e) => setFormData({...formData, password: e.target.value})}
          required
        />
        <input
          type="date"
          value={formData.dateOfBirth}
          onChange={(e) => setFormData({...formData, dateOfBirth: e.target.value})}
          required
        />
        <select
          value={formData.gender}
          onChange={(e) => setFormData({...formData, gender: e.target.value})}
          required
        >
          <option value="">Select Gender</option>
          <option value="Male">Male</option>
          <option value="Female">Female</option>
          <option value="Other">Other</option>
        </select>
        <input
          type="text"
          placeholder="Country"
          value={formData.country}
          onChange={(e) => setFormData({...formData, country: e.target.value})}
          required
        />
        <input
          type="text"
          placeholder="State"
          value={formData.state}
          onChange={(e) => setFormData({...formData, state: e.target.value})}
          required
        />
        <input
          type="text"
          placeholder="PIN Code"
          value={formData.pinCode}
          onChange={(e) => setFormData({...formData, pinCode: e.target.value})}
          required
        />
        <input
          type="text"
          placeholder="Time Zone"
          value={formData.timeZone}
          onChange={(e) => setFormData({...formData, timeZone: e.target.value})}
          required
        />
        <button type="submit" disabled={loading}>
          {loading ? 'Registering...' : 'Register'}
        </button>
      </form>
    </div>
  );
};

export default Registration;
```

## 6. CORS Configuration

Make sure your Spring Boot backend has CORS configured (already done in SecurityConfig).

## 7. Testing the Flow

1. **Start your Spring Boot backend** on port 8080
2. **Start your React frontend** on port 3000
3. **Register a new user** → Should show "Registration successful - waiting for approval"
4. **Login as super admin** → Should redirect to super admin dashboard
5. **Approve users** → Should send email and update status

## 8. Super Admin Credentials

Use these hardcoded credentials:
- **Email:** projecthinfintiy@12.in
- **Password:** Password123@ 