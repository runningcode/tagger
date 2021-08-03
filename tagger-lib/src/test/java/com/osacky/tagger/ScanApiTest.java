package com.osacky.tagger;

import org.gradle.internal.impldep.org.apache.commons.io.FileUtils;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.google.common.truth.Truth.assertThat;

public class ScanApiTest {

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

    private void writeSettingsGradleWithEnterprisePlugin() throws IOException {
        writeToFileWithName("settings.gradle", "" +
                "plugins {\n" +
                "id \"com.gradle.enterprise\" version '3.6.3'\n" +
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
                        "import com.osacky.tagger.ScanApi\n" +
                        "plugins {\n" +
                        "  id \"com.osacky.tagger\"\n"+
                        "}\n" +
                        "new ScanApi(project)." + method + "\n");
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
