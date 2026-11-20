const currencyFormatter = new Intl.NumberFormat('es-CO', {
  style: 'currency',
  currency: 'COP',
  // Siempre con centavos: sin esto el navegador redondea COP a pesos enteros.
  minimumFractionDigits: 2,
  maximumFractionDigits: 2,
})

const dateTimeFormatter = new Intl.DateTimeFormat('es-CO', {
  dateStyle: 'medium',
  timeStyle: 'short',
})

// Formatea un monto (número o string decimal) como pesos colombianos.
export function formatCurrency(amount) {
  return currencyFormatter.format(Number(amount))
}

// Formatea una fecha ISO del backend como fecha y hora local.
export function formatDateTime(isoDate) {
  return dateTimeFormatter.format(new Date(isoDate))
}
