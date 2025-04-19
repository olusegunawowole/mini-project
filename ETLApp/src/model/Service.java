package model;

import java.util.Objects;

public class Service implements Comparable<Service> {
	private long id;
	private String name;
	private long storeId;

	public Service() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getStoreId() {
		return storeId;
	}

	public void setStoreId(long storeId) {
		this.storeId = storeId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Service)) {
			return false;
		}
		Service other = (Service) obj;
		return Objects.equals(name, other.name);
	}

	@Override
	public int compareTo(Service service) {
		return this.name.compareTo(service.name);
	}

	@Override
	public String toString() {
		return name;
	}

}
