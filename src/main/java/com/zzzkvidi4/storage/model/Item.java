package com.zzzkvidi4.storage.model;

import com.zzzkvidi4.storage.annotation.Column;
import com.zzzkvidi4.storage.annotation.Id;
import com.zzzkvidi4.storage.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

/**
 * Item class.
 */
@Data
@Table("item")
@NoArgsConstructor
@AllArgsConstructor
public final class Item {
    @Id
    @Nullable
    @Column("item_id")
    private String id;
    @Nullable
    @Column("name")
    private String name;
    @Nullable
    @Column("code")
    private String code;
}
