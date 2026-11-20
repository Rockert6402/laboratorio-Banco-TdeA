// Utilidades para montos SIN aritmética de punto flotante.
// Los montos se manejan como strings decimales y se comparan en centavos con BigInt.

// Hasta 13 enteros y 2 decimales, con punto o coma decimal. Sin separadores de miles.
const AMOUNT_INPUT_PATTERN = /^\d{1,13}([.,]\d{1,2})?$/

// Devuelve el monto escrito por el usuario como string decimal con punto ("1500.5"),
// o null si el formato no es válido.
export function normalizeAmount(input) {
  const trimmed = String(input ?? '').trim()
  if (!AMOUNT_INPUT_PATTERN.test(trimmed)) return null
  return trimmed.replace(',', '.')
}

// Convierte un monto (string decimal o número del backend con máximo 2 decimales)
// a centavos enteros (BigInt). Devuelve null si no es un monto válido.
export function toCents(value) {
  // toFixed solo formatea el número recibido del backend; no hace cálculos.
  const text = typeof value === 'number' ? value.toFixed(2) : String(value ?? '').trim()
  const match = /^(-?)(\d+)(?:\.(\d{1,2}))?$/.exec(text)
  if (!match) return null
  const [, sign, integerPart, fractionPart = ''] = match
  const cents = BigInt(integerPart) * 100n + BigInt(fractionPart.padEnd(2, '0'))
  return sign ? -cents : cents
}

// Compara dos montos: negativo si a < b, 0 si son iguales, positivo si a > b.
// Devuelve null si alguno no es válido.
export function compareAmounts(a, b) {
  const centsA = toCents(a)
  const centsB = toCents(b)
  if (centsA === null || centsB === null) return null
  if (centsA === centsB) return 0
  return centsA < centsB ? -1 : 1
}
