package td.evaluation.td1jdbc;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {
    private final DBConnection dbConnection = new DBConnection();

    public List<Category> getAllCategories() {
        Connection conn = dbConnection.getDBCOnnection();

        try {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("SELECT id, name from product_category order by id;");

            List<Category> categories = new ArrayList<>();
            while (rs.next()) {
                Category category = new Category(
                        rs.getInt("id"),
                        rs.getString("name")
                );
                categories.add(category);
            }

            return categories;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeDBConnection(conn);
        }
    }

    public List<Product> getProductList(int page, int size) {
        if (page <= 0 || size <= 0) {
            throw new IllegalArgumentException("page and size must be positive");
        }

        String sql = """
                select \
                   product.id, product.name, product.price, product.creation_datetime, \
                   category.id as category_id, category.name as category_name \
                from product \
                JOIN product_category as category ON product.id = category.product_id \
                order by id limit ? offset ?;""";

        Connection conn = dbConnection.getDBCOnnection();
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, size);
            ps.setInt(2, (page - 1) * size);

            ResultSet rs = ps.executeQuery();
            return getProducts(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeDBConnection(conn);
        }
    }

    public List<Product> getProductsByCriteria(String productName, String categoryName, Instant creationMin,
                                               Instant creationMax) {

        if (creationMin != null && creationMax != null && creationMin.isAfter(creationMax)) {
            throw new IllegalArgumentException("creationMin must be before creationMax");
        }

        String sql = buildProductsQuery(productName, categoryName, creationMin, creationMax);
        sql = sql.replace(" limit ? offset ?", "");

        Connection conn = dbConnection.getDBCOnnection();
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            buildProductsParams(ps, productName, categoryName, creationMin, creationMax, null, null);

            ResultSet rs = ps.executeQuery();
            return getProducts(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeDBConnection(conn);
        }
    }

    public List<Product> getProductsByCriteria(String productName, String categoryName, Instant creationMin,
                                               Instant creationMax, int page, int size) {

        if (creationMin != null && creationMax != null && creationMin.isAfter(creationMax)) {
            throw new IllegalArgumentException("creationMin must be before creationMax");
        }

        String sql = buildProductsQuery(productName, categoryName, creationMin, creationMax);
        Connection conn = dbConnection.getDBCOnnection();
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            buildProductsParams(ps, productName, categoryName, creationMin, creationMax, page, size);

            ResultSet rs = ps.executeQuery();
            return getProducts(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeDBConnection(conn);
        }
    }

    private List<Product> getProducts(ResultSet rs) throws SQLException {
        List<Product> products = new ArrayList<>();
        while (rs.next()) {
            Timestamp creationtimestamp = rs.getTimestamp("creation_datetime");
            products.add(
                    new Product(
                            rs.getInt("id"),
                            rs.getString("name"),
                            creationtimestamp.toInstant() == null
                                    ? null
                                    : creationtimestamp.toInstant(),
                            new Category(
                                    rs.getInt("category_id"),
                                    rs.getString("category_name")
                            )
                    ));
        }
        return products;
    }

    private String buildProductsQuery(String productName, String categoryName, Instant creationMin,
                                      Instant creationMax) {
        StringBuilder sql = new StringBuilder("""
                SELECT p.id, p.name, p.price, p.creation_datetime,
                       c.id AS category_id, c.name AS category_name
                FROM product p
                JOIN product_category c ON p.id = c.product_id
                WHERE 1 = 1""");

        if (productName != null && !productName.isBlank()) {
            sql.append(" AND p.name ILIKE ?");
        }

        if (categoryName != null && !categoryName.isBlank()) {
            sql.append(" AND c.name ILIKE ?");
        }

        if (creationMin != null) {
            sql.append(" AND p.creation_datetime >= ?");
        }

        if (creationMax != null) {
            sql.append(" AND p.creation_datetime <= ?");
        }

        sql.append(" ORDER BY p.id limit ? offset ?;");
        return sql.toString();
    }

    private void buildProductsParams(PreparedStatement preparedStatement, String productName, String categoryName,
                                     Instant creationMin, Instant creationMax, Integer page, Integer size) throws SQLException {
        int index = 1;
        if (productName != null && !productName.isBlank()) {
            preparedStatement.setString(index++, "%" + productName + "%");
        }

        if (categoryName != null && !categoryName.isBlank()) {
            preparedStatement.setString(index++, "%" + categoryName + "%");
        }

        if (creationMin != null) {
            preparedStatement.setTimestamp(index++, Timestamp.from(creationMin));
        }

        if (creationMax != null) {
            preparedStatement.setTimestamp(index++, Timestamp.from(creationMax));
        }

        if (page != null && size != null) {
            preparedStatement.setInt(index++, size);
            preparedStatement.setInt(index, (page - 1) * size);
        }
    }
}
