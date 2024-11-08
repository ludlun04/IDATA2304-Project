//package no.ntnu.protocol;
//
//import java.util.regex.Pattern;
//
//public enum MessageSendReceiveFormatPattern {
//
//    // Control-Panel listens:
//    REQUEST_ADD_NODE(
//        "request add_node %d %s",
//        "request add_node (?<node_id>[1-9+]*) (?<data>\b*$)"),
//
//    REQUEST_REMOVE_NODE(
//        "request remove_node %d",
//        "request remove_node (?<node_id>[1-9+]*)"),
//
//    REQUEST_UPDATE_SENSOR_DATA(
//        "request update_sensor_data %d %s",
//        "request update_actuator_state (?<node_id>[1-9+]*) (?<data>\b*$)"),
//
//    REQUEST_UPDATE_ACTUATOR_STATE(
//        "request update_actuator_state %d %s",
//        "request update_actuator_state (?<node_id>[1-9+]*) (?<data>\b*$)"),
//
//    // Greenhouse listens:
//    REQUEST_GET_SENSOR_LIST("request get_sensor_list %d", "request get_sensor_list (?<node_id>[1-9+]*)"),
//    REQUEST_GET_SENSOR_DATA("request get_sensor_list %d %d", "request get_sensor_list (?<node_id>[1-9+]*) (?<node_id>[1-9+]*)"),
//    REQUEST_GET_ACTUATOR_STATE( <node_id> <actuator_id>
//    REQUEST_SET_ACTUATOR_STATE() <node_id> <actuator_id> actuator_state
//
//    RESPONSE_YES_NO(
//        "response [LGTM|NOPE]",
//        "response (?<response>\b*$)"
//    )
//    ;
//
//    private final String sendFormat;
//    private final Pattern receivePattern;
//
//    RequestSendReceiveFormatPattern(String sendFormat, String receivePattern) {
//        this.sendFormat = sendFormat;
//        this.receivePattern = Pattern.compile(receivePattern);
//    }
//
//    public String getSendFormat() {
//        return this.sendFormat;
//    }
//
//    public Pattern getReceivePattern() {
//        return receivePattern;
//    }
//}
//
