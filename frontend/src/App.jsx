import AccountList from './components/AccountList/AccountList'
import TransactionHistory from './components/TransactionHistory/TransactionHistory'
import TransferForm from './components/TransferForm/TransferForm'
import { useAccounts } from './hooks/useAccounts'
import { useTransactionHistory } from './hooks/useTransactionHistory'
import './App.css'

// Pantalla principal: encabezado del banco y las secciones Cuentas, Nueva transferencia e Historial.
export default function App() {
  const accounts = useAccounts()
  const history = useTransactionHistory()

  // Tras cada transferencia se recargan saldos e historial.
  function handleTransferCompleted() {
    accounts.refresh()
    history.refresh()
  }

  return (
    <div className="app">
      <header className="app__header">
        <div className="app__header-content">
          <span className="app__logo" aria-hidden="true">B</span>
          <div>
            <h1 className="app__bank-name">Banco TdeA</h1>
            <p className="app__tagline">Transferencias entre cuentas con comprobante QR</p>
          </div>
        </div>
      </header>

      <main className="app__main">
        <section className="app__section app__section--accounts" aria-label="Cuentas">
          <AccountList
            accounts={accounts.accounts}
            isLoading={accounts.isLoading}
            error={accounts.error}
            onRetry={accounts.refresh}
          />
        </section>

        <section className="app__section app__section--transfer" aria-label="Nueva transferencia">
          <TransferForm
            accounts={accounts.accounts}
            accountsLoading={accounts.isLoading}
            accountsError={accounts.error}
            onRetryAccounts={accounts.refresh}
            onTransferCompleted={handleTransferCompleted}
          />
        </section>

        <section className="app__section app__section--history" aria-label="Historial">
          <TransactionHistory
            accounts={accounts.accounts}
            transactions={history.transactions}
            isLoading={history.isLoading}
            error={history.error}
            accountFilter={history.accountFilter}
            selectedTransaction={history.selectedTransaction}
            onFilterChange={history.handleFilterChange}
            onRetry={history.refresh}
            onOpenReceipt={history.openReceipt}
            onCloseReceipt={history.closeReceipt}
          />
        </section>
      </main>
    </div>
  )
}
