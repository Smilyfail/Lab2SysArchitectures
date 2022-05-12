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
    private int availableSpace = -1;
    private double availableWeight = -1;
    private final Product product;

    public OrderProcessor(ActorContext<OrderCommand> context, ActorRef<FridgeController.FridgeCommand> fridgeController, ActorRef<AmountSensor.AmountCommand> amountSensor, ActorRef<WeightSensor.WeightCommand> weightSensor, Product product) {
        super(context);
        this.fridgeController = fridgeController;
        this.amountSensor = amountSensor;
        this.weightSensor = weightSensor;
        this.product = product;
        this.weightSensor.tell(new WeightSensor.ReadWeight(getContext().getSelf()));
        this.amountSensor.tell(new AmountSensor.AvailableSpace(getContext().getSelf()));

        getContext().getLog().info("OrderProcessor started");
    }

    public static Behavior<OrderCommand> create(ActorRef<FridgeController.FridgeCommand> fridgeController, ActorRef<AmountSensor.AmountCommand> amountSensor, ActorRef<WeightSensor.WeightCommand> weightSensor, Product product) {
        return Behaviors.setup(context -> new OrderProcessor(context, fridgeController, amountSensor, weightSensor, product));
    }

    public Receive<OrderCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(TryOrder.class, this::onTryOrder)
                .onMessage(ReadAvailableSpace.class, this::onRequestingAvailableSpace)
                .onMessage(ReadCurrentWeight.class, this::onRequestingCurrentWeight)
                .build();
    }

    private Behavior<OrderCommand> onTryOrder(TryOrder tryOrder) {
        if(availableWeight != -1 && availableSpace != -1) {
            if (availableSpace < 1) {
                getContext().getLog().info("OrderProcessor cannot order {}, not enough space in Fridge, aborting...", tryOrder.product.getName());
            } else if (availableWeight < tryOrder.product.getWeight()) {
                getContext().getLog().info("OrderProcessor cannot order {}, not enough weight left for the Fridge, aborting...", tryOrder.product.getName());
            } else {
                getContext().getLog().info("OrderProcessor received order for {} for {}€", tryOrder.product.getName(), tryOrder.product.getPrice());
                getContext().getLog().info("OrderProcessor successfully Ordered product {}, storing...", product.getName());
                this.fridgeController.tell(new FridgeController.StoreProduct(product));
            }
            return Behaviors.stopped();
        }
        return this;
    }

    private Behavior<OrderCommand> onRequestingAvailableSpace(ReadAvailableSpace availableSpace) {
        this.availableSpace = availableSpace.availableSpace;
        return this;
    }

    private Behavior<OrderCommand> onRequestingCurrentWeight(ReadCurrentWeight currentWeight) {
        this.availableWeight = currentWeight.availableWeight;
        this.getContext().getSelf().tell(new TryOrder(this.product));
        return this;
    }
}