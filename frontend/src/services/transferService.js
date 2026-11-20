import apiClient from './apiClient'

// Ejecuta una transferencia: { sourceAccountNumber, targetAccountNumber, amount, description }.
// amount se envía como número o string decimal, sin cálculos en el front.
export async function transfer(transferRequest) {
  const { data } = await apiClient.post('/transfers', transferRequest)
  return data
}
