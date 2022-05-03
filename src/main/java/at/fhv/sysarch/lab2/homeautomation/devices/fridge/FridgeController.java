package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

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

    private String groupId;
    private String deviceId;

    public FridgeController(ActorContext<FridgeCommand> context, String groupId, String deviceId) {
        super(context);
        this.groupId = groupId;
        this.deviceId = deviceId;
    }

    public static Behavior<FridgeCommand> create(String groupId, String deviceId) {
        return Behaviors.setup(context -> new FridgeController(context, groupId, deviceId));
    }

    @Override
    public Receive<FridgeCommand> createReceive() {
        return null;
    }
}
