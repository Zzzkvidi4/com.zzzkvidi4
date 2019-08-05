package com.zzzkvidi4.storage.repository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Class to store information about field.
 */
@Getter
@RequiredArgsConstructor
public final class InternalField {
    @NotNull
    private final Class<?> clazz;
    @NotNull
    private final Field field;
    @Nullable
    private final Method setter;
    @Nullable
    private final Method getter;
}
