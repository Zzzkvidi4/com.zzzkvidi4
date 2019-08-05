package com.zzzkvidi4.storage.repository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * Class to store connection information.
 */
@Getter
@RequiredArgsConstructor
public final class DataSource {
    @NotNull
    private final String url;
    @NotNull
    private final String name;
    @NotNull
    private final String password;
}
