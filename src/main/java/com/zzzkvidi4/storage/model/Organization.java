package com.zzzkvidi4.storage.model;

import com.zzzkvidi4.storage.annotation.Column;
import com.zzzkvidi4.storage.annotation.Id;
import com.zzzkvidi4.storage.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

/**
 * Organization entity class.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("organization")
public final class Organization {
    @Id
    @Nullable
    @Column("organization_id")
    private String id;
    @Nullable
    @Column("name")
    private String name;
    @Nullable
    @Column("itn")
    private String itn;
    @Nullable
    @Column("account")
    private String account;
}
