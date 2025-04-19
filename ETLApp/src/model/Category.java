package model;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class Category implements Comparable<Category> {
	private String id;
	private String name;
	private Set<Category> subCategories;

	public Category(String id, String name) {
		setId(id);
		setName(name);
		subCategories = new TreeSet<>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		if (id == null) {
			throw new NullPointerException();
		}
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name == null) {
			throw new NullPointerException();
		}
		this.name = name;
	}

	public Set<Category> getSubCategories() {
		return subCategories;
	}

	public void setSubCategories(Set<Category> subCategories) {
		this.subCategories = subCategories;
	}

	@Override
	public int compareTo(Category category) {
		return this.id.compareTo(category.id);
	}

	@Override
	public String toString() {
		return "id: " + id + ", name: " + name + ", subCategory: " + subCategories.size();
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
		if (!(obj instanceof Category)) {
			return false;
		}
		Category other = (Category) obj;
		return Objects.equals(id, other.id);
	}

}
