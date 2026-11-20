# Reglas del frontend: React + Vite (JavaScript)

Estas reglas aplican **siempre** a todo el código del frontend (`frontend/`). Si una petición entra en conflicto con ellas, señálalo antes de escribir código.

## Idioma

- Componentes, hooks, funciones, variables, props, archivos y clases CSS en **inglés**.
- Textos visibles de la interfaz (etiquetas, botones, mensajes, errores) en **español**.

## Estructura

- `src/services`: llamadas HTTP con Axios sobre la instancia compartida `apiClient.js`; **un archivo por recurso** (`accountService.js`, `transferService.js`, `transactionService.js`). Los componentes y páginas **nunca** importan `axios` directamente. Los servicios devuelven `response.data`; los errores llegan como `Error` con el `message` del backend y el código HTTP en `error.status`.
- `src/hooks`: un hook por componente o página con lógica (`useTransferForm.js`, `useAccounts.js`).
- `src/components`: componentes reutilizables, **una carpeta por componente**.
- `src/pages`: pantallas completas (una carpeta por página, con la misma separación en 3 archivos).
- `src/styles`: solo estilos globales (variables CSS y reset).
- `src/utils`: funciones puras (formato de moneda COP y de fechas).

## Componentes

- Cada componente se separa en **3 archivos**:
  - lógica: hook en `src/hooks` (estado, efectos, llamadas a servicios, manejadores);
  - vista: `Component.jsx`, solo JSX que recibe datos y callbacks del hook;
  - estilos: `Component.css` propio, con clases prefijadas por el nombre del componente (`transfer-form__input`, `account-card--selected`).
- Toda vista con datos remotos cubre los tres estados: **carga**, **error con botón "Reintentar"** y **lista vacía**.
- Accesibilidad básica: cada campo tiene `<label htmlFor>`; el error de un campo se asocia con `aria-describedby` y el campo se marca con `aria-invalid`.

## Dinero

- Los montos se muestran con `Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP' })`, usando `formatCurrency` de `src/utils/format.js`.
- Los montos se envían al backend como número o string decimal tal como los escribió el usuario. **Nunca** se calculan saldos ni montos con operaciones de punto flotante en el front; los cálculos los hace el backend con `BigDecimal`.

## Configuración

- La URL del backend sale de `import.meta.env.VITE_API_URL` (incluye `/api`). Nunca se escribe una URL fija en el código.
- `.env` no se sube al repositorio; `.env.example` sí, con los valores de ejemplo.
- Antes de terminar una tarea: `npm run lint` y `npm run build` sin errores.

## Estructura actual del frontend

Vite 8 + React 19 (JavaScript), lint con oxlint, servidor en el puerto 3000 con `strictPort`. Dependencias: `axios`, `qrcode.react`.

```
frontend/
├── CLAUDE.md
├── .env                    # no versionado
├── .env.example
├── index.html
├── package.json
├── vite.config.js          # puerto 3000, strictPort
├── public/
└── src/
    ├── main.jsx
    ├── App.jsx             # encabezado "Banco TdeA" + secciones Cuentas, Nueva transferencia e Historial
    ├── App.css             # clases app__*
    ├── components/
    │   ├── AccountList/
    │   │   ├── AccountList.jsx    # tarjetas de cuentas; carga, error y lista vacía
    │   │   └── AccountList.css    # clases account-list__*
    │   ├── Toast/
    │   │   ├── Toast.jsx          # aviso flotante (error/success)
    │   │   └── Toast.css          # clases toast__*
    │   ├── TransactionHistory/
    │   │   ├── TransactionHistory.jsx  # tabla, filtro por cuenta, "Ver QR"
    │   │   └── TransactionHistory.css  # clases transaction-history__*
    │   ├── TransactionReceipt/
    │   │   ├── TransactionReceipt.jsx  # comprobante modal con QR, "Descargar QR" y "Cerrar"
    │   │   └── TransactionReceipt.css  # clases transaction-receipt__*
    │   └── TransferForm/
    │       ├── TransferForm.jsx   # selects origen/destino, monto, descripción
    │       └── TransferForm.css   # clases transfer-form__*
    ├── hooks/
    │   ├── useAccounts.js         # accounts, isLoading, error, refresh()
    │   ├── useToast.js            # toast, showToast, closeToast; cierre a los 4 s
    │   ├── useTransactionHistory.js  # historial, filtro, refresh(), comprobante seleccionado
    │   ├── useTransactionReceipt.js  # qrValue (JSON) y descarga del QR como PNG
    │   └── useTransferForm.js     # valores, validación, envío, comprobante y toast
    ├── services/
    │   ├── apiClient.js           # instancia Axios compartida + interceptor de errores
    │   ├── accountService.js      # getAll, getByNumber, create
    │   ├── transactionService.js  # getAll(accountNumber?), getByReference
    │   └── transferService.js     # transfer
    ├── styles/
    │   └── global.css
    └── utils/
        ├── decimal.js      # normalizeAmount, toCents, compareAmounts (BigInt, sin float)
        └── format.js       # formatCurrency (COP, siempre 2 decimales), formatDateTime
```

**Regla de mantenimiento:** cada vez que se cree, mueva, renombre o elimine un archivo o carpeta del frontend, actualiza esta sección en el mismo cambio para que refleje la estructura real del disco.
