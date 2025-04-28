package dev.lukebemish.ziggradle.toolchain.internal;

import dev.lukebemish.ziggradle.toolchain.ZigToolchainDownload;
import dev.lukebemish.ziggradle.toolchain.ZigToolchainProvider;
import dev.lukebemish.ziggradle.toolchain.ZigToolchainRequest;
import dev.lukebemish.ziggradle.toolchain.ZigVersion;

import javax.inject.Inject;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Optional;

public abstract class DefaultToolchainProvider implements ZigToolchainProvider {
    public static final URI ZIG_DOWNLOAD_URL;

    static {
        try {
            ZIG_DOWNLOAD_URL = new URI("https://ziglang.org/download/");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Inject
    public DefaultToolchainProvider() {}

    @Override
    public Optional<ZigToolchainDownload> resolve(ZigToolchainRequest request) {
        var version = request.getJavaToolchainSpec().getVersion().get().toString();
        var arch = request.getBuildPlatform().getArchitecture().name().toLowerCase(Locale.ROOT);
        var os = request.getBuildPlatform().getOperatingSystem().name().toLowerCase(Locale.ROOT);

        var string = version + "/zig-" + os + "-" + arch + "-" + version + ".tar.xz";
        var targetUri = ZIG_DOWNLOAD_URL.resolve(string);

        try {
            var targetUrl = targetUri.toURL();

            var huc = (HttpURLConnection) targetUrl.openConnection();
            huc.setRequestMethod("HEAD");
            if (huc.getResponseCode() == 200) {
                return Optional.of(ZigToolchainDownload.of(targetUri, ZigVersion.of(version)));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return Optional.empty();
    }
}
