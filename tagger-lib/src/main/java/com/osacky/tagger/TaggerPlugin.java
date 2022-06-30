package com.osacky.tagger;

import org.gradle.api.Plugin;
import org.gradle.api.plugins.ExtensionAware;

public class TaggerPlugin implements Plugin<Object> {
    @Override
    public void apply(Object target) {
        if (target instanceof ExtensionAware) {
            DelegatingTaggerApi delegatingScanApi = new DelegatingTaggerApi(new TaggerApi(target), new NoopTaggerApi());
            ((ExtensionAware) target).getExtensions().add("taggerApi", delegatingScanApi);
        }
    }
}
