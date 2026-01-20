package td.evaluation.td1jdbc;

import java.time.Instant;
import java.util.Objects;

public class Product {
    private final int id;
    private final String name;
    private final Instant creationDatetime;
    private final Category category;

    public Product(int id, String name, Instant creationDatetime, Category category) {
        this.id = id;
        this.name = name;
        this.creationDatetime = creationDatetime;
        this.category = category;
    }

    public String getCategoryName() {
        return this.category.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id && Objects.equals(name, product.name) && Objects.equals(creationDatetime, product.creationDatetime) && Objects.equals(category, product.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, creationDatetime, category);
    }

    @Override
    public String toString() {
        return "Product{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", creationDatetime=" + creationDatetime +
               ", category=" + category +
               '}';
    }

    //    @Override
//    public String toString() {
//        return "Product{" +
//                "\n\tid=" + id +
//                ", name='" + name + '\'' +
//                ", creationDatetime=" + creationDatetime +
//                "\n\t, category=Category{" +
//                "\n\t\tid=" + category.getId() +
//                ", name='" + category.getName() + '\'' +
//                "\n\t}\n}";
//    }
}
