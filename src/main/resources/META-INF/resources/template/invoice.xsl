<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:layout-master-set>
        <fo:simple-page-master master-name="A4"
                               page-height="29.7cm"
                               page-width="21cm"
                               margin-top="2cm"
                               margin-bottom="2.5cm"
                               margin-left="2cm"
                               margin-right="2cm">
            <fo:region-body margin-bottom="1.5cm"/>
            <fo:region-after extent="1.5cm"/>
        </fo:simple-page-master>
    </fo:layout-master-set>

    <fo:page-sequence master-reference="A4">
        <!-- FOOTER (appears on every page automatically) -->
        <fo:static-content flow-name="xsl-region-after">
            <fo:block font-size="8.2pt" text-align="left" margin-top="35pt">
                <fo:inline>Bank: </fo:inline>
                <fo:inline font-weight="bold">${bankName}</fo:inline>
                <fo:leader leader-pattern="space"/>

                <fo:inline>IBAN: </fo:inline>
                <fo:inline font-weight="bold">${iban}</fo:inline>
                <fo:leader leader-pattern="space"/>

                <fo:inline>SWIFT: </fo:inline>
                <fo:inline font-weight="bold">${swift}</fo:inline>
                <fo:leader leader-pattern="space"/>

                <fo:inline>Konto: </fo:inline>
                <fo:inline font-weight="bold">${accountNumber}</fo:inline>
                <fo:block space-before="7pt" font-size="10pt" text-align="center">${companyAddress}</fo:block>

                <fo:block space-before="2pt"  font-size="10pt" text-align="right">
                    Seite <fo:page-number/> von
                    <fo:page-number-citation-last ref-id="last-page"/>
                </fo:block>
            </fo:block>
        </fo:static-content>

        <!-- BODY -->
        <fo:flow flow-name="xsl-region-body" font-family="Helvetica" font-size="10pt">

            <!-- Client Info -->
            <fo:block font-size="12pt" margin-top="30px" space-after="20pt">
                <fo:block>${clientNamePrefix}</fo:block>
                <fo:block>${clientFirstname} ${clientLastName}</fo:block>
                <fo:block>${clientAddress}</fo:block>
                <fo:block>${clientPostCode} ${clientCity}</fo:block>
            </fo:block>

            <!-- Invoice Info -->
            <fo:block space-before="1.8cm">
                <fo:inline>  ${invoiceIssueLocation}, ${invoiceIssueDate}</fo:inline>
                <fo:leader leader-length="108mm"/>
                <fo:inline  font-size="9pt">unser Zeichen:</fo:inline>
                <fo:block text-align="right">${pdfCreator}</fo:block>
            </fo:block>

            <fo:block space-before="2cm" border-bottom="2px solid black" font-size="15pt">
                <fo:block text-align-last="justify">
                    <fo:inline font-weight="bold">Rechnung</fo:inline>
                    <fo:leader leader-pattern="space"/>
                    <fo:inline>Nr. ${invoiceNumber}</fo:inline>
                    <fo:leader leader-pattern="space"/>
                    <fo:inline font-size="12pt">CHE-106.409.517 MWST</fo:inline>
                </fo:block>
            </fo:block>
            <fo:block space-before="5pt" font-size="12pt">${comment}</fo:block>

            <!-- Prices (auto-flow, no cutoff) -->
            <fo:block space-before="3cm" font-size="11pt" font-weight="bold" border-bottom="1pt solid black" text-align="right">
                Preis in EUR
            </fo:block>
            <!-- Loop over price items -->
            <!-- Dynamic Price Items inserted here -->
             ${priceItems}
             ${subtotal}
            <fo:block space-before="1cm" font-size="12pt"> ${priceNote}</fo:block>
            <!-- mark the last page for page-number-citation -->
            <fo:block id="last-page"/>
        </fo:flow>
    </fo:page-sequence>

</fo:root>