import React, { useState } from 'react'
import { api } from '../api'

export default function EquipmentForm({ onSaved }) {
  const [form, setForm] = useState({ name: '', category: '', conditionDesc: '', totalQuantity: 1 })
  const [saving, setSaving] = useState(false)
  const [msg, setMsg] = useState('')

  async function save(e) {
    e.preventDefault()
    setSaving(true)
    const res = await api.createEquipment(form)
    if (res.ok) {
      setMsg('Saved')
      setForm({ name: '', category: '', conditionDesc: '', totalQuantity: 1 })
      if (onSaved) onSaved()
    } else {
      setMsg('Error: ' + (res.data || 'unknown'))
    }
    setSaving(false)
  }

  return (
    <div>
      <h2>Add Equipment</h2>
      <form onSubmit={save} className="form">
        <label>Name<input value={form.name} onChange={e => setForm({...form, name: e.target.value})} required/></label>
        <label>Category<input value={form.category} onChange={e => setForm({...form, category: e.target.value})} required/></label>
        <label>Condition<input value={form.conditionDesc} onChange={e => setForm({...form, conditionDesc: e.target.value})} required/></label>
        <label>Total Quantity<input type="number" min="1" value={form.totalQuantity} onChange={e => setForm({...form, totalQuantity: parseInt(e.target.value||1)})} required/></label>
        <button type="submit" disabled={saving}>{saving ? 'Saving...' : 'Save'}</button>
      </form>
      {msg && <p>{msg}</p>}
    </div>
  )
}
