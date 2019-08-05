package com.zzzkvidi4.storage.service;

import com.zzzkvidi4.storage.model.Invoice;
import com.zzzkvidi4.storage.model.InvoiceItem;
import com.zzzkvidi4.storage.repository.InvoiceItemRepository;
import com.zzzkvidi4.storage.repository.InvoiceRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static java.util.stream.Collectors.*;

/**
 * Service to calculate reports.
 */
@RequiredArgsConstructor
public final class InvoiceService {
    @NotNull
    private final InvoiceRepository invoiceRepository;
    @NotNull
    private final InvoiceItemRepository invoiceItemRepository;

    @NotNull
    public DailyInvoiceSummary getDailyInvoiceSummary(@NotNull LocalDate startInclusive, @NotNull LocalDate endExclusive) {
        Map<String, LocalDate> invoices = invoiceRepository.findAllByQuery("" +
                "SELECT *\n" +
                "FROM invoice i\n" +
                "WHERE i.date >= ? AND i.date < ?",
                startInclusive,
                endExclusive
        ).stream().collect(toMap(Invoice::getId, i -> i.getDate().atZone(ZoneId.systemDefault()).toLocalDate()));

        if (invoices.isEmpty()) {
            return new DailyInvoiceSummary(new HashMap<>());
        }

        Map<LocalDate, Optional<InvoiceSummary>> invoiceItems = invoiceItemRepository.findAllByQuery("" +
                "SELECT *\n" +
                "FROM invoice_item ii\n" +
                "WHERE ii.invoice_id IN (" + getParamsString(invoices.size()) + ")",
                invoices.keySet().toArray()
        ).stream().collect(groupingBy(ii -> invoices.get(ii.getInvoiceId()), mapping(ii -> new InvoiceSummary(Math.round(ii.getPrice() * ii.getVolume()), ii.getVolume()), reducing(InvoiceSummary::sum))));
        return new DailyInvoiceSummary(invoiceItems);
    }

    @Nullable
    public Double getAveragePrice(@NotNull LocalDate startInclusive, @NotNull LocalDate endExclusive) {
        List<String> invoices = invoiceRepository.findAllByQuery("" +
                        "SELECT *\n" +
                        "FROM invoice i\n" +
                        "WHERE i.date >= ? AND i.date < ?",
                startInclusive,
                endExclusive
        ).stream().map(Invoice::getId).collect(toList());

        if (invoices.isEmpty()) {
            return null;
        }

        OptionalDouble averagePrice = invoiceItemRepository.findAllByQuery("" +
                "SELECT *\n" +
                "FROM invoice_item ii\n" +
                "WHERE ii.invoice_id IN (" + getParamsString(invoices.size()) + ")",
                invoices.toArray()
        ).stream().mapToInt(InvoiceItem::getPrice).average();
        if (averagePrice.isPresent()) {
            return averagePrice.getAsDouble();
        } else {
            return null;
        }
    }

    @NotNull
    String getParamsString(int size) {
        StringBuilder params = new StringBuilder();
        for (int i = 0; i < size; ++i) {
            if (i != 0) {
                params.append(", ");
            }
            params.append("?");
        }
        return params.toString();
    }

    @Getter
    @AllArgsConstructor
    public static final class InvoiceSummary {
        private long price;
        private double volume;

        @NotNull
        public InvoiceSummary sum(@NotNull InvoiceSummary invoiceSummary) {
            return new InvoiceSummary(this.price + invoiceSummary.price, this.volume + invoiceSummary.volume);
        }
    }

    @Getter
    public static final class DailyInvoiceSummary {
        @NotNull
        private final Map<LocalDate, Optional<InvoiceSummary>> summary;
        @NotNull
        private final InvoiceSummary invoiceSummary;

        public DailyInvoiceSummary(@NotNull Map<LocalDate, Optional<InvoiceSummary>> summary) {
            this.summary = summary;
            invoiceSummary = summary.values()
                    .stream()
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .reduce(InvoiceSummary::sum)
                    .orElseGet(() -> new InvoiceSummary(0, 0));
        }
    }
}
