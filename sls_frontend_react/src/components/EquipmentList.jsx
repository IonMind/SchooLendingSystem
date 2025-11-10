import React, { useEffect, useState } from 'react'
import { api } from '../api'

export default function EquipmentList() {
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(false)

  async function load() {
    setLoading(true)
    const res = await api.listEquipment()
    if (res.ok) setItems(res.data)
    setLoading(false)
  }

  useEffect(() => { load() }, [])

  async function remove(id) {
    if (!confirm('Delete this equipment?')) return
    await api.deleteEquipment(id)
    load()
  }

  return (
    <div>
      <h2>Equipment</h2>
      {loading ? <p>Loading...</p> : (
        <table className="table">
          <thead><tr><th>ID</th><th>Name</th><th>Category</th><th>Condition</th><th>Total</th><th>Actions</th></tr></thead>
          <tbody>
            {items && items.map(it => (
              <tr key={it.id}>
                <td>{it.id}</td>
                <td>{it.name}</td>
                <td>{it.category}</td>
                <td>{it.conditionDesc}</td>
                <td>{it.totalQuantity}</td>
                <td><button onClick={() => remove(it.id)}>Delete</button></td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}
