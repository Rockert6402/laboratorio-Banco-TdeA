import { useMemo, useRef, useState } from 'react'

const PNG_SCALE = 4

// Convierte el SVG del QR en un PNG y lo descarga.
function downloadSvgAsPng(svgElement, fileName) {
  return new Promise((resolve, reject) => {
    const clone = svgElement.cloneNode(true)
    clone.setAttribute('xmlns', 'http://www.w3.org/2000/svg')
    const svgText = new XMLSerializer().serializeToString(clone)
    const svgUrl = URL.createObjectURL(new Blob([svgText], { type: 'image/svg+xml;charset=utf-8' }))

    const { width, height } = svgElement.getBoundingClientRect()
    const image = new Image()

    image.onload = () => {
      const canvas = document.createElement('canvas')
      canvas.width = Math.round(width * PNG_SCALE)
      canvas.height = Math.round(height * PNG_SCALE)
      const context = canvas.getContext('2d')
      context.fillStyle = '#ffffff'
      context.fillRect(0, 0, canvas.width, canvas.height)
      context.imageSmoothingEnabled = false
      context.drawImage(image, 0, 0, canvas.width, canvas.height)
      URL.revokeObjectURL(svgUrl)

      canvas.toBlob((pngBlob) => {
        if (!pngBlob) {
          reject(new Error('No se pudo generar la imagen'))
          return
        }
        const pngUrl = URL.createObjectURL(pngBlob)
        const link = document.createElement('a')
        link.href = pngUrl
        link.download = fileName
        link.click()
        URL.revokeObjectURL(pngUrl)
        resolve()
      }, 'image/png')
    }

    image.onerror = () => {
      URL.revokeObjectURL(svgUrl)
      reject(new Error('No se pudo generar la imagen'))
    }

    image.src = svgUrl
  })
}

// Lógica del comprobante: contenido del QR y descarga como PNG.
export function useTransactionReceipt(transaction) {
  const qrRef = useRef(null)
  const [downloadError, setDownloadError] = useState(null)

  const qrValue = useMemo(
    () =>
      JSON.stringify({
        reference: transaction.reference,
        sourceAccountNumber: transaction.sourceAccountNumber,
        targetAccountNumber: transaction.targetAccountNumber,
        amount: transaction.amount,
        createdAt: transaction.createdAt,
      }),
    [transaction],
  )

  async function downloadQr() {
    if (!qrRef.current) return
    setDownloadError(null)
    try {
      await downloadSvgAsPng(qrRef.current, `comprobante-${transaction.reference}.png`)
    } catch {
      setDownloadError('No se pudo descargar el QR. Intenta de nuevo.')
    }
  }

  return { qrRef, qrValue, downloadQr, downloadError }
}
