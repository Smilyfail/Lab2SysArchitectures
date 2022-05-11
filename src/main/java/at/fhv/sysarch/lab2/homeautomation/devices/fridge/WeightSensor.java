package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.Optional;

public class WeightSensor extends AbstractBehavior<WeightSensor.WeightCommand> {

    public interface WeightCommand {}

    public static final class ReadWeight implements WeightCommand {
        final Optional<Double> value;

        public ReadWeight(Optional<Double> value) {
            this.value = value;
        }
    }

    public static final class AddWeight implements WeightCommand {
        final double weight;

        public AddWeight(double weight) {
            this.weight = weight;
        }
    }

    public static final class RemoveWeight implements WeightCommand {
        final double weight;

        public RemoveWeight(double weight) {
            this.weight = weight;
        }
    }

    public static Behavior<WeightCommand> create(ActorRef<FridgeController.FridgeCommand> fridgeController, String groupId, String deviceId) {
        return Behaviors.setup(context -> new WeightSensor(context, fridgeController, groupId, deviceId));
    }

    private String groupId;
    private String deviceId;
    private final double maxWeight = 50.00;
    private double currentWeight = 0;

    public WeightSensor(ActorContext<WeightCommand> context, ActorRef<FridgeController.FridgeCommand> fridgeController, String groupId, String deviceId) {
        super(context);
        this.groupId = groupId;
        this.deviceId = deviceId;

        getContext().getLog().info("WeightSensor started");
    }

    @Override
    public Receive<WeightCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(ReadWeight.class, this::onReadWeight)
                .onMessage(AddWeight.class, this::onAddWeight)
                .onMessage(RemoveWeight.class, this::onRemoveWeight)
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    private Behavior<WeightCommand> onReadWeight(ReadWeight weight) {
        getContext().getLog().info("WeightSensor received {}", weight.value.get());
        this.fridgeController.tell(new FridgeController.FridgeWeight(weight.value));
        return this;
    }

    private Behavior<WeightCommand> onAddWeight(AddWeight weight) {
        currentWeight += weight.weight;
        getContext().getLog().info("WeightSensor recieved {}kg to add, there are now {} out of {}kg occupied", currentWeight, currentWeight, maxWeight);
        return this;
    }

    private Behavior<WeightCommand> onRemoveWeight(RemoveWeight weight) {
        currentWeight -= weight.weight;
        getContext().getLog().info("WeightSensor recieved {}kg to remove, there are now {} out of {}kg occupied", currentWeight, currentWeight, maxWeight);
        return this;
    }

    private WeightSensor onPostStop() {
        getContext().getLog().info("WeightSensor actor {}-{} stopped", groupId, deviceId);
        return this;
    }
}
