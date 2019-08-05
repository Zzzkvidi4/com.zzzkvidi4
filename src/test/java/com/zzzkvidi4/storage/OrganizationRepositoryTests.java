package com.zzzkvidi4.storage;

import com.opentable.db.postgres.embedded.FlywayPreparer;
import com.opentable.db.postgres.junit.EmbeddedPostgresRules;
import com.opentable.db.postgres.junit.PreparedDbRule;
import com.zzzkvidi4.storage.model.Organization;
import com.zzzkvidi4.storage.repository.DataSource;
import com.zzzkvidi4.storage.repository.OrganizationRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public final class OrganizationRepositoryTests {
    @Rule
    @NotNull
    public PreparedDbRule preparedDbRule = EmbeddedPostgresRules.preparedDatabase(FlywayPreparer.forClasspathLocation("db/migration"));

    @Nullable
    private OrganizationRepository organizationRepository;

    @Before
    public void setUp() {
        organizationRepository = new OrganizationRepository(new DataSource("jdbc:postgresql://localhost:" + preparedDbRule.getConnectionInfo().getPort() + "/" + preparedDbRule.getConnectionInfo().getDbName(), "postgres", "postgres"));
    }

    @Test
    public void whenFindByIdExistingEntityItReturns() {
        Optional<Organization> organizationOpt = organizationRepository.findById("1");
        assertTrue(organizationOpt.isPresent());
        Organization organization = organizationOpt.get();
        assertEqualsOrganization(organization, "ibm", "111", "111111111", "1");
    }

    @Test
    public void whenFindNotExistingEntityEmptyOptionalReturned() {
        Optional<Organization> organizationOpt = organizationRepository.findById("700");
        assertFalse(organizationOpt.isPresent());
    }

    @Test
    public void whenUpdatedExistingEntityItUpdated() {
        Optional<Organization> organizationOpt = organizationRepository.findById("2");
        assertTrue(organizationOpt.isPresent());
        Organization organization = organizationOpt.get();
        String newName = "ibm 2";
        organization.setName(newName);
        String newAccount = "251";
        organization.setAccount(newAccount);
        String newItn = "1111111111";
        organization.setItn(newItn);
        organizationRepository.update(organization);
        organizationOpt = organizationRepository.findById("2");
        assertTrue(organizationOpt.isPresent());
        assertEqualsOrganization(organizationOpt.get(), newName, newAccount, newItn, "2");
    }

    @Test
    public void whenUpdatedNonexistentEntityNothingHappens() {
        Organization organization = new Organization("id", "name", "itn", "account");
        boolean updated = organizationRepository.update(organization);
        assertFalse(updated);
    }

    @Test
    public void whenInsertCorrectEntityItCreated() {
        String id = "id";
        String name = "name";
        String itn = "itn";
        String account = "account";
        Organization organization = new Organization(id, name, itn, account);

        boolean created = organizationRepository.create(organization);
        assertTrue(created);
        Optional<Organization> organizationOpt = organizationRepository.findById(id);
        assertTrue(organizationOpt.isPresent());
        assertEqualsOrganization(organizationOpt.get(), name, account, itn, id);
    }

    @Test
    public void testDeleteNotConnectedEntity() {
        Optional<Organization> organization = organizationRepository.findById("13");
        assertTrue(organization.isPresent());
        List<Organization> organizations = organizationRepository.findAll();
        assertTrue(organizationRepository.deleteById("13"));

        Optional<Organization> deletedOrganization = organizationRepository.findById("13");
        assertFalse(deletedOrganization.isPresent());
        List<Organization> organizationAfterDelete = organizationRepository.findAll();
        assertEquals(organizations.size() - 1, organizationAfterDelete.size());
        assertTrue(organizations.containsAll(organizationAfterDelete));
        assertFalse(organizationAfterDelete.stream().anyMatch(o -> "13".equals(o.getId())));
    }

    @Test(expected = RuntimeException.class)
    public void testDeleteConnectedEntity() {
        organizationRepository.deleteById("1");
    }

    private void assertEqualsOrganization(@NotNull Organization organization, @NotNull String name, @NotNull String account, @NotNull String itn, @NotNull String id) {
        assertEquals(name, organization.getName());
        assertEquals(account, organization.getAccount());
        assertEquals(itn, organization.getItn());
        assertEquals(id, organization.getId());
    }
}
