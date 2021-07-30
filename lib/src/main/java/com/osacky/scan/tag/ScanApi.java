package com.osacky.scan.tag;

import org.gradle.api.Project;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ScanApi {

    private final Object buildScanExtension;

    public ScanApi(Project project) {
        buildScanExtension = project.getRootProject().getExtensions().findByName("buildScan");
    }

    public void tag(String tag) {
        invokeMethodIfAvailable("tag", tag);
    }

    public void value(String name, String value) {
        invokeMethodIfAvailable("value", name, value);
    }

    public void link(String name, String url) {
        invokeMethodIfAvailable("link", name, url);
    }

    private void invokeMethodIfAvailable(String name, String... args) {
        if (buildScanExtension != null) {
            Method method = getBuildscanMethod(name, args.length);
            invokeBuildscanMethod(method, args);
        }
    }

    private void invokeBuildscanMethod(Method method, String... args) {
        try {
            method.invoke(buildScanExtension, (Object[]) args);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Method getBuildscanMethod(String methodName, int argLength) {
        try {
            if (argLength == 1) {
                return buildScanExtension.getClass().getMethod(methodName, String.class);
            } else {
                return buildScanExtension.getClass().getMethod(methodName, String.class, String.class);
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
