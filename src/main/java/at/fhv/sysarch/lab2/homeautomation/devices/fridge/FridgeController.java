package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.sharedobjects.Product;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FridgeController extends AbstractBehavior<FridgeController.FridgeCommand> {

    public interface FridgeCommand {}

    public static final class OrderProduct implements FridgeCommand {
        final Product product;
        final int amount;

        public OrderProduct(Product product, int amount) {
            this.product = product;
            this.amount = amount;
        }
    }

    public static final class RequestProductList implements FridgeCommand {}

    public static final class StoreProduct implements FridgeCommand {
        final Product product;

        public StoreProduct(Product product) {
            this.product = product;
        }
    }

    public static final class ConsumeProduct implements FridgeCommand {
        final String productName;

        public ConsumeProduct(String productName) {
            this.productName = productName;
        }
    }

    private List<Product> currentProducts = new ArrayList<>();
    private String groupId;
    private String deviceId;
    private ActorRef<AmountSensor.AmountCommand> amountSensor;
    private ActorRef<WeightSensor.WeightCommand> weightSensor;
    private ActorRef<OrderProcessor.OrderCommand> orderProcessor;
    public FridgeController(ActorContext<FridgeCommand> context, String groupId, String deviceId) {
        super(context);
        this.groupId = groupId;
        this.deviceId = deviceId;
        this.amountSensor = getContext().spawn(AmountSensor.create("7", "1"), "amountSensor");
        this.weightSensor = getContext().spawn(WeightSensor.create("8", "1"), "weightSensor");

        getContext().getLog().info("FridgeController started");
    }

    public static Behavior<FridgeCommand> create(String groupId, String deviceId) {
        return Behaviors.setup(context -> new FridgeController(context, groupId, deviceId));
    }

    @Override
    public Receive<FridgeCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(ConsumeProduct.class, this::onConsume)
                .onMessage(OrderProduct.class, this::onOrder)
                .onMessage(RequestProductList.class, this::onRequestingProductList)
                .onMessage(StoreProduct.class, this::onStoringProduct)
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    private Behavior<FridgeCommand> onConsume(ConsumeProduct productName) {
        Optional<Product> product = currentProducts.stream().filter(p -> p.getName().equals(productName.productName)).findFirst();

        if(product.isPresent()) {
            currentProducts.remove(product);
            getContext().getLog().info("Removed {} from the fridge, checking for other ones...", productName);
            Optional<Product> product1 = currentProducts.stream().filter(p -> p.getName().equals(productName.productName)).findFirst();

            if(product1.isEmpty()){
                getContext().getLog().info("No more {} is left, ordering new ones...", product.get().getName());
                this.onOrder(new OrderProduct(new Product(product.get().getPrice(), product.get().getWeight(), product.get().getName()), 1));
            }

        }else {
            getContext().getLog().info("Cannot consume Product {}, there is no such Product in the fridge", productName.productName);
        }
        return this;
    }

    private Behavior<FridgeCommand> onOrder(OrderProduct product) {
        this.orderProcessor = getContext().spawn(OrderProcessor.create(getContext().getSelf(), this.amountSensor, this.weightSensor), "OrderProcessor");
        for(int i = 0; i < product.amount; i++) {
            this.orderProcessor.tell(new OrderProcessor.TryOrder(product.product));
        }
        return this;
    }

    private Behavior<FridgeCommand> onRequestingProductList(RequestProductList productList) {
        getContext().getLog().info("Retrieving full list of items stored in the fridge...");
        int i = 1;

        for (Product currentProduct : currentProducts) {
            getContext().getLog().info("Product {}: {}", i, currentProduct.getName());
            i++;
        }
        return this;
    }

    private Behavior<FridgeCommand> onStoringProduct(StoreProduct productToStore) {
        currentProducts.add(productToStore.product);
        getContext().getLog().info("Fridge successfully stored Product {}", productToStore.product.getName());
        return this;
    }

    private FridgeController onPostStop() {
        getContext().getLog().info("FridgeController {}-{}", groupId, deviceId);
        return this;
    }
}