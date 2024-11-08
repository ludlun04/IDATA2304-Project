package no.ntnu.protocol;

import java.util.regex.Pattern;
import no.ntnu.protocol.ParseConstants;


public enum MessageSendReceiveFormatPattern {

    // Control-Panel listens:
    REQUEST_ADD_NODE(
        "request add_node %d %s", 
        "request add_node " + 
            ParseConstants.nodeIDRegex + " " + 
            ParseConstants.dataRegex),
    
    REQUEST_REMOVE_NODE(
        "request remove_node %d", 
        "request remove_node " + ParseConstants.nodeIDRegex),

    REQUEST_UPDATE_SENSOR_DATA(
        "request update_sensor_data %d %s", 
        "request update_actuator_state " + 
            ParseConstants.nodeIDRegex + " " + 
            ParseConstants.dataRegex),

    REQUEST_UPDATE_ACTUATOR_STATE(
        "request update_actuator_state %d %s", 
        "request update_actuator_state " + 
            ParseConstants.nodeIDRegex + " " + 
            ParseConstants.dataRegex),

    // Greenhouse listens:
    REQUEST_GET_SENSOR_LIST(
        "request get_sensor_list %d", 
        "request get_sensor_list " + ParseConstants.nodeIDRegex),

    REQUEST_GET_SENSOR_DATA(
        "request get_sensor_list %d %d", 
        "request get_sensor_list " + 
            ParseConstants.nodeIDRegex + " " + 
            ParseConstants.sensorIDRegex),

    REQUEST_GET_ACTUATOR_STATE(
        "request get_actuator_state %d %d", 
        "request get_actuator_state" + 
            ParseConstants.nodeIDRegex + " " + 
            ParseConstants.actuatorIDRegex + " " + 
            ParseConstants.actuatorDataRegex),

    REQUEST_SET_ACTUATOR_STATE(
        "request set_actuator_state %d %d %s", 
        "request set_actuator_state " + ParseConstants.nodeIDRegex + " " + ParseConstants.actuatorIDRegex + " " + ParseConstants.dataRegex),

    // Responses
    RESPONSE_YES_NO(
        "response [LGTM|NOPE]",
        "response " + ParseConstants.responseDataRegex
    )
    ;
    
    private final String sendFormat;
    private final Pattern receivePattern;


    MessageSendReceiveFormatPattern(String sendFormat, String receivePattern) {
        this.sendFormat = sendFormat;
        this.receivePattern = Pattern.compile(receivePattern);
    }

    public String getSendFormat() {
        return this.sendFormat;
    }

    public Pattern getReceivePattern() {
        return receivePattern;
    }
}

