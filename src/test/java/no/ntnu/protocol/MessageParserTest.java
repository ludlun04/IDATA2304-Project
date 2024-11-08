package no.ntnu.protocol;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import no.ntnu.protocol.MessageSendReceiveFormatPattern;

public class MessageParserTest {
    @Test
    public void testAddNodeParse() {
        MessageSendReceiveFormatPattern addNode = MessageSendReceiveFormatPattern.valueOf("REQUEST_ADD_NODE");
        int sendNodeID = 1;
        String sendData = "1, window; 2, fan; 3, heater;";
        String message = String.format(addNode.getSendFormat(), sendNodeID, sendData);
        Pattern pattern = addNode .getReceivePattern();
        Matcher matcher = pattern.matcher(message);

        int receiveNodeID = 0;
        String receiveData = ""; 

        if (matcher.matches()) {
            receiveNodeID = Integer.parseInt(matcher.group("nodeId"));
            receiveData   = matcher.group("data");
        }

        //String[] matches = Pattern.compile("your regex here")
        //                  .matcher("string to search from here")
        //                  .results()
        //                  .map(MatchResult::group)
        //                  .toArray(String[]::new);

        System.out.println(pattern.toString());
        System.out.println("format nodeID: " + sendNodeID);
        System.out.println("parse nodeID: " + receiveNodeID);
        System.out.println("format data: " + sendData);
        System.out.println("parse data: " + receiveData);
        assertEquals(sendNodeID, receiveNodeID);
        assertTrue(sendData.equals(receiveData));
    }
}
