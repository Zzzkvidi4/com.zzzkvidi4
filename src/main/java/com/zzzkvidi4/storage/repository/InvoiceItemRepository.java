package com.zzzkvidi4.storage.repository;

import com.zzzkvidi4.storage.model.InvoiceItem;
import org.jetbrains.annotations.NotNull;

/**
 * Invoice item implementation of {@link Repository}.
 */
public final class InvoiceItemRepository extends Repository<InvoiceItem, String> {
    public InvoiceItemRepository(@NotNull DataSource dataSource) {
        super(dataSource);
    }
}
