package com.zzzkvidi4.storage.service;

import com.zzzkvidi4.storage.model.Item;
import com.zzzkvidi4.storage.model.Organization;
import com.zzzkvidi4.storage.model.OrganizationWithItem;
import com.zzzkvidi4.storage.repository.OrganizationRepository;
import com.zzzkvidi4.storage.repository.Pair;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.*;

import static java.util.stream.Collectors.*;

/**
 * Service to calculate reports.
 */
@RequiredArgsConstructor
public final class OrganizationService {
    @NotNull
    private final OrganizationRepository organizationRepository;


    @NotNull
    public List<Organization> findTenTheMostActiveOrganizations() {
        return organizationRepository.findAllByQuery("" +
                "WITH inc_count AS (\n" +
                "  SELECT o.*, SUM(ii.volume) AS volume\n" +
                "  FROM organization o\n" +
                "      INNER JOIN invoice i on o.organization_id = i.organization_id\n" +
                "      INNER JOIN invoice_item ii on i.invoice_id = ii.invoice_id\n" +
                "  GROUP BY o.organization_id, o.name, o.itn, o.account\n" +
                ")\n" +
                "SELECT name, itn, organization_id, account\n" +
                "FROM inc_count\n" +
                "ORDER BY volume DESC\n" +
                "LIMIT 10\n"
        );
    }

    @NotNull
    public List<Organization> findOrganizationsWithItemsGreaterThan(@NotNull List<Pair<String, Double>> itemsWithVolume) {
        if (itemsWithVolume.isEmpty()) {
            return new LinkedList<>();
        }

        String query = "" +
                "WITH inc_count AS (\n" +
                "  SELECT o.*, SUM(ii.volume) AS volume\n" +
                "  FROM organization o\n" +
                "      INNER JOIN invoice i on o.organization_id = i.organization_id\n" +
                "      INNER JOIN invoice_item ii on i.invoice_id = ii.invoice_id\n" +
                "  WHERE ii.item_id = ?\n" +
                "  GROUP BY o.organization_id, o.name, o.itn, o.account\n" +
                "  HAVING SUM(ii.volume) > ?\n" +
                ")\n" +
                "SELECT name, itn, organization_id, account\n" +
                "FROM inc_count\n";
        Set<Organization> result = new HashSet<>();
        boolean organizationsInitialized = false;
        for (Pair<String, Double> itemWithVolume : itemsWithVolume) {
            List<Organization> organizations = organizationRepository.findAllByQuery(query, itemWithVolume.getValue1(), itemWithVolume.getValue2());
            if (!organizationsInitialized) {
                result.addAll(organizations);
                organizationsInitialized = true;
            }
            result.retainAll(organizations);
        }
        return new ArrayList<>(result);
    }

    @NotNull
    public Map<Organization, Set<Item>> getOrganizationsWithItems(@NotNull LocalDate startInclusive, @NotNull LocalDate endExclusive) {
        List<OrganizationWithItem> organizationWithItems = organizationRepository.findAllByQuery(
                OrganizationWithItem.class,
                "" +
                        "SELECT DISTINCT\n" +
                        "  o.organization_id AS organization_id,\n" +
                        "  o.name AS organization_name,\n" +
                        "  o.itn AS organization_itn,\n" +
                        "  o.account AS organization_account,\n" +
                        "  it.item_id AS item_id,\n" +
                        "  it.name AS item_name,\n" +
                        "  it.code AS item_code\n" +
                        "FROM organization o\n" +
                        "    LEFT JOIN (SELECT * FROM invoice i WHERE i.date >= ? AND i.date < ?) AS inv ON o.organization_id = inv.organization_id\n" +
                        "    LEFT JOIN invoice_item ii ON inv.invoice_id = ii.invoice_id\n" +
                        "    LEFT JOIN item it ON ii.item_id = it.item_id",
                startInclusive,
                endExclusive
        );
        Map<Organization, Set<Item>> organizationSetMap = organizationWithItems.stream()
                .collect(
                        groupingBy(
                                OrganizationWithItem::getOrganization,
                                mapping(OrganizationWithItem::getItem, toSet())
                        )
                );
        Item emptyItem = new Item();
        organizationSetMap.values().forEach(s -> s.remove(emptyItem));
        return organizationSetMap;
    }
}
