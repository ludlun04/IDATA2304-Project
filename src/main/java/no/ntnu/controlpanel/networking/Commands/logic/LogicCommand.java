package no.ntnu.controlpanel.networking.Commands.logic;

import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.controlpanel.networking.Commands.Command;

public abstract class LogicCommand extends Command {
    protected ControlPanelLogic logic;

    public LogicCommand(ControlPanelLogic logic) {
        this.logic = logic;
    }


}
