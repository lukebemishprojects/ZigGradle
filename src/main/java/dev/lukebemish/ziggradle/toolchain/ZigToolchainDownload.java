package dev.lukebemish.ziggradle.toolchain;

import java.net.URI;

public interface ZigToolchainDownload {
    URI getUri();

    static ZigToolchainDownload fromUri(URI uri) {
        return () -> uri;
    }
}
