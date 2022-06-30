package com.osacky.tagger;

import org.gradle.internal.impldep.org.apache.commons.io.FileUtils;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.google.common.truth.Truth.assertThat;

public class TaggerApiTest {

    @Rule
    public TemporaryFolder testProjectRoot = new TemporaryFolder();

    @Test
    public void canCallTagWithoutGEPlugin() throws IOException {
        writeBuildGradle("tag(\"foo\")");
        writeToFileWithName("settings.gradle", "");

        BuildResult result = gradleRunner()
                .build();

        assertThat(result.getOutput()).contains("SUCCESS");
    }

    @Test
    public void scanApiTagsWithGEPlugin() throws IOException {
        writeBuildGradle("tag(\"foo\")");
        writeSettingsGradleWithEnterprisePlugin();

        BuildResult result = gradleRunner()
                .build();

        assertThat(result.getOutput()).contains("SUCCESS");
    }

    @Test
    public void scanApiValueWithGEPlugin() throws IOException {
        writeBuildGradle("value('foo', 'bar')");
        writeSettingsGradleWithEnterprisePlugin();

        BuildResult result = gradleRunner()
                .build();

        assertThat(result.getOutput()).contains("SUCCESS");
    }

    @Test
    public void scanApiLinkWithGEPlugin() throws IOException {
        writeBuildGradle("link('Gradle', 'https://gradle.com')");
        writeSettingsGradleWithEnterprisePlugin();

        BuildResult result = gradleRunner()
                .build();

        assertThat(result.getOutput()).contains("SUCCESS");
    }

    @Test
    public void taggerApiObjectSupportsCoCa() throws IOException {
        writeBuildGradle("tag('foo')");
        writeSettingsGradleWithEnterprisePlugin();
        File buildGradle = new File(testProjectRoot.getRoot(), "build.gradle");
        FileWriter fileWriter = new FileWriter(buildGradle, true);
        fileWriter.write("tasks.named('help').configure { \n" +
                "def taggerApi = new TaggerApi(project)\n" +
                "doFirst { \n" +
                "taggerApi.tag('help-tag'); \n" +
                "println('hello from help') \n" +
                "} " +
                "}");
        fileWriter.close();

        BuildResult result = gradleRunner()
                .withArguments("help", "--configuration-cache")
                .build();
        assertThat(result.getOutput()).contains("hello from help");
        assertThat(result.getOutput()).contains("Configuration cache entry stored.");

        BuildResult result2 = gradleRunner()
                .withArguments("help", "--configuration-cache")
                .build();
        assertThat(result2.getOutput()).contains("hello from help");
        assertThat(result2.getOutput()).contains("Configuration cache entry reused.");
    }

    @Test
    public void taggerApiExtensionSupportsCoCa() throws IOException {
        writeBuildGradle("tag('foo')");
        writeSettingsGradleWithEnterprisePlugin();
        File buildGradle = new File(testProjectRoot.getRoot(), "build.gradle");
        FileWriter fileWriter = new FileWriter(buildGradle, true);
        fileWriter.write("tasks.named('help').configure { \n" +
                "taggerApi.tag('build-tag'); \n" +
                "def scanApi = taggerApi\n" +
                "  doFirst { \n" +
                "    scanApi.tag('help-tag'); \n" +
                "    println('hello from help') \n" +
                "  } \n" +
                "}");
        fileWriter.close();

        BuildResult result = gradleRunner()
                .withArguments("help", "--configuration-cache")
                .build();
        assertThat(result.getOutput()).contains("hello from help");
        assertThat(result.getOutput()).contains("Configuration cache entry stored.");

        BuildResult result2 = gradleRunner()
                .withArguments("help", "--configuration-cache")
                .build();
        assertThat(result2.getOutput()).contains("hello from help");
        assertThat(result2.getOutput()).contains("Configuration cache entry reused.");
    }

    @Test
    public void canTagBuildScanUsingExtension() throws IOException {
        writeBuildGradle("tag('foo')");
        writeSettingsGradleWithEnterprisePlugin();
        File buildGradle = new File(testProjectRoot.getRoot(), "build.gradle");
        FileWriter fileWriter = new FileWriter(buildGradle, true);
        fileWriter.write("taggerApi { \n" +
                "  tag('extension-tag'); \n" +
                "  value('name', 'key'); \n" +
                "  value('link', 'https://www.gradle.com'); \n" +
                "}");
        fileWriter.close();

        BuildResult result = gradleRunner()
                .withArguments("help", "--configuration-cache")
                .build();
        assertThat(result.getOutput()).contains("SUCCESS");
    }

    private void writeSettingsGradleWithEnterprisePlugin() throws IOException {
        writeToFileWithName("settings.gradle", "" +
                "plugins {\n" +
                "id \"com.gradle.enterprise\" version '3.6.4'\n" +
                "}\n" +
                "gradleEnterprise {\n" +
                "  buildScan {\n" +
                "    publishAlways()\n" +
                "    termsOfServiceUrl = \"https://gradle.com/terms-of-service\"\n" +
                "    termsOfServiceAgree = \"yes\"\n" +
                "  }\n" +
                "}\n"
        );
    }

    private void writeBuildGradle(String method) throws IOException {
        writeToFileWithName("build.gradle",
                        "import com.osacky.tagger.TaggerApi\n" +
                        "plugins {\n" +
                        "  id \"com.osacky.tagger\"\n"+
                        "}\n" +
                        "new TaggerApi(project)." + method + "\n");
    }

    private void writeToFileWithName(String filename, String contents) throws IOException {
        File buildGradle = testProjectRoot.newFile(filename);
        FileUtils.write(buildGradle, contents, StandardCharsets.UTF_8);
    }

    public GradleRunner gradleRunner() {
        return GradleRunner.create()
                .withArguments("help")
                .forwardOutput()
                .withProjectDir(testProjectRoot.getRoot())
                .withPluginClasspath();
    }
}
