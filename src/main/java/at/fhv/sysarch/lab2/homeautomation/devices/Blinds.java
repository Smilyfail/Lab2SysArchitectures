package at.fhv.sysarch.lab2.homeautomation.devices;

import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
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

    public Blinds(ActorContext<BlindsCommand> context) {
        super(context);
    }

    @Override
    public Receive<BlindsCommand> createReceive() {
        return null;
    }
}