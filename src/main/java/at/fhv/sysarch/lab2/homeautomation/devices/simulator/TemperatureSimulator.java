package at.fhv.sysarch.lab2.homeautomation.devices.simulator;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.*;
import at.fhv.sysarch.lab2.homeautomation.devices.sensors.TemperatureSensor;


import java.time.Duration;
import java.util.Random;

public class TemperatureSimulator extends AbstractBehavior<TemperatureSimulator.TemperatureSimulatorCommand> {

    public interface TemperatureSimulatorCommand {
    }

    public static final class CurrentTemperature implements TemperatureSimulatorCommand {
        Temperature currentTemp;

        public CurrentTemperature(Temperature currentTemp) {
            this.currentTemp = currentTemp;
        }
    }

    public static final class TemperatureRequest implements TemperatureSimulatorCommand {
        ActorRef<TemperatureSensor.TemperatureCommand> sensor;

        public TemperatureRequest(ActorRef<TemperatureSensor.TemperatureCommand> sensor) {
            this.sensor = sensor;
        }
    }

    private Random random = new Random();
    private Temperature currentTemperature;
    private ActorRef<TemperatureSensor.TemperatureCommand> tempSensor;
    private String groupId;
    private String deviceId;
    private final TimerScheduler<TemperatureSimulatorCommand> temperatureTimeSchedule;

    public static Behavior<TemperatureSimulatorCommand> create(ActorRef<TemperatureSensor.TemperatureCommand> tempSensor, String groupId, String deviceId) {
        return Behaviors.setup(context -> Behaviors.withTimers(timer -> new TemperatureSimulator(context, tempSensor, groupId, deviceId, timer)));
    }

    public TemperatureSimulator(ActorContext<TemperatureSimulatorCommand> context, ActorRef<TemperatureSensor.TemperatureCommand> tempSensor, String groupId, String deviceId, TimerScheduler<TemperatureSimulatorCommand> temperatureTimeSchedule) {
        super(context);
        this.currentTemperature = new Temperature("Celsius", 20);
        this.tempSensor = tempSensor;
        this.groupId = groupId;
        this.deviceId = deviceId;
        this.temperatureTimeSchedule = temperatureTimeSchedule;
        this.temperatureTimeSchedule.startTimerAtFixedRate(new CurrentTemperature(new Temperature(currentTemperature.unit(), currentTemperature.value())), Duration.ofSeconds(10));

        getContext().getLog().info("TemperatureSimulator started");
    }

    @Override
    public Receive<TemperatureSimulatorCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(CurrentTemperature.class, this::onTemperatureUpdate)
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    private Behavior<TemperatureSimulatorCommand> onTemperatureUpdate(CurrentTemperature currentTemperature) {
        //changes temperature by a maximum of 3
        currentTemperature = new CurrentTemperature(new Temperature(currentTemperature.currentTemp.unit, (currentTemperature.currentTemp.value - (random.nextDouble() % 3))));
        tempSensor.tell(new TemperatureSensor.ReadTemperature(new Temperature(currentTemperature.currentTemp.unit, currentTemperature.currentTemp.value)));
        getContext().getLog().info("TemperatureSimulator received a new temperature, changing to {} {} ", currentTemperature.currentTemp.value, currentTemperature.currentTemp.unit);
        return this;
    }

    private TemperatureSimulator onPostStop() {
        getContext().getLog().info("TemperatureSimulator actor {}-{} stopped", groupId, deviceId);
        return this;
    }
}