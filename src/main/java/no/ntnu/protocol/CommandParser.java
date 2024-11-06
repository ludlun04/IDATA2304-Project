package no.ntnu.protocol;

import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.reflect.Field;

public class CommandParser {
    

    private ArrayList<Pattern> patterns = null;

    // public CommandParser() {
    //     this.patterns = Arrays.asList(this
    //         .getClass()
    //         .getDeclaredFields())
    //         .stream()
    //         .filter(f -> f.getType().getName() == "Pattern")
    //         .map(f -> f.get(this));

    //     }
        
    public Command parse(String request) {
        //Matcher matcher = pattern.matcher(inputString);

        // if (matcher.matches()) {
        //     String latitude = matcher.group("latitude");
        //     String longitude = matcher.group("longitude");
        //     String imei = matcher.group("imei");
            // Use the extracted variables as needed
        //}
    }


}

// Communist Climate-Change Protocol (CCCP)
//
//     A Two-Way Request-Response oriented protocol (endorsed by MDG/Rødt).
//     There are only two concepts that make up this world: 
//
//         The Greenhouse (effect)
//         The Control-Panel
//
//     REQUEST FORMATS:
//     request <command> <args> (key, value, key, value, ... key, value)
//     response [LGTM|NOPE]
//
//     CONTROL-PANEL REQUEST-LISTENER EXAMPLES:
//     In these examples the greenhouse "asks" the control-panel to do something.
//     The control-panel code can then decide for itself whether to accept it or not.
//
//     request add_node <nodeId> (actuator_id, actuator_type, ...)
//     request remove_node <nodeId>
//     request update_sensor_data <node_id> (sensor_id, sensor_data ...)
//     request update_actuator_state <node_id> (actuator_id, actuator_state ...)
//     response [LGTM|NOPE]

//     GREENHOUSE REQUEST-LISTENER EXAMPLES:
//     In these examples the control-panel "asks" the greenhouse to do something.
//     The greenhouse code can then decide for itself whether to accept it or not.
//
//     request request_id get_sensor_list <node_id>
//     request request_id get_sensor_data <node_id> <sensor_id>
//     request request_id get_actuator_state <node_id> <actuator_id>
//     request request_id set_actuator_state <node_id> <actuator_id> actuator_state

// 
//     (information understood in context of the request)