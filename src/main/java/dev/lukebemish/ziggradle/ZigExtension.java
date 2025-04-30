package dev.lukebemish.ziggradle;

import dev.lukebemish.ziggradle.toolchain.ZigCompiler;
import dev.lukebemish.ziggradle.toolchain.ZigToolchainSpec;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.TaskProvider;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public abstract class ZigExtension {
    private final ZigToolchainSpec toolchains;

    @Inject
    protected abstract ObjectFactory getObjectFactory();

    @Inject
    protected abstract Project getProject();

    @Inject
    public ZigExtension() {
        this.toolchains = getObjectFactory().newInstance(ZigToolchainSpec.class);
    }

    public ZigToolchainSpec getToolchain() {
        return toolchains;
    }

    public void toolchain(Action<? super ZigToolchainSpec> action) {
        action.execute(toolchains);
    }

    public Provider<ZigCompiler> compilerFor(Action<? super ZigToolchainSpec> config) {
        var spec = getObjectFactory().newInstance(ZigToolchainSpec.class);
        spec.getVersion().convention(getToolchain().getVersion());
        config.execute(spec);
        return compilerFor(spec);
    }

    public abstract Provider<ZigCompiler> compilerFor(ZigToolchainSpec spec);

    private final Map<String, TaskProvider<Copy>> jniHeadersCopy = new HashMap<>();

    public FileCollection jniHeadersUnixOpenJDK(String versionRef) {
        var sharedDep = getProject().getDependencyFactory().create("dev.lukebemish.ziggradle.internal.openjdk-jni", "share/native/include/jni.h", versionRef);
        var unixDep = getProject().getDependencyFactory().create("dev.lukebemish.ziggradle.internal.openjdk-jni", "unix/native/include/jni_md.h", versionRef);

        var sharedConfig = getProject().getConfigurations().detachedConfiguration(sharedDep);
        var unixConfig = getProject().getConfigurations().detachedConfiguration(unixDep);

        var sharedTask = jniHeadersCopy.computeIfAbsent("shared-"+versionRef, k ->
            getProject().getTasks().register("dev.lukebemish.ziggradle.internal.copy-openjdk-jni-headers.shared."+versionRef.replace('/', '-'), Copy.class, task -> {
                task.from(sharedConfig);
                task.into(getProject().getLayout().getBuildDirectory().dir("dev.lukebemish.ziggradle/openjdk-jni-headers/shared/"+versionRef));
                task.rename(s -> "jni.h");
            })
        );

        var unixTask = jniHeadersCopy.computeIfAbsent("unix-"+versionRef, k ->
            getProject().getTasks().register("dev.lukebemish.ziggradle.internal.copy-openjdk-jni-headers.unix."+versionRef.replace('/', '-'), Copy.class, task -> {
                task.from(unixConfig);
                task.into(getProject().getLayout().getBuildDirectory().dir("dev.lukebemish.ziggradle/openjdk-jni-headers/unix/"+versionRef));
                task.rename(s -> "jni_md.h");
            })
        );

        var files = getObjectFactory().fileCollection();
        files.from(sharedTask.map(Copy::getDestinationDir));
        files.from(unixTask.map(Copy::getDestinationDir));
        files.builtBy(sharedTask, unixTask);
        return files;
    }

    public FileCollection jniHeadersWindowsOpenJDK(String versionRef) {
        var sharedDep = getProject().getDependencyFactory().create("dev.lukebemish.ziggradle.internal.openjdk-jni", "share/native/include/jni.h", versionRef);
        var windowsDep = getProject().getDependencyFactory().create("dev.lukebemish.ziggradle.internal.openjdk-jni", "windows/native/include/jni_md.h", versionRef);

        var sharedConfig = getProject().getConfigurations().detachedConfiguration(sharedDep);
        var windowsConfig = getProject().getConfigurations().detachedConfiguration(windowsDep);

        var sharedTask = jniHeadersCopy.computeIfAbsent("shared-"+versionRef, k ->
            getProject().getTasks().register("dev.lukebemish.ziggradle.internal.copy-openjdk-jni-headers.shared."+versionRef.replace('/', '-'), Copy.class, task -> {
                task.from(sharedConfig);
                task.into(getProject().getLayout().getBuildDirectory().dir("dev.lukebemish.ziggradle/openjdk-jni-headers/shared/"+versionRef));
                task.rename(s -> "jni.h");
            })
        );

        var windowsTask = jniHeadersCopy.computeIfAbsent("windows-"+versionRef, k ->
            getProject().getTasks().register("dev.lukebemish.ziggradle.internal.copy-openjdk-jni-headers.windows."+versionRef.replace('/', '-'), Copy.class, task -> {
                task.from(windowsConfig);
                task.into(getProject().getLayout().getBuildDirectory().dir("dev.lukebemish.ziggradle/openjdk-jni-headers/windows/"+versionRef));
                task.rename(s -> "jni_md.h");
            })
        );

        var files = getObjectFactory().fileCollection();
        files.from(sharedTask.map(Copy::getDestinationDir));
        files.from(windowsTask.map(Copy::getDestinationDir));
        files.builtBy(sharedTask, windowsTask);
        return files;
    }
}
