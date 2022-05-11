package at.fhv.sysarch.lab2.homeautomation;

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
import at.fhv.sysarch.lab2.homeautomation.devices.fridge.AmountSensor;
import at.fhv.sysarch.lab2.homeautomation.devices.fridge.FridgeController;
import at.fhv.sysarch.lab2.homeautomation.devices.fridge.WeightSensor;
import at.fhv.sysarch.lab2.homeautomation.devices.sensors.TemperatureSensor;
import at.fhv.sysarch.lab2.homeautomation.devices.sensors.WeatherSensor;
import at.fhv.sysarch.lab2.homeautomation.devices.simulator.TemperatureSimulator;
import at.fhv.sysarch.lab2.homeautomation.devices.simulator.WeatherSimulator;
import at.fhv.sysarch.lab2.homeautomation.ui.UI;

public class HomeAutomationController extends AbstractBehavior<Void>{
    private ActorRef<TemperatureSensor.TemperatureCommand> tempSensor;
    private ActorRef<AirCondition.AirConditionCommand> airCondition;
    private ActorRef<WeatherSensor.WeatherCommand> weatherSensor;
    private ActorRef<Blinds.BlindsCommand> blinds;
    private ActorRef<MediaStation.MovieCommand> mediaStation;
    private ActorRef<WeightSensor.WeightCommand> fridgeWeightSensor;
    private ActorRef<AmountSensor.AmountCommand> fridgeAmountSensor;
    private ActorRef<FridgeController.FridgeCommand> fridgeController;
    private ActorRef<TemperatureSimulator.TemperatureSimulatorCommand> tempSimulator;
    private ActorRef<WeatherSimulator.WeatherSimulatorCommand> weatherSimulator;

    public static Behavior<Void> create() {
        return Behaviors.setup(HomeAutomationController::new);
    }

    private  HomeAutomationController(ActorContext<Void> context) {
        super(context);
        this.airCondition = getContext().spawn(AirCondition.create("2", "1"), "AirCondition");
        this.tempSensor = getContext().spawn(TemperatureSensor.create(this.airCondition, "1", "1"), "temperatureSensor");
        this.blinds = getContext().spawn(Blinds.create("5", "1"), "blinds");
        this.weatherSensor = getContext().spawn(WeatherSensor.create(this.blinds, "3", "1"),"weatherSensor");
        this.mediaStation = getContext().spawn(MediaStation.create(this.blinds, "4", "1"), "mediaStation");
        this.fridgeController = getContext().spawn(FridgeController.create("6", "1"), "fridgeController");
        this.tempSimulator = getContext().spawn(TemperatureSimulator.create(this.tempSensor, "6", "1"), "tempSimulator");
        this.weatherSimulator = getContext().spawn(WeatherSimulator.create("rainy", this.blinds, "7", "1"), "weatherSimulator");
        ActorRef<Void> ui = getContext().spawn(UI.create(this.tempSensor, this.airCondition, this.weatherSensor, this.mediaStation), "UI");

        getContext().getLog().info("HomeAutomation Application started");
    }

    @Override
    public Receive<Void> createReceive() {
        return newReceiveBuilder().onSignal(PostStop.class, signal -> onPostStop()).build();
    }

    private HomeAutomationController onPostStop() {
        getContext().getLog().info("HomeAutomation Application stopped");
        return this;
    }
}
