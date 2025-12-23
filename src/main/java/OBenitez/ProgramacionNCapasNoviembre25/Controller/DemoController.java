
package OBenitez.ProgramacionNCapasNoviembre25.Controller;

import javax.management.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("demo")
@Controller
public class DemoController {
    
    //Saludo
    @GetMapping("/Hola/{Nombre}")
    public String Hola(@PathVariable String Nombre, Model model){
        model.addAttribute("Nombre", Nombre);
        return "Hola";
    }
    //Calculadora
    @GetMapping("/Suma")
    public String Suma(@RequestParam int numeroUno, @RequestParam int numeroDos, Model model){
        int Resultado = numeroUno + numeroDos;
        model.addAttribute("Resultado", Resultado);
        return "Calculadora";
    }
    @GetMapping("/Resta")
    public String Resta(@RequestParam int numeroUno, @RequestParam int numeroDos, Model model){
        int Resultado = numeroUno - numeroDos;
        model.addAttribute("Resultado", Resultado);
        return "Calculadora";
    }
    @GetMapping("/Multiplicacion")
    public String Multiplicacion(@RequestParam int numeroUno, @RequestParam int numeroDos, Model model){
        int Resultado = numeroUno * numeroDos;
        model.addAttribute("Resultado", Resultado);
        return "Calculadora";
    }
    @GetMapping("/Division")
    public String Division(@RequestParam Float numeroUno, @RequestParam Float numeroDos, Model model){
        Float Resultado = numeroUno / numeroDos;
        model.addAttribute("Resultado", Resultado);
        return "Calculadora";
    }
    
    
}
