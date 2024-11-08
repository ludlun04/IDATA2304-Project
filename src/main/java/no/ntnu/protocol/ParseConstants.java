package no.ntnu.protocol;

public class ParseConstants {

    // Reduce duplication
    public static final String nodeIDRegex = "(?<nodeId>[1-9+]*)";
    public static final String sensorIDRegex = "(?<sensorId>[1-9+]*)";
    public static final String sensorDataRegex = "(?<sensorID>[1-9+]*),\\s(?<actuatorState>\\w*);";
    public static final String actuatorIDRegex = "(?<actuatorId>[1-9+]*)";
    public static final String actuatorDataRegex = "(?<actuatorID>[1-9+]*),\s*(?<actuatorState>\w*);";
    public static final String dataRegex = "(?<data>.*)";
    public static final String responseDataRegex = "(?<responseData>\\w+$)";

}

// sensorID, sensorType, sensorUnit, sensorUnit
