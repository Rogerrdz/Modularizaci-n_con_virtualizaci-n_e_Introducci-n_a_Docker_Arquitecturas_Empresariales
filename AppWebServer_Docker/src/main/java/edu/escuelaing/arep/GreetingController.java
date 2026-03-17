package edu.escuelaing.arep;

@RestController
public class GreetingController {

    @GetMapping("/greeting")
    public static String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return "Hola " + name;
    }

    @GetMapping("/greeting/bye")
    public static String bye(@RequestParam(value = "name", defaultValue = "World") String name) {
        return "Adios " + name;
    }

    @GetMapping("/greeting/welcome")
    public static String welcome(@RequestParam(value = "name", defaultValue = "World") String name) {
        return "Bienvenido " + name;
    }
}