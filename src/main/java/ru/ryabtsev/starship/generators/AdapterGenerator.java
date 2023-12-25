package ru.ryabtsev.starship.generators;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ryabtsev.starship.context.ApplicationContext;

/**
 * The AdapterGenerator class provides the capability to generate simple IoC-based adapters for interfaces.
 */
public class AdapterGenerator {

    private static final Logger logger = LoggerFactory.getLogger(AdapterGenerator.class);

    public Class<?> generateFor(final Class<?> implementedInterface) {
        if (implementedInterface.isInterface()) {
            final String path = createPath(implementedInterface);
            final File file = new File(path);
            try (final OutputStream outputStream = new FileOutputStream(file)) {

            } catch (final IOException e) {
                logger.error("Can't open output stream for file {}", file.getPath(), e);
            }
        }
        throw new IllegalArgumentException(implementedInterface.getName() + " is not an interface.");
    }

    public void createPackageFor(final Class<?> implementedInterface) {
        final Package interfacePackage = implementedInterface.getPackage();
        //final String interfacePackage.get
    }

    public String generateCodeFor(final Class<?> implementedInterface) {
        return new StringBuilder(createPackageSection(implementedInterface))
                .append(createImportSection(implementedInterface))
                .append("\n")
                .append(createClassBeginningSection(implementedInterface))
                .append(createFieldsSection())
                .append(createConstructorSection(implementedInterface))
                .append("\n")
                .append(createMethodImplementations(implementedInterface))
                .append("}\n")
                .toString();
    }

    private String createPackageSection(final Class<?> implementedInterface) {
        final String packageName = implementedInterface.getPackageName();
        return new StringBuilder("package ").append(packageName).append(";\n\n").toString();
    }

    private String createImportSection(final Class<?> implementedInterface) {
        final Package interfacePackage = implementedInterface.getPackage();
        return Stream.concat(Arrays.asList(implementedInterface.getMethods())
                        .stream()
                        .flatMap(AdapterGenerator::getMethodDependencies)
                        .filter(dependency -> isImportRequiredFor(interfacePackage, dependency)),
                Stream.of(ApplicationContext.class, Map.class)
        ).map(AdapterGenerator::createImportString).collect(Collectors.joining(""));
    }

    private static Stream<Class<?>> getMethodDependencies(final Method method) {
        final Class<?> returnType = method.getReturnType();
        final Class<?>[] parameterTypes = method.getParameterTypes();
        return Stream.concat(Stream.of(returnType), Stream.of(parameterTypes));
    }

    boolean isImportRequiredFor(final Package interfacePackage, final Class<?> dependency) {
        return dependency.getPackage() != null && !interfacePackage.equals(dependency.getPackage());
    }

    private static String createImportString(final Class<?> dependency) {
        return "import " + dependency.getName() + ";\n";
    }

    private String createClassBeginningSection(final Class<?> implementedInterface) {
        return "class " + createAdapterName(implementedInterface) + " {\n\n";
    }

    private String createAdapterName(final Class<?> implementedInterface) {
        return implementedInterface.getSimpleName() + "Adapter";
    }

    private String createFieldsSection() {
        return """
                    private ApplicationContext applicationContext;
                     
                    private Map<String, Object> adaptedObject;
                     
                """;
    }

    private String createConstructorSection(final Class<?> implementedInterface) {
        return String.format("""
                    public %s(final ApplicationContext applicationContext, final Map<String, Object> adaptedObject) {
                        this.applicationContext = applicationContext;
                        this.adaptedObject = adaptedObject;
                    }
                """, createAdapterName(implementedInterface));
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

    private String createMethodImplementations(final Class<?> implementedInterface) {
        return Arrays.asList(implementedInterface.getMethods())
                .stream()
                .map(this::createImplementation)
                .collect(Collectors.joining("\n"));
    }

    private String createImplementation(final Method method) {
        final String IDENTATION = "    ";

        return new StringBuilder(IDENTATION)
                .append("@Override\n")
                .append(IDENTATION)
                .append("public ")
                .append(method.getReturnType().getSimpleName())
                .append(" ")
                .append(method.getName())
                .append("(")
                .append(createParametersSection(method))
                .append(") {\n")
                .append(IDENTATION)
                .append("}\n")
                .toString();
    }

    private String createParametersSection(final Method method) {
        final Parameter[] parameters = method.getParameters();
        final List<String> parameterStrings = new ArrayList<>(parameters.length);
        Arrays.stream(method.getParameters()).forEach(
                parameter -> parameterStrings.add(createFinalParameterEntry(parameter))
        );
        return parameterStrings.stream().collect(Collectors.joining(", "));
    }

    private String createFinalParameterEntry(final Parameter parameter) {
        return new StringBuilder("final ")
                .append(parameter.getType().getSimpleName())
                .append(" ")
                .append(parameter.getName())
                .toString();
    }

    public void clearAll() {

    }
}
