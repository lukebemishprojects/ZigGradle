package dev.lukebemish.ziggradle;

import org.gradle.api.tasks.Internal;

import java.io.Serializable;
import java.util.List;

public abstract class ZigOptions extends BaseZigOptions {
    public void addCacheDirArgs() {
        getCompilerArgs().add(CacheDirMarker.INSTANCE);
    }

    @Override
    protected void resolveCompilerArg(Object arg, List<String> result) {
        if (arg instanceof CacheDirMarker) {
            result.addAll(getCacheDirArgs());
            return;
        }
        super.resolveCompilerArg(arg, result);
    }

    private static class CacheDirMarker implements Serializable {
        public static final CacheDirMarker INSTANCE = new CacheDirMarker();
        private CacheDirMarker() {}
    }
}
