import { formatCurrency } from '../../utils/format'
import './AccountList.css'

// Vista de la lista de cuentas en tarjetas. Recibe los datos de useAccounts().
export default function AccountList({ accounts, isLoading, error, onRetry }) {
  const hasAccounts = accounts.length > 0

  if (isLoading && !hasAccounts) {
    return (
      <section className="account-list" aria-busy="true">
        <h2 className="account-list__title">Cuentas</h2>
        <p className="account-list__status" role="status">Cargando cuentas…</p>
      </section>
    )
  }

  if (error && !hasAccounts) {
    return (
      <section className="account-list">
        <h2 className="account-list__title">Cuentas</h2>
        <div className="account-list__error" role="alert">
          <p>{error}</p>
          <button type="button" className="account-list__retry" onClick={onRetry}>
            Reintentar
          </button>
        </div>
      </section>
    )
  }

  if (!hasAccounts) {
    return (
      <section className="account-list">
        <h2 className="account-list__title">Cuentas</h2>
        <p className="account-list__status">No hay cuentas registradas.</p>
      </section>
    )
  }

  return (
    <section className="account-list" aria-busy={isLoading}>
      <div className="account-list__header">
        <h2 className="account-list__title">Cuentas</h2>
        {isLoading && (
          <span className="account-list__refreshing" role="status">Actualizando…</span>
        )}
      </div>

      {error && (
        <div className="account-list__error account-list__error--inline" role="alert">
          <p>No se pudieron actualizar los saldos: {error}</p>
          <button type="button" className="account-list__retry" onClick={onRetry}>
            Reintentar
          </button>
        </div>
      )}

      <ul className="account-list__grid">
        {accounts.map((account) => (
          <li key={account.accountNumber} className="account-list__card">
            <span className="account-list__owner">{account.ownerName}</span>
            <span className="account-list__number">
              <span className="account-list__label">N.º de cuenta</span>
              {account.accountNumber}
            </span>
            <span className="account-list__balance">
              <span className="account-list__label">Saldo</span>
              {formatCurrency(account.balance)}
            </span>
          </li>
        ))}
      </ul>
    </section>
  )
}
