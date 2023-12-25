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

    // language=Java
    private final String EXPECTED_RESULT = """
                package ru.ryabtsev.starship.actions.movement;
                
                import ru.ryabtsev.starship.context.ApplicationContext;
                import java.util.Map;
                
                class MovableAdapter implements Movable {
                
                    private ApplicationContext applicationContext;
                
                    private Map<String, Object> adaptedObject;
                     
                    public MovableAdapter(final ApplicationContext applicationContext, final Map<String, Object> adaptedObject) {
                        this.applicationContext = applicationContext;
                        this.adaptedObject = adaptedObject;
                    }
                    
                    @Override                    
                    public Vector getPosition() {
                        return applicationContext.<Vector>resolve("Actions.Movable:getPosition", new Object[]{ adaptedObject });
                    }
                    
                    @Override
                    public Vector getVelocity() {
                        return applicationContext.<Vector>resolve("Actions.Movable:getVelocity", new Object[]{ adaptedObject });
                    }
                    
                    @Override
                    public void moveTo(final Vector arg0) {
                        applicationContext.resolve("Actions.Movable:moveTo", new Object[]{ adaptedObject, arg0 });
                    }
                }
                """;

    private final AdapterGenerator adapterGenerator = new AdapterGenerator();

    @Test
    void javaCompilerTest() {
        final JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        assertNotNull(javaCompiler);
    }

    @Test
    void codeCreationTest() {
        assertEquals(EXPECTED_RESULT, adapterGenerator.generateCodeFor(Movable.class));
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
    }
}