package at.fhv.sysarch.lab2.homeautomation.devices.sensors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.devices.Blinds;
import at.fhv.sysarch.lab2.homeautomation.devices.simulator.Weather;

import java.util.Optional;

public class WeatherSensor extends AbstractBehavior<WeatherSensor.WeatherCommand> {

    public interface WeatherCommand {}

    public static final class ReadWeather implements WeatherSensor.WeatherCommand {
        final Optional<String> weather;

        public ReadWeather(Optional<String> weather) {
            this.weather = weather;
        }
    }

    private final String groupId;
    private final String deviceId;
    private final ActorRef<Blinds.BlindsCommand> blinds;

    public static Behavior<WeatherCommand> create(ActorRef<Blinds.BlindsCommand> blinds, String groupId, String deviceId) {
        return Behaviors.setup(context -> new WeatherSensor(context, blinds, groupId, deviceId));
    }

    public WeatherSensor (ActorContext<WeatherCommand> context, ActorRef<Blinds.BlindsCommand> blinds, String groupId, String deviceId) {
        super(context);
        this.blinds = blinds;
        this.groupId = groupId;
        this.deviceId = deviceId;

        getContext().getLog().info("WeatherSensor started");
    }

    @Override
    public Receive<WeatherSensor.WeatherCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(ReadWeather.class, this::onReadWeather)
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    private Behavior<WeatherCommand> onReadWeather(ReadWeather weather) {
        getContext().getLog().info("WeatherSensor received {}", weather.weather.get());
        this.blinds.tell(new Blinds.WeatherType(weather.weather));
        return this;
    }

    private WeatherSensor onPostStop() {
        getContext().getLog().info("WeatherSensor actor {}-{} stopped", groupId, deviceId);
        return this;
    }
}