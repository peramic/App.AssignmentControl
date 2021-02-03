package havis.custom.harting.assignmentcontrol.model;

import java.util.ArrayList;
import java.util.List;

public class Assignment {
	private Location location;
	private List<Tag> tags;

	public List<Tag> getTags() {
		if (tags == null) {
			tags = new ArrayList<Tag>();
		}

		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return "Assignment [location=" + location + ", tags=" + tags + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tags == null) ? 0 : tags.hashCode());
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
		Assignment other = (Assignment) obj;
		if (tags == null) {
			if (other.tags != null)
				return false;
		} else if (!tags.equals(other.tags))
			return false;
		return true;
	}
}