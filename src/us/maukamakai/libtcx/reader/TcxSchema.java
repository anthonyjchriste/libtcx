package us.maukamakai.libtcx.reader;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class TcxSchema {
    // http://www8.garmin.com/xmlschemas/TrainingCenterDatabasev2.xsd
    private static final String TCX_SCHEMA = "<?xml version=\"1.0\"?>\n" +
            "<xsd:schema xmlns=\"http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
            "\ttargetNamespace=\"http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2\" xmlns:tc2=\"http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2\"\n" +
            "\telementFormDefault=\"qualified\">\n" +
            "\t<xsd:annotation>\n" +
            "\t\t<xsd:documentation>This schema defines the Garmin Training Center file format.</xsd:documentation>\n" +
            "\t</xsd:annotation>\n" +
            "\t<xsd:element name=\"TrainingCenterDatabase\" type=\"TrainingCenterDatabase_t\">\n" +
            "\t\t<xsd:keyref name=\"ActivityIdKeyRef\" refer=\"tc2:ActivityIdMustBeUnique\">\n" +
            "\t\t\t<xsd:selector xpath=\".//tc2:ActivityRef\"/>\n" +
            "\t\t\t<xsd:field xpath=\"tc2:Id\"/>\n" +
            "\t\t</xsd:keyref>\n" +
            "\t\t<xsd:key name=\"ActivityIdMustBeUnique\">\n" +
            "\t\t\t<xsd:selector xpath=\".//tc2:Activities/tc2:Activity\"/>\n" +
            "\t\t\t<xsd:field xpath=\"tc2:Id\"/>\n" +
            "\t\t</xsd:key>\n" +
            "\t\t<xsd:keyref name=\"MultisportActivityIdKeyRef\" refer=\"tc2:MultisportActivityIdMustBeUnique\">\n" +
            "\t\t\t<xsd:selector xpath=\".//tc2:MultisportActivityRef\"/>\n" +
            "\t\t\t<xsd:field xpath=\"tc2:Id\"/>\n" +
            "\t\t</xsd:keyref>\n" +
            "\t\t<xsd:key name=\"MultisportActivityIdMustBeUnique\">\n" +
            "\t\t\t<xsd:selector xpath=\".//tc2:Activities/tc2:MultiSportSession\"/>\n" +
            "\t\t\t<xsd:field xpath=\"tc2:Id\"/>\n" +
            "\t\t</xsd:key>\n" +
            "\t\t<xsd:keyref name=\"WorkoutNameKeyRef\" refer=\"tc2:WorkoutNameMustBeUnique\">\n" +
            "\t\t\t<xsd:selector xpath=\".//tc2:WorkoutNameRef\"/>\n" +
            "\t\t\t<xsd:field xpath=\"tc2:Id\"/>\n" +
            "\t\t</xsd:keyref>\n" +
            "\t\t<xsd:key name=\"WorkoutNameMustBeUnique\">\n" +
            "\t\t\t<xsd:selector xpath=\".//tc2:Workouts/tc2:Workout\"/>\n" +
            "\t\t\t<xsd:field xpath=\"tc2:Name\"/>\n" +
            "\t\t</xsd:key>\n" +
            "\t\t<xsd:keyref name=\"CourseNameKeyRef\" refer=\"tc2:CourseNameMustBeUnique\">\n" +
            "\t\t\t<xsd:selector xpath=\".//tc2:CourseNameRef\"/>\n" +
            "\t\t\t<xsd:field xpath=\"tc2:Id\"/>\n" +
            "\t\t</xsd:keyref>\n" +
            "\t\t<xsd:key name=\"CourseNameMustBeUnique\">\n" +
            "\t\t\t<xsd:selector xpath=\".//tc2:Courses/tc2:Course\"/>\n" +
            "\t\t\t<xsd:field xpath=\"tc2:Name\"/>\n" +
            "\t\t</xsd:key>\n" +
            "\t</xsd:element>\n" +
            "\t<xsd:complexType name=\"TrainingCenterDatabase_t\">\n" +
            "\t\t<xsd:sequence>\n" +
            "\t\t\t<xsd:element name=\"Folders\" type=\"Folders_t\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"Activities\" type=\"ActivityList_t\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"Workouts\" type=\"WorkoutList_t\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"Courses\" type=\"CourseList_t\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"Author\" type=\"AbstractSource_t\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"Extensions\" type=\"Extensions_t\" minOccurs=\"0\">\n" +
            "\t\t\t\t<xsd:annotation>\n" +
            "\t\t\t\t\t<xsd:documentation>You can extend Training Center by adding your own elements from another schema here.</xsd:documentation>\n" +
            "\t\t\t\t</xsd:annotation>\n" +
            "\t\t\t</xsd:element>\n" +
            "\t\t</xsd:sequence>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"Folders_t\">\n" +
            "\t\t<xsd:sequence>\n" +
            "\t\t\t<xsd:element name=\"History\" type=\"History_t\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"Workouts\" type=\"Workouts_t\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"Courses\" type=\"Courses_t\" minOccurs=\"0\"/>\n" +
            "\t\t</xsd:sequence>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"ActivityList_t\">\n" +
            "\t\t<xsd:sequence>\n" +
            "\t\t\t<xsd:element name=\"Activity\" type=\"Activity_t\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
            "\t\t\t<xsd:element name=\"MultiSportSession\" type=\"MultiSportSession_t\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
            "\t\t</xsd:sequence>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"WorkoutList_t\">\n" +
            "\t\t<xsd:sequence>\n" +
            "\t\t\t<xsd:element name=\"Workout\" type=\"Workout_t\" minOccurs=\"0\" maxOccurs=\"unbounded\">\n" +
            "\t\t\t\t<xsd:annotation>\n" +
            "\t\t\t\t\t<xsd:documentation>\n" +
            "          The StepId should be unique within a workout and should not\n" +
            "          exceed 20. This restricts the number of steps in a workout to 20.\n" +
            "          </xsd:documentation>\n" +
            "\t\t\t\t</xsd:annotation>\n" +
            "\t\t\t\t<xsd:unique name=\"StepIdMustBeUnique\">\n" +
            "\t\t\t\t\t<xsd:selector xpath=\".//*\"/>\n" +
            "\t\t\t\t\t<xsd:field xpath=\"tc2:StepId\"/>\n" +
            "\t\t\t\t</xsd:unique>\n" +
            "\t\t\t</xsd:element>\n" +
            "\t\t</xsd:sequence>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"CourseList_t\">\n" +
            "\t\t<xsd:sequence>\n" +
            "\t\t\t<xsd:element name=\"Course\" type=\"Course_t\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
            "\t\t</xsd:sequence>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"History_t\">\n" +
            "\t\t<xsd:sequence>\n" +
            "\t\t\t<xsd:element name=\"Running\" type=\"HistoryFolder_t\"/>\n" +
            "\t\t\t<xsd:element name=\"Biking\" type=\"HistoryFolder_t\"/>\n" +
            "\t\t\t<xsd:element name=\"Other\" type=\"HistoryFolder_t\"/>\n" +
            "\t\t\t<xsd:element name=\"MultiSport\" type=\"MultiSportFolder_t\"/>\n" +
            "\t\t\t<xsd:element name=\"Extensions\" type=\"Extensions_t\" minOccurs=\"0\">\n" +
            "\t\t\t\t<xsd:annotation>\n" +
            "\t\t\t\t\t<xsd:documentation>You can extend Training Center by adding your own elements from another schema here.</xsd:documentation>\n" +
            "\t\t\t\t</xsd:annotation>\n" +
            "\t\t\t</xsd:element>\n" +
            "\t\t</xsd:sequence>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"ActivityReference_t\">\n" +
            "\t\t<xsd:sequence>\n" +
            "\t\t\t<xsd:element name=\"Id\" type=\"xsd:dateTime\"/>\n" +
            "\t\t</xsd:sequence>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"HistoryFolder_t\">\n" +
            "\t\t<xsd:sequence>\n" +
            "\t\t\t<xsd:element name=\"Folder\" type=\"HistoryFolder_t\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
            "\t\t\t<xsd:element name=\"ActivityRef\" type=\"ActivityReference_t\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
            "\t\t\t<xsd:element name=\"Week\" type=\"Week_t\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
            "\t\t\t<xsd:element name=\"Notes\" type=\"xsd:string\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"Extensions\" type=\"Extensions_t\" minOccurs=\"0\">\n" +
            "\t\t\t\t<xsd:annotation>\n" +
            "\t\t\t\t\t<xsd:documentation>You can extend Training Center by adding your own elements from another schema here.</xsd:documentation>\n" +
            "\t\t\t\t</xsd:annotation>\n" +
            "\t\t\t</xsd:element>\n" +
            "\t\t</xsd:sequence>\n" +
            "\t\t<xsd:attribute name=\"Name\" type=\"xsd:string\" use=\"required\"/>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"MultiSportFolder_t\">\n" +
            "\t\t<xsd:sequence>\n" +
            "\t\t\t<xsd:element name=\"Folder\" type=\"MultiSportFolder_t\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
            "\t\t\t<xsd:element name=\"MultisportActivityRef\" type=\"ActivityReference_t\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
            "\t\t\t<xsd:element name=\"Week\" type=\"Week_t\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
            "\t\t\t<xsd:element name=\"Notes\" type=\"xsd:string\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"Extensions\" type=\"Extensions_t\" minOccurs=\"0\">\n" +
            "\t\t\t\t<xsd:annotation>\n" +
            "\t\t\t\t\t<xsd:documentation>You can extend Training Center by adding your own elements from another schema here.</xsd:documentation>\n" +
            "\t\t\t\t</xsd:annotation>\n" +
            "\t\t\t</xsd:element>\n" +
            "\t\t</xsd:sequence>\n" +
            "\t\t<xsd:attribute name=\"Name\" type=\"xsd:string\" use=\"required\"/>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"Week_t\">\n" +
            "\t\t<xsd:sequence>\n" +
            "\t\t\t<xsd:annotation>\n" +
            "\t\t\t\t<xsd:documentation>\n" +
            "        The week is written out only if the notes are present.\n" +
            "        </xsd:documentation>\n" +
            "\t\t\t</xsd:annotation>\n" +
            "\t\t\t<xsd:element name=\"Notes\" type=\"xsd:string\" minOccurs=\"0\"/>\n" +
            "\t\t</xsd:sequence>\n" +
            "\t\t<xsd:attribute name=\"StartDay\" type=\"xsd:date\" use=\"required\"/>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"MultiSportSession_t\">\n" +
            "\t\t<xsd:sequence>\n" +
            "\t\t\t<xsd:element name=\"Id\" type=\"xsd:dateTime\"/>\n" +
            "\t\t\t<xsd:element name=\"FirstSport\" type=\"FirstSport_t\"/>\n" +
            "\t\t\t<xsd:element name=\"NextSport\" type=\"NextSport_t\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
            "\t\t\t<xsd:element name=\"Notes\" type=\"xsd:string\" minOccurs=\"0\"/>\n" +
            "\t\t</xsd:sequence>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"FirstSport_t\">\n" +
            "\t\t<xsd:sequence>\n" +
            "\t\t\t<xsd:element name=\"Activity\" type=\"Activity_t\"/>\n" +
            "\t\t</xsd:sequence>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"NextSport_t\">\n" +
            "\t\t<xsd:sequence>\n" +
            "\t\t\t<xsd:annotation>\n" +
            "\t\t\t\t<xsd:documentation>\n" +
            "        Each sport contains an optional transition and a run.\n" +
            "        </xsd:documentation>\n" +
            "\t\t\t</xsd:annotation>\n" +
            "\t\t\t<xsd:element name=\"Transition\" type=\"ActivityLap_t\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"Activity\" type=\"Activity_t\"/>\n" +
            "\t\t</xsd:sequence>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:simpleType name=\"Sport_t\">\n" +
            "\t\t<xsd:restriction base=\"Token_t\">\n" +
            "\t\t\t<xsd:enumeration value=\"Running\"/>\n" +
            "\t\t\t<xsd:enumeration value=\"Biking\"/>\n" +
            "\t\t\t<xsd:enumeration value=\"Other\"/>\n" +
            "\t\t</xsd:restriction>\n" +
            "\t</xsd:simpleType>\n" +
            "\t<xsd:complexType name=\"Activity_t\">\n" +
            "\t\t<xsd:sequence>\n" +
            "\t\t\t<xsd:element name=\"Id\" type=\"xsd:dateTime\"/>\n" +
            "\t\t\t<xsd:element name=\"Lap\" type=\"ActivityLap_t\" maxOccurs=\"unbounded\"/>\n" +
            "\t\t\t<xsd:element name=\"Notes\" type=\"xsd:string\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"Training\" type=\"Training_t\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"Creator\" type=\"AbstractSource_t\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"Extensions\" type=\"Extensions_t\" minOccurs=\"0\">\n" +
            "\t\t\t\t<xsd:annotation>\n" +
            "\t\t\t\t\t<xsd:documentation>You can extend Training Center by adding your own elements from another schema here.</xsd:documentation>\n" +
            "\t\t\t\t</xsd:annotation>\n" +
            "\t\t\t</xsd:element>\n" +
            "\t\t</xsd:sequence>\n" +
            "\t\t<xsd:attribute name=\"Sport\" type=\"Sport_t\" use=\"required\"/>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"AbstractSource_t\" abstract=\"true\">\n" +
            "\t\t<xsd:sequence>\n" +
            "\t\t\t<xsd:element name=\"Name\" type=\"Token_t\"/>\n" +
            "\t\t</xsd:sequence>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"Device_t\">\n" +
            "\t\t<xsd:annotation>\n" +
            "\t\t\t<xsd:documentation>Identifies the originating GPS device that tracked a run or\n" +
            "                               used to identify the type of device capable of handling\n" +
            "                               the data for loading.</xsd:documentation>\n" +
            "\t\t</xsd:annotation>\n" +
            "\t\t<xsd:complexContent>\n" +
            "\t\t\t<xsd:extension base=\"AbstractSource_t\">\n" +
            "\t\t\t\t<xsd:sequence>\n" +
            "\t\t\t\t\t<xsd:element name=\"UnitId\" type=\"xsd:unsignedInt\"/>\n" +
            "\t\t\t\t\t<xsd:element name=\"ProductID\" type=\"xsd:unsignedShort\"/>\n" +
            "\t\t\t\t\t<xsd:element name=\"Version\" type=\"Version_t\" minOccurs=\"0\"/>\n" +
//            "\t\t\t\t\t<xsd:element name=\"Version\" type=\"Version_t\"/>\n" +
            "\t\t\t\t</xsd:sequence>\n" +
            "\t\t\t</xsd:extension>\n" +
            "\t\t</xsd:complexContent>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"Application_t\">\n" +
            "\t\t<xsd:annotation>\n" +
            "\t\t\t<xsd:documentation>Identifies a PC software application.</xsd:documentation>\n" +
            "\t\t</xsd:annotation>\n" +
            "\t\t<xsd:complexContent>\n" +
            "\t\t\t<xsd:extension base=\"AbstractSource_t\">\n" +
            "\t\t\t\t<xsd:sequence>\n" +
            "\t\t\t\t\t<xsd:element name=\"Build\" type=\"Build_t\"/>\n" +
            "\t\t\t\t\t<xsd:element name=\"LangID\" type=\"LangID_t\"/>\n" +
            "\t\t\t\t\t<xsd:element name=\"PartNumber\" type=\"PartNumber_t\"/>\n" +
            "\t\t\t\t</xsd:sequence>\n" +
            "\t\t\t</xsd:extension>\n" +
            "\t\t</xsd:complexContent>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:simpleType name=\"LangID_t\">\n" +
            "\t\t<xsd:annotation>\n" +
            "\t\t\t<xsd:documentation>Specifies the two character ISO 693-1 language id that identifies the installed language of this application.\n" +
            "\t\t\t                                  see http://www.loc.gov/standards/iso639-2/ for appropriate ISO identifiers</xsd:documentation>\n" +
            "\t\t</xsd:annotation>\n" +
            "\t\t<xsd:restriction base=\"Token_t\">\n" +
            "\t\t\t<xsd:length value=\"2\"/>\n" +
            "\t\t</xsd:restriction>\n" +
            "\t</xsd:simpleType>\n" +
            "\t<xsd:simpleType name=\"PartNumber_t\">\n" +
            "\t\t<xsd:annotation>\n" +
            "\t\t\t<xsd:documentation>The formatted XXX-XXXXX-XX Garmin part number of a PC application.\n" +
            "</xsd:documentation>\n" +
            "\t\t</xsd:annotation>\n" +
            "\t\t<xsd:restriction base=\"Token_t\">\n" +
            "\t\t\t<xsd:pattern value=\"[\\p{Lu}\\d]{3}-[\\p{Lu}\\d]{5}-[\\p{Lu}\\d]{2}\"/>\n" +
            "\t\t</xsd:restriction>\n" +
            "\t</xsd:simpleType>\n" +
            "\t<xsd:simpleType name=\"Token_t\">\n" +
            "\t\t<xsd:annotation>\n" +
            "\t\t\t<xsd:documentation>Token must be defined as a type because of a bug in the MSXML parser which\n" +
            "    does not correctly process xsd:token using the whiteSpace value of \"collapse\"\n" +
            "</xsd:documentation>\n" +
            "\t\t</xsd:annotation>\n" +
            "\t\t<xsd:restriction base=\"xsd:token\">\n" +
            "\t\t\t<xsd:whiteSpace value=\"collapse\"/>\n" +
            "\t\t</xsd:restriction>\n" +
            "\t</xsd:simpleType>\n" +
            "\t<xsd:complexType name=\"Build_t\">\n" +
            "\t\t<xsd:sequence>\n" +
            "\t\t\t<xsd:element name=\"Version\" type=\"Version_t\"/>\n" +
            "\t\t\t<xsd:element name=\"Type\" type=\"BuildType_t\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"Time\" type=\"Token_t\" minOccurs=\"0\">\n" +
            "\t\t\t\t<xsd:annotation>\n" +
            "\t\t\t\t\t<xsd:documentation>\n" +
            "        A string containing the date and time when an application was built.\n" +
            "        Note that this is not an xsd:dateTime type because this string is\n" +
            "        generated by the compiler and cannot be readily converted to the\n" +
            "        xsd:dateTime format.\n" +
            "        </xsd:documentation>\n" +
            "\t\t\t\t</xsd:annotation>\n" +
            "\t\t\t</xsd:element>\n" +
            "\t\t\t<xsd:element name=\"Builder\" type=\"Token_t\" minOccurs=\"0\">\n" +
            "\t\t\t\t<xsd:annotation>\n" +
            "\t\t\t\t\t<xsd:documentation>\n" +
            "        The login name of the engineer who created this build.\n" +
            "        </xsd:documentation>\n" +
            "\t\t\t\t</xsd:annotation>\n" +
            "\t\t\t</xsd:element>\n" +
            "\t\t</xsd:sequence>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:simpleType name=\"BuildType_t\">\n" +
            "\t\t<xsd:restriction base=\"Token_t\">\n" +
            "\t\t\t<xsd:enumeration value=\"Internal\"/>\n" +
            "\t\t\t<xsd:enumeration value=\"Alpha\"/>\n" +
            "\t\t\t<xsd:enumeration value=\"Beta\"/>\n" +
            "\t\t\t<xsd:enumeration value=\"Release\"/>\n" +
            "\t\t</xsd:restriction>\n" +
            "\t</xsd:simpleType>\n" +
            "\t<xsd:complexType name=\"Version_t\">\n" +
            "\t\t<xsd:sequence>\n" +
            "\t\t\t<xsd:element name=\"VersionMajor\" type=\"xsd:unsignedShort\"/>\n" +
            "\t\t\t<xsd:element name=\"VersionMinor\" type=\"xsd:unsignedShort\"/>\n" +
            "\t\t\t<xsd:element name=\"BuildMajor\" type=\"xsd:unsignedShort\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"BuildMinor\" type=\"xsd:unsignedShort\" minOccurs=\"0\"/>\n" +
            "\t\t</xsd:sequence>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"Training_t\">\n" +
            "\t\t<xsd:sequence>\n" +
            "\t\t\t<xsd:element name=\"QuickWorkoutResults\" type=\"QuickWorkout_t\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"Plan\" type=\"Plan_t\" minOccurs=\"0\"/>\n" +
            "\t\t</xsd:sequence>\n" +
            "\t\t<xsd:attribute name=\"VirtualPartner\" type=\"xsd:boolean\" use=\"required\"/>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"QuickWorkout_t\">\n" +
            "\t\t<xsd:sequence>\n" +
            "\t\t\t<xsd:element name=\"TotalTimeSeconds\" type=\"xsd:double\"/>\n" +
            "\t\t\t<xsd:element name=\"DistanceMeters\" type=\"xsd:double\"/>\n" +
            "\t\t</xsd:sequence>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"Plan_t\">\n" +
            "\t\t<xsd:sequence>\n" +
            "\t\t\t<xsd:element name=\"Name\" type=\"RestrictedToken_t\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"Extensions\" type=\"Extensions_t\" minOccurs=\"0\">\n" +
            "\t\t\t\t<xsd:annotation>\n" +
            "\t\t\t\t\t<xsd:documentation>You can extend Training Center by adding your own elements from another schema here.</xsd:documentation>\n" +
            "\t\t\t\t</xsd:annotation>\n" +
            "\t\t\t</xsd:element>\n" +
            "\t\t</xsd:sequence>\n" +
            "\t\t<xsd:attribute name=\"Type\" type=\"TrainingType_t\" use=\"required\"/>\n" +
            "\t\t<xsd:attribute name=\"IntervalWorkout\" type=\"xsd:boolean\" use=\"required\"/>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:simpleType name=\"TrainingType_t\">\n" +
            "\t\t<xsd:restriction base=\"Token_t\">\n" +
            "\t\t\t<xsd:enumeration value=\"Workout\"/>\n" +
            "\t\t\t<xsd:enumeration value=\"Course\"/>\n" +
            "\t\t</xsd:restriction>\n" +
            "\t</xsd:simpleType>\n" +
            "\t<xsd:complexType name=\"ActivityLap_t\">\n" +
            "\t\t<xsd:sequence>\n" +
            "\t\t\t<xsd:element name=\"TotalTimeSeconds\" type=\"xsd:double\"/>\n" +
            "\t\t\t<xsd:element name=\"DistanceMeters\" type=\"xsd:double\"/>\n" +
            "\t\t\t<xsd:element name=\"MaximumSpeed\" type=\"xsd:double\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"Calories\" type=\"xsd:unsignedShort\"/>\n" +
            "\t\t\t<xsd:element name=\"AverageHeartRateBpm\" type=\"HeartRateInBeatsPerMinute_t\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"MaximumHeartRateBpm\" type=\"HeartRateInBeatsPerMinute_t\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"Intensity\" type=\"Intensity_t\"/>\n" +
            "\t\t\t<xsd:element name=\"Cadence\" type=\"CadenceValue_t\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"TriggerMethod\" type=\"TriggerMethod_t\"/>\n" +
            "\t\t\t<xsd:element name=\"Track\" type=\"Track_t\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
            "\t\t\t<xsd:element name=\"Notes\" type=\"xsd:string\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"Extensions\" type=\"Extensions_t\" minOccurs=\"0\">\n" +
            "\t\t\t\t<xsd:annotation>\n" +
            "\t\t\t\t\t<xsd:documentation>You can extend Training Center by adding your own elements from another schema here.</xsd:documentation>\n" +
            "\t\t\t\t</xsd:annotation>\n" +
            "\t\t\t</xsd:element>\n" +
            "\t\t</xsd:sequence>\n" +
            "\t\t<xsd:attribute name=\"StartTime\" type=\"xsd:dateTime\" use=\"required\"/>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:simpleType name=\"CadenceValue_t\">\n" +
            "\t\t<xsd:restriction base=\"xsd:unsignedByte\">\n" +
            "\t\t\t<xsd:maxInclusive value=\"254\"/>\n" +
            "\t\t</xsd:restriction>\n" +
            "\t</xsd:simpleType>\n" +
            "\t<xsd:simpleType name=\"TriggerMethod_t\">\n" +
            "\t\t<xsd:restriction base=\"Token_t\">\n" +
            "\t\t\t<xsd:enumeration value=\"Manual\"/>\n" +
            "\t\t\t<xsd:enumeration value=\"Distance\"/>\n" +
            "\t\t\t<xsd:enumeration value=\"Location\"/>\n" +
            "\t\t\t<xsd:enumeration value=\"Time\"/>\n" +
            "\t\t\t<xsd:enumeration value=\"HeartRate\"/>\n" +
            "\t\t</xsd:restriction>\n" +
            "\t</xsd:simpleType>\n" +
            "\t<xsd:complexType name=\"Track_t\">\n" +
            "\t\t<xsd:sequence>\n" +
            "\t\t\t<xsd:element name=\"Trackpoint\" type=\"Trackpoint_t\" maxOccurs=\"unbounded\"/>\n" +
            "\t\t</xsd:sequence>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"Trackpoint_t\">\n" +
            "\t\t<xsd:sequence>\n" +
            "\t\t\t<xsd:element name=\"Time\" type=\"xsd:dateTime\"/>\n" +
            "\t\t\t<xsd:element name=\"Position\" type=\"Position_t\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"AltitudeMeters\" type=\"xsd:double\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"DistanceMeters\" type=\"xsd:double\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"HeartRateBpm\" type=\"HeartRateInBeatsPerMinute_t\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"Cadence\" type=\"CadenceValue_t\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"SensorState\" type=\"SensorState_t\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"Extensions\" type=\"Extensions_t\" minOccurs=\"0\">\n" +
            "\t\t\t\t<xsd:annotation>\n" +
            "\t\t\t\t\t<xsd:documentation>You can extend Training Center by adding your own elements from another schema here.</xsd:documentation>\n" +
            "\t\t\t\t</xsd:annotation>\n" +
            "\t\t\t</xsd:element>\n" +
            "\t\t</xsd:sequence>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"Position_t\">\n" +
            "\t\t<xsd:sequence>\n" +
            "\t\t\t<xsd:element name=\"LatitudeDegrees\" type=\"DegreesLatitude_t\"/>\n" +
            "\t\t\t<xsd:element name=\"LongitudeDegrees\" type=\"DegreesLongitude_t\"/>\n" +
            "\t\t</xsd:sequence>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:simpleType name=\"DegreesLongitude_t\">\n" +
            "\t\t<xsd:annotation>\n" +
            "\t\t\t<xsd:documentation/>\n" +
            "\t\t</xsd:annotation>\n" +
            "\t\t<xsd:restriction base=\"xsd:double\">\n" +
            "\t\t\t<xsd:maxExclusive value=\"180.0\"/>\n" +
            "\t\t\t<xsd:minInclusive value=\"-180.0\"/>\n" +
            "\t\t</xsd:restriction>\n" +
            "\t</xsd:simpleType>\n" +
            "\t<xsd:simpleType name=\"DegreesLatitude_t\">\n" +
            "\t\t<xsd:annotation>\n" +
            "\t\t\t<xsd:documentation/>\n" +
            "\t\t</xsd:annotation>\n" +
            "\t\t<xsd:restriction base=\"xsd:double\">\n" +
            "\t\t\t<xsd:maxInclusive value=\"90.0\"/>\n" +
            "\t\t\t<xsd:minInclusive value=\"-90.0\"/>\n" +
            "\t\t</xsd:restriction>\n" +
            "\t</xsd:simpleType>\n" +
            "\t<xsd:simpleType name=\"SensorState_t\">\n" +
            "\t\t<xsd:restriction base=\"Token_t\">\n" +
            "\t\t\t<xsd:enumeration value=\"Present\"/>\n" +
            "\t\t\t<xsd:enumeration value=\"Absent\"/>\n" +
            "\t\t</xsd:restriction>\n" +
            "\t</xsd:simpleType>\n" +
            "\t<xsd:complexType name=\"Workouts_t\">\n" +
            "\t\t<xsd:sequence>\n" +
            "\t\t\t<xsd:element name=\"Running\" type=\"WorkoutFolder_t\">\n" +
            "\t\t\t\t<xsd:unique name=\"RunningSubFolderNamesMustBeUnique\">\n" +
            "\t\t\t\t\t<xsd:selector xpath=\"tc2:Folder\"/>\n" +
            "\t\t\t\t\t<xsd:field xpath=\"@Name\"/>\n" +
            "\t\t\t\t</xsd:unique>\n" +
            "\t\t\t</xsd:element>\n" +
            "\t\t\t<xsd:element name=\"Biking\" type=\"WorkoutFolder_t\">\n" +
            "\t\t\t\t<xsd:unique name=\"BikingSubFolderNamesMustBeUnique\">\n" +
            "\t\t\t\t\t<xsd:selector xpath=\"tc2:Folder\"/>\n" +
            "\t\t\t\t\t<xsd:field xpath=\"@Name\"/>\n" +
            "\t\t\t\t</xsd:unique>\n" +
            "\t\t\t</xsd:element>\n" +
            "\t\t\t<xsd:element name=\"Other\" type=\"WorkoutFolder_t\">\n" +
            "\t\t\t\t<xsd:unique name=\"OtherSubFolderNamesMustBeUnique\">\n" +
            "\t\t\t\t\t<xsd:selector xpath=\"tc2:Folder\"/>\n" +
            "\t\t\t\t\t<xsd:field xpath=\"@Name\"/>\n" +
            "\t\t\t\t</xsd:unique>\n" +
            "\t\t\t</xsd:element>\n" +
            "\t\t\t<xsd:element name=\"Extensions\" type=\"Extensions_t\" minOccurs=\"0\">\n" +
            "\t\t\t\t<xsd:annotation>\n" +
            "\t\t\t\t\t<xsd:documentation>You can extend Training Center by adding your own elements from another schema here.</xsd:documentation>\n" +
            "\t\t\t\t</xsd:annotation>\n" +
            "\t\t\t</xsd:element>\n" +
            "\t\t</xsd:sequence>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"NameKeyReference_t\">\n" +
            "\t\t<xsd:sequence>\n" +
            "\t\t\t<xsd:element name=\"Id\" type=\"RestrictedToken_t\"/>\n" +
            "\t\t</xsd:sequence>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"WorkoutFolder_t\">\n" +
            "\t\t<xsd:sequence>\n" +
            "\t\t\t<xsd:element name=\"Folder\" type=\"WorkoutFolder_t\" minOccurs=\"0\" maxOccurs=\"unbounded\">\n" +
            "\t\t\t\t<xsd:unique name=\"SubFolderNamesMustBeUnique\">\n" +
            "\t\t\t\t\t<xsd:selector xpath=\"tc2:Folder\"/>\n" +
            "\t\t\t\t\t<xsd:field xpath=\"@Name\"/>\n" +
            "\t\t\t\t</xsd:unique>\n" +
            "\t\t\t</xsd:element>\n" +
            "\t\t\t<xsd:element name=\"WorkoutNameRef\" type=\"NameKeyReference_t\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
            "\t\t\t<xsd:element name=\"Extensions\" type=\"Extensions_t\" minOccurs=\"0\">\n" +
            "\t\t\t\t<xsd:annotation>\n" +
            "\t\t\t\t\t<xsd:documentation>You can extend Training Center by adding your own elements from another schema here.</xsd:documentation>\n" +
            "\t\t\t\t</xsd:annotation>\n" +
            "\t\t\t</xsd:element>\n" +
            "\t\t</xsd:sequence>\n" +
            "\t\t<xsd:attribute name=\"Name\" type=\"xsd:string\" use=\"required\"/>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"Workout_t\">\n" +
            "\t\t<xsd:sequence>\n" +
            "\t\t\t<xsd:element name=\"Name\" type=\"RestrictedToken_t\"/>\n" +
            "\t\t\t<xsd:element name=\"Step\" type=\"AbstractStep_t\" maxOccurs=\"unbounded\"/>\n" +
            "\t\t\t<xsd:element name=\"ScheduledOn\" type=\"xsd:date\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
            "\t\t\t<xsd:element name=\"Notes\" type=\"xsd:string\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"Creator\" type=\"AbstractSource_t\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"Extensions\" type=\"Extensions_t\" minOccurs=\"0\">\n" +
            "\t\t\t\t<xsd:annotation>\n" +
            "\t\t\t\t\t<xsd:documentation>You can extend Training Center by adding your own elements from another schema here.</xsd:documentation>\n" +
            "\t\t\t\t</xsd:annotation>\n" +
            "\t\t\t</xsd:element>\n" +
            "\t\t</xsd:sequence>\n" +
            "\t\t<xsd:attribute name=\"Sport\" type=\"Sport_t\" use=\"required\"/>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:simpleType name=\"RestrictedToken_t\">\n" +
            "\t\t<xsd:restriction base=\"Token_t\">\n" +
            "\t\t\t<xsd:minLength value=\"1\"/>\n" +
            "\t\t\t<xsd:maxLength value=\"15\"/>\n" +
            "\t\t</xsd:restriction>\n" +
            "\t</xsd:simpleType>\n" +
            "\t<xsd:complexType name=\"AbstractStep_t\" abstract=\"true\">\n" +
            "\t\t<xsd:sequence>\n" +
            "\t\t\t<xsd:element name=\"StepId\" type=\"StepId_t\"/>\n" +
            "\t\t</xsd:sequence>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:simpleType name=\"StepId_t\">\n" +
            "\t\t<xsd:restriction base=\"xsd:positiveInteger\">\n" +
            "\t\t\t<xsd:maxInclusive value=\"20\"/>\n" +
            "\t\t</xsd:restriction>\n" +
            "\t</xsd:simpleType>\n" +
            "\t<xsd:complexType name=\"Repeat_t\">\n" +
            "\t\t<xsd:complexContent>\n" +
            "\t\t\t<xsd:extension base=\"AbstractStep_t\">\n" +
            "\t\t\t\t<xsd:sequence>\n" +
            "\t\t\t\t\t<xsd:element name=\"Repetitions\" type=\"Repetitions_t\"/>\n" +
            "\t\t\t\t\t<xsd:element name=\"Child\" type=\"AbstractStep_t\" maxOccurs=\"unbounded\"/>\n" +
            "\t\t\t\t</xsd:sequence>\n" +
            "\t\t\t</xsd:extension>\n" +
            "\t\t</xsd:complexContent>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:simpleType name=\"Repetitions_t\">\n" +
            "\t\t<xsd:restriction base=\"xsd:positiveInteger\">\n" +
            "\t\t\t<xsd:minInclusive value=\"2\"/>\n" +
            "\t\t\t<xsd:maxInclusive value=\"99\"/>\n" +
            "\t\t</xsd:restriction>\n" +
            "\t</xsd:simpleType>\n" +
            "\t<xsd:complexType name=\"Step_t\">\n" +
            "\t\t<xsd:complexContent>\n" +
            "\t\t\t<xsd:extension base=\"AbstractStep_t\">\n" +
            "\t\t\t\t<xsd:sequence>\n" +
            "\t\t\t\t\t<xsd:element name=\"Name\" type=\"RestrictedToken_t\" minOccurs=\"0\"/>\n" +
            "\t\t\t\t\t<xsd:element name=\"Duration\" type=\"Duration_t\"/>\n" +
            "\t\t\t\t\t<xsd:element name=\"Intensity\" type=\"Intensity_t\"/>\n" +
            "\t\t\t\t\t<xsd:element name=\"Target\" type=\"Target_t\"/>\n" +
            "\t\t\t\t</xsd:sequence>\n" +
            "\t\t\t</xsd:extension>\n" +
            "\t\t</xsd:complexContent>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"Duration_t\" abstract=\"true\"/>\n" +
            "\t<xsd:simpleType name=\"Intensity_t\">\n" +
            "\t\t<xsd:restriction base=\"Token_t\">\n" +
            "\t\t\t<xsd:enumeration value=\"Active\"/>\n" +
            "\t\t\t<xsd:enumeration value=\"Resting\"/>\n" +
            "\t\t</xsd:restriction>\n" +
            "\t</xsd:simpleType>\n" +
            "\t<xsd:complexType name=\"Target_t\" abstract=\"true\"/>\n" +
            "\t<xsd:complexType name=\"Time_t\">\n" +
            "\t\t<xsd:complexContent>\n" +
            "\t\t\t<xsd:extension base=\"Duration_t\">\n" +
            "\t\t\t\t<xsd:sequence>\n" +
            "\t\t\t\t\t<xsd:element name=\"Seconds\" type=\"xsd:unsignedShort\"/>\n" +
            "\t\t\t\t</xsd:sequence>\n" +
            "\t\t\t</xsd:extension>\n" +
            "\t\t</xsd:complexContent>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"Distance_t\">\n" +
            "\t\t<xsd:complexContent>\n" +
            "\t\t\t<xsd:extension base=\"Duration_t\">\n" +
            "\t\t\t\t<xsd:sequence>\n" +
            "\t\t\t\t\t<xsd:element name=\"Meters\" type=\"xsd:unsignedShort\"/>\n" +
            "\t\t\t\t</xsd:sequence>\n" +
            "\t\t\t</xsd:extension>\n" +
            "\t\t</xsd:complexContent>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"HeartRateAbove_t\">\n" +
            "\t\t<xsd:complexContent>\n" +
            "\t\t\t<xsd:extension base=\"Duration_t\">\n" +
            "\t\t\t\t<xsd:sequence>\n" +
            "\t\t\t\t\t<xsd:element name=\"HeartRate\" type=\"HeartRateValue_t\"/>\n" +
            "\t\t\t\t</xsd:sequence>\n" +
            "\t\t\t</xsd:extension>\n" +
            "\t\t</xsd:complexContent>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"HeartRateValue_t\" abstract=\"true\"/>\n" +
            "\t<xsd:complexType name=\"HeartRateBelow_t\">\n" +
            "\t\t<xsd:complexContent>\n" +
            "\t\t\t<xsd:extension base=\"Duration_t\">\n" +
            "\t\t\t\t<xsd:sequence>\n" +
            "\t\t\t\t\t<xsd:element name=\"HeartRate\" type=\"HeartRateValue_t\"/>\n" +
            "\t\t\t\t</xsd:sequence>\n" +
            "\t\t\t</xsd:extension>\n" +
            "\t\t</xsd:complexContent>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"CaloriesBurned_t\">\n" +
            "\t\t<xsd:complexContent>\n" +
            "\t\t\t<xsd:extension base=\"Duration_t\">\n" +
            "\t\t\t\t<xsd:sequence>\n" +
            "\t\t\t\t\t<xsd:element name=\"Calories\" type=\"xsd:unsignedShort\"/>\n" +
            "\t\t\t\t</xsd:sequence>\n" +
            "\t\t\t</xsd:extension>\n" +
            "\t\t</xsd:complexContent>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"UserInitiated_t\">\n" +
            "\t\t<xsd:complexContent>\n" +
            "\t\t\t<xsd:extension base=\"Duration_t\"/>\n" +
            "\t\t</xsd:complexContent>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"Speed_t\">\n" +
            "\t\t<xsd:complexContent>\n" +
            "\t\t\t<xsd:extension base=\"Target_t\">\n" +
            "\t\t\t\t<xsd:sequence>\n" +
            "\t\t\t\t\t<xsd:element name=\"SpeedZone\" type=\"Zone_t\"/>\n" +
            "\t\t\t\t</xsd:sequence>\n" +
            "\t\t\t</xsd:extension>\n" +
            "\t\t</xsd:complexContent>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"HeartRate_t\">\n" +
            "\t\t<xsd:complexContent>\n" +
            "\t\t\t<xsd:extension base=\"Target_t\">\n" +
            "\t\t\t\t<xsd:sequence>\n" +
            "\t\t\t\t\t<xsd:element name=\"HeartRateZone\" type=\"Zone_t\"/>\n" +
            "\t\t\t\t</xsd:sequence>\n" +
            "\t\t\t</xsd:extension>\n" +
            "\t\t</xsd:complexContent>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"Cadence_t\">\n" +
            "\t\t<xsd:complexContent>\n" +
            "\t\t\t<xsd:extension base=\"Target_t\">\n" +
            "\t\t\t\t<xsd:sequence>\n" +
            "\t\t\t\t\t<xsd:element name=\"Low\" type=\"xsd:double\"/>\n" +
            "\t\t\t\t\t<xsd:element name=\"High\" type=\"xsd:double\"/>\n" +
            "\t\t\t\t</xsd:sequence>\n" +
            "\t\t\t</xsd:extension>\n" +
            "\t\t</xsd:complexContent>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"None_t\">\n" +
            "\t\t<xsd:complexContent>\n" +
            "\t\t\t<xsd:extension base=\"Target_t\"/>\n" +
            "\t\t</xsd:complexContent>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"Zone_t\" abstract=\"true\"/>\n" +
            "\t<xsd:complexType name=\"PredefinedSpeedZone_t\">\n" +
            "\t\t<xsd:complexContent>\n" +
            "\t\t\t<xsd:extension base=\"Zone_t\">\n" +
            "\t\t\t\t<xsd:sequence>\n" +
            "\t\t\t\t\t<xsd:element name=\"Number\" type=\"SpeedZoneNumbers_t\"/>\n" +
            "\t\t\t\t</xsd:sequence>\n" +
            "\t\t\t</xsd:extension>\n" +
            "\t\t</xsd:complexContent>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:simpleType name=\"SpeedZoneNumbers_t\">\n" +
            "\t\t<xsd:restriction base=\"xsd:positiveInteger\">\n" +
            "\t\t\t<xsd:maxInclusive value=\"10\"/>\n" +
            "\t\t</xsd:restriction>\n" +
            "\t</xsd:simpleType>\n" +
            "\t<xsd:complexType name=\"CustomSpeedZone_t\">\n" +
            "\t\t<xsd:complexContent>\n" +
            "\t\t\t<xsd:extension base=\"Zone_t\">\n" +
            "\t\t\t\t<xsd:sequence>\n" +
            "\t\t\t\t\t<xsd:element name=\"ViewAs\" type=\"SpeedType_t\"/>\n" +
            "\t\t\t\t\t<xsd:element name=\"LowInMetersPerSecond\" type=\"SpeedInMetersPerSecond_t\"/>\n" +
            "\t\t\t\t\t<xsd:element name=\"HighInMetersPerSecond\" type=\"SpeedInMetersPerSecond_t\"/>\n" +
            "\t\t\t\t</xsd:sequence>\n" +
            "\t\t\t</xsd:extension>\n" +
            "\t\t</xsd:complexContent>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:simpleType name=\"SpeedInMetersPerSecond_t\">\n" +
            "\t\t<xsd:restriction base=\"xsd:double\">\n" +
            "\t\t\t<xsd:minExclusive value=\"0\"/>\n" +
            "\t\t</xsd:restriction>\n" +
            "\t</xsd:simpleType>\n" +
            "\t<xsd:simpleType name=\"SpeedType_t\">\n" +
            "\t\t<xsd:restriction base=\"Token_t\">\n" +
            "\t\t\t<xsd:enumeration value=\"Pace\"/>\n" +
            "\t\t\t<xsd:enumeration value=\"Speed\"/>\n" +
            "\t\t</xsd:restriction>\n" +
            "\t</xsd:simpleType>\n" +
            "\t<xsd:complexType name=\"PredefinedHeartRateZone_t\">\n" +
            "\t\t<xsd:complexContent>\n" +
            "\t\t\t<xsd:extension base=\"Zone_t\">\n" +
            "\t\t\t\t<xsd:sequence>\n" +
            "\t\t\t\t\t<xsd:element name=\"Number\" type=\"HeartRateZoneNumbers_t\"/>\n" +
            "\t\t\t\t</xsd:sequence>\n" +
            "\t\t\t</xsd:extension>\n" +
            "\t\t</xsd:complexContent>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:simpleType name=\"HeartRateZoneNumbers_t\">\n" +
            "\t\t<xsd:restriction base=\"xsd:positiveInteger\">\n" +
            "\t\t\t<xsd:maxInclusive value=\"5\"/>\n" +
            "\t\t</xsd:restriction>\n" +
            "\t</xsd:simpleType>\n" +
            "\t<xsd:complexType name=\"CustomHeartRateZone_t\">\n" +
            "\t\t<xsd:complexContent>\n" +
            "\t\t\t<xsd:extension base=\"Zone_t\">\n" +
            "\t\t\t\t<xsd:sequence>\n" +
            "\t\t\t\t\t<xsd:element name=\"Low\" type=\"HeartRateValue_t\"/>\n" +
            "\t\t\t\t\t<xsd:element name=\"High\" type=\"HeartRateValue_t\"/>\n" +
            "\t\t\t\t</xsd:sequence>\n" +
            "\t\t\t</xsd:extension>\n" +
            "\t\t</xsd:complexContent>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"HeartRateInBeatsPerMinute_t\">\n" +
            "\t\t<xsd:complexContent>\n" +
            "\t\t\t<xsd:extension base=\"HeartRateValue_t\">\n" +
            "\t\t\t\t<xsd:sequence>\n" +
            "\t\t\t\t\t<xsd:element name=\"Value\" type=\"positiveByte\"/>\n" +
            "\t\t\t\t</xsd:sequence>\n" +
            "\t\t\t</xsd:extension>\n" +
            "\t\t</xsd:complexContent>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"HeartRateAsPercentOfMax_t\">\n" +
            "\t\t<xsd:complexContent>\n" +
            "\t\t\t<xsd:extension base=\"HeartRateValue_t\">\n" +
            "\t\t\t\t<xsd:sequence>\n" +
            "\t\t\t\t\t<xsd:element name=\"Value\" type=\"PercentOfMax_t\"/>\n" +
            "\t\t\t\t</xsd:sequence>\n" +
            "\t\t\t</xsd:extension>\n" +
            "\t\t</xsd:complexContent>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:simpleType name=\"PercentOfMax_t\">\n" +
            "\t\t<xsd:restriction base=\"xsd:unsignedByte\">\n" +
            "\t\t\t<xsd:minInclusive value=\"0\"/>\n" +
            "\t\t\t<xsd:maxInclusive value=\"100\"/>\n" +
            "\t\t</xsd:restriction>\n" +
            "\t</xsd:simpleType>\n" +
            "\t<xsd:simpleType name=\"positiveByte\">\n" +
            "\t\t<xsd:restriction base=\"xsd:unsignedByte\">\n" +
            "\t\t\t<xsd:minInclusive value=\"1\"/>\n" +
            "\t\t</xsd:restriction>\n" +
            "\t</xsd:simpleType>\n" +
            "\t<xsd:simpleType name=\"Gender_t\">\n" +
            "\t\t<xsd:restriction base=\"Token_t\">\n" +
            "\t\t\t<xsd:enumeration value=\"Male\"/>\n" +
            "\t\t\t<xsd:enumeration value=\"Female\"/>\n" +
            "\t\t</xsd:restriction>\n" +
            "\t</xsd:simpleType>\n" +
            "\t<xsd:complexType name=\"Courses_t\">\n" +
            "\t\t<xsd:sequence>\n" +
            "\t\t\t<xsd:element name=\"CourseFolder\" type=\"CourseFolder_t\">\n" +
            "\t\t\t\t<xsd:unique name=\"CourseSubFolderNamesMustBeUnique\">\n" +
            "\t\t\t\t\t<xsd:selector xpath=\"tc2:CourseFolder\"/>\n" +
            "\t\t\t\t\t<xsd:field xpath=\"@Name\"/>\n" +
            "\t\t\t\t</xsd:unique>\n" +
            "\t\t\t</xsd:element>\n" +
            "\t\t\t<xsd:element name=\"Extensions\" type=\"Extensions_t\" minOccurs=\"0\">\n" +
            "\t\t\t\t<xsd:annotation>\n" +
            "\t\t\t\t\t<xsd:documentation>You can extend Training Center by adding your own elements from another schema here.</xsd:documentation>\n" +
            "\t\t\t\t</xsd:annotation>\n" +
            "\t\t\t</xsd:element>\n" +
            "\t\t</xsd:sequence>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"CourseFolder_t\">\n" +
            "\t\t<xsd:sequence>\n" +
            "\t\t\t<xsd:element name=\"Folder\" type=\"CourseFolder_t\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
            "\t\t\t<xsd:element name=\"CourseNameRef\" type=\"NameKeyReference_t\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
            "\t\t\t<xsd:element name=\"Notes\" type=\"xsd:string\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"Extensions\" type=\"Extensions_t\" minOccurs=\"0\">\n" +
            "\t\t\t\t<xsd:annotation>\n" +
            "\t\t\t\t\t<xsd:documentation>You can extend Training Center by adding your own elements from another schema here.</xsd:documentation>\n" +
            "\t\t\t\t</xsd:annotation>\n" +
            "\t\t\t</xsd:element>\n" +
            "\t\t</xsd:sequence>\n" +
            "\t\t<xsd:attribute name=\"Name\" type=\"xsd:string\" use=\"required\"/>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"Course_t\">\n" +
            "\t\t<xsd:sequence>\n" +
            "\t\t\t<xsd:element name=\"Name\" type=\"RestrictedToken_t\"/>\n" +
            "\t\t\t<xsd:element name=\"Lap\" type=\"CourseLap_t\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
            "\t\t\t<xsd:element name=\"Track\" type=\"Track_t\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
            "\t\t\t<xsd:element name=\"Notes\" type=\"xsd:string\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"CoursePoint\" type=\"CoursePoint_t\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
            "\t\t\t<xsd:element name=\"Creator\" type=\"AbstractSource_t\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"Extensions\" type=\"Extensions_t\" minOccurs=\"0\">\n" +
            "\t\t\t\t<xsd:annotation>\n" +
            "\t\t\t\t\t<xsd:documentation>You can extend Training Center by adding your own elements from another schema here.</xsd:documentation>\n" +
            "\t\t\t\t</xsd:annotation>\n" +
            "\t\t\t</xsd:element>\n" +
            "\t\t</xsd:sequence>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"CourseLap_t\">\n" +
            "\t\t<xsd:sequence>\n" +
            "\t\t\t<xsd:element name=\"TotalTimeSeconds\" type=\"xsd:double\"/>\n" +
            "\t\t\t<xsd:element name=\"DistanceMeters\" type=\"xsd:double\"/>\n" +
            "\t\t\t<xsd:element name=\"BeginPosition\" type=\"Position_t\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"BeginAltitudeMeters\" type=\"xsd:double\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"EndPosition\" type=\"Position_t\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"EndAltitudeMeters\" type=\"xsd:double\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"AverageHeartRateBpm\" type=\"HeartRateInBeatsPerMinute_t\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"MaximumHeartRateBpm\" type=\"HeartRateInBeatsPerMinute_t\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"Intensity\" type=\"Intensity_t\"/>\n" +
            "\t\t\t<xsd:element name=\"Cadence\" type=\"CadenceValue_t\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"Extensions\" type=\"Extensions_t\" minOccurs=\"0\">\n" +
            "\t\t\t\t<xsd:annotation>\n" +
            "\t\t\t\t\t<xsd:documentation>You can extend Training Center by adding your own elements from another schema here.</xsd:documentation>\n" +
            "\t\t\t\t</xsd:annotation>\n" +
            "\t\t\t</xsd:element>\n" +
            "\t\t</xsd:sequence>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:complexType name=\"CoursePoint_t\">\n" +
            "\t\t<xsd:sequence>\n" +
            "\t\t\t<xsd:element name=\"Name\" type=\"CoursePointName_t\"/>\n" +
            "\t\t\t<xsd:element name=\"Time\" type=\"xsd:dateTime\"/>\n" +
            "\t\t\t<xsd:element name=\"Position\" type=\"Position_t\"/>\n" +
            "\t\t\t<xsd:element name=\"AltitudeMeters\" type=\"xsd:double\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"PointType\" type=\"CoursePointType_t\"/>\n" +
            "\t\t\t<xsd:element name=\"Notes\" type=\"xsd:string\" minOccurs=\"0\"/>\n" +
            "\t\t\t<xsd:element name=\"Extensions\" type=\"Extensions_t\" minOccurs=\"0\">\n" +
            "\t\t\t\t<xsd:annotation>\n" +
            "\t\t\t\t\t<xsd:documentation>You can extend Training Center by adding your own elements from another schema here.</xsd:documentation>\n" +
            "\t\t\t\t</xsd:annotation>\n" +
            "\t\t\t</xsd:element>\n" +
            "\t\t</xsd:sequence>\n" +
            "\t</xsd:complexType>\n" +
            "\t<xsd:simpleType name=\"CoursePointName_t\">\n" +
            "\t\t<xsd:restriction base=\"Token_t\">\n" +
            "\t\t\t<xsd:minLength value=\"1\"/>\n" +
            "\t\t\t<xsd:maxLength value=\"10\"/>\n" +
            "\t\t</xsd:restriction>\n" +
            "\t</xsd:simpleType>\n" +
            "\t<xsd:simpleType name=\"CoursePointType_t\">\n" +
            "\t\t<xsd:restriction base=\"Token_t\">\n" +
            "\t\t\t<xsd:enumeration value=\"Generic\"/>\n" +
            "\t\t\t<xsd:enumeration value=\"Summit\"/>\n" +
            "\t\t\t<xsd:enumeration value=\"Valley\"/>\n" +
            "\t\t\t<xsd:enumeration value=\"Water\"/>\n" +
            "\t\t\t<xsd:enumeration value=\"Food\"/>\n" +
            "\t\t\t<xsd:enumeration value=\"Danger\"/>\n" +
            "\t\t\t<xsd:enumeration value=\"Left\"/>\n" +
            "\t\t\t<xsd:enumeration value=\"Right\"/>\n" +
            "\t\t\t<xsd:enumeration value=\"Straight\"/>\n" +
            "\t\t\t<xsd:enumeration value=\"First Aid\"/>\n" +
            "\t\t\t<xsd:enumeration value=\"4th Category\"/>\n" +
            "\t\t\t<xsd:enumeration value=\"3rd Category\"/>\n" +
            "\t\t\t<xsd:enumeration value=\"2nd Category\"/>\n" +
            "\t\t\t<xsd:enumeration value=\"1st Category\"/>\n" +
            "\t\t\t<xsd:enumeration value=\"Hors Category\"/>\n" +
            "\t\t\t<xsd:enumeration value=\"Sprint\"/>\n" +
            "\t\t</xsd:restriction>\n" +
            "\t</xsd:simpleType>\n" +
            "\t<xsd:complexType name=\"Extensions_t\">\n" +
            "\t\t<xsd:sequence>\n" +
            "\t\t\t<xsd:any namespace=\"##other\" processContents=\"lax\" minOccurs=\"0\" maxOccurs=\"unbounded\">\n" +
            "\t\t\t\t<xsd:annotation>\n" +
            "\t\t\t\t\t<xsd:documentation>You can extend Training Center by adding your own elements from another schema here.</xsd:documentation>\n" +
            "\t\t\t\t</xsd:annotation>\n" +
            "\t\t\t</xsd:any>\n" +
            "\t\t</xsd:sequence>\n" +
            "\t</xsd:complexType>\n" +
            "</xsd:schema>\n";

    public static void printSchema() {
        System.out.print(TCX_SCHEMA);
    }

    public static void writeSchema(final Path path) throws IOException {
        Files.write(path, TCX_SCHEMA.getBytes());
    }

    public static String getSchemaStr() {
        return TCX_SCHEMA;
    }

    public static Schema getSchema() {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        StreamSource source = new StreamSource(new StringReader(TCX_SCHEMA));
        try {
            return schemaFactory.newSchema(source);
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return null;
    }
}
