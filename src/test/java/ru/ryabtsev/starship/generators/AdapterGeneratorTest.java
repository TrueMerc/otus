package ru.ryabtsev.starship.generators;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import ru.ryabtsev.starship.actions.movement.Movable;

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
    @SneakyThrows
    void classLoadingTest() {
        final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        final Class<?> loadedClass = Class.forName(
                "ru.ryabtsev.starship.actions.movement.MovableAdapter", true, classLoader);
        assertNotNull(loadedClass);
        assertEquals(
                Movable.class.getMethods().length + Object.class.getMethods().length,
                loadedClass.getMethods().length
        );
    }
}