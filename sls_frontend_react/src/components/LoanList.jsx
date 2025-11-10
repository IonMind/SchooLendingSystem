import React, { useEffect, useState } from 'react'
import { api } from '../api'

export default function LoanList() {
  const [loans, setLoans] = useState([])

  async function load() {
    const res = await api.listLoans()
    if (res.ok) setLoans(res.data)
  }

  useEffect(() => { load() }, [])

  async function approve(id) {
    await api.approveLoan(id)
    load()
  }

  async function reject(id) {
    const reason = prompt('Reason for rejection (optional)')
    await api.rejectLoan(id, reason || '')
    load()
  }

  async function markReturn(id) {
    await api.returnLoan(id)
    load()
  }

  return (
    <div>
      <h2>Loan Requests</h2>
      <table className="table">
        <thead><tr><th>ID</th><th>Requester</th><th>Equipment</th><th>Qty</th><th>From</th><th>To</th><th>Status</th><th>Actions</th></tr></thead>
        <tbody>
          {loans && loans.map(l => (
            <tr key={l.id}>
              <td>{l.id}</td>
              <td>{l.requesterName}</td>
              <td>{l.equipment?.name}</td>
              <td>{l.quantity}</td>
              <td>{l.startDate}</td>
              <td>{l.endDate}</td>
              <td>{l.status}</td>
              <td>
                {l.status === 'PENDING' && <><button onClick={() => approve(l.id)}>Approve</button><button onClick={() => reject(l.id)}>Reject</button></>}
                {l.status === 'APPROVED' && <button onClick={() => markReturn(l.id)}>Mark Returned</button>}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
