package us.maukamakai.libtcx.reader;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrainingCenterDatabaseT;
import us.maukamakai.libtcx.computed.Track;
import us.maukamakai.libtcx.computed.TrackPoint;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class TcxReader {
    private JAXBContext jaxbContext;
    private Unmarshaller unmarshaller;
    public final boolean isValidating;

    public TcxReader() {
        this(false);
    }

    public TcxReader(final boolean isValidating){
        this.isValidating = isValidating;
        init();
    }

    private void init() {
        try {
            this.jaxbContext = JAXBContext.newInstance("com.garmin.xmlschemas.trainingcenterdatabase.v2");
            this.unmarshaller = Objects.nonNull(this.jaxbContext) ? this.jaxbContext.createUnmarshaller() : null;

            if(Objects.isNull(this.jaxbContext)) {
                throw new TcxReaderException("Error: JAXBContext is null.");
            }

            if(Objects.isNull(this.unmarshaller)) {
                throw new TcxReaderException("Error: Unmarshaller is null.");
            }

            if(this.isValidating) {
                this.unmarshaller.setSchema(TcxSchema.getSchema());
            }

        } catch (JAXBException e) {
            throw new TcxReaderException(String.format("Error initializing reader\nMessage: %s", e.getMessage()), e.fillInStackTrace());
        }
    }

    public TrainingCenterDatabaseT read(final Path path) {
        if(Objects.isNull(path)) {
            throw new TcxReaderException("Error: path can not be null.");
        }

        if(Objects.isNull(this.jaxbContext)) {
            throw new TcxReaderException("Error: JAXBContext is null.");
        }

        if(Objects.isNull(this.unmarshaller)) {
            throw new TcxReaderException("Error: Unmarshaller is null.");
        }

        try {
            return (TrainingCenterDatabaseT) ((JAXBElement)this.unmarshaller.unmarshal(path.toFile())).getValue();
        } catch (JAXBException | ClassCastException e) {
            throw new TcxReaderException(String.format("Error parsing XML file\nMessage: %s", e.getMessage()), e.fillInStackTrace());
        }
    }

    public static void main(String[] args) {
        Path testTcx = Paths.get("/", "home", "anthony", "Downloads", "6294058868.tcx");

        TcxReader tcxReader = new TcxReader(true);
        TrainingCenterDatabaseT tcd = tcxReader.read(testTcx);

        Track track = new Track(tcd);

        for(TrackPoint trackPoint : track.trackPoints) {
            System.out.println(trackPoint.toStringImperial());
        }
    }

}
