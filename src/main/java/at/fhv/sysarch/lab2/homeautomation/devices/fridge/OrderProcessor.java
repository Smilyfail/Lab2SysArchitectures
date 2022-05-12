package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.sharedobjects.Product;

public class OrderProcessor extends AbstractBehavior<OrderProcessor.OrderCommand> {

    public interface OrderCommand {}

    public static final class TryOrder implements OrderCommand {
        private final Product product;

        public TryOrder(Product product) {
            this.product = product;
        }
    }

    public static final class ReadAvailableSpace implements OrderCommand {
        private final int availableSpace;

        public ReadAvailableSpace(int availableSpace) {
            this.availableSpace = availableSpace;
        }
    }

    public static final class ReadCurrentWeight implements OrderCommand {
        private final double availableWeight;

        public ReadCurrentWeight(double availableWeight) {
            this.availableWeight = availableWeight;
        }
    }

    private final ActorRef<FridgeController.FridgeCommand> fridgeController;
    private final ActorRef<AmountSensor.AmountCommand> amountSensor;
    private final ActorRef<WeightSensor.WeightCommand> weightSensor;
    private int availableSpace;
    private double availableWeight;

    public OrderProcessor(ActorContext<OrderCommand> context, ActorRef<FridgeController.FridgeCommand> fridgeController, ActorRef<AmountSensor.AmountCommand> amountSensor, ActorRef<WeightSensor.WeightCommand> weightSensor) {
        super(context);
        this.fridgeController = fridgeController;
        this.amountSensor = amountSensor;
        this.weightSensor = weightSensor;

        this.weightSensor.tell(new WeightSensor.ReadWeight(getContext().getSelf()));
        this.amountSensor.tell(new AmountSensor.AvailableSpace(getContext().getSelf()));
        getContext().getLog().info("OrderProcessor started");
    }

    public static Behavior<OrderCommand> create(ActorRef<FridgeController.FridgeCommand> fridgeController, ActorRef<AmountSensor.AmountCommand> amountSensor, ActorRef<WeightSensor.WeightCommand> weightSensor) {
        return Behaviors.setup(context -> new OrderProcessor(context, fridgeController, amountSensor, weightSensor));
    }

    public Receive<OrderCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(TryOrder.class, this::onTryOrder)
                .onMessage(ReadAvailableSpace.class, this::onRequestingAvailableSpace)
                .onMessage(ReadCurrentWeight.class, this::onRequestingCurrentWeight)
                .build();
    }

    private Behavior<OrderCommand> onTryOrder(TryOrder tryOrder) {
        if(availableSpace >= 1 && availableWeight >= tryOrder.product.getWeight()) {
            getContext().getLog().info("OrderProcessor received order for {} for {}€", tryOrder.product.getName(), tryOrder.product.getPrice());
            completeOrderRequest(tryOrder.product);
        }else if(availableSpace < 1) {
            getContext().getLog().info("OrderProcessor cannot order {}, not enough space in Fridge, aborting...", tryOrder.product.getName());
        }else if(availableWeight < tryOrder.product.getWeight()) {
            getContext().getLog().info("OrderProcessor cannot order {}, not enough weight left for the Fridge, aborting...", tryOrder.product.getName());
        }
        return Behaviors.stopped();
    }

    private Behavior<OrderCommand> onRequestingAvailableSpace(ReadAvailableSpace availableSpace) {
        this.availableSpace = availableSpace.availableSpace;
        return this;
    }

    private Behavior<OrderCommand> onRequestingCurrentWeight(ReadCurrentWeight currentWeight) {
        this.availableWeight = currentWeight.availableWeight;
        return this;
    }

    private Behavior<OrderCommand> completeOrderRequest(Product product) {
        getContext().getLog().info("OrderProcessor successfully Ordered product {}, storing...", product.getName());
        this.fridgeController.tell(new FridgeController.StoreProduct(product));
        return this;
    }

    private OrderProcessor onPostStop() {
        getContext().getLog().info("OrderProcessor stopped...");
        return this;
    }
}