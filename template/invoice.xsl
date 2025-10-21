<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:layout-master-set>
        <fo:simple-page-master master-name="A4" page-height="29.7cm" page-width="21cm" margin="2cm">
            <fo:region-body/>
        </fo:simple-page-master>
    </fo:layout-master-set>

    <fo:page-sequence master-reference="A4">
        <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
            <!-- Sender Information (Left-aligned blocks) -->
            <fo:block-container font-size="11pt">
                <fo:block>Cineteatro Victoria - Parrocchia San</fo:block>
                <fo:block>Lorenzo</fo:block>
                <fo:block>Via Picchi 2</fo:block>
                <fo:block>23022 Chiavenna</fo:block>
                <fo:block space-before="10pt">Fiscal Code ${fiscalCode}</fo:block>
                <fo:block>VAT Number ${vatNumber}</fo:block>
            </fo:block-container>

            <!-- Date and Reference (Blocks with absolute positioning) -->
            <fo:block-container absolute-position="absolute" top="5.5cm" left="2cm" right="2cm">
                <fo:block>
                    <fo:inline>${city}, ${date}</fo:inline>
                    <fo:leader leader-pattern="space" leader-length.optimum="100%" />
                    <fo:block text-align="right" font-size="8pt">unser Zeichen:</fo:block>
                    <fo:block text-align="right" font-size="10pt">Sophie Stiermemann / Giacometti</fo:block>
                </fo:block>
            </fo:block-container>

            <!-- Invoice Header (Using leader for spacing) -->
            <fo:block space-before="5.5cm" font-size="14pt"  font-weight="bold" text-align-last="justify" line-stacking-strategy="max-height">
                <fo:inline>Rechnung</fo:inline>
                <fo:leader leader-pattern="space" leader-length.optimum="100%" />
                <fo:inline>Nr. ${invoiceNumber}</fo:inline>
            </fo:block>
            <fo:block space-before="0.5cm" font-weight="bold">CHE-106.409.517 MWST</fo:block>
            <fo:block space-before="0.5cm">${invoiceTitle}</fo:block>
            <fo:block border-after-style="solid" border-after-width="1pt" space-before="0.5cm" space-after="1cm"/>

            <fo:block-container border-bottom-style="solid" border-bottom-width="1pt" padding-bottom="0.5cm">
                <fo:block text-align="right">Preis in EUR</fo:block>
                <fo:block space-before="0.5cm" text-align-last="justify">
                    <fo:inline>1. Screening Fee</fo:inline>
                    <fo:leader leader-pattern="space" leader-length.optimum="100%" />
                    <fo:inline>${screeningFee}</fo:inline>
                </fo:block>
            </fo:block-container>

            <!-- Subtotal -->
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

            <!-- Footer with bank information (Blocks with leader for spacing) -->
            <fo:block-container position="absolute" bottom="2cm" top="700" left="2cm" right="2cm">
                <fo:block border-before-style="solid" border-before-width="1pt" space-before="1cm" space-after="0.5cm"></fo:block>
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