import React, { useState, useEffect } from 'react';

export default function Login({ onLogin }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('STUDENT');
  const [msg, setMsg] = useState('');
  const [loggedIn, setLoggedIn] = useState(!!localStorage.getItem('sls_token'));
  const [currentUser, setCurrentUser] = useState(localStorage.getItem('sls_user'));
  const [currentRole, setCurrentRole] = useState(localStorage.getItem('sls_role'));

  useEffect(() => {
    setLoggedIn(!!localStorage.getItem('sls_token'));
    setCurrentUser(localStorage.getItem('sls_user'));
    setCurrentRole(localStorage.getItem('sls_role'));
  }, []);

  async function signup() {
    const res = await fetch(`${import.meta.env.VITE_API_BASE}/auth/signup`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password, role }),
    });
    const text = await res.text();
    if (res.ok) setMsg('Signup successful. You can now log in.');
    else setMsg(text || 'Signup failed');
  }

  async function login() {
    const res = await fetch(`${import.meta.env.VITE_API_BASE}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password }),
    });
    const data = await res.json();
    if (res.ok) {
      localStorage.setItem('sls_token', data.token);
      localStorage.setItem('sls_user', data.username);
      localStorage.setItem('sls_role', data.role);
      setMsg('Logged in successfully');
      setLoggedIn(true);
      setCurrentUser(data.username);
      setCurrentRole(data.role);
      if (onLogin) onLogin();
    } else {
      setMsg(data || 'Login failed');
    }
  }

  async function logout() {
    const token = localStorage.getItem('sls_token');
    await fetch(`${import.meta.env.VITE_API_BASE}/auth/logout`, {
      method: 'POST',
      headers: { 'X-Auth-Token': token },
    });
    localStorage.removeItem('sls_token');
    localStorage.removeItem('sls_user');
    localStorage.removeItem('sls_role');
    setMsg('Logged out');
    setLoggedIn(false);
    setCurrentUser(null);
    setCurrentRole(null);
    if (onLogin) onLogin();
  }

  if (loggedIn) {
    return (
      <div style={{ marginBottom: '10px' }}>
        <strong>Logged in as:</strong> {currentUser} ({currentRole})
        <button onClick={logout} style={{ marginLeft: '10px' }}>
          Logout
        </button>
        <p>{msg}</p>
      </div>
    );
  }

  return (
    <div style={{ marginBottom: '10px' }}>
      <strong>Login / Signup</strong>
      <br />
      <input
        placeholder="username"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
      />
      <input
        placeholder="password"
        type="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
      />
      <select value={role} onChange={(e) => setRole(e.target.value)}>
        <option value="STUDENT">Student</option>
        <option value="STAFF">Staff</option>
        <option value="ADMIN">Admin</option>
      </select>
      <div>
        <button onClick={login}>Login</button>
        <button onClick={signup}>Signup</button>
      </div>
      <p>{msg}</p>
    </div>
  );
}
