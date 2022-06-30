package com.osacky.tagger;

class NoopTaggerApi implements TaggerApiExtension {

    NoopTaggerApi() {
    }

    @Override
    public void tag(String tag) {
    }

    @Override
    public void value(String name, String value) {
    }

    @Override
    public void link(String name, String url) {
    }
}
