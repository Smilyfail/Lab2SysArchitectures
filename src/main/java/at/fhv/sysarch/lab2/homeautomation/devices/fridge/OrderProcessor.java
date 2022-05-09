package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class OrderProcessor extends AbstractBehavior<OrderProcessor.OrderCommand> {

    public interface OrderCommand {}

    private final ActorRef<FridgeController.FridgeCommand> fridgeController;
    private final ActorRef<AmountSensor.AmountCommand> amountSensor;
    private final ActorRef<WeightSensor.WeightCommand> weightSensor;

    public OrderProcessor(ActorContext<OrderCommand> context, ActorRef<FridgeController.FridgeCommand> fridgeController, ActorRef<AmountSensor.AmountCommand> amountSensor, ActorRef<WeightSensor.WeightCommand> weightSensor) {
        super(context);
        this.fridgeController = fridgeController;
        this.amountSensor = amountSensor;
        this.weightSensor = weightSensor;
    }

    public static Behavior<OrderCommand> createActorRef(ActorRef<FridgeController.FridgeCommand> fridgeController, ActorRef<AmountSensor.AmountCommand> amountSensor, ActorRef<WeightSensor.WeightCommand> weightSensor) {
        return Behaviors.setup(context -> new OrderProcessor(context, fridgeController, amountSensor, weightSensor));
    }

    public Receive<OrderCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage()
                .build();
    }

    private Behavior<OrderCommand>tryOrder() {

    }
}
