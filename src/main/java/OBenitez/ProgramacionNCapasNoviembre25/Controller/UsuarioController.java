
package OBenitez.ProgramacionNCapasNoviembre25.Controller;

import OBenitez.ProgramacionNCapasNoviembre25.ML.Colonia;
import OBenitez.ProgramacionNCapasNoviembre25.ML.Direccion;
import OBenitez.ProgramacionNCapasNoviembre25.ML.ErrorCarga;
import OBenitez.ProgramacionNCapasNoviembre25.ML.Pais;
import OBenitez.ProgramacionNCapasNoviembre25.ML.Result;
import OBenitez.ProgramacionNCapasNoviembre25.ML.Rol;
import OBenitez.ProgramacionNCapasNoviembre25.ML.Usuario;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("usuario")
public class UsuarioController {

    private static final String urlBase = "http://localhost:8080/api";
    
    @GetMapping
    public String GetAll(Model model){ 
        RestTemplate restTemplate = new RestTemplate(); 

        try {
            ResponseEntity<Result<Usuario>> responseEntity = restTemplate.exchange(urlBase + "/usuario",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<Result<Usuario>>() {
            });

            ResponseEntity<Result<Rol>> responseEntityRoles = restTemplate.exchange(urlBase + "/rol",
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<Result<Rol>>() {
            });

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                Result result = responseEntity.getBody();
                model.addAttribute("Usuarios", result.Objects);
            } else{
                model.addAttribute("Usuarios", new ArrayList<>());
            }
            if (responseEntityRoles.getStatusCode().is2xxSuccessful()) {
                Result resultRoles = responseEntityRoles.getBody();
                model.addAttribute("Roles", resultRoles.Objects);
            } else{
                model.addAttribute("Roles", new ArrayList<>());
            }
            
            model.addAttribute("usuarioBusqueda", new Usuario());
        } catch (Exception ex) {
            model.addAttribute("Usuarios", new ArrayList<>());
            model.addAttribute("Roles", new ArrayList<>());
            model.addAttribute("usuarioBusqueda", new Usuario());
        }

        return "Index";
    }
    
    @PostMapping("busqueda")
    public String Busqueda(@ModelAttribute("usuario") Usuario usuario, Model model){
        Result result = new Result();
        RestTemplate restTemplate = new RestTemplate(); 

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            HttpEntity<Usuario> requestEntity = new HttpEntity<>(usuario, headers);

            ResponseEntity<Result<Usuario>> responseEntityBusqueda = restTemplate.exchange(
                    urlBase + "/usuario/busqueda",
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<Result<Usuario>>() {}
            );
            
            ResponseEntity<Result<Rol>> responseEntityRoles = restTemplate.exchange(urlBase + "/rol",
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<Result<Rol>>() {
            });
            
            if (responseEntityBusqueda.getStatusCode().is2xxSuccessful()) {
                Result resultBusqueda = responseEntityBusqueda.getBody();
                model.addAttribute("Usuarios", resultBusqueda.Objects);
            } else{
                model.addAttribute("Usuarios", new ArrayList<>());
            }
            if (responseEntityRoles.getStatusCode().is2xxSuccessful()) {
                Result resultRoles = responseEntityRoles.getBody();
                model.addAttribute("Roles", resultRoles.Objects);
            } else{
                model.addAttribute("Roles", new ArrayList<>());
            }
            
            model.addAttribute("usuarioBusqueda", usuario);
        } catch (Exception ex) {
            result.Correct = false;
            result.ErrorMessage = ex.getLocalizedMessage();
            result.Object = "No pudo procesarse la busqueda";
            result.ex = ex;
            model.addAttribute("Usuarios", new ArrayList<>());
            model.addAttribute("Roles", new ArrayList<>());
            model.addAttribute("usuarioBusqueda", new Usuario());
        }
        
        return "Index";
    }
    
    @GetMapping("detail/{IdUsuario}")
    public String Detail(@PathVariable("IdUsuario") int IdUsuario, Model model){
    
        RestTemplate restTemplate = new RestTemplate(); 

        try {
            ResponseEntity<Result<Usuario>> responseEntity = restTemplate.exchange(urlBase + "/usuario/" + IdUsuario,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<Result<Usuario>>() {
            });

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                Result result = responseEntity.getBody();
                model.addAttribute("Usuario", result.Object);
                try {
                    ResponseEntity<Result<Rol>> responseEntityRoles = restTemplate.exchange(urlBase + "/rol",
                        HttpMethod.GET,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<Result<Rol>>() {
                    });
                    ResponseEntity<Result<Pais>> responseEntityPais = restTemplate.exchange(urlBase + "/pais",
                        HttpMethod.GET,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<Result<Pais>>() {
                    });
                    
                    if (responseEntityRoles.getBody() != null) {
                        model.addAttribute("Roles", responseEntityRoles.getBody().Objects);
                    }
                    if (responseEntityPais.getBody() != null) {
                        model.addAttribute("Paises", responseEntityPais.getBody().Objects);
                    }
                } catch (Exception ex) {
                    model.addAttribute("Roles", new ArrayList<>());
                    model.addAttribute("Paises", new ArrayList<>());
                }
            } 
        } catch (Exception ex) {
            return "redirect:/usuario";
        }
        return "UsuarioDetail";
    }
    
    @GetMapping("deleteAddress/{IdDireccion}/{IdUsuario}")
    public String DeleteAddress(@PathVariable("IdDireccion") int IdDireccion, @PathVariable("IdUsuario") int IdUsuario, RedirectAttributes redirectAttributes){
        RestTemplate restTemplate = new RestTemplate(); 
        Result result = new Result();
        try {
            ResponseEntity<Result<Direccion>> responseEntityDeleteAddress = restTemplate.exchange(urlBase + "/direccion/" + IdDireccion,
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<Result<Direccion>>() {
            });
        
            if (responseEntityDeleteAddress.getStatusCode().is2xxSuccessful()) {
                result.Correct = true;
                result.Object = "La direccion fue eliminada";
            } else {
                result.Correct = false;
                result.Object = "No fue posible eliminar la direccion :c";
            }
        } catch (Exception ex) {
            result.Correct = false;
            result.Object = ex.getLocalizedMessage();
            result.ex = ex;
        }
        
        redirectAttributes.addFlashAttribute("resultDeleteAddress", result);
        return "redirect:/usuario/detail/"+IdUsuario;
    }
    
    @PostMapping("/updatePhoto")
    public String updatePhoto(@ModelAttribute Usuario usuario,
                              @RequestParam("imagenUsuario") MultipartFile imagenUsuario,
                              RedirectAttributes redirectAttributes) {
        RestTemplate restTemplate = new RestTemplate(); 
        Result result = new Result();
        
        
        if (imagenUsuario.isEmpty()) {
            result.Correct = false;
            result.Object = "No se seleccionó ninguna imagen";
        } else {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            try {
                String encodedString = Base64.getEncoder().encodeToString(imagenUsuario.getBytes());
                HttpEntity<String> requestEntity = new HttpEntity<>(encodedString, headers);

                ResponseEntity<Result> responseEntityUpdatePhoto = restTemplate.exchange(
                        urlBase + "/usuario/"+usuario.getIdUsuario()+"/updatePhoto",
                        HttpMethod.POST,
                        requestEntity,
                        new ParameterizedTypeReference<Result>() {}
                );
                if (responseEntityUpdatePhoto.getStatusCode().is2xxSuccessful()) {
                    result.Correct = true;
                    result.Object = "Se actualizó correctamente la foto";
                } else {
                    result.Correct = false;
                    result.Object = "No se pudo actualizar la foto :c";
                }
            } catch (IOException ex) {
                result.Correct = false;
                result.ErrorMessage = ex.getLocalizedMessage();
                result.Object = "No pudo procesarse la imagen";
                result.ex = ex;
            }
        }
        redirectAttributes.addFlashAttribute("resultUpdatePhoto", result);
        return "redirect:/usuario/detail/" + usuario.getIdUsuario();
    }

    @PostMapping("deletePhoto/{IdUsuario}")
    public String deletePhoto(@PathVariable int IdUsuario, RedirectAttributes redirectAttributes) {
        RestTemplate restTemplate = new RestTemplate(); 
        Result result = new Result();

        try {
            ResponseEntity<Result> responseEntityDeletePhoto = restTemplate.exchange(
                    urlBase + "/usuario/" + IdUsuario + "/photo",
                    HttpMethod.DELETE,
                    null,
                    new ParameterizedTypeReference<Result>() {}
            );
            if (responseEntityDeletePhoto.getStatusCode().is2xxSuccessful()) {
                result.Correct = true;
                result.Object = "Se eliminó correctamente la foto";
            } else {
                result.Correct = false;
                result.Object = "No se pudo eliminar la foto :c";
            }
        } catch (Exception ex) {
            result.Correct = false;
            result.ErrorMessage = ex.getLocalizedMessage();
            result.Object = "No se pudo eliminar la foto :c";
            result.ex = ex;
        }

        redirectAttributes.addFlashAttribute("resultUpdatePhoto", result);
        return "redirect:/usuario/detail/" + IdUsuario;
    }
    
    @GetMapping("form")
    public String Form(Model model){
        RestTemplate restTemplate = new RestTemplate(); 
        
        ResponseEntity<Result<Rol>> responseEntityRoles = restTemplate.exchange(urlBase + "/rol",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<Result<Rol>>() {
        });
        ResponseEntity<Result<Pais>> responseEntityPaises = restTemplate.exchange(urlBase + "/pais",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<Result<Pais>>() {
        });
        
        Result resultRoles = responseEntityRoles.getBody();
        model.addAttribute("Roles", resultRoles.Objects);
        
        Result resultPais = responseEntityPaises.getBody();
        model.addAttribute("Paises", resultPais.Objects);
        
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(0);
        model.addAttribute("Usuario", usuario);
     
        return "UsuarioForm";
    }
    
    @PostMapping("add")
    public String Add(Model model, @ModelAttribute("Usuario") Usuario usuario, @RequestParam("imagenUsuario") MultipartFile imagenUsuario, RedirectAttributes redirectAttributes) throws IOException{
        RestTemplate restTemplate = new RestTemplate(); 
        Result result = new Result();
        // AGREGAR USUARIO FULL INFO
        if (imagenUsuario.isEmpty()) {
            usuario.setImagen(null);
        } else {
            String encodedString = Base64.getEncoder().encodeToString(imagenUsuario.getBytes());
            usuario.setImagen(encodedString);
        }
        usuario.setStatus(1);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Usuario> requestEntity = new HttpEntity<>(usuario, headers);

            ResponseEntity<Result> responseEntityAddUser = restTemplate.exchange(
                    urlBase + "/usuario",
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<Result>() {}
            );
            Result resultAddUser = responseEntityAddUser.getBody();

            if (resultAddUser != null && responseEntityAddUser.getStatusCode().is2xxSuccessful()) {
                result.Correct = true;
                result.Object = "El usuario se agregó correctamente";
            } else {
                result.Correct = false;
                result.Object = "No fue posible agregar al usuario";
            }
        } catch (Exception ex) {
            result.Correct = false;
            result.Object = "No fue posible agregar al usuario";
            result.ErrorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }
        redirectAttributes.addFlashAttribute("resultAddUserFull", result);
        return "redirect:/usuario";
    }
    
    @PostMapping("formEditable")
    public String Form(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes){
        RestTemplate restTemplate = new RestTemplate(); 
        if (usuario.getDirecciones().get(0).getIdDireccion() == -1) {
            //ACTUALIZAR USUARIO
            Result result = new Result();
            usuario.Direcciones.remove(0);
            
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Usuario> requestEntity = new HttpEntity<>(usuario, headers);

                ResponseEntity<Result> responseEntityUpdateUser = restTemplate.exchange(
                        urlBase + "/usuario",
                        HttpMethod.PUT,
                        requestEntity,
                        new ParameterizedTypeReference<Result>() {}
                );
                Result resultUpdateUser = responseEntityUpdateUser.getBody();

                if (resultUpdateUser != null && resultUpdateUser.Correct) {
                    result.Correct = true;
                    result.Object = "El usuario se actualizó correctamente";
                } else {
                    result.Correct = false;
                    result.Object = "No fue posible actualizar al usuario";
                }
            } catch (Exception ex) {
                result.Correct = false;
                result.Object = "No fue posible actualizar al usuario";
                result.ErrorMessage = ex.getLocalizedMessage();
                result.ex = ex;
            }

            redirectAttributes.addFlashAttribute("resultEditUserBasic", result);
            return "redirect:/usuario/detail/" + usuario.getIdUsuario();
        }else if(usuario.Direcciones.get(0).getIdDireccion() == 0){
//            AGREGA UNA DIRECCION NUEVA
            Result result = new Result();
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                Direccion direccionNueva = usuario.Direcciones.get(0);
                direccionNueva.Usuario = new Usuario();
                direccionNueva.Usuario.setIdUsuario(usuario.getIdUsuario());
                HttpEntity<Direccion> requestEntity = new HttpEntity<>(direccionNueva, headers);

                ResponseEntity<Result> responseEntityAddAddress = restTemplate.exchange(
                        urlBase + "/direccion",
                        HttpMethod.POST,
                        requestEntity,
                        new ParameterizedTypeReference<Result>() {}
                );
                Result resultAddAddress = responseEntityAddAddress.getBody();

                if (resultAddAddress != null && responseEntityAddAddress.getStatusCode().is2xxSuccessful()) {
                    result.Correct = true;
                    result.Object = "La direccion se agregó correctamente";
                } else {
                    result.Correct = false;
                    result.Object = "No fue posible agregar la direccion";
                }
            } catch (Exception ex) {
                result.Correct = false;
                result.Object = "No fue posible agregar la direccion";
                result.ErrorMessage = ex.getLocalizedMessage();
                result.ex = ex;
            }
            redirectAttributes.addFlashAttribute("resultAddAddress", result);
            return "redirect:/usuario/detail/"+usuario.getIdUsuario();
        }else{
//            ACTUALIZA UNA DIRECCION           
            Result result = new Result();
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                Direccion dirreccion = usuario.Direcciones.get(0);
                HttpEntity<Direccion> requestEntity = new HttpEntity<>(dirreccion, headers);

                ResponseEntity<Result> responseEntityUpdateAddress = restTemplate.exchange(
                        urlBase + "/direccion",
                        HttpMethod.PUT,
                        requestEntity,
                        new ParameterizedTypeReference<Result>() {}
                );
                Result resultUpdateAddress = responseEntityUpdateAddress.getBody();

                if (resultUpdateAddress != null && resultUpdateAddress.Correct) {
                    result.Correct = true;
                    result.Object = "La direccion se actualizó correctamente";
                } else {
                    result.Correct = false;
                    result.Object = "No fue posible actualizar la direccion";
                }
            } catch (Exception ex) {
                result.Correct = false;
                result.Object = "No fue posible actualizar la direccion";
                result.ErrorMessage = ex.getLocalizedMessage();
                result.ex = ex;
            }
            redirectAttributes.addFlashAttribute("resultEditAddress", result);
            return "redirect:/usuario/detail/"+usuario.getIdUsuario();
        }
    }
    
 
    
//    @GetMapping("CargaMasiva")
//    public String CargaMsiva(){
//        return "CargaMasiva";
//    }
//    
//    @PostMapping("CargaMasiva")
//    public String CargaMasiva(@ModelAttribute MultipartFile archivo, Model model, HttpSession sesion) throws IOException {
//    
//        String extension = archivo.getOriginalFilename().split("\\.")[1];
//        
//        String path = System.getProperty("user.dir");
//        String pathArchivo = "src/main/resources/archivos";
//        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
//        
//        String rutaAbsoluta = path + "/" + pathArchivo + "/" + fecha + archivo.getOriginalFilename();
//        
//        archivo.transferTo(new File(rutaAbsoluta));
//        
//        List<Usuario> usuarios = new ArrayList<>();
//        
//        if (extension.equals("txt")) {
//            usuarios = LecturaArchivo(new File(rutaAbsoluta));
//        } else {
//            usuarios = LecturaArchivoExcel(new File(rutaAbsoluta));    
//        }
//        
//        List<ErrorCarga> errores = ValidarDatos(usuarios);
//        
//        if (errores != null && !errores.isEmpty()) {
//            model.addAttribute("errores", errores);
//            model.addAttribute("tieneErrores", true);
//        } else {
//            model.addAttribute("mensajeExito", "Carga exitosa. Se cargaron " + usuarios.size() + " usuario(s) correctamente");
//            model.addAttribute("tieneErrores", false);
//            
//            sesion.setAttribute("archivoCargaMasiva", rutaAbsoluta);
//        }
//        
//        return "CargaMasiva";
//    }
//
//    private List<Usuario> LecturaArchivo(File archivo) {
//        
//        List<Usuario> usuarios = new ArrayList<>();
//        
//        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(archivo))){
//            
//            bufferedReader.readLine();
//            String line;
//            
//            while ((line = bufferedReader.readLine()) != null) {                
//                
//                String[] datos = line.split("\\|");
//                
//                Usuario usuario = new Usuario();
//                usuario.setUsername(datos[0]);
//                usuario.setNombre(datos[1]);
//                usuario.setApellidoPaterno(datos[2]);
//                usuario.setApellidoMaterno(datos[3]);
//                usuario.setEmail(datos[4]);
//                usuario.setPassword(datos[5]);
//                usuario.setFechaNacimiento(java.sql.Date.valueOf(datos[6]));
//                usuario.setSexo(datos[7]);
//                usuario.setTelefono(datos[8]);
//                usuario.setCelular(datos[9]);
//                usuario.setCurp(datos[10]);
//                
//                //Direccion
//                usuario.Rol = new Rol();
//                usuario.Rol.setIdRol(Integer.parseInt(datos[11]));
//                
//                //DIRECCION
//                usuario.Direcciones = new ArrayList<>();
//                Direccion Direccion = new Direccion();
//                Direccion.setCalle(datos[12]);
//                Direccion.setNumeroExterior(datos[13]);
//                Direccion.setNumeroInterior(datos[14]);
//                usuario.Direcciones.add(Direccion);
//                
//                Direccion.Colonia = new Colonia();
//                Direccion.Colonia.setIdColonia(Integer.parseInt(datos[15]));
//                
//                usuarios.add(usuario);
//            }
//        }
//        catch(Exception ex){
//            usuarios = null;
//        }
//        
//        return usuarios;
//    }
//
//    private List<Usuario> LecturaArchivoExcel(File archivo) {
//        
//        List<Usuario> usuarios = new ArrayList<>();
//        
//         try (XSSFWorkbook workbook = new XSSFWorkbook(archivo)) {
//             
//            XSSFSheet sheet = workbook.getSheetAt(0);
//
//            for (Row row : sheet) {
//                
//                Usuario usuario = new Usuario();
//                Cell cell0 = row.getCell(0);
//                if (cell0 != null) {
//                    usuario.setUsername(row.getCell(0).toString());
//                } else {
//                    continue;
//                }
//                
//                usuario.setNombre(row.getCell(1).toString());
//                usuario.setApellidoPaterno(row.getCell(2).toString());
//                usuario.setApellidoMaterno(row.getCell(3).toString());
//                usuario.setEmail(row.getCell(4).toString());
//                usuario.setPassword(row.getCell(5).toString());
//                
//                java.util.Date utilDate = row.getCell(6).getDateCellValue();
//                java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
//                usuario.setFechaNacimiento(sqlDate);
//                
//                usuario.setSexo(row.getCell(7).toString());
//                usuario.setCelular(row.getCell(8).toString());
//                usuario.setTelefono(row.getCell(9).toString());
//                usuario.setCurp(row.getCell(10).toString());
//                
//                usuario.Rol = new Rol();
//                usuario.Rol.setIdRol((int) row.getCell(11).getNumericCellValue());
//                //DIRECCION
//                usuario.Direcciones = new ArrayList<>();
//                Direccion Direccion = new Direccion();
//                Direccion.setCalle(row.getCell(12).toString());
//                Direccion.setNumeroExterior(row.getCell(13).toString());
//                Direccion.setNumeroInterior(row.getCell(14).toString());
//                usuario.Direcciones.add(Direccion);
//                
//                Direccion.Colonia = new Colonia();
//                Direccion.Colonia.setIdColonia((int) row.getCell(15).getNumericCellValue());
//                
//                usuarios.add(usuario);
//            }
//            
//        } catch (Exception ex) {
//            usuarios = null;
//        }
//        
//        return usuarios;
//    }
//    
//    private List<ErrorCarga> ValidarDatos(List<Usuario> usuarios) {
//        
//        List<ErrorCarga> erroresCarga = new ArrayList<>();
//        int LineaError = 0;
//        
//        for (Usuario usuario : usuarios) {
//            LineaError++;
//            BindingResult bindingResult = validationService.validateObjects(usuario);
//            List<ObjectError> errors = bindingResult.getAllErrors();
//            
//            if (usuario.Rol == null) {
//                usuario.Rol = new Rol();
//            }
//            if (usuario.Direcciones == null) {
//                usuario.Direcciones = new ArrayList<>();
//                Direccion Direccion = new Direccion();
//                usuario.Direcciones.add(Direccion);
//            }
//            
//            BindingResult bindingResultRol = validationService.validateObjects(usuario.Rol);
//            List<ObjectError> errorsRol = bindingResultRol.getAllErrors();
//            
//            BindingResult bindingResultDireccion = validationService.validateObjects(usuario.Direcciones.get(0));
//            List<ObjectError> errorsDireccion = bindingResultDireccion.getAllErrors();
//            
//            List<ObjectError> listaCombinada = new ArrayList<>(errors);
//            
//            if (!errorsRol.isEmpty()) { 
//                listaCombinada.addAll(errorsRol);
//            }
//            if (!errorsDireccion.isEmpty()) { 
//                listaCombinada.addAll(errorsDireccion);
//            }
//            
//            for (ObjectError error : listaCombinada) {
//                FieldError fieldError = (FieldError) error;
//                ErrorCarga errorCarga = new ErrorCarga();
//                errorCarga.Linea = LineaError;
//                errorCarga.Campo = fieldError.getField();
//                errorCarga.Descripcion = fieldError.getDefaultMessage();
//                
//                erroresCarga.add(errorCarga);
//            }
//        }
//        
//        return erroresCarga;
//    }
//    
//    @GetMapping("CargaMasiva/procesar")
//    public String ProcesarArchivo(HttpSession sesion, Model model, RedirectAttributes redirectAttributes){
//    
//        String rutaArchivo = sesion.getAttribute("archivoCargaMasiva").toString();
//        
//        File archivo = new File(rutaArchivo);
//        String nombreArchivo = archivo.getName();
//        String extension = nombreArchivo.split("\\.")[1];
//        List<Usuario> usuarios = new ArrayList<>();
//        
//        if (extension.equals("txt")) {
//            usuarios = LecturaArchivo(archivo);
//        } else {
//            usuarios = LecturaArchivoExcel(archivo);  
//        }
//        
//        if (usuarios != null && !usuarios.isEmpty()) {
//            
//            List<OBenitez.ProgramacionNCapasNoviembre25.JPA.Usuario> usuariosJPA = new ArrayList<>();
//            for (Usuario usuario : usuarios) {
//                ModelMapper modelMapper = new ModelMapper();
//                OBenitez.ProgramacionNCapasNoviembre25.JPA.Usuario usuarioJPA = modelMapper.map(usuario, OBenitez.ProgramacionNCapasNoviembre25.JPA.Usuario.class);
//                usuariosJPA.add(usuarioJPA);
//            }
//            
//            Result result = usuarioJPADAOImplementation.AddAll(usuariosJPA);
//            sesion.removeAttribute("archivoCargaMasiva");
//            
//            if(result.Correct){
//                result.Object = "Se agregó " + usuarios.size() + " usuario(s) nuevo(s)";
//            } else{
//                result.Object = "No fue posible agregar a los usuarios :c";
//            }
//            redirectAttributes.addFlashAttribute("resultCargaMasiva", result);
//            
//            return "redirect:/Usuario";
//        } else {
//            
//            return "redirect:/Usuario/CargaMasiva";
//        }
//    }
//    
//    
}
