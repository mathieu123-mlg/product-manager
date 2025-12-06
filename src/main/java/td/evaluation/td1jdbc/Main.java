package td.evaluation.td1jdbc;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        DataRetriever dataRetriever = new DataRetriever();

        System.out.println("====================================");
        System.out.println("       Liste des categories");
        System.out.println("====================================");

        List<Category> categories = dataRetriever.getAllCategories();
        System.out.println(categories);
        System.out.println("=================================================");
        System.out.println("=================================================");

        System.out.println("================================================");
        System.out.println("       Liste des Produits page 1 size 2");
        System.out.println("=================================================");
        List<Product> products1 = dataRetriever.getProductList(1, 2);
        System.out.println(products1);
        System.out.println("size: " + products1.size());
        System.out.println("=================================================");
        System.out.println("=================================================");
    }
}
