# Buscaminas (Minesweeper) - Android App

Esta es una aplicación móvil de Buscaminas desarrollada en **Kotlin** utilizando Jetpack Compose para la interfaz gráfica. La aplicación incluye varias características avanzadas como persistencia de estado, múltiples niveles de dificultad, guardado y carga de partidas en diferentes formatos, y un sistema de puntuación.

## Características

### 🎮 Funcionalidades del Juego
1. **Persistencia de Estado**: El juego guarda automáticamente el estado actual para poder retomarlo más tarde.
2. **Sistema de Puntuación**: Calcula la puntuación basada en el tiempo, dificultad y progreso.
3. **Niveles de Dificultad**:
   - Fácil (8x8)
   - Difícil (15x15)
4. **Temporizador Activo**: Registra y muestra el tiempo transcurrido del juego en tiempo real.

### 💾 Gestión de Archivos
1. **Formatos de Guardado**:
   - XML
   - JSON
2. **Selección de Formato**: Los usuarios pueden elegir el formato de guardado preferido desde el menú de opciones.
3. **Carga de Partidas**: Carga partidas guardadas en cualquiera de los formatos.
4. **Exportación de Partidas**: Exporta partidas guardadas a almacenamiento externo para compartir.

### 🖥️ Interfaz de Usuario
1. **Menú Principal**:
   - Nueva Partida
   - Cargar Partida
   - Opciones
2. **Pantalla de Juego**:
   - Tablero interactivo con detección de minas.
   - Indicador de puntuación, tiempo y nivel.
   - Modo bandera para marcar minas.
3. **Gestión de Partidas Guardadas**:
   - Muestra una lista con metadatos de las partidas guardadas (puntuación, nivel, formato, etc.).
   - Permite eliminar y exportar partidas guardadas.
4. **Menú de Opciones**:
   - Selección del formato de guardado preferido (XML o JSON).
   - Visualización de instrucciones del juego.

### 🛠️ Tecnologías Utilizadas
- **Kotlin**: Lenguaje de programación principal.
- **Jetpack Compose**: Framework para la interfaz de usuario.
- **Coroutines**: Manejo de operaciones asíncronas.
- **Material Design 3**: Diseño basado en las últimas guías de Material Design.
- **FileProvider**: Para exportar y compartir archivos guardados.

---

## Estructura del Proyecto

```plaintext
app/
├── src/main/
│   ├── java/com/example/minesweeper/
│   │   ├── data/                # Modelos de datos (GameState, Cell, SavedGame, etc.)
│   │   ├── logic/               # Lógica del juego (GameLogic, MineGenerator, GameTimer)
│   │   ├── persistence/         # Gestión de almacenamiento (XML, JSON)
│   │   ├── ui/                  # Pantallas y componentes de la interfaz de usuario
│   │   │   ├── components/      # Componentes reutilizables (GameBoard, CellView)
│   │   │   ├── screens/         # Pantallas principales (GameScreen, OptionsScreen, etc.)
│   │   ├── MainActivity.kt      # Actividad principal
│   │   ├── MinesweeperApp.kt    # Navegación de la aplicación
│   └── res/
├── build.gradle
└── README.md                    # Este archivo
```

---

## Instrucciones de Instalación

### 1. Requisitos Previos
- **Android Studio**: Versión 2022.1 o superior.
- **SDK de Android**: Configurado para API 24 (Android 7.0) o superior.

### 2. Clonar el Repositorio
```bash
git clone https://github.com/DenovanMonroy/minesweeper.git
cd minesweeper
```

### 3. Configurar el Entorno
1. Abre el proyecto en **Android Studio**.
2. Sincroniza las dependencias de Gradle.

### 4. Ejecutar la Aplicación
1. Conecta un dispositivo Android o usa un emulador configurado.
2. Haz clic en el botón **Run** en Android Studio.

---

## Uso de la Aplicación

### Menú Principal
1. Selecciona `Nueva Partida` para comenzar un juego nuevo.
2. Selecciona `Cargar Partida` para cargar una partida guardada.
3. Accede a `Opciones` para configurar el formato de guardado.

### Durante el Juego
- **Revela celdas** tocando en el tablero.
- **Marca minas** activando el modo bandera (icono de bandera en la barra superior).
- Gana el juego revelando todas las celdas sin minas.

### Guardado y Exportación
- Guarda tu partida en cualquier momento desde el botón `Guardar`.
- Exporta partidas a almacenamiento externo desde la pantalla de `Partidas Guardadas`.

---

## Capturas de Pantalla

### Menú Principal
![image](https://github.com/user-attachments/assets/d7f08e0b-c472-4cda-a526-7440ca8d53a2)

### Pantalla de Juego
![image](https://github.com/user-attachments/assets/97d2957f-8608-4c91-b6de-24a9f7bf7952)

### Partidas Guardadas
![image](https://github.com/user-attachments/assets/2c3205f4-f6ea-459a-90a2-8ae177b531bf)

![image](https://github.com/user-attachments/assets/d3e9cf33-38a4-4dc6-a96a-904ee39b5ef9)


### Opciones

![image](https://github.com/user-attachments/assets/519cd99d-c4e0-457c-8ee1-88af653d1427)


---

