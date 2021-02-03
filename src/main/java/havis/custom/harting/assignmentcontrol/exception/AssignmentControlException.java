package havis.custom.harting.assignmentcontrol.exception;

public class AssignmentControlException extends Exception {

	private static final long serialVersionUID = 1L;

	public AssignmentControlException(String message) {
		super(message);
	}

	public AssignmentControlException(String message, Throwable cause) {
		super(message, cause);
	}

	public AssignmentControlException(Throwable cause) {
		super(cause);
	}
}