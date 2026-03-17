package edu.escuelaing.arep;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MicroSpringBoot2 {

    static Map<String, Method> controllerMethod = new HashMap<>();

    public static void main(String[] args) throws ClassNotFoundException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, IOException, URISyntaxException {

        System.out.println("Loading controller classes...");

        List<Class<?>> controllers = findControllers();
        System.out.println("Total controladores encontrados: " + controllers.size()); 

        for (Class<?> c : findControllers()) {
            if (c.isAnnotationPresent(RestController.class)) {
                for (Method m : c.getDeclaredMethods()) {
                    if (m.isAnnotationPresent(GetMapping.class)) {
                        GetMapping a = m.getAnnotation(GetMapping.class);
                        controllerMethod.put(a.value(), m);
                        System.out.println("Registrando ruta: " + a.value() + " -> " + m.getName());
                    }
                }
            }
        }

        for (Map.Entry<String, Method> entry : controllerMethod.entrySet()) {
            String path = entry.getKey();
            Method m = entry.getValue();

            WebFramework.get(path, (req, res) -> {
                try {
                    Parameter[] params = m.getParameters();
                    Object[] values = new Object[params.length];

                    for (int i = 0; i < params.length; i++) {
                        if (params[i].isAnnotationPresent(RequestParam.class)) {
                            RequestParam rp = params[i].getAnnotation(RequestParam.class);
                            String val = req.getValue(rp.value());
                            values[i] = (val != null) ? val : rp.defaultValue();
                        }
                    }

                    return (String) m.invoke(null, values);

                } catch (Exception e) {
                    return "Error: " + e.getMessage();
                }
            });
        }

        HttpServer.staticfiles("webroot");
        HttpServer.main(args);
    }

    private static List<Class<?>> findControllers() {
    List<Class<?>> found = new ArrayList<>();
    try {
        URL root = MicroSpringBoot2.class.getClassLoader().getResource("");
        if (root == null) return found;

        File classesDir = new File(root.toURI());
        System.out.println("Escaneando: " + classesDir.getAbsolutePath());
        scanDirectory(classesDir, classesDir, found);
    } catch (Exception e) {
        System.out.println("Error escaneando: " + e.getMessage());
    }
    return found;
}

    private static void scanDirectory(File rootDir, File currentDir, List<Class<?>> found) {
        File[] files = currentDir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(rootDir, file, found);
            } else if (file.getName().endsWith(".class")) {
                String relativePath = rootDir.toURI().relativize(file.toURI()).getPath();
                String className = relativePath
                        .replace("/", ".")
                        .replace("\\", ".")
                        .replace(".class", "");
                try {
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(RestController.class)) {
                        System.out.println("Controlador encontrado: " + className);
                        found.add(clazz);
                    }
                } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
                }
            }
        }
    }
}