import axios from 'axios'

const CONNECTION_ERROR_MESSAGE =
  'No se pudo conectar con el servidor. Verifica tu conexión e intenta de nuevo.'

// Instancia compartida de Axios: todos los servicios la usan.
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  headers: { 'Content-Type': 'application/json' },
  timeout: 10000,
})

// Convierte cualquier error HTTP en un Error con el mensaje del backend
// (ErrorResponse { message, timestamp }) o, si no hubo respuesta, un mensaje de conexión.
// Conserva el código HTTP en error.status para que los hooks puedan distinguir casos (404, 409...).
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status
    const message = error.response
      ? (error.response.data?.message ?? `Error inesperado del servidor (${status})`)
      : CONNECTION_ERROR_MESSAGE

    const apiError = new Error(message, { cause: error })
    apiError.status = status
    return Promise.reject(apiError)
  },
)

export default apiClient
