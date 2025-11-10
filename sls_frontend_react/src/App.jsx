import React, { useState } from 'react'
import EquipmentList from './components/EquipmentList'
import EquipmentForm from './components/EquipmentForm'
import LoanRequestForm from './components/LoanRequestForm'
import LoanList from './components/LoanList'
import Login from './components/Login'

export default function App() {
  const [view, setView] = useState('equipment')
  const [loggedIn, setLoggedIn] = useState(!!localStorage.getItem('sls_token'))
  const [role, setRole] = useState(localStorage.getItem('sls_role'))

  const isAdmin = role === 'ADMIN'
  const canManageLoans = role === 'ADMIN' || role === 'STAFF'

  const navBtn = (id, label, show = true) =>
    show && <button className={view === id ? 'active' : ''} onClick={() => setView(id)}>{label}</button>

  return (
    <div className="container fade-in">
      <header className="layout-header">
        <h1>SLS Equipment Lending</h1>
        <Login onLogin={() => {
          setLoggedIn(!!localStorage.getItem('sls_token'))
          setRole(localStorage.getItem('sls_role'))
        }} />
        <nav className="layout-nav">
          {navBtn('equipment','Equipment')}
          {navBtn('new-equipment','Add Equipment', isAdmin)}
          {navBtn('request','Request Loan')}
          {navBtn('loans','Loan Requests', canManageLoans)}
        </nav>
      </header>

      <main>
        {view === 'equipment' && <EquipmentList role={role} />}
        {view === 'new-equipment' && <EquipmentForm role={role} onSaved={() => setView('equipment')} />}
        {view === 'request' && <LoanRequestForm role={role} />}
        {view === 'loans' && <LoanList role={role} />}
      </main>

      <footer>
        <small>Frontend connects to: {import.meta.env.VITE_API_BASE}</small>
      </footer>
    </div>
  )
}
