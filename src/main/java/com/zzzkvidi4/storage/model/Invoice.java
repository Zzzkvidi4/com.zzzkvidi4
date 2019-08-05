package com.zzzkvidi4.storage.model;

import com.zzzkvidi4.storage.annotation.Column;
import com.zzzkvidi4.storage.annotation.Id;
import com.zzzkvidi4.storage.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

/**
 * Invoice entity class.
 */
@Data
@Table("invoice")
@NoArgsConstructor
@AllArgsConstructor
public final class Invoice {
    @Id
    @Nullable
    @Column("invoice_id")
    private String id;
    @Nullable
    @Column("date")
    private Instant date;
    @Nullable
    @Column("organization_id")
    private String organization;
}
