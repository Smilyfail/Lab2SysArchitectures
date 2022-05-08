package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

public class Product {
    private double weight;
    private double price;
    private Double space;
    private String name;

    public Product(double weight, double price, double space, String name) {
        this.weight = weight;
        this.price = price;
        this.space = space;
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public double getPrice() {
        return price;
    }

    public double getSpace() {
        return space;
    }

    public String getName() {
        return name;
    }
}
