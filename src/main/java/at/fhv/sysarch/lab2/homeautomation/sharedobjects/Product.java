package at.fhv.sysarch.lab2.homeautomation.sharedobjects;

public class Product {
    private double weight;
    private double price;
    private String name;

    public Product(double weight, double price, String name) {
        this.weight = weight;
        this.price = price;
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }
}
