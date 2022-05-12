package at.fhv.sysarch.lab2.homeautomation.ui;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.devices.AirCondition;
import at.fhv.sysarch.lab2.homeautomation.devices.MediaStation;
import at.fhv.sysarch.lab2.homeautomation.devices.fridge.FridgeController;
import at.fhv.sysarch.lab2.homeautomation.devices.sensors.TemperatureSensor;
import at.fhv.sysarch.lab2.homeautomation.devices.sensors.WeatherSensor;
import at.fhv.sysarch.lab2.homeautomation.devices.simulator.Temperature;
import at.fhv.sysarch.lab2.homeautomation.devices.simulator.TemperatureSimulator;
import at.fhv.sysarch.lab2.homeautomation.devices.simulator.WeatherSimulator;
import at.fhv.sysarch.lab2.homeautomation.sharedobjects.Product;

import java.util.Optional;
import java.util.Scanner;

public class UI extends AbstractBehavior<Void> {
  
    private ActorRef<TemperatureSensor.TemperatureCommand> tempSensor;
    private ActorRef<AirCondition.AirConditionCommand> airCondition;
    private ActorRef<WeatherSensor.WeatherCommand> weatherSensor;
    private ActorRef<MediaStation.MovieCommand> mediaStation;
    private ActorRef<FridgeController.FridgeCommand> fridgeController;


    public static Behavior<Void> create(ActorRef<TemperatureSensor.TemperatureCommand> tempSensor,
                                        ActorRef<AirCondition.AirConditionCommand> airCondition,
                                        ActorRef<WeatherSensor.WeatherCommand> weatherSensor,
                                        ActorRef<MediaStation.MovieCommand> mediaStation,
                                        ActorRef<FridgeController.FridgeCommand> fridgeController) {
        return Behaviors.setup(context -> new UI(context, tempSensor, airCondition, weatherSensor, mediaStation, fridgeController));
    }

    private  UI(ActorContext<Void> context,
                ActorRef<TemperatureSensor.TemperatureCommand> tempSensor,
                ActorRef<AirCondition.AirConditionCommand> airCondition,
                ActorRef<WeatherSensor.WeatherCommand> weatherSensor,
                ActorRef<MediaStation.MovieCommand> mediaStation,
                ActorRef<FridgeController.FridgeCommand> fridgeController) {

        super(context);
        this.airCondition = airCondition;
        this.tempSensor = tempSensor;
        this.weatherSensor = weatherSensor;
        this.mediaStation = mediaStation;
        this.fridgeController = fridgeController;
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
        String reader = "";

        while (!reader.equalsIgnoreCase("quit") && scanner.hasNextLine()) {
            reader = scanner.nextLine();
            String[] command = reader.split(" ");

            if(command[0].equals("temperature")) {
                this.tempSensor.tell(new TemperatureSensor.ReadTemperature(new Temperature("Celsius", Double.parseDouble(command[1]))));
            }else if(command[0].equals("ac")) {
                this.airCondition.tell(new AirCondition.PowerAirCondition(Optional.of(Boolean.valueOf(command[1]))));
            }else if(command[0].equals("weather")) {
                this.weatherSensor.tell(new WeatherSensor.ReadWeather(Optional.of(String.valueOf(command[1]))));
            }else if(command[0].equals("mediastation")) {
                this.mediaStation.tell(new MediaStation.ReadMediaStationStatus(Optional.of(Boolean.valueOf(command[1]))));
            }else if(command[0].equals("consume") && command[1] != null) {
                this.fridgeController.tell(new FridgeController.ConsumeProduct(command[1]));
            }else if(command[0].equals("order") && command[1] != null){
                int amount = 1;
                if (!command[4].isEmpty()){
                    amount = Integer.parseInt(command[4]);
                }
                this.fridgeController.tell(new FridgeController.OrderProduct(new Product(Double.parseDouble(command[1]), Double.parseDouble(command[2]), command[3]), amount));
            }
        }
        getContext().getLog().info("UI done");
    }
}
