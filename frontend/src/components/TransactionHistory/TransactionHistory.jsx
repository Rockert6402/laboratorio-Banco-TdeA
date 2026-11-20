import { formatCurrency, formatDateTime } from '../../utils/format'
import TransactionReceipt from '../TransactionReceipt/TransactionReceipt'
import './TransactionHistory.css'

const STATUS_LABELS = {
  COMPLETED: 'Completada',
  FAILED: 'Fallida',
}

// Abrevia la referencia UUID para la tabla; la completa queda en el title.
function shortReference(reference) {
  return `${reference.slice(0, 8)}…`
}

// Historial de transacciones. Recibe los datos de useTransactionHistory() y las cuentas para el filtro.
export default function TransactionHistory({
  accounts,
  transactions,
  isLoading,
  error,
  accountFilter,
  selectedTransaction,
  onFilterChange,
  onRetry,
  onOpenReceipt,
  onCloseReceipt,
}) {
  const hasTransactions = transactions.length > 0

  return (
    <div className="transaction-history">
      <div className="transaction-history__toolbar">
        <h2 className="transaction-history__title">Historial</h2>

        <div className="transaction-history__filter">
          <label className="transaction-history__label" htmlFor="history-account-filter">
            Filtrar por cuenta
          </label>
          <select
            id="history-account-filter"
            className="transaction-history__select"
            value={accountFilter}
            onChange={onFilterChange}
          >
            <option value="">Todas</option>
            {accounts.map((account) => (
              <option key={account.accountNumber} value={account.accountNumber}>
                {account.accountNumber} · {account.ownerName}
              </option>
            ))}
          </select>
        </div>
      </div>

      {isLoading && !hasTransactions && (
        <p className="transaction-history__status" role="status">Cargando transacciones…</p>
      )}

      {!isLoading && error && (
        <div className="transaction-history__error" role="alert">
          <p>{error}</p>
          <button type="button" className="transaction-history__retry" onClick={onRetry}>
            Reintentar
          </button>
        </div>
      )}

      {!isLoading && !error && !hasTransactions && (
        <p className="transaction-history__status">
          {accountFilter
            ? 'Esta cuenta todavía no tiene transacciones.'
            : 'Todavía no hay transacciones registradas.'}
        </p>
      )}

      {hasTransactions && (
        <div
          className="transaction-history__table-wrapper"
          role="region"
          aria-label="Tabla de transacciones"
          aria-busy={isLoading}
          tabIndex={0}
        >
          <table className="transaction-history__table">
            <thead>
              <tr>
                <th scope="col">Fecha</th>
                <th scope="col">Referencia</th>
                <th scope="col">Origen</th>
                <th scope="col">Destino</th>
                <th scope="col" className="transaction-history__amount-cell">Monto</th>
                <th scope="col">Descripción</th>
                <th scope="col">Estado</th>
                <th scope="col"><span className="transaction-history__sr-only">Acciones</span></th>
              </tr>
            </thead>
            <tbody>
              {transactions.map((transaction) => (
                <tr key={transaction.reference}>
                  <td className="transaction-history__nowrap">{formatDateTime(transaction.createdAt)}</td>
                  <td className="transaction-history__reference" title={transaction.reference}>
                    {shortReference(transaction.reference)}
                  </td>
                  <td>
                    <span className="transaction-history__account">{transaction.sourceAccountNumber}</span>
                    <span className="transaction-history__owner">{transaction.sourceOwnerName}</span>
                  </td>
                  <td>
                    <span className="transaction-history__account">{transaction.targetAccountNumber}</span>
                    <span className="transaction-history__owner">{transaction.targetOwnerName}</span>
                  </td>
                  <td className="transaction-history__amount-cell">{formatCurrency(transaction.amount)}</td>
                  <td className="transaction-history__description">{transaction.description || '—'}</td>
                  <td>
                    <span
                      className={`transaction-history__status-badge transaction-history__status-badge--${transaction.status.toLowerCase()}`}
                    >
                      {STATUS_LABELS[transaction.status] ?? transaction.status}
                    </span>
                  </td>
                  <td>
                    <button
                      type="button"
                      className="transaction-history__qr-button"
                      onClick={() => onOpenReceipt(transaction)}
                      aria-label={`Ver QR de la transacción ${shortReference(transaction.reference)}`}
                    >
                      Ver QR
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {selectedTransaction && (
        <TransactionReceipt transaction={selectedTransaction} onClose={onCloseReceipt} />
      )}
    </div>
  )
}
