package com.zzzkvidi4.storage.repository;

import com.zzzkvidi4.storage.model.Organization;
import org.jetbrains.annotations.NotNull;

/**
 * Organization implementation of {@link Repository}.
 */
public final class OrganizationRepository extends Repository<Organization, String> {
    public OrganizationRepository(@NotNull DataSource dataSource) {
        super(dataSource);
    }
}
