package com.osacky.tagger;

class DelegatingTaggerApi implements TaggerApiExtension {

    private final TaggerApi functional;
    private final TaggerApiExtension noop;

    DelegatingTaggerApi(TaggerApi functional, TaggerApiExtension noop) {
        this.functional = functional;
        this.noop = noop;
    }

    private TaggerApiExtension getDelegate() {
        if (functional.isAvailable()) {
            return functional;
        } else {
            return noop;
        }
    }

    @Override
    public void tag(String tag) {
        getDelegate().tag(tag);
    }

    @Override
    public void value(String name, String value) {
        getDelegate().value(name, value);
    }

    @Override
    public void link(String name, String url) {
        getDelegate().link(name, url);
    }
}
