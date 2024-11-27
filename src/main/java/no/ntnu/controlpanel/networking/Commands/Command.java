package no.ntnu.controlpanel.networking.Commands;

import no.ntnu.controlpanel.ControlPanelLogic;

public abstract class Command {
    protected ControlPanelLogic logic;

    public Command(ControlPanelLogic logic) {
        this.logic = logic;
    }

    public abstract void execute();
}
