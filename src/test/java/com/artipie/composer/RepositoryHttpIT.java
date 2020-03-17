/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 artipie.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.artipie.composer;

import com.google.common.collect.ImmutableList;
import com.jcabi.log.Logger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.UUID;
import org.cactoos.list.ListOf;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.AllOf;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Integration test for PHP Composer repository.
 *
 * @since 0.1
 */
class RepositoryHttpIT {

    // @checkstyle VisibilityModifierCheck (5 lines)
    /**
     * Temporary directory.
     */
    @TempDir
    Path temp;

    /**
     * Path to PHP project directory.
     */
    private Path project;

    @BeforeEach
    void setUp() throws Exception {
        this.project = this.temp.resolve("project");
        this.project.toFile().mkdirs();
        this.ensureComposerInstalled();
    }

    @Test
    //@Disabled("Not implemented")
    void shouldInstallAddedPackage() throws Exception {
        //todo: add package to repo via HTTP
        Files.write(
            this.project.resolve("composer.json"),
            String.join(
                "",
                "{",
                "\"config\":{ \"secure-http\": false },",
                "\"repositories\": [",
                "{\"type\": \"composer\", \"url\": \"http://localhost:8080/myrepo/\"},",
                "{\"packagist.org\": false} ",
                "],",
                "\"require\": { \"vendor/package\": \"1.1.2\" }",
                "}"
            ).getBytes()
        );
        /**
         * Fails with:
         * [Composer\Downloader\TransportException]
         *   The "http://localhost:8080/myrepo/packages.json" file could not be downloaded: failed to open stream: Connection refused
         */
        //todo: see https://getcomposer.org/doc/05-repositories.md#composer
        //todo: http://localhost:8080/myrepo/packages.json should return something like
        /*
        https://packagist.org/packages.json:
        {
  "packages": [],
  "notify": "https://packagist.org/downloads/%package%",
  "notify-batch": "https://packagist.org/downloads/",
  "providers-url": "/p/%package%$%hash%.json",
  "metadata-url": "/p2/%package%.json",
  "search": "https://packagist.org/search.json?q=%query%&type=%type%",
  "providers-api": "https://packagist.org/providers/%package%.json",
  "provider-includes": {
    "p/provider-2013$%hash%.json": {
      "sha256": "16fd94bf81f2a7432092624d3da664c71aec9d5a9f47a6779b3bf6ad4433e29d"
    },
    "p/provider-2014$%hash%.json": {
      "sha256": "6e4e2dcc0e8c320eb5b0f809b0aae95bcd4f6018a9bbb9b07c0243663613ec6c"
    },
    "p/provider-2015$%hash%.json": {
      "sha256": "56abb9775a6ffd5098443faffbdc8c7d96c7ec0fc95232c37eee5b01b2871b74"
    },
    "p/provider-2016$%hash%.json": {
      "sha256": "add955d8964d9c8a224f4814da11b0f037c3d6f4660a401b0212a0bf404ad95d"
    },
    "p/provider-2017$%hash%.json": {
      "sha256": "b8e95120783b12ce73a23efaf869e1cbf00faa23a95e0714614c112e8f6f245a"
    },
    "p/provider-2018$%hash%.json": {
      "sha256": "a4ed665ef3980b026ba4420a2883f40c748e6f3942eae3ebd367d5ecc9f7ea0e"
    },
    "p/provider-2019$%hash%.json": {
      "sha256": "fa2130016ab757beb17228a501c59d433c07cc7401e747f9de53a4722721a2b2"
    },
    "p/provider-2019-04$%hash%.json": {
      "sha256": "9d5cc528a23be764050aa29e004a12d8122bd36ba42477e9e69c6fa4a3987756"
    },
    "p/provider-2019-07$%hash%.json": {
      "sha256": "f448cdf3fd6baa103b61c66f9c048168a99436d0bfbb1302bed4dec63bb3255a"
    },
    "p/provider-2019-10$%hash%.json": {
      "sha256": "3d6e650ecb20c1881b01095e4afe74a570ce835a36e9c74200d544f394701d1f"
    },
    "p/provider-2020-01$%hash%.json": {
      "sha256": "ea62143dfc925dcc97e108404ff410d64e3662e924fac56ad8c7a8db63ab7471"
    },
    "p/provider-archived$%hash%.json": {
      "sha256": "95b7476cfaab2a59b34e1fbc07c1b5eaea0ffab22762f7f3baab102b0cc62822"
    },
    "p/provider-latest$%hash%.json": {
      "sha256": "69617e8092790cf11bd3c51a7d1b36e3157dc974051007461f161328592fc464"
    }
  }
}
         */
        MatcherAssert.assertThat(
            this.run("install"),
            new AllOf<>(
                new ListOf<Matcher<? super String>>(
                    new StringContains(false, "Installs: vendor/package:1.1.2"),
                    new StringContains(false, "100%")
                )
            )
        );
    }

    @Test
    @Disabled("Just a working example") //todo: remove the test
    void testInstall() throws Exception {
        Files.write(
            this.project.resolve("composer.json"),
            "{\"require\": { \"psr/log\": \"1.1.2\" } }".getBytes()
        );
        MatcherAssert.assertThat(
            this.run("install"),
            new AllOf<>(
                Arrays.asList(
                    new StringContains(false, "Installs: psr/log:1.1.2"),
                    new StringContains(false, "100%")
                )
            )
        );
    }

    private void ensureComposerInstalled() throws Exception {
        final String output = this.run("--version");
        if (!output.startsWith("Composer version")) {
            throw new IllegalStateException("Composer not installed");
        }
    }

    private String run(final String... args) throws Exception {
        final Path stdout = this.temp.resolve(
            String.format("%s-stdout.txt", UUID.randomUUID().toString())
        );
        final int code = new ProcessBuilder()
            .directory(this.project.toFile())
            .command(
                ImmutableList.<String>builder()
                    .add("composer")
                    .add(args)
                    .add("--verbose")
                    .add("--no-cache")
                    .build()
            )
            .redirectOutput(stdout.toFile())
            .redirectErrorStream(true)
            .start()
            .waitFor();
        final String log = new String(Files.readAllBytes(stdout));
        Logger.debug(this, "Full stdout/stderr:\n%s", log);
        if (code != 0) {
            throw new IllegalStateException(String.format("Not OK exit code: %d", code));
        }
        return log;
    }
}
