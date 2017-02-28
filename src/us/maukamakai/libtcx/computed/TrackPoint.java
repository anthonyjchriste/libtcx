package us.maukamakai.libtcx.computed;

import java.time.ZonedDateTime;
import java.util.Objects;

public class TrackPoint implements Comparable<TrackPoint> {
    private final ZonedDateTime timestamp;
    private final double latitudeDegrees;
    private final double longitudeDegrees;
    private final double altitudeMeters;
    private final double distanceMeters;
    private final short heartRateBpm;
    private final double speedKilometersPerHour;

    private final double FEET_PER_METER = 3.28084;


    public TrackPoint(final ZonedDateTime timestamp,
                      final double latitudeDegrees,
                      final double longitudeDegrees,
                      final double altitudeMeters,
                      final double distanceMeters,
                      final short heartRateBpm,
                      final double speedKilometersPerHour) {
        this.timestamp = timestamp;
        this.latitudeDegrees = latitudeDegrees;
        this.longitudeDegrees = longitudeDegrees;
        this.altitudeMeters = altitudeMeters;
        this.distanceMeters = distanceMeters;
        this.heartRateBpm = heartRateBpm;
        this.speedKilometersPerHour = speedKilometersPerHour;
    }

    public final ZonedDateTime getTimestamp() {
        return this.timestamp;
    }

    public final double getLatitudeDegrees() {
        return this.latitudeDegrees;
    }

    public final double getLongitudeDegrees() {
        return this.longitudeDegrees;
    }

    public final double getAltitudeMeters() {
        return this.altitudeMeters;
    }

    public final double getAltitudeFeet() {
        return this.altitudeMeters * FEET_PER_METER;
    }

    public final double getDistanceMeters() {
        return this.distanceMeters;
    }

    public final double getDistanceFeet() {
        return this.distanceMeters * FEET_PER_METER;
    }

    public final int getHeartRateBpm() {
        return this.heartRateBpm;
    }

    public final double getSpeedKilometersPerHour() {
        return this.speedKilometersPerHour;
    }

    public final double getSpeedMilesPerHour() {
        return this.speedKilometersPerHour * 0.621371; // 1 kph = 0.621371 mph
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.timestamp,
                            this.latitudeDegrees,
                            this.longitudeDegrees,
                            this.altitudeMeters,
                            this.distanceMeters,
                            this.heartRateBpm,
                            this.speedKilometersPerHour);
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof TrackPoint)) {
            return false;
        }
        if(this == other) {
            return true;
        }

        TrackPoint trackPoint = (TrackPoint) other;
        return  this.timestamp.equals(trackPoint.timestamp)             &&
                this.latitudeDegrees    == trackPoint.latitudeDegrees   &&
                this.longitudeDegrees   == trackPoint.longitudeDegrees  &&
                this.altitudeMeters     == trackPoint.altitudeMeters    &&
                this.distanceMeters     == trackPoint.distanceMeters    &&
                this.heartRateBpm       == trackPoint.heartRateBpm      &&
                this.speedKilometersPerHour == trackPoint.speedKilometersPerHour;
    }

    @Override
    public int compareTo(TrackPoint trackPoint) {
        return this.timestamp.compareTo(trackPoint.timestamp);
    }

    @Override
    public String toString() {
        return String.format("{\n\t%s\n\t%f, %f\n\t%f\n\t%f\n\t%d\n\t%f\n}",
                this.timestamp,
                this.latitudeDegrees, this.longitudeDegrees,
                this.altitudeMeters,
                this.distanceMeters,
                this.heartRateBpm,
                this.speedKilometersPerHour);
    }

    public String toStringImperial() {
        return String.format("{\n\t%s\n\t%f, %f\n\t%f\n\t%f\n\t%d\n\t%f\n}",
                this.timestamp,
                this.latitudeDegrees, this.longitudeDegrees,
                this.getAltitudeFeet(),
                this.getDistanceFeet(),
                this.heartRateBpm,
                this.getSpeedMilesPerHour());
    }
}
