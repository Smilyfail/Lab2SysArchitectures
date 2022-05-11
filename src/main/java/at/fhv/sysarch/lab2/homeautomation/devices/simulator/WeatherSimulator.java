package at.fhv.sysarch.lab2.homeautomation.devices.simulator;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.*;
import at.fhv.sysarch.lab2.homeautomation.devices.Blinds;
import at.fhv.sysarch.lab2.homeautomation.devices.sensors.WeatherSensor;


import java.time.Duration;
import java.util.Optional;

public class WeatherSimulator extends AbstractBehavior<WeatherSimulator.WeatherSimulatorCommand> {

    public interface WeatherSimulatorCommand {}

    public static final class WeatherUpdate implements WeatherSimulatorCommand {}

    public static Behavior<WeatherSimulatorCommand> create(String weather, ActorRef<Blinds.BlindsCommand> blinds, String groupId, String deviceId) {
        return Behaviors.setup(context -> Behaviors.withTimers(timers -> new WeatherSimulator(context, blinds, groupId, deviceId, weather, timers)));
    }

    private String currentWeather;
    private ActorRef<Blinds.BlindsCommand> blinds;
    private String groupId;
    private String deviceId;

    public WeatherSimulator(ActorContext<WeatherSimulatorCommand> context, ActorRef<Blinds.BlindsCommand> blinds, String groupId, String deviceId, String weather, TimerScheduler<WeatherSimulatorCommand> scheduler) {
        super(context);
        this.currentWeather = weather;
        this.blinds = blinds;
        this.groupId = groupId;
        this.deviceId = deviceId;

        getContext().getLog().info("WeatherSimulator started");
        scheduler.startTimerAtFixedRate(new WeatherUpdate(), Duration.ofSeconds(30));
    }

    @Override
    public Receive<WeatherSimulatorCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(WeatherUpdate.class, this::changeWeather)
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    private Behavior<WeatherSimulatorCommand> changeWeather(WeatherUpdate weatherUpdate) {
        currentWeather = Weather.random().toString();
        this.blinds.tell();
        getContext().getLog().info("WeatherSimulator received {}, changing weather...", currentWeather);
        return Behaviors.same();
    }

    private WeatherSimulator onPostStop() {
        getContext().getLog().info("WeatherSimulator actor {}-{} stopped", groupId, deviceId);
        return this;
    }
}
