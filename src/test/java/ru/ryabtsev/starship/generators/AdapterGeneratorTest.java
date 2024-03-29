package ru.ryabtsev.starship.generators;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import ru.ryabtsev.starship.actions.movement.Movable;
import ru.ryabtsev.starship.context.ApplicationContext;
import ru.ryabtsev.starship.context.SimpleApplicationContext;

class AdapterGeneratorTest {

    private final AdapterGenerator adapterGenerator = new AdapterGenerator();

    @Test
    void javaCompilerTest() {
        final JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        assertNotNull(javaCompiler);
    }

    @Test
    @SneakyThrows
    void classFileCreationTest() {
        final String simpleClassCode = adapterGenerator.generateCodeFor(Movable.class);

        final File file = new File(createPath(Movable.class));
        try (final OutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(simpleClassCode.getBytes(StandardCharsets.UTF_8));
        }

        assertTrue(file.exists());
    }

    private String createPath(final Class<?> implementedInterface) {
        final String separator = FileSystems.getDefault().getSeparator();
        final String fileName = implementedInterface.getSimpleName() + "Adapter.java";
        final String resourceName = separator + Arrays.stream(implementedInterface.getPackageName()
                .split("\\."))
                .collect(Collectors.joining(separator));
        return Optional.ofNullable(implementedInterface.getResource(resourceName))
                .map(URL::getPath)
                .orElseThrow(() -> new IllegalStateException("Can't find resource " + resourceName))
                + separator
                + fileName;
    }

    @Test
    void compilationTest() {
        final JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        final String path = createPath(Movable.class);
        assertEquals(0, javaCompiler.run(null, null, null, path));
    }

    @Test
    void classLoadingTest() {
        final Class<?> loadedClass = adapterGenerator.generateFor(Movable.class);
        assertEquals(
                Movable.class.getMethods().length + Object.class.getMethods().length,
                loadedClass.getMethods().length
        );
    }

    @SneakyThrows
    @Test
    void instanceCreationTest() {
        final Class<?> loadedClass = adapterGenerator.generateFor(Movable.class);
        final ApplicationContext applicationContext = new SimpleApplicationContext();
        final Map<String, Object> adaptedObject = new HashMap<>();

        final Object instance = loadedClass.getDeclaredConstructor(ApplicationContext.class, Map.class)
                .newInstance(applicationContext, adaptedObject);
        assertTrue(instance instanceof Movable);
    }
}