package com.zzzkvidi4.storage;

import com.opentable.db.postgres.embedded.ConnectionInfo;
import com.opentable.db.postgres.embedded.FlywayPreparer;
import com.opentable.db.postgres.junit.EmbeddedPostgresRules;
import com.opentable.db.postgres.junit.PreparedDbRule;
import com.zzzkvidi4.storage.repository.DataSource;
import com.zzzkvidi4.storage.repository.InvoiceItemRepository;
import com.zzzkvidi4.storage.repository.InvoiceRepository;
import com.zzzkvidi4.storage.service.InvoiceService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.Assert.*;

public final class InvoiceServiceTests {
    @Rule
    @NotNull
    public PreparedDbRule db = EmbeddedPostgresRules.preparedDatabase(FlywayPreparer.forClasspathLocation("db/migration"));
    @Nullable
    private InvoiceService invoiceService;

    @Before
    public void setUp() {
        ConnectionInfo connectionInfo = db.getConnectionInfo();
        DataSource dataSource = new DataSource("jdbc:postgresql://localhost:" + connectionInfo.getPort() + "/" + connectionInfo.getDbName(), "postgres", "postgres");
        invoiceService = new InvoiceService(new InvoiceRepository(dataSource), new InvoiceItemRepository(dataSource));
    }

    @Test
    public void whenGetDailyInvoiceSummaryItIsCorrect() {
        InvoiceService.DailyInvoiceSummary dailyInvoiceSummary = invoiceService.getDailyInvoiceSummary(
                LocalDate.of(2019, 8, 1),
                LocalDate.of(2019, 8, 10)
        );
        Optional<InvoiceService.InvoiceSummary> summaryOpt = dailyInvoiceSummary.getSummary().get(LocalDate.of(2019, 8, 6));
        assertTrue(summaryOpt.isPresent());
        InvoiceService.InvoiceSummary summary = summaryOpt.get();
        assertEquals(34280, summary.getPrice());
        assertEquals(1040.0, summary.getVolume(), 0.001);
        System.out.println();
    }

    @Test
    public void whenGetAveragePriceItIsCorrect() {
        Double value = invoiceService.getAveragePrice(
                LocalDate.of(2018, 5, 20),
                LocalDate.of(2019, 8, 1)
        );
        assertNotNull(value);
        assertEquals(550.0, value, 0.001);

        value = invoiceService.getAveragePrice(
                LocalDate.of(2018, 5, 20),
                LocalDate.of(2019, 8, 3)
        );
        assertNotNull(value);
        assertEquals(337.5, value, 0.001);

        value = invoiceService.getAveragePrice(
                LocalDate.of(1970, 2, 1),
                LocalDate.of(1990, 3, 4)
        );

        assertNull(value);
        System.out.println();
    }
}
