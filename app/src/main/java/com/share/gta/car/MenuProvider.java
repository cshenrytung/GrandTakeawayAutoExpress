package com.share.gta.car;

import android.content.Context;

import com.share.gt.model.Category;
import com.share.gt.model.Product;
import com.share.gta.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by russell on 3/21/2015.
 */
public class MenuProvider {

    private static MenuProvider INSTANCE;

    private final Context context;

    private static final String[] CATEGORIES = {
            "Burgers", "Pizza", "Pasta", "Dessert", "Drinks"
    };

    private List<Category> categoryList;
    private List<Product> productList;

    private MenuProvider(Context context) {
        this.context = context;
    }

    public static MenuProvider getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new MenuProvider(context);
        }

        return INSTANCE;
    }

    public List<Category> getMenu() {
        categoryList = new ArrayList<>();
        productList = new ArrayList<>();

        for (int i = 0; i < CATEGORIES.length; i++) {
            Category category = new Category();
            category.setId(i);
            category.setName(CATEGORIES[i]);

            List<Product> productList = new ArrayList<Product>();
            int numProducts = 2;
            for (int j = 0; j < numProducts; j++) {
                Product product = new Product();
                product.setId((i * CATEGORIES.length) + j);
                product.setName(category.getName() + "/" + j);
                product.setDescription("Description " + i + "/" + j);
                product.setPrice(new Double(i*CATEGORIES.length) + j);
                productList.add(product);
            }
            category.setProductList(productList);
            categoryList.add(category);
        }

        return categoryList;
    }

    //stub the product list
    public List<Product> getProducts() {
        String[] names = {"Americano", "Cappuccino", "Iced Mocha"};
        String[] urls = {
                "android.resource://com.share.gta/drawable/bg_americano",
                "android.resource://com.share.gta/drawable/bg_cappuccino",
                "android.resource://com.share.gta/drawable/bg_mocha"
        };
        int[] resourceIds = {R.drawable.bg_americano, R.drawable.bg_cappuccino, R.drawable.bg_mocha};
        double[] prices = {35.0, 32.0, 40.};

        productList = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            Product product = new Product();
            product.setId(i);
            product.setName(names[i]);
            product.setPrice(prices[i]);
            product.setImageResourceId(resourceIds[i]);
            product.setImageUrl(urls[i]);
            productList.add(product);
        }
        return productList;
    }

    public Category getCategoryById(long id) {
        for (Category category : categoryList) {
            if (category.getId() == id)
                return category;
        }
        return null;
    }

    public Product getProductById(long id) {
        for (Product product : productList) {
            if (product.getId() == id)
                return product;
        }
        return null;
    }
}
