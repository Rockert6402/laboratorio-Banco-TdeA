import { useState } from 'react'
import { transfer } from '../services/transferService'
import { compareAmounts, normalizeAmount } from '../utils/decimal'
import { formatCurrency } from '../utils/format'
import { useToast } from './useToast'

const EMPTY_VALUES = {
  sourceAccountNumber: '',
  targetAccountNumber: '',
  amount: '',
  description: '',
}

const DESCRIPTION_MAX_LENGTH = 255

// Valida el formulario y devuelve un objeto { campo: mensaje } solo con los campos con error.
function validate(values, accounts) {
  const errors = {}
  const source = accounts.find((account) => account.accountNumber === values.sourceAccountNumber)

  if (!values.sourceAccountNumber) {
    errors.sourceAccountNumber = 'Selecciona la cuenta de origen.'
  }

  if (!values.targetAccountNumber) {
    errors.targetAccountNumber = 'Selecciona la cuenta de destino.'
  } else if (values.targetAccountNumber === values.sourceAccountNumber) {
    errors.targetAccountNumber = 'La cuenta de destino debe ser distinta de la de origen.'
  }

  const amount = normalizeAmount(values.amount)
  if (!values.amount.trim()) {
    errors.amount = 'Ingresa el monto.'
  } else if (amount === null) {
    errors.amount = 'Ingresa un monto válido, sin separadores de miles (ej. 150000 o 150000,50).'
  } else if (compareAmounts(amount, '0') <= 0) {
    errors.amount = 'El monto debe ser mayor que 0.'
  } else if (source && compareAmounts(amount, source.balance) > 0) {
    errors.amount = `El monto supera el saldo disponible (${formatCurrency(source.balance)}).`
  }

  if (values.description.length > DESCRIPTION_MAX_LENGTH) {
    errors.description = `La descripción no puede superar ${DESCRIPTION_MAX_LENGTH} caracteres.`
  }

  return errors
}

// Lógica del formulario de transferencia.
// accounts: cuentas de useAccounts(). onTransferCompleted(transaction): se llama tras
// una transferencia exitosa para que la página refresque cuentas e historial.
export function useTransferForm({ accounts, onTransferCompleted }) {
  const [values, setValues] = useState(EMPTY_VALUES)
  const [errors, setErrors] = useState({})
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [receipt, setReceipt] = useState(null)
  const { toast, showToast, closeToast } = useToast()

  function handleChange(event) {
    const { name, value } = event.target
    setValues((current) => ({ ...current, [name]: value }))
    // El error del campo desaparece al corregirlo; se vuelve a validar al enviar.
    setErrors((current) => {
      if (!current[name]) return current
      const { [name]: _removed, ...rest } = current
      return rest
    })
  }

  async function handleSubmit(event) {
    event.preventDefault()
    if (isSubmitting) return

    const validationErrors = validate(values, accounts)
    setErrors(validationErrors)
    if (Object.keys(validationErrors).length > 0) return

    setIsSubmitting(true)
    try {
      const description = values.description.trim()
      const transaction = await transfer({
        sourceAccountNumber: values.sourceAccountNumber,
        targetAccountNumber: values.targetAccountNumber,
        amount: normalizeAmount(values.amount),
        description: description || null,
      })
      setReceipt(transaction)
      setValues((current) => ({ ...current, amount: '', description: '' }))
      onTransferCompleted?.(transaction)
    } catch (err) {
      showToast(err.message)
    } finally {
      setIsSubmitting(false)
    }
  }

  function closeReceipt() {
    setReceipt(null)
  }

  return {
    values,
    errors,
    isSubmitting,
    receipt,
    toast,
    descriptionMaxLength: DESCRIPTION_MAX_LENGTH,
    handleChange,
    handleSubmit,
    closeReceipt,
    closeToast,
  }
}
