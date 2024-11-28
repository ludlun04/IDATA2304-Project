package no.ntnu.controlpanel.networking.commands;

import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.utils.commands.Command;

public abstract class LogicCommand extends Command {
  protected ControlPanelLogic logic;

  public LogicCommand(ControlPanelLogic logic) {
    this.logic = logic;
  }


}
