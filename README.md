# To Do List app

Aplicación «ToDoList» que utiliza Spring Boot y plantillas Thymeleaf. Se basa en 
presentar una lista de tareas pendientes de un usuario.
## Requisitos
Tenes que instalado en tu sistema:
- Java 8 SDK o superior.
## Ejecución
Puedes ejecutar la aplicación utilizando el objetivo `run` del _plugin_ de Maven
para Spring Boot:
```
$ ./mvn spring-boot:run 
// o solo..
$ mvn spring-boot:run
```
Ya puedes crear un archivo `jar` y ejecutarlo:
```
$ ./mvn clean package 
$  mvn clean package
```
Una vez que la aplicación esté en funcionamiento, puedes abrirlo en tu navegador favorito y conectarte:
- [http://localhost:8080/login](http://localhost:8080/login) o [http://localhost:8080](http://localhost:8080)

## Desarrollo
### Página About

Para la creación de la página about se han creado lo siguiente:

- `./controller/HomeController`: Una nueva clase llamada `HomeController` que accediendo al enlace [http://localhost:8080/about](http://localhost:8080/about)
devuelve una vista llamada `about.html` (`./resources/templates/about.html`)
- `./test.../controller/AboutTestPage` : Test que asegura que cuando un usuario accede a la URL `/about` de la aplicación, 
el controlador encargado responde con éxito y el contenido de la página menciona el nombre del proyecto, "ToDoList".

Nota: Se ha agregado en el archivo `pom.xml` la siguiente dependencia para que la lógica de la barra de menú funcione:
```xml
<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity6</artifactId>
    <version>3.1.2.RELEASE</version>
</dependency>
```

### Barra de Menú (Menu Bar)

La barra de menú es común a todas las páginas excepto las de login y registro. Se implementó usando Bootstrap Navbar.

**Componentes implementados:**

- `./resources/templates/fragments.html`: Fragmento que contiene la barra de menú reutilizable
- `./model/Usuario.java`: Se agregó la propiedad `admin` para identificar usuarios administradores

**Descripción:**
La barra de menú se muestra en la parte superior de cada página y contiene:

- **Lado izquierdo:**
  - "ToDoList": Link a la página About
  - "Tareas": Link a la lista de tareas del usuario (solo si está logeado)

- **Lado derecho:**
  - Si el usuario NO está logeado: Enlaces a "Iniciar sesión" y "Crear cuenta"
  - Si el usuario está logeado: Dropdown con el nombre del usuario que incluye:
    - "Cuenta": Placeholder para futura funcionalidad de gestión de cuenta
    - "Cerrar Sesión": Desloguea al usuario y lo redirige a login

### Listado de Usuarios (User List)

Permite visualizar todos los usuarios registrados en la aplicación. Esta funcionalidad es protegida y solo es accesible por el administrador.

**Componentes implementados:**

- `./controller/UsuarioController.java`: Controlador encargado de gestionar la lista de usuarios
- `./resources/templates/listaUsuarios.html`: Template que muestra la tabla de usuarios
- `./model/Usuario.java`: Se agregaron propiedades `admin` y `activo` para gestionar el estado del usuario
- `./repository/UsuarioRepository.java`: Se agregó método `findByAdmin()` para buscar el usuario administrador

**Endpoint:** `GET /registered`

**Funcionalidades:**
- Muestra una tabla con todos los usuarios registrados (Id, Email, Nombre, Estado)
- Muestra el estado actual de cada usuario (Activo/Inactivo)
- Botón "Ver Perfil" para acceder a los detalles completos del usuario
- Botón "Activar/Desactivar" para cambiar el estado del usuario

**Código relevante:**
```java
@GetMapping("/registered")
public String listadoUsuarios(Model model) {
    Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
    verificarAdmin(idUsuarioLogeado);
    
    List<UsuarioData> usuarios = usuarioService.findAllUsuarios();
    model.addAttribute("usuarios", usuarios);
    UsuarioData usuarioAdmin = usuarioService.findById(idUsuarioLogeado);
    model.addAttribute("usuario", usuarioAdmin);
    return "listaUsuarios";
}
```

### Descripción de Usuario (User Description)

Muestra los detalles completos de un usuario específico, excluyendo la contraseña. Esta funcionalidad es protegida y solo es accesible por el administrador.

**Componentes implementados:**

- `./controller/UsuarioController.java`: Método `perfilUsuario()` para mostrar el perfil
- `./resources/templates/perfilUsuario.html`: Template que muestra los detalles del usuario

**Endpoint:** `GET /registered/{id}`

**Datos mostrados:**
- Identificador del usuario
- Email
- Nombre
- Fecha de nacimiento
- Estado (Activo/Inactivo)
- Si es administrador (Sí/No)

**Botones de acción:**
- "Desactivar Usuario" / "Activar Usuario": Toggle del estado del usuario
- "Volver a Lista de Usuarios": Regresa al listado de usuarios

### Usuario Administrador (Admin User)

La aplicación permite registrar un usuario como administrador. Solo puede existir un administrador en la plataforma.

**Componentes modificados:**

- `./dto/RegistroData.java`: Se agregó propiedad `admin` (Boolean)
- `./model/Usuario.java`: Se agregó propiedad `admin` (Boolean, por defecto false) y `activo` (Boolean, por defecto true)
- `./controller/LoginController.java`: Se modificó para mostrar checkbox de admin en registro y redirigir al admin al listado de usuarios

**Funcionalidades:**
- En el formulario de registro, si NO existe un administrador, aparece un checkbox que dice "Registrarse como administrador"
- Si ya existe un administrador, el checkbox desaparece
- Al registrarse como administrador, el usuario es redirigido automáticamente al listado de usuarios
- Los usuarios normales son redirigidos al listado de tareas después del login

**Código relevante - Validación en servicio:**
```java
@Transactional
public UsuarioData registrar(UsuarioData usuario) {
    // ... validaciones previas ...
    if (usuario.getAdmin() != null && usuario.getAdmin()) {
        Optional<Usuario> admin = usuarioRepository.findByAdmin(true);
        if (admin.isPresent()) {
            throw new UsuarioServiceException("Ya existe un usuario administrador");
        }
    }
    // ... resto del código ...
}
```

### Protección de Listado y Descripción de Usuarios

Los endpoints `/registered` y `/registered/{id}` están protegidos para que solo el administrador pueda acceder a ellos.

**Mecanismo de protección:**

La clase `UsuarioController` implementa una verificación en todos sus métodos:

```java
private void verificarAdmin(Long idUsuarioLogeado) {
    if (idUsuarioLogeado == null || !usuarioService.isAdmin(idUsuarioLogeado)) {
        throw new UsuarioNoLogeadoException();
    }
}
```

**Comportamiento:**
- Si un usuario no autenticado intenta acceder: Se lanza `UsuarioNoLogeadoException` que devuelve HTTP 401 Unauthorized
- Si un usuario autenticado pero no administrador intenta acceder: Se lanza la misma excepción

### Bloqueo de Usuarios por Administrador

El administrador puede desactivar o activar usuarios para bloquear su acceso a la aplicación.

**Componentes implementados:**

- `./model/Usuario.java`: Se agregó propiedad `activo` (Boolean, por defecto true)
- `./service/UsuarioService.java`: Se agregaron métodos `toggleUsuarioActivo()` e `isAdmin()`
- `./repository/UsuarioRepository.java`: Se agregó método `findByAdmin()`
- `./controller/UsuarioController.java`: Endpoint POST `/registered/{id}/toggle-activo`

**Funcionalidades:**
- En el listado de usuarios (`/registered`), aparece un botón para "Activar" o "Desactivar" cada usuario
- En el perfil del usuario (`/registered/{id}`), aparece el botón para cambiar su estado
- Si un usuario desactivado intenta hacer login, recibe un mensaje de error: "Usuario desactivado. Contacta con el administrador"

**Cambios en el flujo de login:**
El servicio `UsuarioService` valida que el usuario esté activo:

```java
@Transactional(readOnly = true)
public LoginStatus login(String eMail, String password) {
    Optional<Usuario> usuario = usuarioRepository.findByEmail(eMail);
    if (!usuario.isPresent()) {
        return LoginStatus.USER_NOT_FOUND;
    } else if (!usuario.get().getPassword().equals(password)) {
        return LoginStatus.ERROR_PASSWORD;
    } else if (!usuario.get().getActivo()) {
        return LoginStatus.USER_DISABLED;
    } else {
        return LoginStatus.LOGIN_OK;
    }
}
```

**Cambios en el enum LoginStatus:**
Se agregó el estado `USER_DISABLED` para distinguir cuando un usuario está desactivado.

```java
public enum LoginStatus {LOGIN_OK, USER_NOT_FOUND, ERROR_PASSWORD, USER_DISABLED}
```

## Enlaces y Recursos

### Docker Hub

La imagen Docker de esta aplicación está disponible en Docker Hub. Para descargarla e instalarla, ejecuta el siguiente comando:

```bash
docker pull jheneralbarado/p2-todolistapp:1.1.0
```

**Enlace al repositorio Docker Hub:** [https://hub.docker.com/repository/docker/jheneralbarado/p2-todolistapp/general]

### GitHub Repository

El código fuente del proyecto se encuentra alojado en el siguiente repositorio de GitHub:

**Enlace al GitHub repositorio:** [https://github.com/Jhennner/p2-todolist-app]

### Trello

La gestión del proyecto y seguimiento de tareas se realiza mediante Trello:

**Enlace al Trello Dashboard:** [https://trello.com/invite/b/69d8262499e8db938afcb315/ATTIf7b790b9eb130f8e4d5bad4697f34a4cAC1F64C8/e2-to-do-list-app]
