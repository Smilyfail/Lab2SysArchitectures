package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

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

    public static final class FridgeFill implements FridgeCommand {
        final Optional<Integer> value;

        public FridgeFill(Optional<Integer> value) {
            this.value = value;
        }
    }


    private  double maxWeight;
    private final double maxSpace;
    private List<Product> currentProducts = new ArrayList<>();

    private String groupId;
    private String deviceId;

    public FridgeController(ActorContext<FridgeCommand> context, String groupId, String deviceId) {
        super(context);
        this.maxWeight = 50;
        this.maxSpace = 50;
        this.groupId = groupId;
        this.deviceId = deviceId;
        this.currentProducts = new ArrayList<>();
        currentProducts.add(new Product(0.2, 1.49, 0.5, "Red Bull"));
        currentProducts.add(new Product(2, 4.99, 3, "Potatoes"));
        currentProducts.add(new Product(0.5, 2.99, 1, "Carrots"));
        currentProducts.add(new Product(2, 11.99, 5, "Steak"));
        currentProducts.add(new Product(0.5, 0.49, 0.5, "Milk"));
        currentProducts.add(new Product(0.5, 0.49, 0.5, "Milk"));
        currentProducts.add(new Product(0.5, 0.49, 0.5, "Milk"));
        currentProducts.add(new Product(0.25, 5.99, 1, "Eggs"));
    }

    public static Behavior<FridgeCommand> create(String groupId, String deviceId) {
        return Behaviors.setup(context -> new FridgeController(context, groupId, deviceId));
    }

    @Override
    public Receive<FridgeCommand> createReceive() {
        return null;
    }
}
