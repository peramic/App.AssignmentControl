package havis.custom.harting.assignmentcontrol.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Tag {
	private String epc;
	private String tid;
	
	@JsonIgnore
	private Date timestamp;

	public Tag() {
		super();
	}

	public Tag(String epc, String tid, Date timestamp) {
		this.epc = epc;
		this.tid = tid;
		this.timestamp = timestamp;
	}

	public String getEpc() {
		return epc;
	}

	public void setEpc(String epc) {
		this.epc = epc;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	@JsonIgnore
	public Date getTimestamp() {
		return timestamp;
	}

	@JsonProperty
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "Tag [epc=" + epc + ", tid=" + tid + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((epc == null) ? 0 : epc.hashCode());
		result = prime * result + ((tid == null) ? 0 : tid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tag other = (Tag) obj;
		if (epc == null) {
			if (other.epc != null)
				return false;
		} else if (!epc.equals(other.epc))
			return false;
		if (tid == null) {
			if (other.tid != null)
				return false;
		} else if (!tid.equals(other.tid))
			return false;
		return true;
	}
}
