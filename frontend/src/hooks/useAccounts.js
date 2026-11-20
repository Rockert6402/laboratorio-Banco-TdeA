import { useCallback, useEffect, useState } from 'react'
import { getAll } from '../services/accountService'

// Carga la lista de cuentas y expone refresh() para recargarla
// (por ejemplo, después de cada transferencia).
// Durante una recarga se conservan las cuentas ya mostradas para evitar parpadeos.
export function useAccounts() {
  const [accounts, setAccounts] = useState([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState(null)
  // Cada incremento dispara una nueva carga en el efecto.
  const [reloadKey, setReloadKey] = useState(0)

  useEffect(() => {
    // Descarta la respuesta si llega otra recarga o el componente se desmonta.
    let ignore = false

    getAll()
      .then((data) => {
        if (!ignore) {
          setAccounts(data)
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
  }, [reloadKey])

  const refresh = useCallback(() => {
    setIsLoading(true)
    setReloadKey((key) => key + 1)
  }, [])

  return { accounts, isLoading, error, refresh }
}
