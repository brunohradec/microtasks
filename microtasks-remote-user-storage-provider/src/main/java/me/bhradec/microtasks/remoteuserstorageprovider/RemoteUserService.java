package me.bhradec.microtasks.remoteuserstorageprovider;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface RemoteUserService {
    @GET
    @Path("/{username}")
    UserDto getUserByUsername(@PathParam("username") String username);

    @POST
    @Path("/{username}/verify")
    Boolean verifyUserPasswordByUsername(@PathParam("username") String username);
}
