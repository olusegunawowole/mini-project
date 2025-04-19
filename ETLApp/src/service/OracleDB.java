package service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import application.ProgressIndicator;
import oracle.jdbc.datasource.impl.OracleDataSource;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Platform;
import model.Category;
import model.Product;
import model.Service;
import model.Store;

public class OracleDB {
	private OracleDataSource ods;

	public OracleDB() {
		Dotenv dotenv = Dotenv.configure().load();
		try {
			ods = new OracleDataSource();
			String url = "jdbc:oracle:thin:@//" + dotenv.get("hostname") + ":" + dotenv.get("port") + "/XE";
			ods.setURL(url);
			ods.setUser(dotenv.get("user"));
			ods.setPassword(dotenv.get("password"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	// **********Persistence service begins here**********

	public int saveCategories(Set<Category> categories, ProgressIndicator indicator) {
		int saveCount = 0; // Number of records saved
		int counter = 0;
		try (Connection conn = ods.getConnection()) {
			for (Category category : categories) {
				int result = saveCategory(conn, category);
				if (result > 0) {
					saveCount++;
					saveSubCategories(conn, category);
				}
				counter++;
				if (indicator != null) {
					double currentCount = counter;
					Platform.runLater(() -> indicator.update(categories.size(), currentCount));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return saveCount;
	}

	public int saveCategory(Connection conn, Category category) {
		String query = "{call create_category(?, ?, ?)}";
		try (CallableStatement statement = conn.prepareCall(query)) {
			statement.setString(1, category.getId());
			statement.setString(2, category.getName());
			statement.registerOutParameter(3, Types.NUMERIC);
			statement.execute();
			int result = statement.getInt(3);
			statement.close();
			saveSubCategories(conn, category);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return -1;
	}

	public int saveSubCategories(Connection conn, Category category) {
		String query = "{call create_sub_category(?, ?, ?, ?)}";
		int result = 0;
		for (Category subCategory : category.getSubCategories()) {
			try (CallableStatement statement = conn.prepareCall(query)) {
				statement.setString(1, category.getId());
				statement.setString(2, subCategory.getId());
				statement.setString(3, subCategory.getName());
				statement.registerOutParameter(4, Types.NUMERIC);
				statement.execute();
				result += statement.getInt(4);
				statement.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public int saveProducts(Set<Product> products, ProgressIndicator indicator) {
		int saveCount = 0; // Number of records saved
		int counter = 0;
		try (Connection conn = ods.getConnection()) {
			for (Product product : products) {
				int result = saveProduct(conn, product);
				if (result > 0) {
					saveCount++;
				}
				counter++;
				if (indicator != null) {
					double currentCount = counter;
					Platform.runLater(() -> indicator.update(products.size(), currentCount));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return saveCount;
	}

	public int saveProduct(Connection conn, Product product) {
		String query = "{call create_product(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
		try (CallableStatement statement = conn.prepareCall(query)) {
			statement.setLong(1, product.getSku());
			statement.setString(2, product.getName());
			statement.setString(3, product.getType());
			statement.setDouble(4, product.getPrice());
			statement.setString(5, product.getUpc());
			statement.setDouble(6, product.getShipping());
			statement.setString(7, product.getDescription());
			statement.setString(8, product.getManufacturer());
			statement.setString(9, product.getModel());
			statement.setString(10, product.getUrl());
			statement.setString(11, product.getImage());
			statement.registerOutParameter(12, Types.NUMERIC);
			statement.execute();

			int result = statement.getInt(12);
			statement.close();
			saveProductCategory(conn, product);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return -1;
	}

	private int saveProductCategory(Connection conn, Product product) {
		String query = "{call create_product_category_rel(?, ?, ?, ?)}";
		int result = -1;
		for (Category category : product.getCategories()) {
			try (CallableStatement statement = conn.prepareCall(query)) {
				statement.setLong(1, product.getSku());
				statement.setString(2, category.getId());
				statement.setString(3, category.getName());
				statement.registerOutParameter(4, Types.NUMERIC);
				statement.execute();
				result = statement.getInt(4);
				statement.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public int saveStore(Connection conn, Store store) {
		String query = "{call create_store(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
		try (CallableStatement statement = conn.prepareCall(query)) {
			statement.setLong(1, store.getId());
			statement.setString(2, store.getType());
			statement.setString(3, store.getName());
			statement.setString(4, store.getAddress());
			statement.setString(5, store.getAddress2());
			statement.setString(6, store.getCity());
			statement.setString(7, store.getState());
			statement.setString(8, store.getZip());
			statement.setDouble(9, store.getLocationLat());
			statement.setDouble(10, store.getLocationLon());
			statement.setString(11, store.getHours());
			statement.registerOutParameter(12, Types.NUMERIC);
			statement.execute();

			int result = statement.getInt(12);
			statement.close();
			saveStoreServices(conn, store);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return -1;
	}

	private int saveStoreServices(Connection conn, Store store) {
		String query = "{call create_store_service(?, ?, ?)}";
		for (Service service : store.getServices()) {
			try (CallableStatement statement = conn.prepareCall(query)) {
				statement.setLong(1, store.getId());
				statement.setString(2, service.getName());
				statement.registerOutParameter(3, Types.NUMERIC);
				statement.execute();
				int result = statement.getInt(4);
				statement.close();
				return result;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return -1;
	}

	public int saveStores(Set<Store> stores, ProgressIndicator indicator) {
		int saveCount = 0; // Number of records saved
		int counter = 0;
		try (Connection conn = ods.getConnection()) {
			for (Store store : stores) {
				int result = saveStore(conn, store);
				if (result > 0) {
					saveCount++;
				}
				counter++;
				if (indicator != null) {
					double currentCount = counter;
					Platform.runLater(() -> indicator.update(stores.size(), currentCount));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			// System.out.println(tempStore.getId());
		}
		return saveCount;
	}

	// **********Persistence service ends here**********

	// **********Query service begins here**********

	public String[] getCities(String state) {
		try {
			PreparedStatement statement = ods.getConnection()
					.prepareStatement("SELECT DISTINCT city FROM stores WHERE state = ? ORDER BY city");
			statement.setString(1, state);
			ResultSet resultSet = statement.executeQuery();
			ArrayList<String> list = new ArrayList<>();

			while (resultSet.next()) {
				list.add(resultSet.getString(1));
			}
			return list.toArray(new String[list.size()]);
		} catch (SQLException e) {
			e.printStackTrace();
			return new String[0];
		}
	}

	public String[] getStoreType() {
		try {
			PreparedStatement statement = ods.getConnection()
					.prepareStatement("SELECT DISTINCT type FROM stores ORDER BY type");
			ResultSet resultSet = statement.executeQuery();
			ArrayList<String> list = new ArrayList<>();
			list.add("All");
			while (resultSet.next()) {
				list.add(resultSet.getString(1));
			}
			return list.toArray(new String[list.size()]);
		} catch (SQLException e) {
			e.printStackTrace();
			return new String[0];
		}

	}

	public Store[] getStores(String state, String city, String type) {
		StringBuilder sb = new StringBuilder("Select * FROM stores");
		boolean whereClauseFlag = false;
		if (state != null && !state.isEmpty()) {
			sb.append(" WHERE state = '").append(state).append("'");
			whereClauseFlag = true;
		}

		if (city != null && !city.isEmpty()) {
			if (whereClauseFlag) {
				sb.append(" AND city = '").append(city).append("'");
			} else {
				sb.append(" WHERE city = '").append(city).append("'");
			}
			whereClauseFlag = true;
		}

		if (type != null && !type.isEmpty() && !type.equalsIgnoreCase("all")) {
			if (whereClauseFlag) {
				sb.append(" AND type = '").append(type).append("'");
			} else {
				sb.append(" WHERE type = '").append(type).append("'");
			}
		}

		sb.append(" ORDER BY name");

		try {
			PreparedStatement statement = ods.getConnection().prepareStatement(sb.toString());
			ResultSet resultSet = statement.executeQuery();
			ArrayList<Store> stores = new ArrayList<>();

			while (resultSet.next()) {
				Store store = new Store();
				store.setId(resultSet.getLong(1));
				store.setType(resultSet.getString(2));
				store.setName(resultSet.getString(3));
				store.setAddress(resultSet.getString(4));
				store.setAddress2(resultSet.getString(5));
				store.setCity(resultSet.getString(6));
				store.setState(resultSet.getString(7));
				store.setZip(resultSet.getString(8));
				store.setLocationLat(resultSet.getDouble(9));
				store.setLocationLon(resultSet.getDouble(10));
				store.setHours(resultSet.getString(11));
				store.setServices(getServices(resultSet.getLong(1)));
				stores.add(store);
			}
			return stores.toArray(new Store[stores.size()]);
		} catch (SQLException e) {
			e.printStackTrace();
			return new Store[0];
		}
	}

	// Returns all stores
	public Store[] getStores() {
		return getStores(null, null, null);
	}

	private Set<Service> getServices(long storeId) {
		Set<Service> services = new TreeSet<>();
		try {
			String query = "SELECT * FROM stores_services WHERE store_id = ? ORDER BY service_name";
			PreparedStatement statement = ods.getConnection().prepareStatement(query);
			statement.setLong(1, storeId);
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {
				Service service = new Service();
				service.setId(resultSet.getLong(1));
				service.setStoreId(resultSet.getLong(2));
				service.setName(resultSet.getString(3));
				services.add(service);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return services;
	}

	public Category[] getCategories() {
		try {
			String query = "SELECT id, name FROM categories WHERE id IN (SELECT MIN(pc.category_id) FROM products_categories pc JOIN categories c ON pc.category_id = c.id GROUP BY c.name) ORDER BY 2";
			PreparedStatement statement = ods.getConnection().prepareStatement(query);
			ResultSet resultSet = statement.executeQuery();
			ArrayList<Category> list = new ArrayList<>();

			while (resultSet.next()) {
				String id = resultSet.getString(1);
				String name = resultSet.getString(2).trim();
				Category category = new Category(id, name);
				list.add(category);
			}
			return list.toArray(new Category[list.size()]);
		} catch (SQLException e) {
			e.printStackTrace();
			return new Category[0];
		}
	}

	public String[] getCategoryNames() {
		try {
			String query = "SELECT name FROM categories WHERE id IN (SELECT MIN(pc.category_id) FROM products_categories pc JOIN categories c ON pc.category_id = c.id GROUP BY c.name) ORDER BY 2";
			PreparedStatement statement = ods.getConnection().prepareStatement(query);
			ResultSet resultSet = statement.executeQuery();
			ArrayList<String> list = new ArrayList<>();

			while (resultSet.next()) {
				String name = resultSet.getString(1).trim();
				list.add(name);
			}
			return list.toArray(new String[list.size()]);
		} catch (SQLException e) {
			e.printStackTrace();
			return new String[0];
		}
	}

	public String[] getBrands(String categoryId) {
		try {
			String query = "SELECT p.manufacturer FROM products p JOIN products_categories pc ON p.sku = pc.product_sku JOIN categories c ON pc.category_id = c.id WHERE c.id = ? ORDER BY 1";
			PreparedStatement statement = ods.getConnection().prepareStatement(query);
			statement.setString(1, categoryId);
			ResultSet resultSet = statement.executeQuery();
			Set<String> list = new TreeSet<>();
			while (resultSet.next()) {
				list.add(resultSet.getString(1));
			}
			return list.toArray(new String[list.size()]);
		} catch (SQLException e) {
			e.printStackTrace();
			return new String[0];
		}
	}

	private String createProductQuery(String categoryName, String minPrice, String maxPrice, String... brands) {
		StringBuilder sb = new StringBuilder(
				"SELECT p.name, p.price, p.manufacturer, p.description, p.image FROM products p JOIN products_categories pc ON p.sku = pc.product_sku JOIN categories c ON pc.category_id = c.id");
		boolean whereClauseFlag = false;
		if (categoryName != null && !categoryName.isEmpty()) {
			sb.append(" WHERE c.name = '").append(categoryName).append("'");
			whereClauseFlag = true;
		}
		if (minPrice != null && !minPrice.isEmpty() && maxPrice != null && !maxPrice.isEmpty()) {
			if (whereClauseFlag) {
				sb.append(" AND p.price BETWEEN ").append(minPrice).append(" AND ").append(maxPrice);
			} else {
				sb.append(" WHERE p.price BETWEEN ").append(minPrice).append(" AND ").append(maxPrice);
				whereClauseFlag = true;
			}
		} else if (minPrice != null && !minPrice.isEmpty()) {
			if (whereClauseFlag) {
				sb.append(" AND p.price >= ").append(minPrice);
			} else {
				sb.append(" WHERE p.price >= ").append(minPrice);
				whereClauseFlag = true;
			}
		} else if (maxPrice != null && !maxPrice.isEmpty()) {
			if (whereClauseFlag) {
				sb.append(" AND p.price <= ").append(maxPrice);
			} else {
				sb.append(" WHERE p.price <= ").append(maxPrice);
				whereClauseFlag = true;
			}
		}

		if (brands.length > 0) {
			if (whereClauseFlag) {
				sb.append(" AND  p.manufacturer IN (");
			} else {
				sb.append(" WHERE p.manufacturer IN (");
			}
			for (int index = 0; index < brands.length; index++) {
				sb.append("'").append(brands[index]).append("'");
				if (index < brands.length - 1) {
					sb.append(", ");
				} else {
					sb.append(")");
				}
			}
		}

		sb.append(" ORDER BY p.name");
		return sb.toString();
	}

	public Product[] getProducts(String categoryName, String minPrice, String maxPrice, String... brands) {
		try {
			String query = createProductQuery(categoryName, minPrice, maxPrice, brands);
			PreparedStatement statement = ods.getConnection().prepareStatement(query);
			ResultSet resultSet = statement.executeQuery();
			ArrayList<Product> list = new ArrayList<>();

			while (resultSet.next()) {
				Product product = new Product();
				product.setName(resultSet.getString(1));
				product.setPrice(resultSet.getDouble(2));
				product.setManufacturer(resultSet.getString(3));
				product.setDescription(resultSet.getString(4));
				product.setImage(resultSet.getString(5));
				list.add(product);
			}
			return list.toArray(new Product[list.size()]);
		} catch (SQLException e) {
			e.printStackTrace();
			return new Product[0];
		}
	}

	// **********Query service ends here**********
}
