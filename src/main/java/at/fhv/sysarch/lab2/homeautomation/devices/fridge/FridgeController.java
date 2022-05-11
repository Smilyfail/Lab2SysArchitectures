package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

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

    public static final class FridgeWeight implements FridgeCommand {
        final Optional<Double> weight;

        public FridgeWeight(Optional<Double> weight) {
            this.weight = weight;
        }
    }

    private static final class ConsumeProduct implements FridgeCommand {
        final String productName;

        public ConsumeProduct(String productName) {
            this.productName = productName;
        }
    }

    private List<Product> currentProducts = new ArrayList<>();
    private String groupId;
    private String deviceId;

    public FridgeController(ActorContext<FridgeCommand> context, String groupId, String deviceId) {
        super(context);
        this.groupId = groupId;
        this.deviceId = deviceId;
        this.currentProducts = new ArrayList<>();
        currentProducts.add(new Product(0.2, 1.49,"Red Bull"));
        currentProducts.add(new Product(2, 4.99, "Potatoes"));
        currentProducts.add(new Product(0.5, 2.99, "Carrots"));
        currentProducts.add(new Product(2, 11.99,"Steak"));
        currentProducts.add(new Product(0.5, 0.49,"Milk"));
        currentProducts.add(new Product(0.5, 0.49,"Milk"));
        currentProducts.add(new Product(0.5, 0.49,"Milk"));
        currentProducts.add(new Product(0.25, 5.9, "Eggs"));
    }

    public static Behavior<FridgeCommand> create(String groupId, String deviceId) {
        return Behaviors.setup(context -> new FridgeController(context, groupId, deviceId));
    }

    @Override
    public Receive<FridgeCommand> createReceive() {
        return newReceiveBuilder()
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    private Behavior<FridgeCommand>onFridgeWeightRead() {

        return this;
    }

    private Behavior<FridgeCommand>onFridgeFillRead() {
        return this;
    }

    private FridgeController onPostStop() {
        getContext().getLog().info("Fridge Controller Application stopped");
        return this;
    }
}