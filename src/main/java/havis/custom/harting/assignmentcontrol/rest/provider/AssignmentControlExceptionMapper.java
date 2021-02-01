package havis.custom.harting.assignmentcontrol.rest.provider;

import havis.custom.harting.assignmentcontrol.exception.AssignmentControlException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AssignmentControlExceptionMapper implements ExceptionMapper<AssignmentControlException> {

	@Override
	public Response toResponse(AssignmentControlException e) {
		return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
	}
}