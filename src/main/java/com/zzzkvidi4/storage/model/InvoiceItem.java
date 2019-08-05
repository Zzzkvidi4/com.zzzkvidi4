package com.zzzkvidi4.storage.model;

import com.zzzkvidi4.storage.annotation.Column;
import com.zzzkvidi4.storage.annotation.Id;
import com.zzzkvidi4.storage.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

/**
 * Invoice item entity class.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("invoice_item")
public final class InvoiceItem {
    @Id
    @Nullable
    @Column("invoice_item_id")
    private String id;
    @Nullable
    @Column("invoice_id")
    private String invoiceId;
    @Nullable
    @Column("item_id")
    private String itemId;
    @Column("price")
    private int price;
    @Column("volume")
    private double volume;
}
