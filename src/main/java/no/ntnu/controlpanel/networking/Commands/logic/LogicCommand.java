package no.ntnu.controlpanel.networking.Commands.logic;

import no.ntnu.controlpanel.ControlPanelLogic;

public abstract class LogicCommand extends no.ntnu.controlpanel.networking.Commands.Command {
    protected ControlPanelLogic logic;

    public LogicCommand(ControlPanelLogic logic) {
        this.logic = logic;
    }


}
