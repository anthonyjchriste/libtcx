package us.maukamakai.libtcx.computed;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrackT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrackpointT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrainingCenterDatabaseT;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Track {
    public final List<TrackPoint> trackPoints;

    public Track(final TrainingCenterDatabaseT trainingCenterDatabaseT) {
        this(trainingCenterDatabaseT.getActivities().getActivity().get(0).getLap().get(0).getTrack().get(0));
    }

    public Track(final TrackT trackT) {
        this.trackPoints = this.computeTrackPoints(trackT);
    }

    private final List<TrackPoint> computeTrackPoints(final TrackT trackT) {
        List<TrackPoint> emptyList = Collections.emptyList();
        List<TrackPoint> results = new ArrayList<>();

        if(Objects.isNull(trackT)) {
            return emptyList;
        }

        List<TrackpointT> trackPointTs = trackT.getTrackpoint();

        if(Objects.isNull(trackPointTs)) {
            return emptyList;
        }

        if(trackPointTs.size() < 2) {
            return emptyList;
        }

        TrackpointT firstTrackPoint = trackPointTs.get(0);
        results.add(new TrackPoint(
                firstTrackPoint.getTime().toGregorianCalendar().toZonedDateTime(),
                firstTrackPoint.getPosition().getLatitudeDegrees(),
                firstTrackPoint.getPosition().getLongitudeDegrees(),
                firstTrackPoint.getAltitudeMeters(),
                firstTrackPoint.getDistanceMeters(),
                firstTrackPoint.getHeartRateBpm().getValue(),
                0.0
        ));

        for(int i = 1; i < trackPointTs.size(); i++) {
            TrackpointT prev = trackPointTs.get(i - 1);
            TrackpointT cur = trackPointTs.get(i);

            results.add(new TrackPoint(
                    zonedDateTimeFromXMLGregorianCalendar(cur.getTime()),
                    cur.getPosition().getLatitudeDegrees(),
                    cur.getPosition().getLongitudeDegrees(),
                    cur.getAltitudeMeters(),
                    cur.getDistanceMeters(),
                    cur.getHeartRateBpm().getValue(),
                    getSpeedKilometersPerHour(prev, cur)
            ));
        }

        return results;
    }

    private ZonedDateTime zonedDateTimeFromXMLGregorianCalendar(final XMLGregorianCalendar xmlGregorianCalendar) {
        return xmlGregorianCalendar.toGregorianCalendar().toZonedDateTime();
    }

    private double getSpeedKilometersPerHour(final TrackpointT a, final TrackpointT b) {
        double deltaDistanceMeters = b.getDistanceMeters() - a.getDistanceMeters();
        double deltaTimeMilliseconds =  b.getTime().toGregorianCalendar().toInstant().toEpochMilli() -
                                        a.getTime().toGregorianCalendar().toInstant().toEpochMilli();
        double deltaDistanceKilometers = deltaDistanceMeters / 1000.0;
        double deltaTimeHours = deltaTimeMilliseconds
                / 1000.0    // Milliseconds in a seconds
                / 60.0      // Seconds in a minute
                / 60.0;     // Minutes in an hour

        return deltaDistanceKilometers / deltaTimeHours;
    }
}
