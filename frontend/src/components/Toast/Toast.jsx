import './Toast.css'

// Aviso flotante. Recibe el toast de useToast() ({ id, message, type }) y onClose.
export default function Toast({ toast, onClose }) {
  if (!toast) return null

  return (
    <div
      key={toast.id}
      className={`toast toast--${toast.type}`}
      role={toast.type === 'error' ? 'alert' : 'status'}
    >
      <p className="toast__message">{toast.message}</p>
      <button type="button" className="toast__close" onClick={onClose} aria-label="Cerrar aviso">
        ×
      </button>
    </div>
  )
}
