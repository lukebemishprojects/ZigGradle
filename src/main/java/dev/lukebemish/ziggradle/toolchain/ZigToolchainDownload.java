package dev.lukebemish.ziggradle.toolchain;

import java.net.URI;

public interface ZigToolchainDownload {
    URI getUri();
    ZigVersion getVersion();

    static ZigToolchainDownload of(URI uri, ZigVersion version) {
        return new ZigToolchainDownload() {
            @Override
            public URI getUri() {
                return uri;
            }

            @Override
            public ZigVersion getVersion() {
                return version;
            }
        };
    }
}
