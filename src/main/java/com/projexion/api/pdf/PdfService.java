/*
 * Copyright (c) 2025 ProjXion. All rights reserved.
 */
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
import java.io.InputStream;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Base64;
import java.util.regex.Matcher;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

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
//                String mimeType = view.equalsIgnoreCase("png") ? MimeConstants.MIME_PNG : MimeConstants.MIME_PDF;
                String mimeType =  MimeConstants.MIME_PDF;
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
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            result = result.replaceAll(key, Matcher.quoteReplacement(value));
        }
        return result;
    }

    /**
     * Generates a QR code and saves it to a temporary file, returns file:// URL
     */
    private String generateQrCode(String data, int width, int height) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, width, height);
            
            Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
            Files.createDirectories(tempDir);
            Path qrFile = Files.createTempFile(tempDir, "qr_", ".png");
            
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", qrFile);
            
            String fileUrl = qrFile.toUri().toString();
            LOGGER.info("QR code generated at: " + fileUrl);
            return fileUrl;
        } catch (WriterException | IOException e) {
            LOGGER.error("Error generating QR code: " + e.getMessage(), e);
            return "";
        }
    }

    /**
     * Converts PdfEntity → Map<String, Object> for template filling
     */
    private Map<String, Object> dtoToMap(PdfEntity dto) {
        Map<String, Object> map = new HashMap<>();
        DecimalFormat df = new DecimalFormat("#,##0.00");
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
     // map.put("invoiceIssueLocation", dto.getInvoiceIssueLocation());
        map.put("invoiceIssueDate", formatedDate());
     // map.put("invoiceIssueDate", dto.getInvoiceIssueDate());
        map.put("invoiceNumber", dto.getInvoiceNumber());
        map.put("invoiceTitle", dto.getInvoiceTitle());
        map.put("comment", dto.getComment());
        map.put("priceNote", dto.getPriceNote());

        String qrCodeData = "Invoice: " + dto.getInvoiceNumber() + " | IBAN: CH50 0900 0000 9136 7960 5";
        String qrCodeBase64 = generateQrCode(qrCodeData, 200, 200);
        LOGGER.info("QR Code generated with length: " + qrCodeBase64.length());
        map.put("qrCode", qrCodeBase64);
        //  map.put("bankName", dto.getBankName());
        // map.put("iban", dto.getIban());
       //  map.put("swift", dto.getSwift());
        map.put("bankName", "Swiss PostFinance Bern ");
        map.put("iban", "CH50 0900 0000 9136 7960 5");
        map.put("accountNumber", "91-387960-5");
        map.put("swift", "POFICHBEXXXX");
        map.put("companyAddress", "Dschoint Ventschr Filmproduktion AG, Molkenstrasse 21, 8004 Zürich");


        map.put("exchangeRate", dto.getExchangeRate());
        map.put("totalEur", dto.getTotalEur());
        map.put("totalChf", dto.getTotalChf());

        StringBuilder priceBlocks = new StringBuilder();
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal taxRate = new BigDecimal("0.081"); // 8.1%

// format to 2 decimals
//        DecimalFormat df = new DecimalFormat("#,##0.00");

// build each price line
        for (PdfEntity.PriceItemDto item : dto.getPriceList()) {
            priceBlocks.append(
                    "<fo:block space-before=\"12pt\" font-size=\"12pt\" text-align-last=\"justify\">" +
                            "<fo:inline>" + item.getDescription() + "</fo:inline>" +
                            "<fo:leader leader-pattern=\"space\" leader-length.optimum=\"100%\"/>" +
                            "<fo:inline>" + df.format(item.getAmount()) + "</fo:inline>" +
                            "</fo:block>"
            );
            if (item.getAmount() != null)
                subtotal = subtotal.add(item.getAmount());
        }

// calculate tax and totals
        BigDecimal taxAmount = subtotal.multiply(taxRate);
        BigDecimal totalEur = subtotal.add(taxAmount);
        BigDecimal totalChf = dto.getExchangeRate() != null
                ? totalEur.multiply(dto.getExchangeRate())
                : BigDecimal.ZERO;

// append subtotal and totals in FO
        priceBlocks.append(
                "<fo:block space-before=\"12pt\" font-size=\"12pt\" border-top=\"0.5pt solid black\" " +
                        "font-weight=\"bold\" text-align-last=\"justify\">" +
                        "<fo:inline>Summe EUR</fo:inline>" +
                        "<fo:leader leader-pattern=\"space\" leader-length.optimum=\"100%\"/>" +
                        "<fo:inline>" + df.format(subtotal) + "</fo:inline>" +
                        "</fo:block>" +

                        "<fo:block space-before=\"5pt\" font-size=\"10pt\" text-align-last=\"justify\">" +
                        "<fo:inline>MWSt 8.1% EUR</fo:inline>" +
                        "<fo:leader leader-pattern=\"space\"/>" +
                        "<fo:inline>" + df.format(taxAmount) + "</fo:inline>" +
                        "</fo:block>" +

                        "<fo:block border-top=\"1pt solid black\" font-size=\"12pt\" font-weight=\"bold\" text-align-last=\"justify\">" +
                        "<fo:inline>Rechnungsbetrag EUR</fo:inline>" +
                        "<fo:leader leader-pattern=\"space\"/>" +
                        "<fo:inline>" + df.format(totalEur) + "</fo:inline>" +
                        "</fo:block>" +

                        "<fo:block space-before=\"8pt\" text-align-last=\"justify\">" +
                        "<fo:inline>Total in CHF zum Kurs: " +
                        (dto.getExchangeRate() != null ? df.format(dto.getExchangeRate()) : "?") +
                        "</fo:inline>" +
                        "<fo:leader leader-pattern=\"space\"/>" +
                        "<fo:inline>" + (dto.getExchangeRate() != null ? df.format(totalChf) : "?") + "</fo:inline>" +
                        "</fo:block>"
        );

// put values in the map
        map.put("priceItems", priceBlocks.toString());
        map.put("subtotal", subtotal);
        map.put("taxAmountEur", taxAmount);
        map.put("totalEur", totalEur);
        map.put("totalChf", totalChf);


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
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("META-INF/resources/template/invoice.xsl")) {
            if (in == null) {
                throw new RuntimeException("Template file not found in : META-INF/resources/template/invoice.xsl");
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Error reading template file", e);
        }
    }
}

