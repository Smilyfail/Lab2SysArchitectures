package at.fhv.sysarch.lab2.homeautomation.devices;

import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
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
    private boolean activeMovie = false;
    private boolean closed = false;

    public static Behavior<BlindsCommand> create(String groupId, String deviceId) {
        return Behaviors.setup(context -> new Blinds(context, groupId, deviceId));
    }

    public Blinds(ActorContext<BlindsCommand> context, String groupId, String deviceId) {
        super(context);
        this.groupId = groupId;
        this.deviceId = deviceId;

        getContext().getLog().info("Blinds started");
    }

    @Override
    public Receive<BlindsCommand> createReceive() {
        return newReceiveBuilder()
            .onMessage(WeatherType.class, this::onWeatherTypeRead)
            .onMessage(MediaStationPower.class, this::onMediaStationPowerRead)
            .onSignal(PostStop.class, signal -> onPostStop())
            .build();
    }

    private Behavior<BlindsCommand> onWeatherTypeRead(WeatherType weatherType) {
        if (weatherType.weatherType.get().equals("sunny")){
            getContext().getLog().info("Closing blinds...");
            this.closed = true;
        }else{
            if(!this.activeMovie){
                getContext().getLog().info("Opening blinds...");
                this.closed = false;
            }
        }
        return this;
    }

    private Behavior<BlindsCommand> onMediaStationPowerRead(MediaStationPower mediaStationPower) {
        if (mediaStationPower.mediaStationPower.get()) {
            this.activeMovie = true;
            getContext().getLog().info("Closing blinds...");
            this.closed = true;
        }else{
            this.activeMovie = false;
            getContext().getLog().info("Opening blinds...");
            this.closed = false;
        }
        return this;
    }

    private Blinds onPostStop() {
        getContext().getLog().info("Blinds actor {}-{} stopped", groupId, deviceId);
        return this;
    }
}