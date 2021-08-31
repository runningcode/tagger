package com.osacky.tagger;

import org.gradle.api.Project;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * For build and plugin authors to easily tag build scans.
 */
public class TaggerApi implements TaggerApiExtension {

    private final Object buildScanExtension;

    public TaggerApi(Project project) {
        buildScanExtension = project.getRootProject().getExtensions().findByName("buildScan");
    }

    public TaggerApi(Object object) {
        if (object instanceof Project) {
            buildScanExtension = ((Project)object).getRootProject().getExtensions().findByName("buildScan");
        } else {
            buildScanExtension = null;
        }
    }

    /**
     * Calls {@link com.gradle.scan.plugin.BuildScanExtension#tag(String)} if available.
     * @param tag a value to add as a Build scan tag
     */
    @Override
    public void tag(String tag) {
        invokeMethodIfAvailable("tag", tag);
    }

    /**
     * Calls {@link com.gradle.scan.plugin.BuildScanExtension#value(String, String)} if available.
     * @param name the key for the custom value
     * @param value the data to add
     */
    @Override
    public void value(String name, String value) {
        invokeMethodIfAvailable("value", name, value);
    }

    /**
     * Calls {@link com.gradle.scan.plugin.BuildScanExtension#link(String, String)} if available.
     * @param name the name of the URL link to be displayed in the UI
     * @param url the URL to follow when the link is clicked
     */
    @Override
    public void link(String name, String url) {
        invokeMethodIfAvailable("link", name, url);
    }

    private void invokeMethodIfAvailable(String name, String... args) {
        if (buildScanExtension != null) {
            Method method = getBuildScanMethod(name, args.length);
            invokeBuildScanMethod(method, args);
        }
    }

    private void invokeBuildScanMethod(Method method, String... args) {
        try {
            method.invoke(buildScanExtension, (Object[]) args);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Method getBuildScanMethod(String methodName, int argLength) {
        try {
            Class<?>[] classes = new Class<?>[argLength];
            Arrays.fill(classes, String.class);
            return buildScanExtension.getClass().getMethod(methodName, classes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isAvailable() {
        return buildScanExtension != null;
    }
}
