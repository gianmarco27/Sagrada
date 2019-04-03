package ingsw;

import ingsw.Client.View.CliView;
import ingsw.Client.View.GuiViewWrapper;
import ingsw.Client.View.HandleChoices;
import ingsw.Client.View.View;
import ingsw.Server.Utility.LoggerUtils;

public class ClientMain {
    public static void main(String[] args) throws Exception {
        LoggerUtils.initLoggerSettings();
        boolean running = true;
        System.out.println("Do you wish to use (1) CLI or (2) GUI");
        int choice = HandleChoices.handleChoices(1, 2);
        View v;
        if (choice == 1) {
            v = new CliView();
        } else {
            v = new GuiViewWrapper();
        }
        v.initConnection();
        while(running){
            v.run();
        }
    }
}
