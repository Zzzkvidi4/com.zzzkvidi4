package com.zzzkvidi4.storage.repository;

import com.zzzkvidi4.storage.model.Invoice;
import org.jetbrains.annotations.NotNull;

/**
 * Invoice implementation of {@link Repository}.
 */
public final class InvoiceRepository extends Repository<Invoice, String> {
    public InvoiceRepository(@NotNull DataSource dataSource) {
        super(dataSource);
    }
}
