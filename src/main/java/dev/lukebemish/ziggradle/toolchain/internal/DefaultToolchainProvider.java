package dev.lukebemish.ziggradle.toolchain.internal;

import dev.lukebemish.ziggradle.toolchain.ZigToolchainDownload;
import dev.lukebemish.ziggradle.toolchain.ZigToolchainProvider;
import dev.lukebemish.ziggradle.toolchain.ZigToolchainRequest;

import javax.inject.Inject;
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
    
    @SuppressWarnings("Convert2Lambda")
    @Override
    public Optional<ZigToolchainDownload> resolve(ZigToolchainRequest request) {
        var version = request.getJavaToolchainSpec().getVersion().get().toString();
        var arch = request.getBuildPlatform().getArchitecture().name().toLowerCase(Locale.ROOT);
        var os = request.getBuildPlatform().getOperatingSystem().name().toLowerCase(Locale.ROOT);
        
        var string = version + "/zig-" + os + "-" + arch + "-" + version + ".tar.xz";
        
        return Optional.of(new ZigToolchainDownload() {
            @Override
            public URI getUri() {
                return ZIG_DOWNLOAD_URL.resolve(string);
            }
        });
    }
}
