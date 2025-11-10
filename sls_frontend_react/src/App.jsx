import React, { useState } from 'react'
import EquipmentList from './components/EquipmentList'
import EquipmentForm from './components/EquipmentForm'
import LoanRequestForm from './components/LoanRequestForm'
import LoanList from './components/LoanList'

export default function App() {
  const [view, setView] = useState('equipment')

  return (
    <div className="container">
      <header>
        <h1>SLS Equipment Lending</h1>
        <nav>
          <button onClick={() => setView('equipment')}>Equipment</button>
          <button onClick={() => setView('new-equipment')}>Add Equipment</button>
          <button onClick={() => setView('request')}>Request Loan</button>
          <button onClick={() => setView('loans')}>Loan Requests</button>
        </nav>
      </header>

      <main>
        {view === 'equipment' && <EquipmentList />}
        {view === 'new-equipment' && <EquipmentForm onSaved={() => setView('equipment')} />}
        {view === 'request' && <LoanRequestForm />}
        {view === 'loans' && <LoanList />}
      </main>

      <footer>
        <small>Frontend connects to: {import.meta.env.VITE_API_BASE}</small>
      </footer>
    </div>
  )
}
