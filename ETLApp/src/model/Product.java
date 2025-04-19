package model;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class Product implements Comparable<Product> {

	private long sku;
	private String name;
	private String type;
	private double price;
	private String upc;
	private double shipping;
	private String description;
	private String manufacturer;
	private String model;
	private String url;
	private String image;
	private Set<Category> categories;

	public Product() {
		categories = new TreeSet<>();
	}
	
	public long getSku() {
		return sku;
	}

	public void setSku(long sku) {
		this.sku = sku;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getUpc() {
		return upc;
	}

	public void setUpc(String upc) {
		this.upc = upc;
	}

	public double getShipping() {
		return shipping;
	}

	public void setShipping(double shipping) {
		this.shipping = shipping;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Set<Category> getCategories() {
		return categories;
	}

	public void setCategories(Set<Category> categories) {
		this.categories = categories;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder("sku: ");
		sb.append(sku).append(", name: ").append(name);
		return sb.toString();
	}

	@Override
	public int compareTo(Product otherProduct) {
		if (sku < otherProduct.sku) {
			return -1;
		}
		if (sku > otherProduct.sku)
			return 1;
		return 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(sku);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Product)) {
			return false;
		}
		Product other = (Product) obj;
		return sku == other.sku;
	}

}
