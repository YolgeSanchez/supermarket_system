# 🛒 Sistema de Gestión de Supermercado (POS)

![Java](https://img.shields.io/badge/Java-21%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Swing](https://img.shields.io/badge/Java_Swing-FlatLaf-007396?style=for-the-badge&logo=java&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.4-005C84?style=for-the-badge&logo=mysql&logoColor=white)

Un sistema integral de Punto de Venta (POS) y gestión administrativa diseñado para supermercados. Este proyecto implementa una arquitectura robusta **Cliente-Servidor**, separando la lógica de negocio (Backend API REST) de la interfaz de usuario de escritorio (Frontend Swing).

---

## 🎓 Información Académica

Este proyecto fue desarrollado como trabajo final para la asignatura de **Programación I**.

* **Institución:** Instituto Tecnologico de Las Americas (ITLA)
* **Profesor:** Freidy Nuñez

### 👥 Desarrolladores

| Nombre | Matrícula |
| :--- | :--- |
| **Jorge Raynieri Sanchez Pichardo** | 2025-1023 |
| **Franciel Antonio Beltré Rodríguez** | 2025-1247 |

---

## ✨ Características Principales

### 🖥️ Cliente de Escritorio (Frontend)
* **UI/UX Moderna:** Interfaz gráfica construida con **Java Swing** y estilizada con **FlatLaf** (Soporte para temas claros/oscuros).
* **Gestión de Roles:**
    * 🛡️ **Admin:** Control total (Usuarios, Promociones, Reportes).
    * 📦 **Inventario:** Gestión de Productos y Categorías.
    * 🛒 **Cajero:** Acceso exclusivo al Punto de Venta (POS) y Clientes.
* **Punto de Venta (POS):** Interfaz optimizada con búsqueda en tiempo real, carrito de compras y cálculo automático de totales.
* **Navegación Fluida:** Sistema de menús laterales y carga asíncrona de vistas para no congelar la interfaz.

### ⚙️ Servidor (Backend API)
* **Seguridad:** Autenticación robusta mediante **JWT (JSON Web Tokens)**.
* **Arquitectura:** API RESTful escalable con Spring Boot.
* **Patrones de Diseño:** Uso de DTOs, Services, Repositories y manejo global de excepciones.
* **Base de Datos:** Relacional con MySQL y JPA/Hibernate.

---

## 📂 Estructura del Proyecto

El proyecto está dividido en dos módulos principales:

```
ROOT
├── supermarket              # MÓDULO BACKEND (Spring Boot)
│   ├── src/main/java/com/yolge/supermarket
│   │   ├── config           # Configuración de Seguridad (JWT, CORS)
│   │   ├── controller       # Endpoints REST (API)
│   │   ├── dto              # Data Transfer Objects
│   │   ├── entity           # Entidades JPA (Base de Datos)
│   │   ├── repository       # Interfaces de Acceso a Datos
│   │   └── service          # Lógica de Negocio
│   └── resources            # application.properties
│
└── supermarketClient        # MÓDULO FRONTEND (Java Swing)
    ├── src/main/java/com/yolge/client
    │   ├── core             # Cliente HTTP y Sesión
    │   ├── dto              # Espejos de los DTOs del backend
    │   ├── service          # Lógica de conexión con la API
    │   └── ui               # Vistas (Swing, MigLayout)
    │       ├── vwLogin.java
    │       ├── vwSale.java  # Punto de Venta
    │       └── ...
    └── resources            # Iconos SVG y Temas
```

---

## 🚀 Instalación y Ejecución

Sigue estos pasos para poner en marcha el sistema en tu entorno local.

### Prerrequisitos

- Java JDK 17 o superior
- Maven
- MySQL Server

### 1. Configuración de Variables de Entorno

El backend utiliza variables de entorno para la configuración. Crea un archivo `.env` en la raíz del módulo `supermarket` basándote en el archivo `.env.example`:

**Archivo `.env.example` (Backend):**
```env
MYSQL_USER=root
MYSQL_PASSWORD=tu_contraseña
DB_URL=jdbc:mysql://127.0.0.1:3306/supermarket?createDatabaseIfNotExist=true
SECRET_KEY=tu_clave_secreta_jwt_aqui
```

> 💡 **Nota:** La base de datos se creará automáticamente gracias al parámetro `createDatabaseIfNotExist=true`.

Copia este archivo y renómbralo a `.env`, luego ajusta los valores según tu configuración local:

```bash
cd supermarket
cp .env.example .env
# Edita el archivo .env con tus credenciales
```

> ⚠️ **Importante:** Asegúrate de configurar correctamente el `SECRET_KEY` con una clave segura para la generación de tokens JWT.

### 2. Ejecutar el Backend (Servidor)

Una vez configurado el archivo `.env`, levanta el servidor:

```bash
cd supermarket
./mvnw spring-boot:run
```

> 💡 El servidor iniciará en el puerto `8080` por defecto.

### 3. Ejecutar el Cliente (App de Escritorio)

Abre una nueva terminal, navega a la carpeta del cliente y ejecútalo. **Asegúrate de que el backend esté corriendo primero.**

```bash
cd supermarketClient
mvn clean compile exec:java
```

> 📌 Si usas un IDE como IntelliJ IDEA o Eclipse, puedes ejecutar directamente la clase `Main.java` o `App.java`.

---

## 📸 Capturas de Pantalla

### Login
<img width="800" alt="Login - Tema Claro" src="https://github.com/user-attachments/assets/f3c9e837-6eee-420a-8d96-7e50da18cfcc" />
<img width="800" alt="Login - Tema Oscuro" src="https://github.com/user-attachments/assets/c4644582-8d8d-456c-962e-09b302366ecd" />

### Vista de Productos
<img width="800" alt="Gestión de Productos" src="https://github.com/user-attachments/assets/170de67f-d11d-4e0d-9676-53d8be4f18d7" />
<img width="800" alt="Agregar Producto" src="https://github.com/user-attachments/assets/72dd9489-666c-4426-8823-e8fd2a1bcfd8" />
<img width="800" alt="Gestión de Categorías" src="https://github.com/user-attachments/assets/0dcbae07-8b4d-43c5-8fa0-bb9dfed219b3" />
<img width="800" alt="Agregar Categoría" src="https://github.com/user-attachments/assets/a2e11a99-9bf5-40be-9000-48f55d51e3d6" />
<img width="800" alt="Vista de Clientes" src="https://github.com/user-attachments/assets/eff5ba07-d601-460b-bb12-25fd47944ce9" />

### Vista de Promociones
<img width="800" alt="Gestión de Promociones" src="https://github.com/user-attachments/assets/b9b73c32-ebc0-41ee-9580-a97b8f56d116" />

### Punto de Venta (POS)
<img width="800" alt="Interfaz POS - Carrito Vacío" src="https://github.com/user-attachments/assets/ff423c75-891b-446d-92e0-6fcd819baf95" />
<img width="800" alt="Interfaz POS - Con Productos" src="https://github.com/user-attachments/assets/47057b3c-711e-4874-8da0-dfc64c89747f" />

### Gestión de Usuarios
<img width="800" alt="Lista de Usuarios" src="https://github.com/user-attachments/assets/1496ab37-d099-416b-803f-a4452c54b7f2" />
<img width="800" alt="Crear Usuario" src="https://github.com/user-attachments/assets/ec28ef59-2941-4bdc-aed1-4bc07ae16a8a" />
<img width="800" alt="Editar Usuario" src="https://github.com/user-attachments/assets/14398976-f99d-476e-90d5-0d58eab64b28" />


---

## 🛠️ Tecnologías

- **Lenguaje:** Java
- **Frameworks:** Spring Boot, Hibernate, Java Swing
- **Librerías UI:** FlatLaf, MigLayout, Raven SwingPack
- **Herramientas:** Maven, Lombok, Jackson, Java HTTP Client
- **Base de Datos:** MySQL

---

## 👨‍💻 Desarrolladores

Desarrollado por **Jorge Raynieri Sanchez Pichardo** y **Franciel Antonio Beltré Rodríguez**

---

## 📄 Licencia

Este proyecto es de código abierto y está disponible bajo la licencia MIT.

---

⭐ Si te gusta este proyecto, no olvides darle una estrella en GitHub!
