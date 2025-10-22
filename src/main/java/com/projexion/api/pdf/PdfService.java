package com.projexion.api.pdf;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.fop.apps.FOPException;
import org.jboss.logging.Logger;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@ApplicationScoped
public class PdfService {
    private static final Logger LOGGER = Logger.getLogger(PdfService.class.getName());
    private static final FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
    private static final TransformerFactory transformerFactory = TransformerFactory.newInstance();

    /**
     * Builds a PDF from frontend JSON (mapped to PdfEntity)
     */
    public Uni<byte[]> generatePdf(PdfEntity dto, String view) {
        Map<String, Object> data = dtoToMap(dto);
        // Map<String, Object> data = dtoToMap(createSampleInvoice());
        return buildPdf(data, view);
    }

    /**
     * Builds a PDF using a named FO template and data map from JSON
     */
    public Uni<byte[]> buildPdf(Map<String, Object> data, String view) {
        return Uni.createFrom().item(() -> {
            String foTemplate = findTemplateByName();
            String filledFo = fillTemplate(foTemplate, data);

            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
                String mimeType = view.equalsIgnoreCase("png") ? MimeConstants.MIME_PNG : MimeConstants.MIME_PDF;
                Fop fop = fopFactory.newFop(mimeType, foUserAgent, out);

                Transformer transformer = transformerFactory.newTransformer();
                Source src = new StreamSource(new StringReader(filledFo));
                Result res = new SAXResult(fop.getDefaultHandler());

                transformer.transform(src, res);
                return out.toByteArray();
            } catch (FOPException | TransformerException e ) {
                throw new RuntimeException(e);
            }
        }).onFailure().recoverWithItem(() -> {
            throw new RuntimeException("Error generating output");
        });
    }


    /**
     * Replace placeholders like ${name} with values from map
     */
    private String fillTemplate(String foTemplate, Map<String, Object> data) {
        String result = foTemplate;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = "\\$\\{" + entry.getKey() + "\\}";
            result = result.replaceAll(key, entry.getValue() != null ? entry.getValue().toString() : "");
        }
        return result;
    }

    /**
     * Converts PdfEntity → Map<String, Object> for template filling
     */
    private Map<String, Object> dtoToMap(PdfEntity dto) {
        Map<String, Object> map = new HashMap<>();
        map.put("pdfCreator", dto.getPdfCreator());
        map.put("clientNamePrefix", dto.getClientNamePrefix());
        map.put("clientFirstname", dto.getClientFirstname());
        map.put("clientLastName", dto.getClientLastname());
        map.put("clientAddress", dto.getClientAddress());
        map.put("clientPostCode", dto.getClientPostCode());
        map.put("clientCountry", dto.getClientCountry());
        map.put("clientCity", dto.getClientCity());

        map.put("vatNumber", dto.getVatNumber());
        map.put("invoiceIssueLocation", "Zürich");
     //  map.put("invoiceIssueLocation", dto.getInvoiceIssueLocation());
        map.put("invoiceIssueDate", formatedDate());
    //  map.put("invoiceIssueDate", dto.getInvoiceIssueDate());
        map.put("invoiceNumber", dto.getInvoiceNumber());
        map.put("invoiceTitle", dto.getInvoiceTitle());
        map.put("comment", dto.getComment());
        map.put("bankName", dto.getBankName());
        map.put("iban", dto.getIban());
        map.put("swift", dto.getSwift());
        map.put("exchangeRate", dto.getExchangeRate());
        map.put("totalEur", dto.getTotalEur());
        map.put("totalChf", dto.getTotalChf());

        // For item list, build a string (since FOP doesn’t process JSON arrays directly)
        if (dto.getPriceList() != null) {
            StringBuilder sb = new StringBuilder();
            for (PdfEntity.PriceItemDto item : dto.getPriceList()) {
                sb.append(item.getDescription()).append(" - ")
                        .append(item.getPrice()).append(" EUR\n");
            }
            map.put("priceListText", sb.toString());
        } else {
            map.put("priceListText", "");
        }
        return map;
    }

   public String formatedDate() {
        Instant now = Instant.now();
        ZonedDateTime zonedDateTime = now.atZone(ZoneId.of("Europe/Berlin"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d. MMMM yyyy", Locale.GERMAN);
        return zonedDateTime.format(formatter);   // e.g., "22. Oktober 2025"


    }


    /**
     * Example static data for testing without frontend
     */
    public PdfEntity createSampleInvoice() {
        PdfEntity invoice = new PdfEntity();
        invoice.setClientFirstname("Cineteatro Victoria");
        invoice.setClientLastname("Parrocchia San Lorenzo");
        invoice.setClientAddress("Via Picchi 2");
        invoice.setClientPostCode("81000030148");
        invoice.setClientCity("Zürich");
        invoice.setClientCountry("CH");
        invoice.setVatNumber("IT 00477190144");
        invoice.setInvoiceIssueDate("30. Januar 2025");
        invoice.setInvoiceNumber("6737");
        invoice.setInvoiceTitle("Screening Fee of Giacometti by Susanna Fanzun");
        invoice.setBankName("Swiss PostFinance Bern");
        invoice.setIban("CH50 0900 0000 9136 7960 5");
        invoice.setSwift("POFICHBEXXXX");
        invoice.setExchangeRate(new BigDecimal("0.96"));
        invoice.setPriceList(List.of(
                new PdfEntity.PriceItemDto("1x Screening Fee", new BigDecimal("250.00"))
        ));
        return invoice;
    }

    /**
     * FO template (XSL-FO) with placeholders
     */
    public String findTemplateByName() {
        File file = new File("template/invoice.xsl");
        if (!file.exists()) {
            throw new RuntimeException("Template file not found: " + file.getAbsolutePath());
        }
        try {
            return new String(Files.readAllBytes(Paths.get(file.getPath())));
        } catch (IOException e) {
            throw new RuntimeException("Error reading template file", e);
        }
    }
}

