package at.fhv.sysarch.lab2.homeautomation.devices.simulator;

import java.util.Random;

public enum Weather {
    SUNNY,
    RAINY,
    STORMY,
    SNOWY,
    CLOUDY;

    private static Random random = new Random();

    public static Weather random() {

        int x = (random.nextInt() % 5);
        if (x == 0) {
            return Weather.RAINY;
        } else if (x == 1) {
            return Weather.SUNNY;
        } else if (x == 2) {
            return Weather.STORMY;
        }else if (x == 3) {
            return Weather.SNOWY;
        }
        return Weather.CLOUDY;
    }
}
