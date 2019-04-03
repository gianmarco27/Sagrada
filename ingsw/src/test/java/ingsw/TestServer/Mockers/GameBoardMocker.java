package ingsw.TestServer.Mockers;

import ingsw.Server.Dice.Dice;
import ingsw.Server.Dice.DiceBag;
import ingsw.Server.GameBoard;
import ingsw.Server.GameFlow.GameType;
import ingsw.Server.Grid.Grid;
import ingsw.Server.Grid.GridLoader;
import ingsw.Server.Grid.InvalidJsonException;
import ingsw.Server.Objective.*;
import ingsw.Server.Player;
import ingsw.Server.Tools.*;
import ingsw.Server.Utility.RoundTracker;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Arrays;
import java.util.List;

import static ingsw.Server.Dice.DiceColor.*;
import static ingsw.Server.GameFlow.GameFlowState.*;

public class GameBoardMocker {

    public GameBoard getGameBoard(int state, int players) {
        if (state == 0) {
            return gameFinished(players);
        }
        if (state == 1) {
            return gameInit(players);
        }
        if (state == 2) {
            return midGame(players);
        }
        if (state == 3) {
            return midGame2(players);
        }
        if (state == 4) {
            return gameInitSingle();
        }
        if (state == 5) {
            return gameFinishedSingle();
        } else return null;
    }

    private GameBoard gameFinished(int players) {

        GameBoard gbMock = baseGameboard(players);
        MockupGrid gridMocker = new MockupGrid();
        RoundTracker rtMock = new RoundTracker();

        List <PublicObjective> pubObj = new CopyOnWriteArrayList<>();
        List <Tool> toolList = new CopyOnWriteArrayList<>();
        List <PrivateObjective> privObj = Arrays.asList(new ShadesOfBlue(), new ShadesOfGreen(), new ShadesOfRed(), new ShadesOfYellow());
        List <Grid> grids = Arrays.asList(gridMocker.getGrid(1), gridMocker.getGrid(2), gridMocker.getGrid(3), gridMocker.getGrid(5));

        pubObj.add(new LightShades());          //8     4   4   2
        pubObj.add(new ColumnShadeVariety());   //20    16  4   8
        pubObj.add(new ColorVariety());         //16    8   8   4
        gbMock.setPublicObjectives(pubObj);

        // Setting Private Objectives  Grids and Tokens for Players
        playerSetup(grids, privObj, gbMock);

        toolList.add(new LensCutter());
        toolList.add(new GlazingHammer());
        toolList.add(new EglomiseBrush());
        gbMock.setTools(toolList);

        rtMock.placeDice(0, new Dice(1, RED));
        rtMock.placeDice(1, new Dice(2, BLUE));
        rtMock.placeDice(2, new Dice(3, YELLOW));
        rtMock.placeDice(3, new Dice(4, PURPLE));
        rtMock.placeDice(4, new Dice(5, GREEN));
        rtMock.placeDice(5, new Dice(6, RED));
        rtMock.placeDice(6, new Dice(1, BLUE));
        rtMock.placeDice(7, new Dice(2, YELLOW));
        rtMock.placeDice(8, new Dice(3, PURPLE));
        rtMock.placeDice(9, new Dice(4, GREEN));
        gbMock.setRoundTracker(rtMock);

        gbMock.setCurrentState(GAMEFINISH);
        for (int i = 0; i < 10; i++) {
            gbMock.nextTurn();
        }
        return gbMock;
    }

    private GameBoard gameFinishedSingle() {

        GameBoard gbMock = baseGameboard(1);
        MockupGrid gridMocker = new MockupGrid();
        RoundTracker rtMock = new RoundTracker();

        List <PublicObjective> pubObj = new CopyOnWriteArrayList<>();
        List <Tool> toolList = new CopyOnWriteArrayList<>();
        List <PrivateObjective> privObj = Arrays.asList(new ShadesOfBlue());
        List <Grid> grids = Arrays.asList(gridMocker.getGrid(1));

        pubObj.add(new LightShades());          //8
        pubObj.add(new ColumnShadeVariety());   //20
        gbMock.setPublicObjectives(pubObj);

        // Setting Private Objectives  Grids and Tokens for Players
        playerSetup(grids, privObj, gbMock);

        Player p = gbMock.getPlayers().get(0);
        gbMock.getAllPrivateObjective(p).add(new ShadesOfGreen());

        toolList.add(new LensCutter(GameType.SINGLEPLAYER));
        toolList.add(new GlazingHammer(GameType.SINGLEPLAYER));
        toolList.add(new EglomiseBrush(GameType.SINGLEPLAYER));
        gbMock.setTools(toolList);

        rtMock.placeDice(0, new Dice(1, RED));
        rtMock.placeDice(1, new Dice(2, BLUE));
        rtMock.placeDice(2, new Dice(3, YELLOW));
        rtMock.placeDice(3, new Dice(4, PURPLE));
        rtMock.placeDice(4, new Dice(5, GREEN));
        rtMock.placeDice(6, new Dice(6, RED));
        rtMock.placeDice(6, new Dice(1, BLUE));
        rtMock.placeDice(7, new Dice(2, YELLOW));
        rtMock.placeDice(8, new Dice(3, PURPLE));
        rtMock.placeDice(9, new Dice(4, GREEN));
        gbMock.setRoundTracker(rtMock); // 25

        gbMock.setCurrentState(GAMEFINISH);
        for (int i = 0; i < 10; i++) {
            gbMock.nextTurn();
        }
        return gbMock;
    }

    private GameBoard gameInitSingle() {

        GameBoard gbMock = baseGameboard(1);
        MockupGrid gridMocker = new MockupGrid();
        RoundTracker rtMock = new RoundTracker();

        List <PublicObjective> pubObj = new CopyOnWriteArrayList<>();
        List <Tool> toolList = new CopyOnWriteArrayList<>();
        List <PrivateObjective> privObj = Arrays.asList(new ShadesOfBlue());
        List <Grid> grids = Arrays.asList(gridMocker.getGrid(0));

        pubObj.add(new LightShades());          //0
        pubObj.add(new ColumnShadeVariety());   //0
        gbMock.setPublicObjectives(pubObj);

        // Setting Private Objectives  Grids and Tokens for Players
        playerSetup(grids, privObj, gbMock);

        Player p = gbMock.getPlayers().get(0);
        gbMock.getAllPrivateObjective(p).add(new ShadesOfGreen());
        toolList.add(new LensCutter(GameType.SINGLEPLAYER));
        toolList.add(new GlazingHammer(GameType.SINGLEPLAYER));
        toolList.add(new EglomiseBrush(GameType.SINGLEPLAYER));
        gbMock.setTools(toolList);

        gbMock.setRoundTracker(rtMock);

        return gbMock;
    }

    private GameBoard gameInit(int players) {

        GameBoard gbMock = baseGameboard(players);
        MockupGrid gridMocker = new MockupGrid();
        RoundTracker rtMock = new RoundTracker();

        List <Tool> toolList = new CopyOnWriteArrayList<>();
        List <PublicObjective> pubObj = new CopyOnWriteArrayList<>();
        List <PrivateObjective> privObj = Arrays.asList(new ShadesOfBlue(), new ShadesOfGreen(), new ShadesOfRed(), new ShadesOfYellow());
        List <Grid> grids = Arrays.asList(gridMocker.getGrid(0), gridMocker.getGrid(0), gridMocker.getGrid(0), gridMocker.getGrid(0));

        // Setting Public Objectives
        pubObj.add(new RowColorVariety());
        pubObj.add(new ColumnColorVariety());
        pubObj.add(new ShadeVariety());
        gbMock.setPublicObjectives(pubObj);

        // Setting Tools
        toolList.add(new CopperFoilBurnishes());
        toolList.add(new EglomiseBrush());
        toolList.add(new CorkBackedStraightedge());
        gbMock.setTools(toolList);
        
        
        // Setting Private Objectives  Grids and Tokens for Players
        playerSetup(grids, privObj, gbMock);

        // Setting Roundtracker
        gbMock.setRoundTracker(rtMock);
        //Setting Current State
        gbMock.setCurrentState(PLAYERTURNBEGIN);
        return gbMock;
    }

    private GameBoard midGame(int players) {

        GameBoard gbMock = baseGameboard(players);
        MockupGrid gridMocker = new MockupGrid();
        RoundTracker rtMock = new RoundTracker();

        List <Tool> toolList = new CopyOnWriteArrayList<>();
        List <PublicObjective> pubObj = new CopyOnWriteArrayList<>();
        List <PrivateObjective> privObj = Arrays.asList(new ShadesOfBlue(), new ShadesOfGreen(), new ShadesOfRed(), new ShadesOfYellow());
        List <Grid> grids = Arrays.asList(gridMocker.getGrid(1), gridMocker.getGrid(4), gridMocker.getGrid(3), gridMocker.getGrid(5));

        pubObj.add(new LightShades());
        pubObj.add(new ColumnShadeVariety());
        pubObj.add(new ColorVariety());
        gbMock.setPublicObjectives(pubObj);

        toolList.add(new RunningPliers());
        toolList.add(new Lathekin());
        toolList.add(new TapWheel());
        gbMock.setTools(toolList);

        //setting up player specific obj and grids with token
        playerSetup(grids, privObj, gbMock);

        rtMock.placeDice(0, new Dice(1, RED));
        rtMock.placeDice(1, new Dice(2, BLUE));
        rtMock.placeDice(2, new Dice(3, YELLOW));
        rtMock.placeDice(3, new Dice(4, PURPLE));
        rtMock.placeDice(4, new Dice(5, GREEN));
        rtMock.placeDice(5, new Dice(6, RED));
        gbMock.setRoundTracker(rtMock);

        gbMock.setCurrentState(PLAYERTURNEND);
        for (int i = 0; i < 6; i++) {
            gbMock.nextTurn();
        }
        return gbMock;
    }

    private GameBoard midGame2(int players) {
        GameBoard gbMock = baseGameboard(players);
        RoundTracker rtMock = new RoundTracker();

        List <Tool> toolList = new CopyOnWriteArrayList<>();
        List <PublicObjective> pubObj = new CopyOnWriteArrayList<>();
        List <PrivateObjective> privObj = Arrays.asList(new ShadesOfBlue(), new ShadesOfGreen(), new ShadesOfRed(), new ShadesOfYellow());
        int objIndex= 0;
        pubObj.add(new LightShades());
        pubObj.add(new ColumnShadeVariety());
        pubObj.add(new ColorVariety());
        gbMock.setPublicObjectives(pubObj);

        toolList.add(new RunningPliers());
        toolList.add(new Lathekin());
        toolList.add(new TapWheel());
        gbMock.setRoundTracker(rtMock);
        gbMock.setTools(toolList);

        for (Player player : gbMock.getPlayers()) {
            try {
                player.setGrid(GridLoader.loadFromFile("./resources/grids/10 - Industria.json"));
            } catch (IOException | InvalidJsonException e) {
                e.printStackTrace();
            }
            player.setToken(player.getGrid().getFavorPoints());
            gbMock.setPrivateObjective(player, privObj.get(objIndex));
            objIndex++;
        }
        return gbMock;
    }

    private GameBoard baseGameboard(int players) {
        GameBoard gbMock = new GameBoard();
        List<String> PlayerNames = Arrays.asList("Federico", "Giovanni", "Gianmarco", "Marco");
        for (int i = 0; i < players; i++) {
            gbMock.addPlayer(new Player(PlayerNames.get(i)));
        }
        gbMock.setPlayers();
        gbMock.setDiceBag(new DiceBag());
        gbMock.setGameType((players == 1) ? GameType.SINGLEPLAYER : GameType.MULTIPLAYER);
        return  gbMock;
    }

    private void playerSetup(List<Grid> grids, List<PrivateObjective> privObj, GameBoard gbMock) {
        int gridIdx = 0;
        int objIndex = 0;
        for (Player player : gbMock.getPlayers()) {
            player.setGrid(grids.get(gridIdx));
            player.setToken(player.getGrid().getFavorPoints());
            gbMock.setPrivateObjective(player, privObj.get(objIndex));
            //setting possible grids
            player.setPossibleGrids(((Grid[]) grids.toArray()));
            
            objIndex++;
            gridIdx++;
        }
    }
}
