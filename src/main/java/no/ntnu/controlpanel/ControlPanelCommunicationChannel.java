package no.ntnu.controlpanel;

public class ControlPanelCommunicationChannel implements CommunicationChannel {
    private ControlPanelLogic logic;

    public ControlPanelCommunicationChannel(ControlPanelLogic logic) {
        if (logic == null) {
            throw new IllegalArgumentException("logic cannot be null");
        }

        this.logic = logic;
    }

    @Override
    public void sendActuatorChange(int nodeId, int actuatorId, boolean isOn) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendActuatorChange'");
    }

    @Override
    public boolean open() {
        


        

        return true;
    }
    
}
