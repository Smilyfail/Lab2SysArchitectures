package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

import akka.actor.Actor;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.Optional;

public class AmountSensor extends AbstractBehavior<AmountSensor.AmountCommand> {

    public interface AmountCommand {}

    public static final class ReadAmount implements AmountCommand {
        final Optional<Integer> value;

        public ReadAmount(Optional<Integer> value) {
            this.value = value;
        }
    }

    public static Behavior<AmountCommand> create(ActorRef<FridgeController.FridgeCommand> fridgeController, String groupId, String deviceId) {
        return Behaviors.setup(context -> new AmountSensor(context, fridgeController, groupId, deviceId));
    }

    private String groupId;
    private String deviceId;
    private ActorRef<FridgeController.FridgeCommand> fridgeController;

    public AmountSensor(ActorContext<AmountCommand> context, ActorRef<FridgeController.FridgeCommand> fridgeController, String groupId, String deviceId) {
        super(context);
        this.groupId = groupId;
        this.deviceId = deviceId;
        this.fridgeController = fridgeController;

        getContext().getLog().info("AmountSensor started");
    }

    @Override
    public Receive<AmountCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(ReadAmount.class, this::onReadAmount)
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    private Behavior<AmountCommand> onReadAmount(ReadAmount readAmount) {
        getContext().getLog().info("AmountSensor received {}", readAmount.value.get());
        this.fridgeController.tell(new FridgeController.FridgeFill(readAmount.value));
        return this;
    }

    private AmountSensor onPostStop() {
        getContext().getLog().info("AmountSensor actor {}-{} stopped", groupId, deviceId);
        return this;
    }
}