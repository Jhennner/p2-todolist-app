package todolist.controller;

import todolist.authentication.ManagerUserSession;
import todolist.controller.exception.UsuarioNoLogeadoException;
import todolist.dto.UsuarioData;
import todolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ManagerUserSession managerUserSession;

    /**
     * Verifica que el usuario logeado sea administrador
     */
    private void verificarAdmin(Long idUsuarioLogeado) {
        if (idUsuarioLogeado == null || !usuarioService.isAdmin(idUsuarioLogeado)) {
            throw new UsuarioNoLogeadoException();
        }
    }

    /**
     * Listado de todos los usuarios registrados
     * Solo accesible para el administrador
     */
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

    /**
     * Descripción detallada de un usuario específico
     * Solo accesible para el administrador
     */
    @GetMapping("/registered/{id}")
    public String perfilUsuario(@PathVariable(value = "id") Long idUsuario, Model model) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        verificarAdmin(idUsuarioLogeado);

        UsuarioData usuario = usuarioService.findById(idUsuario);
        if (usuario == null) {
            throw new UsuarioNoLogeadoException();
        }

        UsuarioData usuarioAdmin = usuarioService.findById(idUsuarioLogeado);
        model.addAttribute("usuario", usuarioAdmin);
        model.addAttribute("usuarioDescripcion", usuario);
        return "perfilUsuario";
    }

    /**
     * Cambiar el estado activo/inactivo de un usuario
     * Solo accesible para el administrador
     */
    @PostMapping("/registered/{id}/toggle-activo")
    public String toggleUsuarioActivo(@PathVariable(value = "id") Long idUsuario, RedirectAttributes flash) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        verificarAdmin(idUsuarioLogeado);

        UsuarioData usuario = usuarioService.findById(idUsuario);
        if (usuario == null) {
            throw new UsuarioNoLogeadoException();
        }

        usuarioService.toggleUsuarioActivo(idUsuario);
        String nuevoEstado = usuario.getActivo() ? "desactivado" : "activado";
        flash.addFlashAttribute("mensaje", "Usuario " + nuevoEstado + " correctamente");

        return "redirect:/registered";
    }
}
