package com.mstendo.rtp.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

public final class CaffeineFactory {
    private static final ForkJoinPool loaderPool = new ForkJoinPool();
    private static Object caffeineBuilder;
    
    static {
        try {
            Class<?> caffeineClass = Class.forName("com.github.benmanes.caffeine.cache.Caffeine");
            caffeineBuilder = caffeineClass.getMethod("newBuilder").invoke(null);
        } catch (Exception e) {
            caffeineBuilder = null;
        }
    }
    
    public static Object newBuilder() {
        if (caffeineBuilder == null) {
            try {
                Class<?> caffeineClass = Class.forName("com.github.benmanes.caffeine.cache.Caffeine");
                Object builder = caffeineClass.getMethod("newBuilder").invoke(null);
                if (builder != null) {
                    builder.getClass().getMethod("executor", Executor.class).invoke(builder, loaderPool);
                    return builder;
                }
            } catch (Exception e) {
                // Caffeine not available
            }
            return null;
        }
        try {
            Object builder = caffeineBuilder.getClass().getMethod("newBuilder").invoke(null);
            builder.getClass().getMethod("executor", Executor.class).invoke(builder, loaderPool);
            return builder;
        } catch (Exception e) {
            return null;
        }
    }
    
    public static Executor executor() {
        return loaderPool;
    }
}
