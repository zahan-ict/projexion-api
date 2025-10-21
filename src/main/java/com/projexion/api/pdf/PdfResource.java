package com.projexion.api.pdf;

import com.projexion.api.company.CompanyResource;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.Map;

@Path("/pdf")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_OCTET_STREAM)
public class PdfResource {
    private static final Logger LOGGER = Logger.getLogger(PdfResource.class.getName());
    @Inject
    PdfService pdfService;

//    @POST
//    @Path("/generate")
//    public Response pdfGenerate(PdfEntity entity) {
//        byte[] pdfBytes = pdfService.generatePdf(entity);
//
//        return Response.ok(pdfBytes)
//                .header("Content-Disposition", "attachment; filename=hello.pdf")
//                .build();
//    }

    @POST
    @Path("/view")
    public Response pdfView(PdfEntity requestData) {
        byte[] pdfBytes = pdfService.generatePdf(requestData);
        return Response.ok(pdfBytes)
                .header("Content-Disposition", "attachment; filename=hello.pdf")
                .build();
    }
}
