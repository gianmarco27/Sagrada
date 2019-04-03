package ingsw.Client.View;


import ingsw.Server.Actions.DiceActionParameter;
import ingsw.Server.Dice.DicePool;
import ingsw.Server.GameFlow.GameFlowState;
import ingsw.Server.Grid.Grid;
import ingsw.Server.Objective.PrivateObjective;
import ingsw.Server.Objective.PublicObjective;
import ingsw.Server.Player;
import ingsw.Server.Tools.Tool;
import ingsw.Server.Utility.GameException;
import ingsw.Settings;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

import static ingsw.Client.View.HandleChoices.handleChoices;
import static ingsw.Server.Utility.GameException.genericError;
import static java.lang.Math.max;

/**
 * This class is responsible for all the printed text UI and its view
 */
public class CliView extends View {

    private static final Logger log = Logger.getLogger( CliView.class.getName() );

    private boolean COLORED = Settings.COLORED_OUTPUT;

    private static final String SPLITTING_SEQUENCE = "\\r?\\n";

    public CliView() throws RemoteException {
        super();
        this.c = null;
        conn = new GameConnection(this);
    }


    /**
     * this method is responsible for printing the tools available for use in the game
     */
    private synchronized void printAllTools() {
        List<String[] > allTools = new ArrayList<>();
        for (Tool t : gameBoard.getTools()) {
            allTools.add((t.toString() + "\n").split(SPLITTING_SEQUENCE));
        }
        if (allTools.isEmpty()) {
            return;
        } else {
            int nLine = 0;
            for (String[] t : allTools) {
                nLine = max(nLine, t.length);
            }
            for (int line = 0; line <= nLine; line++) {
                for (String[] t : allTools) {
                    if (line < t.length) {
                        // If we have yet to print some line we print it normally
                        System.out.print(t[line] + "    ");
                    } else {
                        // Otherwise we allocate space to make the indentation consistent
                        System.out.print(new String(new char[t[0].length() - 1]).replace("\0", " ") + "     ");
                    }
                }
                System.out.print("\n");
            }
            System.out.println("\n");
        }
    }

    /**
     * this function is used to print the Grids in the CLI
     */
    private synchronized void printGrids(List<String[]> grids) {
        int nLine = grids.get(0).length;
        int maxSize = (grids.get(0))[0].length(); // We assume the first line is the largest one
        for (int line = 0; line < nLine; line++) {
            for (String[] s : grids) {
                String toPrint = s[line];
                System.out.print(s[line]);
                if (toPrint.length() < maxSize) { // If a line is less then the max length we pad it accordingly
                    System.out.print(new String(new char[maxSize - toPrint.length()]).replace("\0", " "));
                }
                System.out.print("\t");
            }
            System.out.print("\n");
        }
        System.out.println("\n");
    }

    /**
     * this function is used to print the other opponents grids during the game
     */
    private synchronized void printOthersGrids() throws IOException {
        List<String[]> otherGrid = new ArrayList<>();
        Map<Player,Grid> grids = c.getOthersGrids();
        if (grids == null) {
            return;
        }
        for (Player p : grids.keySet()) {
            otherGrid.add((grids.get(p).toString(COLORED) + "\n" + p).split(SPLITTING_SEQUENCE));
        }
        if (otherGrid.isEmpty()) {
            return;
        } else {
            printGrids(otherGrid);
        }
    }

    /**
     * this method is used to print the choice of the grids
     */
    private synchronized void printChoiceGrid(Grid[] toDisplay) {
        for (int row = 0; row < 4; row += 2) {
            List<String[] > otherGrid = new ArrayList<>();
            for (int idx = 0; idx < 2; idx++) {
                otherGrid.add(((toDisplay[idx + row].toString(COLORED)) + "\n" + "N° " + (idx + row + 1)).split(SPLITTING_SEQUENCE));
            }
            printGrids(otherGrid);
        }
    }

    /**
     * this method is used to ask the user for his ingame name
     */
    public synchronized String choosePlayerName() {
        Scanner scanner = new Scanner(System.in);
            String name = null;
            while (name == null || name.length() < 1) {
                System.out.println("Please pick a name\n");
                name = scanner.nextLine();
            }
            return name;
    }


    /**
     * this method prints the welcome prompt at the game start
     */
    private void welcomePrompt() {
        System.out.println("Hello and welcome to a new game of.....");
        System.out.println("\n" +
                "   _______  ________  ___   ___  ___ \n" +
                "  / __/ _ |/ ___/ _ \\/ _ | / _ \\/ _ |\n" +
                " _\\ \\/ __ / (_ / , _/ __ |/ // / __ |\n" +
                "/___/_/ |_\\___/_/|_/_/ |_/____/_/ |_|\n" +
                "                                     \n");
    }

    /**
     * this method calls the connection for the user to the server with his nickname
     */
    public void connect(){
        boolean loop = true;
        while (loop) {
            String name = choosePlayerName();
            try {
                conn.connect(name);

                if (this.c == null) {
                    log.warning("Couldn't connect!");
                }
                loop = false;
            } catch (GameException e) {
                System.out.println(e.getMessage());
                conn.setConnected(false);
            } catch (IOException | NotBoundException | ClassNotFoundException e) {
                conn.setConnected(false);
                loop = false;
            }
        }
    }

    /**
     * this function is the base core driving the user through the main phases of the game
     */
    public void run() {
        welcomePrompt();
        connect();
        while (conn.isConnected()) {
            update();
        }
        if (gameBoard == null || gameBoard.getCurrentState() == GameFlowState.GAMEFINISH) {
            System.out.println("You have lost connection to the game server, please check your connection, than reconnect.");
        }
    }

    @Override
    protected void showDisconnectionNotification(String info) {
        System.out.println(info);
    }

    @Override
    protected void showTimeoutNotification(String info) {
        System.out.println(info);
    }

    @Override
    protected void gameFinish() {
        showGameFinish();
    }

    @Override
    protected void playerTurnEnd() {
        turnEnd();
    }

    @Override
    protected void toolAction() throws IOException {
        playTool();
    }

    @Override
    protected void diceAction() throws IOException {
        playDice();
    }


    /**
     * this method is used to prompt the player for each turn End with the grid of the player who ended the turn
     */
    private void turnEnd() {
        System.out.println("End of " + gameBoard.getCurrentPlayer().getName() + "'s turn.");
        System.out.println("This is his grid :");
        System.out.println(gameBoard.getCurrentPlayer().getGrid().toString(COLORED));
    }

    /**
     *This method is used at your very own turn begin to prompt you the available actions and the generic infos
     */
    private void turnBegin() throws GameException, IOException {
        if (gameBoard.getCurrentPlayer().equals(c.getPlayer())) {
            printOthersGrids();
            System.out.println("Current Round: N°" + gameBoard.getCurrentTurn() + " STEP: " + (gameBoard.getCurrentPlayer().turnPlayed + 1) + " TOKENS: " + gameBoard.getCurrentPlayer().getToken());
            System.out.println(gameBoard.getRoundTracker().toString(COLORED));
            System.out.println(c.getPlayer().getGrid().toString(COLORED));
            boolean toolUsed = (gameBoard.getCurrentPlayer().isToolPlayed());
            boolean diceUsed = (gameBoard.getCurrentPlayer().isDicePlayed());
            List<Integer> skipped = new ArrayList<>();
            System.out.println("This is your turn, what would you like to do?");
            System.out.println("0 - End your turn");
            if (diceUsed) {
                skipped.add(1);
            } else {
                System.out.println("1 - Pick die");
            }
            if (toolUsed) {
                skipped.add(2);
            } else {
                System.out.println("2 - Use tool");
            }
            int picked = handleChoices(0, 2, skipped);
            c.sendTurnBeginChoice(picked);
        } else {
            System.out.println("Start of the turn N° " + gameBoard.getCurrentTurn() + " for " + gameBoard.getCurrentPlayer().getName());
        }
    }

    /**
     * This method prompts the players with the Drafted Dice for the current turn
     */
    private void showTurnDice() {
        System.out.println("This turn dice are going to be .... :");
        System.out.println(gameBoard.getDicePool().toString(COLORED));
    }

    /**
     * This method is used to prompt every user with the final result sorted by winning order
     */
    private void showGameFinish() {
        System.out.println("\n The game is finished! Time to draw the score and the conclusions\n");
        System.out.println(gameBoard.getRoundTracker().toString(COLORED));
        String standing = gameBoard.getScoreBoardStanding();
        System.out.println(standing);
        System.out.println("\n\n GG WP, That's a wrap. Hope you enjoyed the game :) \n");

    }

    /**
     * This method is used to print the common objectives at the begining of the game
     */
    private void showCommonObjectives() {
        System.out.println("Now these are the common Objective, have great care in scoring as much as you can\n");
        System.out.println();
        List<PublicObjective> l = gameBoard.getPublicObjectives();
        for (PublicObjective p : l) {
            System.out.println(p);
        }
    }

    /**
     * this method prints the player order at the beginning of the game
     */
    private void displayOrder() {
        System.out.println("We are now all set to enjoy a nice game of S A G R A D A!");
        System.out.println("This is the player order!");
        for (Player p : gameBoard.getPlayersOrder()) {
            System.out.println(p.getName());
        }
    }

    /**
     * this method prints the tools that have been extracted at the beginning of the game
     */
    private void showTools() {
        System.out.println("Time to discover what tool ya bois gonna be playing with!\n");
        printAllTools();
    }

    /**
     * this method prompts each user with their very own private objective at the beginning of the game
     */
    private void showPrivateObjective() throws GameException, IOException {
        System.out.println("\n\n...GAME IS STARTING... \n\n");
        System.out.println("Let's get the game rolling. Here's the random selected private Objective\n");
        System.out.println("Roll the drums! YOU (" + c.getPlayer().getName() + ") GOT :\n");
        for (PrivateObjective po : c.getAllPrivateObjective()) {
            System.out.println(po);
        }
    }


    /**
     * this method allows the user to select his own grid at the beginning of the game and then prompts him with the grids selected by his opponents
     */
    private synchronized void chooseGrid() throws IOException {
        try {
            Grid[] possibleGrids = c.getPlayer().getPossibleGrids();
            if (possibleGrids == null) {
                return;
            }

            System.out.println("Now it's time for the artists to discover what piece of glass they are gonna to work with!\n");
            System.out.println(c.getPlayer().getName().toUpperCase() + " time to chose!");

            possibleGrids = c.getPlayer().getPossibleGrids();
            printChoiceGrid(possibleGrids);
            int picked = handleChoices(1, possibleGrids.length);
            while (!c.chooseGrid(possibleGrids[picked - 1])) {
                System.out.println("Error, try again");
                picked = handleChoices(1, possibleGrids.length);
            }
            System.out.println("Wise choice " + c.getPlayer().getName() + ", N° " + picked + " it is!");
            System.out.println(c.getPlayer().getGrid().toString(COLORED));
        } catch (GameException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * This method displays the current game Lobby while the players are waiting for the game to start
     */
    private synchronized void showLobby() {
        System.out.println("Current lobby : ");
        for(Player p : gameBoard.getPlayers()){
            System.out.println(p);
        }
    }

    /**
     * depending if it's your turn or not this method asks you which die you want to play and in which coordinates
     * or if someone else is picking a Die
     */
    private void playDice() throws GameException, IOException {
        Scanner scanner = new Scanner(System.in);
        if(!gameBoard.getCurrentPlayer().equals(c.getPlayer()))
        {
            System.out.println("Player " + gameBoard.getCurrentPlayer().getName().toUpperCase() + " is choosing a dice.");
            System.out.println("Please wait");
            return;
        }
        DiceActionParameter param = new DiceActionParameter();

        boolean done = false;
        do {
            System.out.println("So, " + gameBoard.getCurrentPlayer() + " what dice do you want to use?");

            DicePool dp = gameBoard.getDicePool();
            System.out.println(dp.toString(COLORED));
            int choice = handleChoices(0, dp.getAllDice().size()-1);
            param.chooseDie(choice);
            System.out.println("You picked \t" + dp.getAllDice().get(choice).toString(COLORED) + "\n");
            System.out.println("Where do you want to place it?\n");
            System.out.println(c.getPlayer().getGrid().toString(COLORED));
            System.out.println("Give me the X: ");
            int x = handleChoices(0, 4);
            System.out.println("Tell me the Y : ");
            int y = handleChoices(0, 3);
            param.choseTarget(x, y);
            String reason = null;
            System.out.println("You selected " + x + "-" + y + " type 'back' if you wish to go back to the turn option\n" +
                            "Anything else to proceed");
            String in = scanner.nextLine();
            if (in.equals("back")) {
                c.back();
                return;
            }
            try {
                done = c.chooseAndPlaceDie(param);
            } catch (GameException ge) {
                reason = ge.getMessage();
                done = false;
            }
            if (!done) {
                System.out.println("Error, invalid choice");
                if (reason != null) {
                    System.out.println(reason);
                }
                System.out.println("Type 'back' to go back to your turn menu. 'retry' to try again");
                String s = scanner.nextLine();
                if (s.equals("back")) {
                    c.back();
                    return;
                }
            }
        } while (!done);
    }

    /**
     * calls the print of the DicePool draft at the beginning of a round
     */
    @Override
    protected void playerTurnBegin() throws IOException {
        turnBegin();
    }

    /**
     * calls the print of the dicepool
     */
    @Override
    protected void turnHandler() {
        showTurnDice();
    }

    /**
     * calls the print of the extracted common objectives
     */
    @Override
    protected void commonObjSetup() {
        showCommonObjectives();
    }

    /**
     * calls the print of the Tools at their extraction
     */
    @Override
    protected void toolSetup() {
        showTools();
    }

    /**
     * calls the method that allows the player to choose the grid to use
     */
    @Override
    protected void gridSetup() throws IOException{
        chooseGrid();
    }

    /**
     * calls the functions to prompt the user with his own private objective and then the players order
     */
    @Override
    protected void privateObjSetup() throws IOException {
        showPrivateObjective();
        displayOrder();
    }

    @Override
    protected void setup() { return;}

    /**
     * during the phase of player registration displays them the current lobby
     */
    @Override
    protected void playerRegistration() {
        showLobby();
    }

    /**
     * this method is used to parse user input in the tool action section
     */
    private ToolParameterParserCLI parseActionCLI(boolean callBack) throws IOException {
        ToolParameterParserCLI parserTool = ToolParameterParserCLI.getInstance();
        boolean done = false;
        printAllTools();
        while (!done) { // Loop that repeat until we successfully parse something from the IO
            if (!callBack) {
                System.out.println(parserTool.getInstruction());
            }
            System.out.println("Or type 'back' to go back to choosing the action");
            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine();
            if (command.contains("back")) {
                c.back();
                return null;
            }
            done = parserTool.toolFromString(command);
            if (!done)
                System.out.println("Error, failed to parse, try again");
        }
        parserTool.setCallback(callBack);
        return parserTool;
    }

    /**
     * This method takes care of managing the use of the tool from CLI for the current player and displaying it to the other players
     */
    private void playTool() throws IOException {
        if (!gameBoard.getCurrentPlayer().equals(c.getPlayer())) {
            System.out.println("Player " + gameBoard.getCurrentPlayer() + " is using a tool.");
            System.out.println("Please wait");
            return;
        }
        boolean checkServer = false;
        System.out.println("TIME TO PLAY WITH THE TOOLS");

        while (!checkServer && !c.getPlayer().isToolPlayed()) { // Loop that repeat until we receive confirmation from the server
            ToolParameterParserCLI parserTool = parseActionCLI(false);
            if (parserTool == null) {
                return;
            }
            try {
                checkServer = c.useTap(parserTool.createCommand());
            } catch (GameException e1) {
                if (!e1.isCallBack()) { // If we are not in a callback situation simply print the error message
                    String reason = e1.getMessage();
                    // We check if it was a generic Error or a specific GameException
                    if (reason.equals(genericError)) {
                        System.out.println("Error, wrong parameters, impossible to use this tool that way");
                    } else {
                        System.out.println(reason);
                    }
                } else if (e1.isCallBack()) {
                    boolean resolvedCallBack = false;
                    while (!resolvedCallBack && !c.getPlayer().isToolPlayed()) {
                        String furtherInstruction = e1.getMessage();
                        System.out.println(furtherInstruction);
                        parserTool = parseActionCLI(true);
                        if (parserTool == null) {
                            return;
                        }
                        try {
                            checkServer = c.useTap(parserTool.createCommand());
                            resolvedCallBack = true;
                        } catch (GameException ex2) {
                            String reason = ex2.getMessage();
                            System.out.println(reason);
                        }
                    }
                }
            }
        }
    }

}
