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

    public static final class AvailableSpace implements AmountCommand {
        final Optional<Integer> value;

        public AvailableSpace(Optional<Integer> value) {
            this.value = value;
        }
    }

    public static final class OccupySpace implements AmountCommand {}
    public static final class FreeSpace implements AmountCommand {}

    public static Behavior<AmountCommand> create(String groupId, String deviceId) {
        return Behaviors.setup(context -> new AmountSensor(context, groupId, deviceId));
    }

    private String groupId;
    private String deviceId;
    private final int maxSpace = 50;
    private int occupiedSpace = 0;

    public AmountSensor(ActorContext<AmountCommand> context, String groupId, String deviceId) {
        super(context);
        this.groupId = groupId;
        this.deviceId = deviceId;

        getContext().getLog().info("AmountSensor started");
    }

    @Override
    public Receive<AmountCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(AvailableSpace.class, this::onReadAmount)
                .onMessage(OccupySpace.class, this::onAddItem)
                .onMessage(FreeSpace.class, this::onRemoveItem)
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    private Behavior<AmountCommand> onReadAmount(AvailableSpace readAmount) {
        getContext().getLog().info("AmountSensor reads {} of {} spaces are occupied", occupiedSpace, maxSpace);

        return this;
    }

    private Behavior<AmountCommand> onAddItem(OccupySpace occupySpace) {
        this.occupiedSpace += 1;
        getContext().getLog().info("An item was added to the Fridge, now there are {} from {} spaces occupied", occupiedSpace, maxSpace);
        return this;
    }

    private Behavior<AmountCommand> onRemoveItem(FreeSpace freeSpace) {
        this.occupiedSpace -= 1;
        getContext().getLog().info("An item was removed from the Fridge, now there are {} from {} spaces occupied", occupiedSpace, maxSpace);
        return this;
    }

    private AmountSensor onPostStop() {
        getContext().getLog().info("AmountSensor actor {}-{} stopped", groupId, deviceId);
        return this;
    }
}