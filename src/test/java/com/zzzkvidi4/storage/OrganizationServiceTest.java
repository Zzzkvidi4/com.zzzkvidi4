package com.zzzkvidi4.storage;

import com.opentable.db.postgres.embedded.ConnectionInfo;
import com.opentable.db.postgres.embedded.FlywayPreparer;
import com.opentable.db.postgres.junit.EmbeddedPostgresRules;
import com.opentable.db.postgres.junit.PreparedDbRule;
import com.zzzkvidi4.storage.model.Item;
import com.zzzkvidi4.storage.model.Organization;
import com.zzzkvidi4.storage.repository.DataSource;
import com.zzzkvidi4.storage.repository.OrganizationRepository;
import com.zzzkvidi4.storage.repository.Pair;
import com.zzzkvidi4.storage.service.OrganizationService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.*;

public final class OrganizationServiceTest {
    @Rule
    @NotNull
    public final PreparedDbRule db = EmbeddedPostgresRules.preparedDatabase(FlywayPreparer.forClasspathLocation("db/migration"));
    @Nullable
    private OrganizationService organizationService;
    @Nullable
    private OrganizationRepository organizationRepository;

    @Before
    public void setUp() {
        ConnectionInfo connectionInfo = db.getConnectionInfo();
        DataSource dataSource = new DataSource("jdbc:postgresql://localhost:" + connectionInfo.getPort() + "/" + connectionInfo.getDbName(), "postgres", "postgres");
        organizationRepository = new OrganizationRepository(dataSource);
        organizationService = new OrganizationService(organizationRepository);
    }

    @Test
    public void whenGetOrganizationsWithItemsForPeriodResultIsCorrect() {
        Map<Organization, Set<Item>> organizationsWithItems = organizationService.getOrganizationsWithItems(
                LocalDate.of(2018, 12, 1),
                LocalDate.of(2019, 7, 20)
        );
        Set<Organization> organizations = new HashSet<>(organizationRepository.findAll());
        assertTrue(organizationsWithItems.keySet().containsAll(organizations));
        for (Map.Entry<Organization, Set<Item>> organizationWithItems : organizationsWithItems.entrySet()) {
            Organization organization = organizationWithItems.getKey();
            if ("1".equals(organization.getId())) {
                assertEquals(2, organizationWithItems.getValue().size());
                assertEquals(new HashSet<>(asList("1", "2")), organizationWithItems.getValue().stream().map(Item::getId).collect(toSet()));
            } else {
                assertEquals(0, organizationWithItems.getValue().size());
            }
        }
    }

    @Test
    public void whenGetTenMostActiveOrganizationsListIsCorrect() {
        List<Organization> organizations = organizationService.findTenTheMostActiveOrganizations();
        List<String> organizationIds = organizations.stream().map(Organization::getId).collect(Collectors.toList());
        List<String> expectedOrder = asList("3", "6", "5", "2", "4", "1", "9", "8", "7");
        assertEquals(expectedOrder.size(), organizationIds.size());
        for (int i = 0; i < organizationIds.size(); i++) {
            String id = organizationIds.get(i);
            String expectedId = expectedOrder.get(i);
            assertEquals(expectedId, id);
        }
    }

    @Test
    public void whenGetOrganizationsWithMoreItemsItIsCorrect() {
        List<Organization> organizationsWithItemsGreaterThan = organizationService.findOrganizationsWithItemsGreaterThan(asList(new Pair<>("3", 15.0)));
        assertEquals(2, organizationsWithItemsGreaterThan.size());
        assertTrue(organizationsWithItemsGreaterThan.stream().map(Organization::getId).collect(toSet()).containsAll(asList("2", "3")));

        organizationsWithItemsGreaterThan = organizationService.findOrganizationsWithItemsGreaterThan(asList(new Pair<>("3", 15.0), new Pair<>("2", 1.0)));
        assertEquals(1, organizationsWithItemsGreaterThan.size());
        assertEquals("2", organizationsWithItemsGreaterThan.get(0).getId());
    }
}
