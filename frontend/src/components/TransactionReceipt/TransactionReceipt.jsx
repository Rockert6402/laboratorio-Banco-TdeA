import { QRCodeSVG } from 'qrcode.react'
import { useTransactionReceipt } from '../../hooks/useTransactionReceipt'
import { formatCurrency, formatDateTime } from '../../utils/format'
import './TransactionReceipt.css'

const STATUS_LABELS = {
  COMPLETED: 'Completada',
  FAILED: 'Fallida',
}

// Comprobante de una transferencia con su código QR.
export default function TransactionReceipt({ transaction, onClose }) {
  const { qrRef, qrValue, downloadQr, downloadError } = useTransactionReceipt(transaction)

  return (
    <div
      className="transaction-receipt__backdrop"
      onKeyDown={(event) => event.key === 'Escape' && onClose()}
    >
      <article
        className="transaction-receipt"
        role="dialog"
        aria-modal="true"
        aria-labelledby="transaction-receipt-title"
      >
        <header className="transaction-receipt__header">
          <h2 id="transaction-receipt-title" className="transaction-receipt__title">
            Comprobante de transferencia
          </h2>
          <span
            className={`transaction-receipt__status transaction-receipt__status--${transaction.status.toLowerCase()}`}
          >
            {STATUS_LABELS[transaction.status] ?? transaction.status}
          </span>
        </header>

        <p className="transaction-receipt__amount">{formatCurrency(transaction.amount)}</p>

        <dl className="transaction-receipt__details">
          <div className="transaction-receipt__row">
            <dt>Origen</dt>
            <dd>
              {transaction.sourceAccountNumber}
              <span className="transaction-receipt__owner">{transaction.sourceOwnerName}</span>
            </dd>
          </div>
          <div className="transaction-receipt__row">
            <dt>Destino</dt>
            <dd>
              {transaction.targetAccountNumber}
              <span className="transaction-receipt__owner">{transaction.targetOwnerName}</span>
            </dd>
          </div>
          <div className="transaction-receipt__row">
            <dt>Fecha</dt>
            <dd>{formatDateTime(transaction.createdAt)}</dd>
          </div>
          {transaction.description && (
            <div className="transaction-receipt__row">
              <dt>Descripción</dt>
              <dd>{transaction.description}</dd>
            </div>
          )}
          <div className="transaction-receipt__row">
            <dt>Referencia</dt>
            <dd className="transaction-receipt__reference">{transaction.reference}</dd>
          </div>
        </dl>

        <div className="transaction-receipt__qr">
          <QRCodeSVG
            ref={qrRef}
            value={qrValue}
            size={192}
            marginSize={2}
            bgColor="#ffffff"
            fgColor="#000000"
            title={`Código QR de la transacción ${transaction.reference}`}
          />
        </div>

        {downloadError && (
          <p className="transaction-receipt__error" role="alert">{downloadError}</p>
        )}

        <footer className="transaction-receipt__actions">
          <button type="button" className="transaction-receipt__button" onClick={downloadQr}>
            Descargar QR
          </button>
          <button
            type="button"
            className="transaction-receipt__button transaction-receipt__button--primary"
            onClick={onClose}
            autoFocus
          >
            Cerrar
          </button>
        </footer>
      </article>
    </div>
  )
}
