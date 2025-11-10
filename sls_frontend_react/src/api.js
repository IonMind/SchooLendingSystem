const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8080/api';

function getToken() {
  return localStorage.getItem('sls_token') || '';
}

async function request(path, options = {}) {
  // Build headers and include token
  const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) };
  const token = getToken();
  if (token) {
    headers['X-Auth-Token'] = token;
  }


  try { console.log('[API] Request', options.method || 'GET', API_BASE + path, 'Headers:', headers); } catch(e){}

  const res = await fetch(`${API_BASE}${path}`, {
    headers,
    ...options,
  });

  const text = await res.text();
  try {
    return { ok: res.ok, data: text ? JSON.parse(text) : null };
  } catch (e) {
    return { ok: res.ok, data: text };
  }
}

// export functions...
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
};
