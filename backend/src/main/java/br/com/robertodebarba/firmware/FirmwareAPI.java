package br.com.robertodebarba.firmware;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("firmware")
@Produces(MediaType.APPLICATION_JSON)
public class FirmwareAPI {

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public Response compile(String sourceCode) {
        System.out.println(sourceCode);
        return Response.ok().build();
    }

}
