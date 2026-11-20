import apiClient from './apiClient'

// Lista las transacciones, más recientes primero.
// Si se indica accountNumber, solo las de esa cuenta (como origen o destino).
export async function getAll(accountNumber) {
  const params = accountNumber ? { accountNumber } : undefined
  const { data } = await apiClient.get('/transactions', { params })
  return data
}

// Consulta una transacción por su referencia (UUID).
export async function getByReference(reference) {
  const { data } = await apiClient.get(`/transactions/${encodeURIComponent(reference)}`)
  return data
}
