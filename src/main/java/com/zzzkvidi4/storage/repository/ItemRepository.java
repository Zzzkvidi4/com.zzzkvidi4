package com.zzzkvidi4.storage.repository;

import com.zzzkvidi4.storage.model.Item;
import org.jetbrains.annotations.NotNull;

/**
 * Item implementation of {@link Repository}.
 */
public final class ItemRepository extends Repository<Item, String> {
    public ItemRepository(@NotNull DataSource dataSource) {
        super(dataSource);
    }
}
