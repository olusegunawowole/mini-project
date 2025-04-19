DECLARE
    c_category_id      categories.id%TYPE := 'ctgr001';
    c_category_id2     categories.id%TYPE := 'ctgr002';
    c_category_name    categories.name%TYPE := 'Sample Category';
    c_subcategory_id   sub_categories.id%TYPE := 'subctgr001';
    c_subcategory_id2  sub_categories.id%TYPE := 'subctgr002';
    c_subcategory_name sub_categories.name%TYPE := 'Sample Sub Category';
    c_sku              products.sku%TYPE := 20;
    v_result           NUMBER;
BEGIN
    dbms_output.put_line('******TESTS STARTED******');
    create_category(c_category_id, c_category_name, v_result);
    dbms_output.put_line('1 - Testing create_category procedure');
    dbms_output.put_line('1.1 - Create new category: expected 1, got '
                         || v_result
                         || '.');
    create_category(c_category_id, c_category_name, v_result);
    dbms_output.put_line('1.2 - Duplicate category: expected 0, got '
                         || v_result
                         || '.');
    create_sub_category(c_category_id, c_subcategory_id, c_subcategory_name, v_result);
    dbms_output.put_line('2 - Testing create_sub_category procedure');
    dbms_output.put_line('2.1 - Create new sub-category: expected 2, got '
                         || v_result
                         || '.');
    create_sub_category(c_category_id, c_subcategory_id, c_subcategory_name, v_result);
    dbms_output.put_line('2.2 - Duplicate sub-category: expected 0, got '
                         || v_result
                         || '.');
    INSERT INTO sub_categories (
        id,
        name
    ) VALUES (
        c_subcategory_id2,
        c_subcategory_name
    );

    create_sub_category(c_category_id, c_subcategory_id2, c_subcategory_name, v_result);
    dbms_output.put_line('2.2 - Link category with sub-category: expected 1, got '
                         || v_result
                         || '.');
    create_product(c_sku, 'sample_name', 'sample_type', 5.99, 'sample_upc',
                  5.99, 'sample_description', 'sample_manufacturer', 'sample_model', 'sample_url',
                  'sample_image', v_result);

    dbms_output.put_line('3 - Testing create_product procedure');
    dbms_output.put_line('3.1 - Create new product: expected 1, got '
                         || v_result
                         || '.');
    create_product(c_sku, 'sample_name', 'sample_type', 5.99, 'sample_upc',
                  5.99, 'sample_description', 'sample_manufacturer', 'sample_model', 'sample_url',
                  'sample_image', v_result);

    dbms_output.put_line('3.2 - Duplicate product: expected 0, got '
                         || v_result
                         || '.');
    create_product_category_rel(c_sku, c_category_id, c_category_name, v_result);
    dbms_output.put_line('4 - Testing create_product_category_rel procedure');
    dbms_output.put_line('4.1 - Link existing product and category: expected 1, got '
                         || v_result
                         || '.');
    create_product_category_rel(c_sku, c_category_id, c_category_name, v_result);
    dbms_output.put_line('4.2 - Duplicate existing relationship: expected 0, got '
                         || v_result
                         || '.');
    create_product_category_rel(c_sku, c_category_id2, c_category_name, v_result);
    dbms_output.put_line('4.3 - Link existing product with new category: expected 2, got '
                         || v_result
                         || '.');
    create_product_category_rel(999, c_category_id, c_category_name, v_result);
    dbms_output.put_line('4.4 - Link invalid product with an existing category: expected -1, got '
                         || v_result
                         || '.');
    create_store(-1, 'BigBox', 'Asheville South', '10 McKenna Rd', NULL,
                'Arden', 'NC', '28704', 35.442486, -82.536293,
                'Mon: 10-9; Tue: 10-9; Wed: 10-9; Thurs: 10-9; Fri: 10-9; Sat: 10-9; Sun: 11-8', v_result);

    dbms_output.put_line('5 - Testing create_store procedure');
    dbms_output.put_line('5.1 - Create new store: expected 1, got '
                         || v_result
                         || '.');
    create_store(-1, 'BigBox', 'Asheville South', '10 McKenna Rd', NULL,
                'Arden', 'NC', '28704', 35.442486, -82.536293,
                'Mon: 10-9; Tue: 10-9; Wed: 10-9; Thurs: 10-9; Fri: 10-9; Sat: 10-9; Sun: 11-8', v_result);

    dbms_output.put_line('5.2 - Duplicate store: expected 0, got '
                         || v_result
                         || '.');
    create_store_service(-1, 'BigBox', v_result);
    dbms_output.put_line('6 - Testing create_store procedure');
    dbms_output.put_line('6.1 - Link existing store and service: expected 1, got '
                         || v_result
                         || '.');
    create_store_service(-1, 'BigBox', v_result);
    dbms_output.put_line('6.2 - Duplicate existing relationship 0, got '
                         || v_result
                         || '.');
    create_store_service(-2, 'BigBox', v_result);
    dbms_output.put_line('6.3 - Link invalid store with a service: expected -1, got '
                         || v_result
                         || '.');                        
-- Delete test data from the database;
    DELETE FROM categories_subcategories
    WHERE
            category_id = c_category_id
        AND subcategory_id IN ( c_subcategory_id, c_subcategory_id2 );

    DELETE FROM products_categories
    WHERE
            product_sku = c_sku
        AND category_id IN ( c_category_id, c_category_id2 );

    DELETE FROM sub_categories
    WHERE
        id IN ( c_subcategory_id, c_subcategory_id2 );

    DELETE FROM products
    WHERE
        sku = c_sku;

    DELETE FROM categories
    WHERE
        id IN ( c_category_id, c_category_id2 );

    DELETE FROM stores_services
    WHERE
        store_id = - 1;

    DELETE FROM stores
    WHERE
        id = - 1;

    dbms_output.put_line('******TESTS COMPLETED******');
END;
/