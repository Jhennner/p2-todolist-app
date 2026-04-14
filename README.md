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
- [http://localhost:8080/login](http://localhost:8080/login)

## Desarrollo
### Pagina About.

Para la creación de la página about se han creado lo siquiente:

- `./controller/HomeControler`: Una nueva clase clase llamada `HomeController` que accediendo al enlace [http://localhost:8080/about](http://localhost:8080/about)
devuelve una vista llamada `about.html` (`./resources/templates/about.html`)
- `./test.../controller/AboutTestPage` : Test que asegura que cuando un usuario accede a la URL `/about` de la aplicación, 
el controlador encargado responde con éxito y el contenido de la página menciona el nombre del proyecto, "ToDoList".

Nota: Se ha agregado en el archivo `pom.xml` la siguiente dependecia para que la lógica barra menu funcione:
```
<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity6</artifactId>
    <version>3.1.2.RELEASE</version>
</dependency>
```