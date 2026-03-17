package edu.escuelaing.arep;

@RestController
public class HelloController {

    @GetMapping("/")
    public static String index() {
        return "Greetings from Spring Boot!";
    }

    @GetMapping("/pi")
    public static String pi() {
        return "PI = " + Math.PI;
    }

    @GetMapping("/euler")
    public static String euler() {
        return "Euler = " + Math.E;
    }

    @GetMapping("/hello")
    public static String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        return "Hello, " + name + "!";
    }
}