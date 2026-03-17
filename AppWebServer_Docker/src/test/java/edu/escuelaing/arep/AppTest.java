package edu.escuelaing.arep;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

public class AppTest {

    @Test
    public void testHelloWorldDefault() {
        String result = HelloController.hello("World");
        assertEquals("Hello, World!", result);
    }

    @Test
    public void testHelloWithName() {
        String result = HelloController.hello("Roger");
        assertEquals("Hello, Roger!", result);
    }

    @Test
    public void testPi() {
        String result = HelloController.pi();
        assertTrue(result.contains("3.14"));
    }

    @Test
    public void testEuler() {
        String result = HelloController.euler();
        assertTrue(result.contains("2.71"));
    }

    @Test
    public void testGreeting() {
        String result = GreetingController.greeting("Juan");
        assertEquals("Hola Juan", result);
    }

    @Test
    public void testGreetingDefault() {
        String result = GreetingController.greeting("World");
        assertEquals("Hola World", result);
    }

    @Test
    public void testGreetingWithName() {
        String result = GreetingController.greeting("Roger");
        assertEquals("Hola Roger", result);
    }

    @Test
    public void testGreetingEmptyName() {
        String result = GreetingController.greeting("");
        assertEquals("Hola ", result);
    }

    @Test
    public void testGreetingBye() {
        String result = GreetingController.bye("Juan");
        assertEquals("Adios Juan", result);
    }

    @Test
    public void testGreetingByeDefault() {
        String result = GreetingController.bye("World");
        assertEquals("Adios World", result);
    }

    @Test
    public void testGreetingWelcome() {
        String result = GreetingController.welcome("Roger");
        assertEquals("Bienvenido Roger", result);
    }

    @Test
    public void testGreetingWelcomeDefault() {
        String result = GreetingController.welcome("World");
        assertEquals("Bienvenido World", result);
    }

    @Test
    public void testHttpRequestGetValue() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "Roger");
        HttpRequest req = new HttpRequest(params);
        assertEquals("Roger", req.getValue("name"));
    }

    @Test
    public void testHttpRequestNullParam() {
        Map<String, String> params = new HashMap<>();
        HttpRequest req = new HttpRequest(params);
        assertNull(req.getValue("name"));
    }

    @Test
    public void testWebFrameworkRegisterAndGet() {
        WebFramework.get("/test", (req, res) -> "test response");
        assertNotNull(WebFramework.getRoute("/test"));
    }

    @Test
    public void testWebFrameworkRouteNotFound() {
        assertNull(WebFramework.getRoute("/ruta-inexistente"));
    }
}