/*
 * Copyright (c) 2025 ProjXion. All rights reserved.
 */
package com.projexion.api.pdf;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;


@Path("/pdf")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_OCTET_STREAM)
public class PdfResource {
    private static final Logger LOGGER = Logger.getLogger(PdfResource.class.getName());
    @Inject
    PdfService pdfService;

    @POST
    @Path("/pdf-generate")
    public Uni<Response> pdfGenerate(PdfEntity entity) {
        final String pdf = "pdf";
        return pdfService.generatePdf(entity, pdf)
                .onItem().transform(pdfBytes ->
                        Response.ok(pdfBytes)
                                .header("Content-Disposition", "attachment; filename=Invoice.pdf")
                                .build()
                );
    }

//    @POST
//    @Path("/pdf-view")
//    public Uni<Response> pdfView(PdfEntity entity) {
//        final String png = "png";
//        return pdfService.generatePdf(entity, png)
//                .onItem().transform(pngBytes -> {
//                    String base64 = Base64.getEncoder().encodeToString(pngBytes);
//                    return Response.ok(base64).header("Content-Type", "text/plain").build();
//                });
//    }

    @POST
    @Path("/pdf-view")
    @Produces("application/pdf")
    @Consumes("application/json")
    public Uni<Response> pdfView(PdfEntity entity) {
        return pdfService.generatePdf(entity, "pdf")
                .onItem().transform(pdfBytes ->
                        Response.ok(pdfBytes)
                                .header("Content-Type", "application/pdf")
                                .header("Content-Disposition", "inline; filename=preview.pdf")
                                .build()
                );
    }
}
