package td.evaluation.td1jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

}
