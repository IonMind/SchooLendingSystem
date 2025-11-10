import React, { useEffect, useState } from 'react'
import { api } from '../api'

export default function LoanRequestForm() {
  const [equipment, setEquipment] = useState([])
  const [form, setForm] = useState({ equipmentId: '', requesterName: '', quantity: 1, startDate: '', endDate: '', note: '' })
  const [msg, setMsg] = useState('')

  useEffect(() => { api.listEquipment().then(r => r.ok && setEquipment(r.data)) }, [])

  async function submit(e) {
    e.preventDefault()
    setMsg('')
    const res = await api.createLoan(form)
    if (res.ok) {
      setMsg('Request created')
      setForm({ equipmentId: '', requesterName: '', quantity: 1, startDate: '', endDate: '', note: '' })
    } else {
      setMsg('Error: ' + (res.data || 'unknown'))
    }
  }

  return (
    <div>
      <h2>Request Equipment</h2>
      <form onSubmit={submit} className="form">
        <label>Equipment
          <select value={form.equipmentId} onChange={e => setForm({...form, equipmentId: parseInt(e.target.value||'')})} required>
            <option value="">--Select--</option>
            {equipment && equipment.map(eq => <option key={eq.id} value={eq.id}>{eq.name} ({eq.totalQuantity})</option>)}
          </select>
        </label>
        <label>Your Name<input value={form.requesterName} onChange={e => setForm({...form, requesterName: e.target.value})} required/></label>
        <label>Quantity<input type="number" min="1" value={form.quantity} onChange={e => setForm({...form, quantity: parseInt(e.target.value||1)})} required/></label>
        <label>Start Date<input type="date" value={form.startDate} onChange={e => setForm({...form, startDate: e.target.value})} required/></label>
        <label>End Date<input type="date" value={form.endDate} onChange={e => setForm({...form, endDate: e.target.value})} required/></label>
        <label>Note<input value={form.note} onChange={e => setForm({...form, note: e.target.value})}/></label>
        <button type="submit">Request</button>
      </form>
      {msg && <p>{msg}</p>}
    </div>
  )
}
