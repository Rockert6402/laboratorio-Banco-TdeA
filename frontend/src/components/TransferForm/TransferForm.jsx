import { useTransferForm } from '../../hooks/useTransferForm'
import { formatCurrency } from '../../utils/format'
import Toast from '../Toast/Toast'
import TransactionReceipt from '../TransactionReceipt/TransactionReceipt'
import './TransferForm.css'

// Formulario de transferencia. Las cuentas vienen de useAccounts() en la página.
export default function TransferForm({
  accounts,
  accountsLoading,
  accountsError,
  onRetryAccounts,
  onTransferCompleted,
}) {
  const {
    values,
    errors,
    isSubmitting,
    receipt,
    toast,
    descriptionMaxLength,
    handleChange,
    handleSubmit,
    closeReceipt,
    closeToast,
  } = useTransferForm({ accounts, onTransferCompleted })

  const hasAccounts = accounts.length > 0

  return (
    <section className="transfer-form">
      <h2 className="transfer-form__title">Nueva transferencia</h2>

      {accountsLoading && !hasAccounts && (
        <p className="transfer-form__status" role="status">Cargando cuentas…</p>
      )}

      {accountsError && !hasAccounts && (
        <div className="transfer-form__load-error" role="alert">
          <p>{accountsError}</p>
          <button type="button" className="transfer-form__retry" onClick={onRetryAccounts}>
            Reintentar
          </button>
        </div>
      )}

      {!accountsLoading && !accountsError && !hasAccounts && (
        <p className="transfer-form__status">No hay cuentas disponibles para transferir.</p>
      )}

      {hasAccounts && (
        <form className="transfer-form__form" onSubmit={handleSubmit} noValidate>
          <div className="transfer-form__field">
            <label className="transfer-form__label" htmlFor="transfer-source">
              Cuenta de origen
            </label>
            <select
              id="transfer-source"
              name="sourceAccountNumber"
              className="transfer-form__control"
              value={values.sourceAccountNumber}
              onChange={handleChange}
              aria-invalid={Boolean(errors.sourceAccountNumber)}
              aria-describedby={errors.sourceAccountNumber ? 'transfer-source-error' : undefined}
            >
              <option value="">Selecciona una cuenta</option>
              {accounts.map((account) => (
                <option key={account.accountNumber} value={account.accountNumber}>
                  {account.accountNumber} · {account.ownerName} · {formatCurrency(account.balance)}
                </option>
              ))}
            </select>
            {errors.sourceAccountNumber && (
              <p id="transfer-source-error" className="transfer-form__error">
                {errors.sourceAccountNumber}
              </p>
            )}
          </div>

          <div className="transfer-form__field">
            <label className="transfer-form__label" htmlFor="transfer-target">
              Cuenta de destino
            </label>
            <select
              id="transfer-target"
              name="targetAccountNumber"
              className="transfer-form__control"
              value={values.targetAccountNumber}
              onChange={handleChange}
              aria-invalid={Boolean(errors.targetAccountNumber)}
              aria-describedby={errors.targetAccountNumber ? 'transfer-target-error' : undefined}
            >
              <option value="">Selecciona una cuenta</option>
              {accounts.map((account) => (
                <option key={account.accountNumber} value={account.accountNumber}>
                  {account.accountNumber} · {account.ownerName} · {formatCurrency(account.balance)}
                </option>
              ))}
            </select>
            {errors.targetAccountNumber && (
              <p id="transfer-target-error" className="transfer-form__error">
                {errors.targetAccountNumber}
              </p>
            )}
          </div>

          <div className="transfer-form__field">
            <label className="transfer-form__label" htmlFor="transfer-amount">
              Monto (COP)
            </label>
            <input
              id="transfer-amount"
              name="amount"
              type="text"
              inputMode="decimal"
              autoComplete="off"
              placeholder="Ej. 150000 o 150000,50"
              className="transfer-form__control"
              value={values.amount}
              onChange={handleChange}
              aria-invalid={Boolean(errors.amount)}
              aria-describedby={errors.amount ? 'transfer-amount-error' : undefined}
            />
            {errors.amount && (
              <p id="transfer-amount-error" className="transfer-form__error">
                {errors.amount}
              </p>
            )}
          </div>

          <div className="transfer-form__field">
            <label className="transfer-form__label" htmlFor="transfer-description">
              Descripción <span className="transfer-form__optional">(opcional)</span>
            </label>
            <textarea
              id="transfer-description"
              name="description"
              rows={2}
              maxLength={descriptionMaxLength}
              className="transfer-form__control transfer-form__control--textarea"
              value={values.description}
              onChange={handleChange}
              aria-invalid={Boolean(errors.description)}
              aria-describedby={errors.description ? 'transfer-description-error' : undefined}
            />
            {errors.description && (
              <p id="transfer-description-error" className="transfer-form__error">
                {errors.description}
              </p>
            )}
          </div>

          <button type="submit" className="transfer-form__submit" disabled={isSubmitting}>
            {isSubmitting ? 'Transfiriendo…' : 'Transferir'}
          </button>
        </form>
      )}

      {receipt && <TransactionReceipt transaction={receipt} onClose={closeReceipt} />}

      <Toast toast={toast} onClose={closeToast} />
    </section>
  )
}
