import { useCallback, useEffect, useState } from 'react'

const TOAST_DURATION_MS = 4000

// Maneja un aviso temporal que se cierra solo a los 4 segundos.
// Cada aviso nuevo reinicia el temporizador, aunque repita el mismo mensaje.
export function useToast() {
  const [toast, setToast] = useState(null)

  const showToast = useCallback((message, type = 'error') => {
    setToast({ id: Date.now(), message, type })
  }, [])

  const closeToast = useCallback(() => {
    setToast(null)
  }, [])

  useEffect(() => {
    if (!toast) return undefined
    const timeoutId = setTimeout(() => setToast(null), TOAST_DURATION_MS)
    return () => clearTimeout(timeoutId)
  }, [toast])

  return { toast, showToast, closeToast }
}
