import apiClient from './apiClient'

// Lista todas las cuentas.
export async function getAll() {
  const { data } = await apiClient.get('/accounts')
  return data
}

// Consulta una cuenta por su número de 10 dígitos.
export async function getByNumber(number) {
  const { data } = await apiClient.get(`/accounts/${encodeURIComponent(number)}`)
  return data
}

// Crea una cuenta: { accountNumber, ownerName, initialBalance }.
export async function create(account) {
  const { data } = await apiClient.post('/accounts', account)
  return data
}
