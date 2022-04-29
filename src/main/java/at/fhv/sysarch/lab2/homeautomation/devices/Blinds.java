package at.fhv.sysarch.lab2.homeautomation.devices;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.Optional;

public class Blinds extends AbstractBehavior<Blinds.BlindsCommand> {

    public interface BlindsCommand {}

    public static final class WeatherType implements BlindsCommand {
        Optional<String> weatherType;

        public WeatherType(Optional<String> weatherType) {
            this.weatherType = weatherType;
        }
    }

    public static final class MediaStationPower implements BlindsCommand {
        Optional<Boolean> mediaStationPower;

        public MediaStationPower(Optional<Boolean> mediaStationPower) {
            this.mediaStationPower = mediaStationPower;
        }
    }

    private final String groupId;
    private final String deviceId;


    public Blinds(ActorContext<BlindsCommand> context, String groupId, String deviceId) {
        super(context);
        this.groupId = groupId;
        this.deviceId = deviceId;
    }

    public static Behavior<BlindsCommand> create(String groupId, String deviceId) {
        return Behaviors.setup(context -> new Blinds(context, groupId, deviceId));
    }


    @Override
    public Receive<BlindsCommand> createReceive() {
        return null;
    }
}