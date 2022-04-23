package at.fhv.sysarch.lab2.homeautomation.devices;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.Optional;

public class MediaStation extends AbstractBehavior<MediaStation.MovieCommand> {

    public interface MovieCommand {}

    public final static class ReadMediaStationStatus implements MovieCommand {
        final Optional<Boolean> isRunning;

        public ReadMediaStationStatus(Optional<Boolean> isRunning) {
            this.isRunning = isRunning;
        }
    }

    private final String groupId;
    private final String deviceId;
    private ActorRef<Blinds.BlindsCommand> blinds;

    public static Behavior<MovieCommand> create(ActorRef<Blinds.BlindsCommand> blinds, String groupId, String deviceId){
        return Behaviors.setup(context -> new MediaStation(context, blinds, groupId, deviceId));
    }

    public MediaStation(ActorContext<MovieCommand> context, ActorRef<Blinds.BlindsCommand> blinds, String groupId, String deviceId) {
        super(context);
        this.blinds = blinds;
        this.groupId = groupId;
        this.deviceId = deviceId;
    }

    @Override
    public Receive<MovieCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(ReadMediaStationStatus.class, this::onReadMediaStationStatus)
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    private Behavior<MovieCommand> onReadMediaStationStatus(ReadMediaStationStatus mediaStationStatus) {
        getContext().getLog().info("MediaStation received {}", mediaStationStatus.isRunning.get());
        this.blinds.tell(new Blinds.MediaStationPower(mediaStationStatus.isRunning));
        return this;
    }

    private MediaStation onPostStop() {
        getContext().getLog().info("MediaStation actor {}-{} stopped", groupId, deviceId);
        return this;
    }
}
