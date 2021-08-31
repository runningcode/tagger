package com.osacky.tagger;

interface TaggerApiExtension {

    void tag(String tag);

    void value(String name, String value);

    void link(String name, String url);
}
