package at.fhv.sysarch.lab2.homeautomation.ui;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.devices.AirCondition;
import at.fhv.sysarch.lab2.homeautomation.devices.Blinds;
import at.fhv.sysarch.lab2.homeautomation.devices.MediaStation;
import at.fhv.sysarch.lab2.homeautomation.devices.sensors.TemperatureSensor;
import at.fhv.sysarch.lab2.homeautomation.devices.sensors.WeatherSensor;
import at.fhv.sysarch.lab2.homeautomation.devices.simulator.Temperature;
import at.fhv.sysarch.lab2.homeautomation.devices.simulator.TemperatureSimulator;
import at.fhv.sysarch.lab2.homeautomation.devices.simulator.WeatherSimulator;

import java.util.Optional;
import java.util.Scanner;

public class UI extends AbstractBehavior<Void> {
  
    private ActorRef<TemperatureSensor.TemperatureCommand> tempSensor;
    private ActorRef<AirCondition.AirConditionCommand> airCondition;
    private ActorRef<WeatherSensor.WeatherCommand> weatherSensor;
    private ActorRef<MediaStation.MovieCommand> mediaStation;
    private ActorRef<TemperatureSimulator.TemperatureSimulatorCommand> tempSimulator;
    private ActorRef<WeatherSimulator.WeatherSimulatorCommand> weatherSimulator;

    public static Behavior<Void> create(ActorRef<TemperatureSensor.TemperatureCommand> tempSensor,
                                        ActorRef<AirCondition.AirConditionCommand> airCondition,
                                        ActorRef<WeatherSensor.WeatherCommand> weatherSensor,
                                        ActorRef<MediaStation.MovieCommand> mediaStation) {
        return Behaviors.setup(context -> new UI(context, tempSensor, airCondition, weatherSensor, mediaStation));
    }

    private  UI(ActorContext<Void> context,
                ActorRef<TemperatureSensor.TemperatureCommand> tempSensor,
                ActorRef<AirCondition.AirConditionCommand> airCondition,
                ActorRef<WeatherSensor.WeatherCommand> weatherSensor,
                ActorRef<MediaStation.MovieCommand> mediaStation) {

        super(context);
        this.airCondition = airCondition;
        this.tempSensor = tempSensor;
        this.weatherSensor = weatherSensor;
        this.mediaStation = mediaStation;
        new Thread(() -> { this.runCommandLine(); }).start();

        getContext().getLog().info("UI started");
    }

    @Override
    public Receive<Void> createReceive() {
        return newReceiveBuilder().onSignal(PostStop.class, signal -> onPostStop()).build();
    }

    private UI onPostStop() {
        getContext().getLog().info("UI stopped");
        return this;
    }

    public void runCommandLine() {
        Scanner scanner = new Scanner(System.in);
        String[] input = null;
        String reader = "";

        while (!reader.equalsIgnoreCase("quit") && scanner.hasNextLine()) {
            reader = scanner.nextLine();
            String[] command = reader.split(" ");

            if(command[0].equals("t")) {
                this.tempSensor.tell(new TemperatureSensor.ReadTemperature(new Temperature("Celsius", Double.parseDouble(command[1]))));
            }else if(command[0].equals("a")) {
                this.tempSensor.tell(new TemperatureSensor.ReadTemperature(Optional.of(Double.valueOf(command[1]))));
            }else if(command[0].equals("w")) {
                this.weatherSensor.tell(new WeatherSensor.ReadWeather(Optional.of(String.valueOf(command[1]))));
            }else if(command[0].equals("m")) {
                this.mediaStation.tell(new MediaStation.ReadMediaStationStatus(Optional.of(Boolean.valueOf(command[1]))));
            }
        }
        getContext().getLog().info("UI done");
    }
}
