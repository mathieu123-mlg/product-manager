package td.evaluation.td1jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {
    private Connection dbConnection;

    public DataRetriever() {
        this.dbConnection = new DBConnection().getDBCOnnection();
    }

    public DataRetriever(DBConnection dbConnection) {
        this.dbConnection = dbConnection.getDBCOnnection();
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String category_list = "SELECT id, name from product_category;";

        try (
            PreparedStatement preparedStatement = dbConnection.prepareStatement(category_list);
            ResultSet result = preparedStatement.executeQuery();
        ) {
            while (result.next()) {
                int id = result.getInt("id");
                String name = result.getString("name");

                Category category = new Category(id, name);

                categories.add(category);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des catégories: " + e.getMessage());
            e.printStackTrace();
        }
        return categories;
    }

    private List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String product_list =
                "select " +
                "    product.id, product.name, product.price, product.creation_datetime, " +
                "    category.id as category_id, category.name as category_name " +
                "from product " +
                "JOIN product_category as category ON product.id = category.product_id;";

        try (
                PreparedStatement preparedStatement = dbConnection.prepareStatement(product_list);
                ResultSet result = preparedStatement.executeQuery();
        ) {
            while (result.next()) {
                int id = result.getInt("id");
                String name = result.getString("name");
                Float price = result.getFloat("price");
                Instant creationDatetime = result.getTimestamp("creation_datetime").toInstant();
                int category_id = result.getInt("category_id");
                String category_name = result.getString("category_name");

                Product product = new Product(
                        id, name, creationDatetime,
                        new Category(
                                category_id, category_name
                        )
                );

                products.add(product);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des Produits: " + e.getMessage());
            e.printStackTrace();
        }
        return products;
    }

    public List<Product> getProductList (int page, int size) {
        if (page <= 0 || size <= 0) {
            throw new IllegalArgumentException("page and size must be positive");
        }

        if (getAllProducts().isEmpty() || page > getAllProducts().size()) {
            return new ArrayList<>();
        }

        int offset = (page - 1) * size;

        List<Product> products = new ArrayList<>();
        String query =
                "select " +
                        "    product.id, product.name, product.price, product.creation_datetime, " +
                        "    category.id as category_id, category.name as category_name " +
                        "from product " +
                        "JOIN product_category as category ON product.id = category.product_id " +
                        "order by id limit ? offset ?;";

        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(query)) {
            preparedStatement.setInt(1, size);
            preparedStatement.setInt(2, offset);

            try (ResultSet result = preparedStatement.executeQuery();) {
                while (result.next()) {
                    products.add(
                            new Product(
                                    result.getInt("id"),
                                    result.getString("name"),
                                    result.getTimestamp("creation_datetime").toInstant(),
                                    new Category(
                                            result.getInt("category_id"),
                                            result.getString("category_name")
                                    )
                            ));
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la recuperation des Produits pages: " + e.getMessage());
            e.printStackTrace();
        }

        return products;
    }

    public List<Product> getProductsByCriteria(String productName, String categoryName, Instant creationMin, Instant creationMax) {
        return null;
    }

    public List<Product> getProductsByCriteria(String productName, String categoryName, Instant creationMin, Instant creationMax, int page, int size) {
        return null;
    }
}
