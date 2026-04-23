package todolist.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import todolist.authentication.ManagerUserSession;
import todolist.dto.UsuarioData;
import todolist.service.UsuarioService;

import javax.servlet.http.HttpSession;

@Controller
public class HomeController {

    @Autowired
    private ManagerUserSession managerUserSession;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/about")
    public String about(Model model, HttpSession session) {
        Long id = managerUserSession.usuarioLogeado();
        if (id != null){
            UsuarioData usuarioData = usuarioService.findById(id);
            model.addAttribute("usuario", usuarioData);
        }else {
            model.addAttribute("usuario", null);
        }
        return "about";
    }
}
