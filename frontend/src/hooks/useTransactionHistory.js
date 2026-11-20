import { useCallback, useEffect, useState } from 'react'
import { getAll } from '../services/transactionService'

// Carga el historial de transacciones (más recientes primero), con filtro opcional por cuenta.
// Expone refresh() para recargar después de cada transferencia y el estado del comprobante abierto.
export function useTransactionHistory() {
  const [transactions, setTransactions] = useState([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState(null)
  // '' significa "Todas las cuentas".
  const [accountFilter, setAccountFilter] = useState('')
  // Cada incremento dispara una nueva carga en el efecto.
  const [reloadKey, setReloadKey] = useState(0)
  const [selectedTransaction, setSelectedTransaction] = useState(null)

  useEffect(() => {
    // Descarta la respuesta si cambia el filtro, llega otra recarga o se desmonta.
    let ignore = false

    getAll(accountFilter || undefined)
      .then((data) => {
        if (!ignore) {
          setTransactions(data)
          setError(null)
        }
      })
      .catch((err) => {
        if (!ignore) setError(err.message)
      })
      .finally(() => {
        if (!ignore) setIsLoading(false)
      })

    return () => {
      ignore = true
    }
  }, [accountFilter, reloadKey])

  const refresh = useCallback(() => {
    setIsLoading(true)
    setReloadKey((key) => key + 1)
  }, [])

  function handleFilterChange(event) {
    // Al cambiar de cuenta se limpian las filas para no mostrar datos de otro filtro.
    setTransactions([])
    setError(null)
    setIsLoading(true)
    setAccountFilter(event.target.value)
  }

  function openReceipt(transaction) {
    setSelectedTransaction(transaction)
  }

  function closeReceipt() {
    setSelectedTransaction(null)
  }

  return {
    transactions,
    isLoading,
    error,
    accountFilter,
    selectedTransaction,
    refresh,
    handleFilterChange,
    openReceipt,
    closeReceipt,
  }
}
