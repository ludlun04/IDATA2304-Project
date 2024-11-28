package no.ntnu.controlpanel.networking.Commands.logic;

import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.controlpanel.networking.Commands.Command;

/**
 * Abstract {@link Command} class that has a {@link ControlPanelLogic}
 */
public abstract class LogicCommand extends Command {
    protected ControlPanelLogic logic;

    /**
     * Constructs a {@link LogicCommand} object
     *
     * @param logic existing {@link ControlPanelLogic} class to store internally
     */
    public LogicCommand(ControlPanelLogic logic) {
        this.logic = logic;
    }

}
