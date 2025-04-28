package dev.lukebemish.ziggradle.toolchain.internal;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.gradle.api.artifacts.transform.CacheableTransform;
import org.gradle.api.artifacts.transform.InputArtifact;
import org.gradle.api.artifacts.transform.TransformAction;
import org.gradle.api.artifacts.transform.TransformOutputs;
import org.gradle.api.artifacts.transform.TransformParameters;
import org.gradle.api.file.FileSystemLocation;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@CacheableTransform
public abstract class ToolchainUnpackTransform implements TransformAction<TransformParameters.None> {
    @InputArtifact
    @PathSensitive(PathSensitivity.NAME_ONLY)
    public abstract Provider<FileSystemLocation> getInputArtifact();
    
    @Override
    public void transform(TransformOutputs outputs) {
        var input = getInputArtifact().get().getAsFile();
        var outputDir = outputs.dir(input.getName() + "-decompressed");
        decompress(input, outputDir);
    }

    private void decompress(File input, File outputDir) {
        try (
                var fis = new FileInputStream(input);
                var bis = new BufferedInputStream(fis);
                XZCompressorInputStream xzis = new XZCompressorInputStream(bis);
                TarArchiveInputStream tais = new TarArchiveInputStream(xzis)
        ) {
            TarArchiveEntry entry;
            while ((entry = tais.getNextEntry()) != null) {
                Path outputPath = outputDir.toPath().resolve(entry.getName()).normalize();
                if (!outputPath.startsWith(outputDir.toPath())) {
                    throw new IOException("Entry is outside of the target directory: "
                            + entry.getName());
                }
                
                if (entry.isDirectory()) {
                    Files.createDirectories(outputPath);
                } else {
                    Files.createDirectories(outputPath.getParent());
                    Files.copy(tais, outputPath);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
