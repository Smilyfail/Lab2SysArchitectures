package at.fhv.sysarch.lab2.homeautomation.devices.simulator;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import at.fhv.sysarch.lab2.homeautomation.devices.sensors.WeatherSensor;


import java.time.Duration;

public class WeatherSimulator extends AbstractBehavior<WeatherSimulator.WeatherSimulatorCommand> {

    public interface WeatherSimulatorCommand { }

    public static final class WeatherUpdate implements WeatherSimulatorCommand { }

    public static final class WeatherRequest implements WeatherSimulatorCommand {
        ActorRef<WeatherSensor.WeatherCommand> sender;

        public WeatherRequest(ActorRef<WeatherSensor.WeatherCommand> sender) {
            this.sender = sender;
        }
    }

    public static Behavior<WeatherSimulatorCommand> create(Weather weather) {
        return Behaviors.setup(context -> Behaviors.withTimers(timers -> new WeatherSimulator(context, weather, timers)));
    }

    private Weather currentWeather;

    public WeatherSimulator(ActorContext<WeatherSimulatorCommand> context, Weather weather, TimerScheduler<WeatherSimulatorCommand> scheduler) {
        super(context);
        this.currentWeather = weather;

        getContext().getLog().info("Initializing Simulator ...");
        scheduler.startTimerAtFixedRate(new WeatherUpdate(), Duration.ofSeconds(30));
    }

    @Override
    public Receive<WeatherSimulatorCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(WeatherUpdate.class, this::changeWeather)
                .onMessage(WeatherRequest.class, this::sendWeather)
                .build();
    }

    private Behavior<WeatherSimulatorCommand> changeWeather(WeatherUpdate weatherUpdate) {
        currentWeather = Weather.random();
        getContext().getLog().info("[SIMULATOR] Weather set to " + currentWeather);
        return Behaviors.same();
    }

    private Behavior<WeatherSimulatorCommand> sendWeather(WeatherRequest request) {
        request.sender.tell(new WeatherSensor.ReadWeather(currentWeather));
        return this;
    }
}
