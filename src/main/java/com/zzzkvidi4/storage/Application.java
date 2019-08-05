package com.zzzkvidi4.storage;


import org.flywaydb.core.Flyway;
import org.jetbrains.annotations.NotNull;

public final class Application {
    public static void main(@NotNull String[] args) {
        if (args.length < 2) {
            throw new RuntimeException("Not enough arguments!");
        }
        Flyway flyway = new Flyway(Flyway.configure().dataSource(args[0], args[1], args.length >= 3 ? args[2] : null));
        flyway.migrate();
    }
}
