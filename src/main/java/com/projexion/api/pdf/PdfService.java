package com.projexion.api.pdf;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.jboss.logging.Logger;

import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class PdfService {
    private static final Logger LOGGER = Logger.getLogger(PdfService.class.getName());
    private static final FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
    private static final TransformerFactory transformerFactory = TransformerFactory.newInstance();

    /**
     * Builds a PDF from frontend JSON (mapped to PdfEntity)
     */
    public byte[] generatePdf(PdfEntity dto) {
        // Convert DTO to a simple map for placeholder replacement
          Map<String, Object> data = dtoToMap(createSampleInvoice());

        return buildPdf(data);
    }

    /**
     * Builds a PDF using a named FO template and data map from JSON
     */
    public byte[] buildPdf(Map<String, Object> data) {
        try {
            String foTemplate = findTemplateByName();
            String filledFo = fillTemplate(foTemplate, data);
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
                Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);
                Source src = new StreamSource(new StringReader(filledFo));
                Transformer transformer = transformerFactory.newTransformer();
                Result res = new SAXResult(fop.getDefaultHandler());
                transformer.transform(src, res);
                return out.toByteArray();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
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
        map.put("clientName", dto.getClientName());
        map.put("clientAddress", dto.getClientAddress());
        map.put("clientCity", dto.getClientCity());
        map.put("fiscalCode", dto.getFiscalCode());
        map.put("vatNumber", dto.getVatNumber());
        map.put("city", dto.getCity());
        map.put("date", dto.getDate());
        map.put("invoiceNumber", dto.getInvoiceNumber());
        map.put("invoiceTitle", dto.getInvoiceTitle());
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

    /**
     * Example static data for testing without frontend
     */
    public PdfEntity createSampleInvoice() {
        PdfEntity invoice = new PdfEntity();
        invoice.setClientName("Cineteatro Victoria - Parrocchia San Lorenzo");
        invoice.setClientAddress("Via Picchi 2");
        invoice.setClientCity("23022 Chiavenna");
        invoice.setFiscalCode("81000030148");
        invoice.setVatNumber("IT 00477190144");
        invoice.setCity("Zürich");
        invoice.setDate("30. Januar 2025");
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

//            """
//            <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
//              <fo:layout-master-set>
//                <fo:simple-page-master master-name="A4" page-height="29.7cm" page-width="21cm" margin="2cm">
//                  <fo:region-body/>
//                </fo:simple-page-master>
//              </fo:layout-master-set>
//              <fo:page-sequence master-reference="A4">
//
//                <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
//                  <fo:block font-size="12pt" font-weight="bold">${clientName}</fo:block>
//                  <fo:block>${clientAddress}</fo:block>
//                  <fo:block>${clientCity}</fo:block>
//                  <fo:block space-after="8pt">Fiscal Code: ${fiscalCode} | VAT: ${vatNumber}</fo:block>
//                  <fo:block text-align="right">${city}, ${date}</fo:block>
//                  <fo:block font-size="14pt" font-weight="bold" space-after="6pt">
//                    Rechnung Nr. ${invoiceNumber}
//                  </fo:block>
//                  <fo:block font-size="10pt" space-after="10pt">${invoiceTitle}</fo:block>
//                  <fo:block space-after="10pt">${priceListText}</fo:block>
//                  <fo:block font-weight="bold" space-before="10pt">
//                    Total EUR: ${totalEur}
//                  </fo:block>
//                  <fo:block font-weight="bold" space-before="4pt">
//                    Total CHF: ${totalChf}
//                  </fo:block>
//                  <fo:block font-size="10pt" space-before="10pt">
//                    Bank: ${bankName} | IBAN: ${iban} | SWIFT: ${swift}
//                  </fo:block>
//                </fo:flow>
//              </fo:page-sequence>
//            </fo:root>
//        """;



    }


}

