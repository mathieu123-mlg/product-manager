package td.evaluation.td1jdbc;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {
    private final Connection dbConnection;

    public DataRetriever() {
        this.dbConnection = new DBConnection().getDBCOnnection();
    }

    public DataRetriever(DBConnection dbConnection) {
        this.dbConnection = dbConnection.getDBCOnnection();
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String category_list = new StringBuilder("SELECT id, name from product_category;").toString();

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
        String product_list = new StringBuilder(
                "select " +
                "    product.id, product.name, product.price, product.creation_datetime, " +
                "    category.id as category_id, category.name as category_name " +
                "from product " +
                "JOIN product_category as category ON product.id = category.product_id;"
        ).toString();

        try (
                PreparedStatement preparedStatement = dbConnection.prepareStatement(product_list);
                ResultSet result = preparedStatement.executeQuery();
        ) {
            while (result.next()) {
                int id = result.getInt("id");
                String name = result.getString("name");
                double price = result.getDouble("price");
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
        String query = new StringBuilder(
                "select " +
                "   product.id, product.name, product.price, product.creation_datetime, " +
                "   category.id as category_id, category.name as category_name " +
                "from product " +
                "JOIN product_category as category ON product.id = category.product_id " +
                "order by id limit ? offset ?;"
        ).toString();

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
        List<Product> products = new ArrayList<>();

        if (getAllProducts().isEmpty()) {
            return new ArrayList<>();
        }

        if (creationMin != null && creationMax != null && creationMin.isAfter(creationMax)) {
            throw new IllegalArgumentException("creationMin must be before creationMax");
        }

        StringBuilder sql = new StringBuilder(
                "SELECT " +
                "    product.id, product.name, product.price, product.creation_datetime, " +
                "    category.id as category_id, category.name as category_name " +
                "FROM product " +
                "JOIN product_category as category ON product.id = category.product_id "
        );

        List<String> conditions = new ArrayList<>();
        List<Object> parameters = new ArrayList<>();

        if (productName != null && !productName.trim().isEmpty()) {
            conditions.add("product.name ILIKE ?");
            parameters.add("%" + productName + "%");
        }

        if (categoryName != null && !categoryName.trim().isEmpty()) {
            conditions.add("category.name ILIKE ?");
            parameters.add("%" + categoryName + "%");
        }

        if (creationMin != null) {
            conditions.add("product.creation_datetime >= ?");
            parameters.add(Timestamp.from(creationMin));
        }

        if (creationMax != null) {
            conditions.add("product.creation_datetime <= ?");
            parameters.add(Timestamp.from(creationMax));
        }

        if (!conditions.isEmpty()) {
            sql.append(" WHERE ");
            sql.append(String.join(" AND ", conditions));
        }

        String query = sql.toString();

        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(query)) {

            for (int i = 0; i < parameters.size(); i++) {
                Object param = parameters.get(i);
                if (param instanceof String) {
                    preparedStatement.setString(i + 1, (String) param);
                } else if (param instanceof Timestamp) {
                    preparedStatement.setTimestamp(i + 1, (Timestamp) param);
                }
            }

            try (ResultSet result = preparedStatement.executeQuery()) {
                while (result.next()) {
                    Timestamp timestamp = result.getTimestamp("creation_datetime");
                    Instant creationInstant = null;
                    if (timestamp != null) {
                        creationInstant = timestamp.toInstant();
                    }

                    products.add(new Product(
                            result.getInt("id"),
                            result.getString("name"),
                            creationInstant,
                            new Category(
                                    result.getInt("category_id"),
                                    result.getString("category_name")
                            )
                    ));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des Produits avec critères: " + e.getMessage());
            e.printStackTrace();
        }

        return products;
    }

    public List<Product> getProductsByCriteria(String productName, String categoryName, Instant creationMin, Instant creationMax, int page, int size) {
        List<Product> products = new ArrayList<>();

        if (getAllProducts().isEmpty()) {
            return new ArrayList<>();
        }

        if (creationMin != null && creationMax != null && creationMin.isAfter(creationMax)) {
            throw new IllegalArgumentException("creationMin must be before creationMax");
        }

        StringBuilder sql = new StringBuilder(
                "SELECT " +
                "   product.id, product.name, product.price, product.creation_datetime, " +
                "   category.id as category_id, category.name as category_name " +
                "FROM product " +
                "JOIN product_category as category ON product.id = category.product_id "
        );

        List<String> conditions = new ArrayList<>();
        List<Object> parameters = new ArrayList<>();

        if (productName != null && !productName.trim().isEmpty()) {
            conditions.add("product.name ILIKE ?");
            parameters.add("%" + productName + "%");
        }

        if (categoryName != null && !categoryName.trim().isEmpty()) {
            conditions.add("category.name ILIKE ?");
            parameters.add("%" + categoryName + "%");
        }

        if (creationMin != null) {
            conditions.add("product.creation_datetime >= ?");
            parameters.add(Timestamp.from(creationMin));
        }

        if (creationMax != null) {
            conditions.add("product.creation_datetime <= ?");
            parameters.add(Timestamp.from(creationMax));
        }

        if (!conditions.isEmpty()) {
            sql.append(" WHERE ");
            sql.append(String.join(" AND ", conditions));
        }

        sql.append(" limit ? offset ? ");
        parameters.add(size);
        parameters.add((page - 1) * size);

        String query = sql.toString();

        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(query)) {
            for (int i = 0; i < parameters.size(); i++) {
                Object param = parameters.get(i);
                if (param instanceof String) {
                    preparedStatement.setString(i + 1, (String) param);
                } else if (param instanceof Timestamp) {
                    preparedStatement.setTimestamp(i + 1, (Timestamp) param);
                } else if (param instanceof Integer) {
                    preparedStatement.setInt(i + 1, (Integer) param);
                }
            }

            try (ResultSet result = preparedStatement.executeQuery()) {
                while (result.next()) {
                    Timestamp timestamp = result.getTimestamp("creation_datetime");
                    Instant creationInstant = null;
                    if (timestamp != null) {
                        creationInstant = timestamp.toInstant();
                    }

                    products.add(new Product(
                            result.getInt("id"),
                            result.getString("name"),
                            creationInstant,
                            new Category(
                                    result.getInt("category_id"),
                                    result.getString("category_name")
                            )
                    ));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des Produits avec critères: " + e.getMessage());
            e.printStackTrace();
        }

        return products;
    }
}
