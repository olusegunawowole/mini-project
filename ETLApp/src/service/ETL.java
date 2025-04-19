package service;

import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import application.ProgressIndicator;
import model.Category;
import model.Product;
import model.Service;
import model.Store;

public class ETL {

	public ETL() {

	}

	// Utility method to create product from JSON object
	private Product createProduct(Map<?, ?> item) {
		Set<?> keys = item.keySet();
		Product product = new Product();
		for (Object obj : keys) {
			String key = (String) obj;
			Object value = item.get(key);
			switch (key) {
			case "image":
				product.setImage((String) value);
				break;
			case "shipping":
				if (value instanceof Long) {
					long longValue = (long) value;
					double doubleValue = longValue * 1.0;
					product.setShipping(doubleValue);
				} else if (value instanceof Double) {
					product.setShipping((double) value);
				}
				break;
			case "price":
				if (value instanceof Long) {
					long longValue = (long) value;
					double doubleValue = longValue * 1.0;
					product.setPrice(doubleValue);
				} else if (value instanceof Double) {
					product.setPrice((double) value);
				}
				break;
			case "name":
				product.setName((String) value);
				break;
			case "upc":
				product.setUpc((String) value);
				break;
			case "description":
				product.setDescription((String) value);
				break;
			case "model":
				product.setModel((String) value);
				break;
			case "sku":
				product.setSku((Long) value);
				break;
			case "type":
				product.setType((String) value);
				break;
			case "category":
				JSONArray categoryJsonArray = (JSONArray) value;
				Set<Category> categories = extractCategories(categoryJsonArray);
				product.setCategories(categories);
				break;
			case "url":
				product.setUrl((String) value);
				break;
			case "manufacturer":
				product.setManufacturer((String) value);
			default:
				break;
			}
		}
		return product;
	}

	/**
	 * Extracts and transforms JSON objects into product objects
	 * 
	 * @param file
	 * @return set of products
	 */
	public Set<Product> extractProducts(File file) throws Exception {
		Set<Product> products = new TreeSet<>();
		Object object = new JSONParser().parse(new FileReader(file));
		JSONArray jsonArray = (JSONArray) object;
		for (int index = 0; index < jsonArray.size(); index++) {
			Map<?, ?> item = (Map<?, ?>) jsonArray.get(index);
			Product product = createProduct(item);
			products.add(product);
		}
		return products;
	}

	public int loadProducts(Set<Product> products, ProgressIndicator indicator) {
		OracleDB db = new OracleDB();
		return db.saveProducts(products, indicator);
	}

	private Set<Category> extractCategories(JSONArray categoryJsonArray) {
		Set<Category> categories = new TreeSet<>();
		for (int index = 0; index < categoryJsonArray.size(); index++) {
			Map<?, ?> item = (Map<?, ?>) categoryJsonArray.get(index);
			String id = (String) item.get("id");
			String name = (String) item.get("name");
			if (id == null || name == null) {
				continue;
			}
			Category category = new Category(id, name);
			categories.add(category);
		}
		return categories;
	}

	/**
	 * Extracts and transforms JSON objects into category objects
	 * 
	 * @param file
	 * @return set of categories
	 */
	public Set<Category> extractCategories(File file) throws Exception {
		Set<Category> categories = new TreeSet<>();
		Object object = new JSONParser().parse(new FileReader(file));
		JSONArray jsonArray = (JSONArray) object;
		for (int index = 0; index < jsonArray.size(); index++) {
			Map<?, ?> item = (Map<?, ?>) jsonArray.get(index);
			String id = (String) item.get("id");
			String name = (String) item.get("name");
			if (id == null || name == null) {
				continue;
			}
			Category category = new Category(id, name);

			// Get subCategories
			JSONArray subCategoryJsonArray = (JSONArray) item.get("subCategories");
			Set<Category> subCategories = new TreeSet<>();
			for (int subIndex = 0; subIndex < subCategoryJsonArray.size(); subIndex++) {
				Map<?, ?> subItem = (Map<?, ?>) subCategoryJsonArray.get(subIndex);
				id = (String) subItem.get("id");
				name = (String) subItem.get("name");
				if (id == null || name == null) {
					continue;
				}
				Category subCategory = new Category(id, name);
				subCategories.add(subCategory);
			}
			category.setSubCategories(subCategories);
			categories.add(category);
		}
		return categories;
	}

	public int loadCategories(Set<Category> categories, ProgressIndicator indicator) {
		OracleDB db = new OracleDB();
		return db.saveCategories(categories, indicator);
	}

	private Set<Service> extractServices(JSONArray serviceJsonArray) {
		Set<Service> services = new TreeSet<>();
		for (int index = 0; index < serviceJsonArray.size(); index++) {
			String name = (String) serviceJsonArray.get(index);
			Service service = new Service();
			service.setName(name);
			services.add(service);
		}
		return services;
	}

	private Store createStore(Map<?, ?> item) {
		Set<?> keys = item.keySet();
		Store store = new Store();
		for (Object obj : keys) {
			String key = (String) obj;
			Object value = item.get(key);
			switch (key) {
			case "id":
				store.setId((long) value);
				break;
			case "type":
				store.setType((String) value);
				break;
			case "name":
				store.setName((String) value);
				break;
			case "address":
				store.setAddress((String) value);
				break;
			case "address2":
				store.setAddress2((String) value);
				break;
			case "city":
				store.setCity((String) value);
				break;
			case "state":
				store.setState((String) value);
				break;
			case "zip":
				store.setZip((String) value);
				break;
			case "location":
				JSONObject locationObject = (JSONObject) value;
				double lat = (double) locationObject.get("lat");
				double lon = (double) locationObject.get("lon");
				store.setLocationLat(lat);
				store.setLocationLon(lon);
				break;
			case "hours":
				store.setHours((String) value);
				break;
			case "services":
				JSONArray serviceJsonArray = (JSONArray) value;
				Set<Service> services = extractServices(serviceJsonArray);
				store.setServices(services);
				break;
			default:
				break;
			}

		}
		return store;
	}

	public Set<Store> extractStores(File file) throws Exception {
		Set<Store> stores = new TreeSet<>();
		Object object = new JSONParser().parse(new FileReader(file));
		JSONArray jsonArray = (JSONArray) object;
		for (int index = 0; index < jsonArray.size(); index++) {
			Map<?, ?> item = (Map<?, ?>) jsonArray.get(index);
			stores.add(createStore(item));
		}

		return stores;
	}

	public int loadStores(Set<Store> stores, ProgressIndicator indicator) {
		OracleDB db = new OracleDB();
		return db.saveStores(stores, indicator);
	}
}
