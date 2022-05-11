package at.fhv.sysarch.lab2.homeautomation.devices.simulator;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import at.fhv.sysarch.lab2.homeautomation.devices.sensors.TemperatureSensor;


import java.time.Duration;
import java.util.Random;

public class TemperatureSimulator extends AbstractBehavior<TemperatureSimulator.TemperatureSimulatorCommand> {

    public interface TemperatureSimulatorCommand {}

    public static final class TemperatureAuto implements TemperatureSimulatorCommand {

    }

    public static final class TemperatureManual implements TemperatureSimulatorCommand {
        Temperature temperature;

        public TemperatureManual(Temperature temperature) {
            this.temperature = temperature;
        }
    }

    public static final class TemperatureUpdate implements TemperatureSimulatorCommand {
        Temperature currentTemp;

        public TemperatureUpdate(Temperature currentTemp) {
            this.currentTemp = currentTemp;
        }
    }

    public static final class ReceiveTemperatureRequest implements TemperatureSimulatorCommand {
        ActorRef<TemperatureSensor.TemperatureCommand> sensor;

        public ReceiveTemperatureRequest(ActorRef<TemperatureSensor.TemperatureCommand> sensor) {
            this.sensor = sensor;
        }
    }


    private Random random = new Random();
    private Temperature currentTemperature;
    private boolean isAuto = true;
    private final TimerScheduler<TemperatureSimulatorCommand> temperatureTimeSchedule;

    public static Behavior<TemperatureSimulatorCommand> create(Temperature initTemp)  {
        return Behaviors.setup(context -> Behaviors.withTimers(timer -> new TemperatureSimulator(context, initTemp, timer)));
    }

    public TemperatureSimulator(ActorContext<TemperatureSimulatorCommand> context, Temperature initTemp, TimerScheduler<TemperatureSimulatorCommand> temperatureTimeSchedule) {
        super(context);
        this.currentTemperature = initTemp;
        this.temperatureTimeSchedule = temperatureTimeSchedule;
        this.temperatureTimeSchedule.startTimerAtFixedRate(new TemperatureUpdate(new Temperature(currentTemperature.unit(), currentTemperature.value())), Duration.ofSeconds(10));
    }


    @Override
    public Receive<TemperatureSimulatorCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(TemperatureUpdate.class, this::onTemperatureUpdate)
                .onMessage(ReceiveTemperatureRequest.class, this::onTemperatureRequest)
                .onMessage(TemperatureManual.class, this::manualTemperature)
                .onMessage(TemperatureAuto.class, this::autoTemperature)
                .build();
    }

    private Behavior<TemperatureSimulatorCommand> manualTemperature(TemperatureManual t) {
        getContext().getLog().info("[SIMULATOR] Temperature Simulator set to manual mode with " + t.temperature);
        isAuto = false;
        currentTemperature = new Temperature(t.temperature.unit(), t.temperature.value());
        return this;
    }

    private Behavior<TemperatureSimulatorCommand> autoTemperature(TemperatureAuto t) {
        getContext().getLog().info("[SIMULATOR] Temperature Simulator set to auto mode");
        isAuto = true;
        return this;
    }

    private Behavior<TemperatureSimulatorCommand> onTemperatureUpdate(TemperatureUpdate t) {
        if (isAuto) {
            //FIND OTHER CALCULATION
            double temperatureChange = (random.nextInt(62) - 31) / (double) 10;

            currentTemperature = new Temperature(t.currentTemp.unit(), t.currentTemp.value() - temperatureChange);

            getContext().getLog().info("[SIMULATOR] New temperature is: " + currentTemperature);
        }
        return this;
    }

    private Behavior<TemperatureSimulatorCommand> onTemperatureRequest(ReceiveTemperatureRequest req) {
        req.sensor.tell(new TemperatureSensor.ReadTemperature(new Temperature(currentTemperature.unit(), currentTemperature.value())));
        return this;
    }
}
