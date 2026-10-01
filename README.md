#  Inmobiliaria - App Android de Gestión Inmobiliaria

Aplicación móvil nativa para Android desarrollada en **Java y Material Design 3**, diseñada para optimizar la gestión integral de propiedades, clientes, agentes, visitas guiadas y cierres de ventas.

---

##  Características Principales

###  1. Gestión de Propiedades
- **Catálogo Completo**: Publicación de viviendas, departamentos, terrenos y locales comerciales.
- **Filtros Avanzados**: Búsqueda por título, ubicación o estado (*Activo, Pendiente, Reservado, Vendido, Alquilado*).
- **Detalle de Rendimiento**: Métricas en tiempo real de vistas, consultas acumuladas y días activo.
- **Estados Independientes**: Diferenciación estricta entre propiedades **Vendidas** y **Alquiladas**.

###  2. Cartera de Clientes
- **Gestión de Leads**: Clasificación por interés de compra (*Comprar, Alquilar, Inversión*).
- **Galería de Fotos**: Carga de fotografía de perfil guardada de forma permanente en el almacenamiento privado del dispositivo.
- **Ficha de Contacto**: Acceso directo a teléfono/WhatsApp, correo electrónico y presupuesto configurado.

###  3. Equipo de Agentes
- **Especialidades**: Asignación por sector (*Residencial, Comercial, Oficinas*).
- **Cartera de Propiedades Asignadas**: Módulo que vincula dinámicamente en tiempo real los inmuebles gestionados por cada agente.
- **Fotografía de Perfil**: Selección nativa de foto desde la galería.

###  4. Módulo de Ventas
- **Cierre de Ventas**: Registro de propiedades vendidas con comisión calculada automáticamente.
- **Métodos de Pago**: Integración de métodos de pago (*Transferencia, Efectivo, Tarjeta, Crédito Hipotecario*) con íconos dinámicos.
- **Vista de Comprobante**: Modal interactivo para visualizar y compartir comprobantes de venta.

###  5. Agendamiento de Visitas
- **Control de Agenda**: Programación de visitas conectando propiedad, cliente interesado y agente asignado.
- **Estado de Visita**: Acción rápida para marcar visita como *"Realizada"*.

---

##  Arquitectura y Tecnologías

- **Lenguaje**: Java (Android SDK, API 24+)
- **UI Framework**: Material Components (Material Design 3) con soporte para Modo Claro forzado.
- **Base de Datos**: SQLite nativo (`DatabaseHelper`) con claves foráneas activadas y persistencia privada de archivos.
- **Carga de Imágenes**: Glide v4 con almacenamiento en directorio privado de la app (`getFilesDir()`).
- **Navegación**: Single-Activity Architecture (`MainActivity`) hosting Fragments.

---

##  Pantallas y Modales Overview

Todos los módulos integran el diseño base estandarizado con:
- **Botón Ojito **: Apertura directa del modal de resumen/detalles.
- **Botón Lápiz **: Apertura del formulario de edición.
- **Botón Tacho **: Eliminación con confirmación.
- **Botón "Cerrar"**: Salida limpia y consistente en la barra superior e inferior.

---

##  Instalación y Compilación

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/tu-usuario/Inmobiliaria.git
   ```
2. Abrir el proyecto en **Android Studio**.
3. Sincronizar Gradle (`Sync Project with Gradle Files`).
4. Ejecutar en un emulador o dispositivo físico Android (Android 7.0 / API 24 o superior).

---

##  Licencia

Este proyecto se distribuye bajo la licencia MIT.
