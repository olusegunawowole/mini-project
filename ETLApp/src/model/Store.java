package model;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class Store implements Comparable<Store> {
	private long id;
	private String type;
	private String name;
	private String address;
	private String address2;
	private String city;
	private String state;
	private String zip;
	private double locationLat;
	private double locationLon;
	private String hours;
	private String fullAddress;
	private Set<Service> services;

	public Store() {
		services = new TreeSet<>();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getHours() {
		return hours;
	}

	public void setHours(String hours) {
		this.hours = hours;
	}

	public double getLocationLat() {
		return locationLat;
	}

	public void setLocationLat(double locationLat) {
		this.locationLat = locationLat;
	}

	public double getLocationLon() {
		return locationLon;
	}

	public void setLocationLon(double locationLon) {
		this.locationLon = locationLon;
	}

	public String getFullAddress() {
		StringBuilder sb = new StringBuilder(address).append(" ");
		sb.append(address2).append(" ");
		sb.append(city).append(" ");
		sb.append(state).append(" ");
		sb.append(zip);
		fullAddress = sb.toString();

		return fullAddress;
	}

	public void setFullAddress(String fullAddress) {
		this.fullAddress = fullAddress;
	}

	public Set<Service> getServices() {
		return services;
	}

	public void setServices(Set<Service> services) {
		this.services = services;
	}

	@Override
	public String toString() {
		return name + " " + getFullAddress();
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Store)) {
			return false;
		}
		Store other = (Store) obj;
		return id == other.id;
	}

	@Override
	public int compareTo(Store otherProduct) {
		if (id < otherProduct.id) {
			return -1;
		}
		if (id > otherProduct.id)
			return 1;
		return 0;
	}

}
