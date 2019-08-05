package com.zzzkvidi4.storage.model;

import com.zzzkvidi4.storage.annotation.Column;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Projection to build report.
 */
@Data
public final class OrganizationWithItem {
    @Nullable
    @Column("organization_id")
    private String organizationId;
    @Nullable
    @Column("organization_name")
    private String organizationName;
    @Nullable
    @Column("organization_itn")
    private String organizationItn;
    @Nullable
    @Column("organization_account")
    private String organizationAccount;
    @Nullable
    @Column("item_id")
    private String itemId;
    @Nullable
    @Column("item_name")
    private String itemName;
    @Nullable
    @Column("item_code")
    private String itemCode;

    @NotNull
    public Organization getOrganization() {
        return new Organization(organizationId, organizationName, organizationItn, organizationAccount);
    }

    @NotNull
    public Item getItem() {
        return new Item(itemId, itemName, itemCode);
    }
}
