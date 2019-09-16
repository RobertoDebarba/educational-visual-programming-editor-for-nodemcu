package br.com.robertodebarba.firmware;

import org.slf4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("firmware")
@Produces(MediaType.APPLICATION_JSON)
public class FirmwareAPI {

    @Inject
    private Logger logger;

    @Inject
    private FirmwareService firmwareService;

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public Response compile(String sourceCode) {
        logger.info("Compile request >>>>>>>>>>>>>>>>>>>>>\n"
                + sourceCode
                + "\n<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

        boolean isCompileSuccess = this.firmwareService.compile(sourceCode);

        if (isCompileSuccess) {
            return Response.ok().build();
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }

}
