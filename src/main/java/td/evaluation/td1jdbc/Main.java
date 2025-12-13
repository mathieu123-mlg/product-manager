package td.evaluation.td1jdbc;

import java.time.Instant;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DataRetriever dataRetriever = new DataRetriever();

        System.out.println("====================================");
        System.out.println("       Liste des categories");
        System.out.println("====================================");

        System.out.println("method: getAllCategories()");
        List<Category> categories = dataRetriever.getAllCategories();
        System.out.println(categories);
        System.out.println();
        System.out.println();


        System.out.println("================================================");
        System.out.println("       Liste des Produits");
        System.out.println("=================================================");

        System.out.println("method: getProductList(page = 1, pageSize = 10)");
        List<Product> products_b1 = dataRetriever.getProductList(1, 10);
        System.out.println(products_b1);
        System.out.println();

        System.out.println("method: getProductList(page = 1, pageSize = 5)");
        List<Product> products_b2 = dataRetriever.getProductList(1, 5);
        System.out.println(products_b2);
        System.out.println();

        System.out.println("method: getProductList(page = 1, pageSize = 3)");
        List<Product> products_b3 = dataRetriever.getProductList(1, 3);
        System.out.println(products_b3);
        System.out.println();

        System.out.println("method: getProductList(page = 2, pageSize = 2)");
        List<Product> products_b4 = dataRetriever.getProductList(2, 2);
        System.out.println(products_b4);
        System.out.println();
        System.out.println();


        System.out.println("================================================");
        System.out.println("        Liste des Produits par critere");
        System.out.println("=================================================");

        System.out.println("method: getProductsByCriteria(name = 'Dell', category = null, creationMin = null, creationMax = null)");
        List<Product> products_c1 = dataRetriever.getProductsByCriteria(
                "Dell",
                null,
                null,
                null
        );
        System.out.println(products_c1);
        System.out.println();

        System.out.println("method: getProductsByCriteria(name = null, category = 'info', creationMin = null, creationMax = null)");
        List<Product> products_c2 = dataRetriever.getProductsByCriteria(
                null,
                "info",
                null,
                null
        );
        System.out.println(products_c2);
        System.out.println();

        System.out.println("method: getProductsByCriteria(name = 'iPhone', category = 'mobile', creationMin = null, creationMax = null)");
        List<Product> products_c3 = dataRetriever.getProductsByCriteria(
                "iPhone",
                "mobile",
                null,
                null
        );
        System.out.println(products_c3);
        System.out.println();

        System.out.println("method: getProductsByCriteria(name = null, category = null, creationMin = '2024-02-01', creationMax = '2024-03-01')");
        List<Product> products_c4 = dataRetriever.getProductsByCriteria(
                null,
                null,
                Instant.parse("2024-02-01T00:00:00Z"),
                Instant.parse("2024-03-01T00:00:00Z")
        );
        System.out.println(products_c4);
        System.out.println();

        System.out.println("method: getProductsByCriteria(name = 'Samsung', category = 'bureau', creationMin = null, creationMax = null)");
        List<Product> products_c5 = dataRetriever.getProductsByCriteria(
                "Samsung",
                "bureau",
                null,
                null
        );
        System.out.println(products_c5);
        System.out.println();

        System.out.println("method: getProductsByCriteria(name = 'Sony', category = 'informatique', creationMin = null, creationMax = null)");
        List<Product> products_c6 = dataRetriever.getProductsByCriteria(
                "Sony",
                "informatique",
                null,
                null
        );
        System.out.println(products_c6);
        System.out.println();

        System.out.println("method: getProductsByCriteria(name = null, category = 'audio', creationMin = '2024-01-01', creationMax = '2024-12-01')");
        List<Product> products_c7 = dataRetriever.getProductsByCriteria(
                null,
                "audio",
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-12-01T00:00:00Z")
        );
        System.out.println(products_c7);
        System.out.println();

        System.out.println("method: getProductsByCriteria(name = null, category = null, creationMin = null, creationMax = null)");
        List<Product> products_c8 = dataRetriever.getProductsByCriteria(
                null,
                null,
                null,
                null
        );
        System.out.println(products_c8);
        System.out.println();
        System.out.println();


        System.out.println("==============================================================");
        System.out.println("       Liste des Produits par critere avec pagination");
        System.out.println("==============================================================");

        System.out.println("method: getProductsByCriteria(name = null, category = null, creationMin = null, creationMax = null, page = 1, pageSize = 10)");
        List<Product> products_d1 = dataRetriever.getProductsByCriteria(
                null,
                null,
                null,
                null,
                1,
                10
        );
        System.out.println(products_d1);
        System.out.println();

        System.out.println("method: getProductsByCriteria(name = 'Dell', category = null, creationMin = null, creationMax = null, page = 1, pageSize = 5)");
        List<Product> products_d2 = dataRetriever.getProductsByCriteria(
                "Dell",
                null,
                null,
                null,
                1,
                5
        );
        System.out.println(products_d2);
        System.out.println();

        System.out.println("method: getProductsByCriteria(name = null, category = 'informatique', creationMin = null, creationMax = null, page = 1, pageSize = 10)");
        List<Product> products_d3 = dataRetriever.getProductsByCriteria(
                null,
                "informatique",
                null,
                null,
                1,
                10
        );
        System.out.println(products_d3);
    }
}