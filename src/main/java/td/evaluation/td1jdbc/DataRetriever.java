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
            ResultSet rs = stm.executeQuery("SELECT id, name from product_category;");

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

            System.out.println("sql ? :" + sql);

            return getProducts(ps);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeDBConnection(conn);
        }
    }

    public List<Product> getProductsByCriteria(String productName, String categoryName, Instant creationMin, Instant creationMax) {
        if (creationMin != null && creationMax != null && creationMin.isAfter(creationMax)) {
            throw new IllegalArgumentException("creationMin must be before creationMax");
        }

        StringBuilder sql = new StringBuilder("""
                SELECT \
                    product.id, product.name, product.price, product.creation_datetime, \
                    category.id as category_id, category.name as category_name \
                FROM product \
                JOIN product_category as category ON product.id = category.product_id where 1 = 1""");

        Connection conn = dbConnection.getDBCOnnection();
        try {
            PreparedStatement ps = conn.prepareStatement(sql.toString());
            buildProductsStatement(sql, ps, productName, categoryName, creationMin, creationMax, null, null);

            System.out.println("sql ? : " + sql.toString());

            return getProducts(ps);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeDBConnection(conn);
        }
    }

    public List<Product> getProductsByCriteria(String productName, String categoryName, Instant creationMin, Instant creationMax, int page, int size) {
        if (creationMin != null && creationMax != null && creationMin.isAfter(creationMax)) {
            throw new IllegalArgumentException("creationMin must be before creationMax");
        }

        if (page <= 0 || size <= 0) {
            throw new IllegalArgumentException("page and size must be positive");
        }

        StringBuilder sql = new StringBuilder("""
                SELECT \
                    product.id, product.name, product.price, product.creation_datetime, \
                    category.id as category_id, category.name as category_name \
                FROM product \
                JOIN product_category as category ON product.id = category.product_id where 1 = 1""");

        Connection conn = dbConnection.getDBCOnnection();
        try {
            PreparedStatement ps = conn.prepareStatement(sql.toString());
            buildProductsStatement(sql, ps, productName, categoryName, creationMin, creationMax, page, size);

            System.out.println("sql ? : " + sql.toString());

            return getProducts(ps);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeDBConnection(conn);
        }
    }

    private List<Product> getProducts(PreparedStatement ps) throws SQLException {
        ResultSet rs = ps.executeQuery();
        List<Product> products = new ArrayList<>();
        while (rs.next()) {
            products.add(new Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getTimestamp("creation_datetime").toInstant(),
                    new Category(
                            rs.getInt("category_id"),
                            rs.getString("category_name")
                    )
            ));
        }
        return products;
    }

    private void buildProductsStatement(
            StringBuilder sql,
            PreparedStatement ps,
            String productName,
            String categoryName,
            Instant creationMin,
            Instant creationMax,
            Integer page,
            Integer size
    ) throws SQLException {

        int index = 1;
        if (productName != null) {
            sql.append(" AND product.name ilike ? ");
            ps.setString(++index, '%' + productName + '%');
            index += 1;
        }

        if (categoryName != null) {
            sql.append(" AND category.name ilike ? ");
            ps.setString(index, "%" + categoryName + "%");
            index += 1;
        }

        if (creationMin != null) {
            sql.append(" AND creation_datetime >= ? ");
            ps.setTimestamp(index, Timestamp.from(creationMin));
            index += 1;
        }

        if (creationMax != null) {
            sql.append(" AND creation_datetime <= ? ");
            ps.setTimestamp(index, Timestamp.from(creationMax));
            index += 1;
        }

        sql.append(" order by product.id");

        if (size != null) {
            sql.append(" limit ? ");
            ps.setInt(index, size);
            index += 1;
        }

        if (page != null && size != null) {
            sql.append(" offset ? ");
            ps.setInt(index, (page - 1) * size);
        }
    }
}
