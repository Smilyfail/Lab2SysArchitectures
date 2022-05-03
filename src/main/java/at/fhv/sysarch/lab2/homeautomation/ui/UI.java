package at.fhv.sysarch.lab2.homeautomation.ui;

import akka.actor.Actor;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.devices.AirCondition;
import at.fhv.sysarch.lab2.homeautomation.devices.MediaStation;
import at.fhv.sysarch.lab2.homeautomation.devices.fridge.AmountSensor;
import at.fhv.sysarch.lab2.homeautomation.devices.fridge.WeightSensor;
import at.fhv.sysarch.lab2.homeautomation.devices.sensors.TemperatureSensor;
import at.fhv.sysarch.lab2.homeautomation.devices.sensors.WeatherSensor;

import java.util.Optional;
import java.util.Scanner;

public class UI extends AbstractBehavior<Void> {

    private ActorRef<TemperatureSensor.TemperatureCommand> tempSensor;
    private ActorRef<AirCondition.AirConditionCommand> airCondition;
    private ActorRef<WeatherSensor.WeatherCommand> weatherSensor;
    private ActorRef<MediaStation.MovieCommand> mediaStation;
    private ActorRef<WeightSensor.WeightCommand> weightSensor;
    private ActorRef<AmountSensor.AmountCommand> amountSensor;

    public static Behavior<Void> create(ActorRef<TemperatureSensor.TemperatureCommand> tempSensor,
                                        ActorRef<AirCondition.AirConditionCommand> airCondition,
                                        ActorRef<WeatherSensor.WeatherCommand> weatherSensor,
                                        ActorRef<MediaStation.MovieCommand> mediaStation,
                                        ActorRef<WeightSensor.WeightCommand> weightSensor,
                                        ActorRef<AmountSensor.AmountCommand> amountSensor) {
        return Behaviors.setup(context -> new UI(context, tempSensor, airCondition, weatherSensor, mediaStation, weightSensor, amountSensor));
    }

    private  UI(ActorContext<Void> context,
                ActorRef<TemperatureSensor.TemperatureCommand> tempSensor,
                ActorRef<AirCondition.AirConditionCommand> airCondition,
                ActorRef<WeatherSensor.WeatherCommand> weatherSensor,
                ActorRef<MediaStation.MovieCommand> mediaStation,
                ActorRef<WeightSensor.WeightCommand> weightSensor,
                ActorRef<AmountSensor.AmountCommand> amountSensor) {

        super(context);
        // TODO: implement actor and behavior as needed
        // TODO: move UI initialization to appropriate place
        this.airCondition = airCondition;
        this.tempSensor = tempSensor;
        this.weatherSensor = weatherSensor;
        this.mediaStation = mediaStation;
        this.weightSensor = weightSensor;
        this.amountSensor = amountSensor;
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
        // TODO: Create Actor for UI Input-Handling
        Scanner scanner = new Scanner(System.in);
        String[] input = null;
        String reader = "";

        while (!reader.equalsIgnoreCase("quit") && scanner.hasNextLine()) {
            reader = scanner.nextLine();
            // TODO: change input handling
            String[] command = reader.split(" ");
            if(command[0].equals("t")) {
                this.tempSensor.tell(new TemperatureSensor.ReadTemperature(Optional.of(Double.valueOf(command[1]))));
            }
            if(command[0].equals("a")) {
                this.airCondition.tell(new AirCondition.PowerAirCondition(Optional.of(Boolean.valueOf(command[1]))));
            }
            if(command[0].equals("w")) {
                this.weatherSensor.tell(new WeatherSensor.ReadWeather(Optional.of(String.valueOf(command[1]))));
            }
            if(command[0].equals("m")) {
                this.mediaStation.tell(new MediaStation.ReadMediaStationStatus(Optional.of(Boolean.valueOf(command[1]))));
            }
            // TODO: process Input
        }
        getContext().getLog().info("UI done");
    }
}
