<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:layout-master-set>
        <fo:simple-page-master master-name="A4" page-height="29.7cm" page-width="21cm" margin="2cm">
            <fo:region-body/>
        </fo:simple-page-master>
    </fo:layout-master-set>

    <fo:page-sequence master-reference="A4">
        <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
            <!-- Sender Information (Left-aligned blocks) -->
            <fo:block-container font-size="11pt" absolute-position="absolute" top="20">
                <fo:block>${clientNamePrefix}</fo:block>
                <fo:block>${clientFirstname} - ${clientLastName}</fo:block>
                <fo:block>${clientAddress}</fo:block>
                <fo:block>${clientPostCode} ${clientCity}</fo:block>
<!--                <fo:block space-before="10pt">Fiscal Code ${fiscalCode}</fo:block>-->
<!--                <fo:block>VAT Number ${vatNumber}</fo:block>-->
            </fo:block-container>

            <!-- Date and Reference (Blocks with absolute positioning) -->
            <fo:block-container absolute-position="absolute" top="5.5cm">
                <fo:block>
                    <fo:inline>${invoiceIssueLocation}, ${invoiceIssueDate}</fo:inline>
                </fo:block>
            </fo:block-container>

            <fo:block-container absolute-position="absolute" left="8cm" top="5.1cm">
                <fo:block>
                    <fo:block text-align="right" font-size="9pt">unser Zeichen:</fo:block>
                    <fo:block text-align="right" font-size="10pt">${pdfCreator}</fo:block>
                </fo:block>
            </fo:block-container>

            <!-- Invoice Header (Using leader for spacing) -->
            <fo:block-container absolute-position="absolute" top="8cm">
                <fo:block font-size="15pt" border-bottom-style="solid">
                    <fo:inline  font-weight="bold">Rechnung</fo:inline>
                    <fo:leader leader-length="20mm"/>
                    <fo:inline>Nr. ${invoiceNumber}</fo:inline>
                    <fo:leader leader-length="40mm"/>
                    <fo:inline font-size="12pt">CHE-106.409.517 MWST</fo:inline>
                </fo:block>
                <fo:block font-size="12pt" margin-top="10px">${comment}</fo:block>
            </fo:block-container>

            <!-- Price -->
            <fo:block-container absolute-position="absolute"
                                top="12cm"
                                padding-top="0.5cm">
                <fo:block text-align="right"  border-bottom-style="solid"
                          border-bottom-width="1pt">Preis in EUR</fo:block>
                <fo:block space-before="0.5cm" text-align-last="justify">
                    <fo:inline>1. Screening Fee</fo:inline>
                    <fo:leader leader-pattern="space" leader-length.optimum="100%" />
                    <fo:inline>${screeningFee}</fo:inline>
                </fo:block>
            </fo:block-container>

            <!-- Subtotal -->
            <fo:block-container absolute-position="absolute" top="14cm">

            <fo:block space-before="0.5cm" font-weight="bold" text-align="right">Summe EUR ${subtotal}</fo:block>

            <!-- Totals Section (Manual alignment with leader) -->
            <fo:block space-before="1cm" text-align-last="justify">
                <fo:inline/>
                <fo:leader leader-pattern="space" leader-length.optimum="100%" />
                <fo:inline>EUR</fo:inline>
                <fo:leader leader-pattern="space" leader-length.optimum="100%" />
                <fo:inline>${taxBaseEur}</fo:inline>
            </fo:block>
            <fo:block text-align-last="justify">
                <fo:inline>MWSt 8.1% EUR</fo:inline>
                <fo:leader leader-pattern="space" leader-length.optimum="100%" />
                <fo:inline>EUR</fo:inline>
                <fo:leader leader-pattern="space" leader-length.optimum="100%" />
                <fo:inline>${taxAmountEur}</fo:inline>
            </fo:block>
            <fo:block text-align-last="justify" border-before-style="solid" border-before-width="1pt" font-weight="bold">
                <fo:inline>Rechnungsbetrag EUR</fo:inline>
                <fo:leader leader-pattern="space" leader-length.optimum="100%" />
                <fo:inline>${totalEur}</fo:inline>
            </fo:block>
                <fo:block space-before="1cm" font-size="10pt">Total in CHF zum Kurs: </fo:block>
                <!-- Payment Terms -->
                <fo:block space-before="2cm" font-size="10pt">Zahlbar rein netto innert 10 Tagen</fo:block>
            </fo:block-container>


            <!-- Footer with bank information (Blocks with leader for spacing) -->
            <fo:block-container position="absolute"  top="700" left="2cm" right="2cm">
                <fo:block  space-before="1cm" space-after="0.5cm"></fo:block>
                <fo:block font-size="8pt" text-align-last="justify" line-stacking-strategy="max-height">
                    <fo:inline>Bank: ${bankName}</fo:inline>
                    <fo:leader leader-pattern="space" />
                    <fo:inline>IBAN: ${iban}</fo:inline>
                    <fo:leader leader-pattern="space" />
                    <fo:inline>SWIFT: ${swift}</fo:inline>
                    <fo:leader leader-pattern="space" />
                    <fo:inline>Konto: ${accountNumber}</fo:inline>
                </fo:block>
                <fo:block font-size="8pt" space-before="5pt" text-align="center">${companyAddress}</fo:block>
            </fo:block-container>
        </fo:flow>
    </fo:page-sequence>
</fo:root>