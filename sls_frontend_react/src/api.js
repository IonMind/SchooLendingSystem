const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8080/api'

async function request(path, options = {}) {
  const res = await fetch(`${API_BASE}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  })

  const text = await res.text()
  try {
    return { ok: res.ok, data: text ? JSON.parse(text) : null }
  } catch (e) {
    return { ok: res.ok, data: text }
  }
}

export const api = {
  listEquipment: () => request('/equipment'),
  createEquipment: (body) => request('/equipment', { method: 'POST', body: JSON.stringify(body) }),
  updateEquipment: (id, body) => request(`/equipment/${id}`, { method: 'PUT', body: JSON.stringify(body) }),
  deleteEquipment: (id) => request(`/equipment/${id}`, { method: 'DELETE' }),

  createLoan: (body) => request('/loans', { method: 'POST', body: JSON.stringify(body) }),
  listLoans: () => request('/loans'),
  approveLoan: (id) => request(`/loans/${id}/approve`, { method: 'POST' }),
  rejectLoan: (id, reason) => request(`/loans/${id}/reject`, { method: 'POST', body: JSON.stringify(reason) }),
  returnLoan: (id) => request(`/loans/${id}/return`, { method: 'POST' }),
}
