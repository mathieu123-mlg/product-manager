package td.evaluation.td1jdbc;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DataRetriever dataRetriever = new DataRetriever();

        System.out.println("====================================");
        System.out.println("       Liste des categories");
        System.out.println("====================================");

        List<Category> categories = dataRetriever.getAllCategories();
        System.out.println(categories);
        System.out.println("==================================================================================================");


        System.out.println("================================================");
        System.out.println("       Liste des Produits");
        System.out.println("=================================================");
        List<List<Product>> products1 = List.of(
                dataRetriever.getProductList(1, 10),
                dataRetriever.getProductList(1, 5),
                dataRetriever.getProductList(1, 3),
                dataRetriever.getProductList(2, 2)
        );
        System.out.println(products1);
        System.out.println("==================================================================================================");


        System.out.println("================================================");
        System.out.println("       Liste des Produits par critere");
        System.out.println("=================================================");
        List<List<Product>> products2 = List.of(
                dataRetriever.getProductsByCriteria(
                        "Dell",
                        null,
                        null,
                        null
                ),
                dataRetriever.getProductsByCriteria(
                        null,
                        "info",
                        null,
                        null
                ),
                dataRetriever.getProductsByCriteria(
                        "iPhone",
                        "mobile",
                        null,
                        null
                ),
                dataRetriever.getProductsByCriteria(
                        null,
                        null,
                        Instant.parse("2024-02-01T00:00:00Z"),
                        Instant.parse("2024-03-01T00:00:00Z")
                ),
                dataRetriever.getProductsByCriteria(
                        "Samsung",
                        "bureau",
                        null,
                        null
                ),
                dataRetriever.getProductsByCriteria(
                        "Sony",
                        "informatique",
                        null,
                        null
                ),
                dataRetriever.getProductsByCriteria(
                        null,
                        "audio",
                        Instant.parse("2024-01-01T00:00:00Z"),
                        Instant.parse("2024-12-01T00:00:00Z")
                ),
                dataRetriever.getProductsByCriteria(
                        null,
                        null,
                        null,
                        null
                )
        );

        products2.forEach(System.out::println);
        System.out.println("==================================================================================================");


        System.out.println("==============================================================");
        System.out.println("       Liste des Produits par critere avec pagination");
        System.out.println("==============================================================");
        List<List<Product>> products3 = List.of(
                dataRetriever.getProductsByCriteria(
                        null,
                        null,
                        null,
                        null,
                        1,
                        10
                ),
                dataRetriever.getProductsByCriteria(
                        "Dell",
                        null,
                        null,
                        null,
                        1,
                        5
                ),
                dataRetriever.getProductsByCriteria(
                        null,
                        "informatique",
                        null,
                        null,
                        1,
                        10
                )
        );

        products3.forEach(System.out::println);
        System.out.println("==================================================================================================");
    }
}
