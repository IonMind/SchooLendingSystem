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
      <div className="section" style={{ padding: '12px 16px' }}>
        <strong>Logged in:</strong> {currentUser} ({currentRole})
        <button className="outline" onClick={logout} style={{ marginLeft: '8px' }}>
          Logout
        </button>
        {msg && <p style={{ margin: '6px 0 0', fontSize: '.75rem' }}>{msg}</p>}
      </div>
    );
  }

  return (
    <div className="section" style={{ padding: '12px 16px' }}>
      <strong>Login / Signup</strong>
      <div className="form" style={{ gridTemplateColumns: 'repeat(auto-fit,minmax(140px,1fr))', marginTop: '8px' }}>
        <label>Username
          <input value={username} onChange={e => setUsername(e.target.value)} />
        </label>
        <label>Password
          <input type="password" value={password} onChange={e => setPassword(e.target.value)} />
        </label>
        <label>Role
          <select value={role} onChange={e => setRole(e.target.value)}>
            <option value="STUDENT">Student</option>
            <option value="STAFF">Staff</option>
            <option value="ADMIN">Admin</option>
          </select>
        </label>
      </div>
      <div style={{ marginTop: '12px', display: 'flex', gap: '8px' }}>
        <button onClick={login}>Login</button>
        <button className="outline" onClick={signup}>Signup</button>
      </div>
      {msg && <p style={{ margin: '6px 0 0', fontSize: '.7rem' }}>{msg}</p>}
    </div>
  );
}
