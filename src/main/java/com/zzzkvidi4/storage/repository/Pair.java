package com.zzzkvidi4.storage.repository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

/**
 * Util class to store pair of values.
 *
 * @param <T1> - type of value 1
 * @param <T2> - type of value 2
 */
@Getter
@RequiredArgsConstructor
public final class Pair<T1, T2> {
    @Nullable
    private final T1 value1;
    @Nullable
    private final T2 value2;
}
