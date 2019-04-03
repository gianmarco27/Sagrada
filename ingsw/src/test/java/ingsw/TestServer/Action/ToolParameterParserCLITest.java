package ingsw.TestServer.Action;

import ingsw.Client.View.ToolParameterParserCLI;
import org.junit.Assert;
import org.junit.Test;
import ingsw.Server.Actions.*;

import java.lang.String;

public class ToolParameterParserCLITest {

    @Test
    public void testInvalidInputParser() {
        ToolParameterParserCLI testParser = ToolParameterParserCLI.getInstance();
        String testBrokenInput1 = "[3][2]";
        Assert.assertEquals(false,testParser.toolFromString(testBrokenInput1));
        String testBrokenInput2 = "[3] [";
        Assert.assertEquals(false, testParser.toolFromString(testBrokenInput2));
        String testBrokenInput3 = "[3-4][3]";
        Assert.assertEquals(false, testParser.toolFromString(testBrokenInput3));

    }

    @Test
    public void testPlacing() {
        ToolParameterParserCLI testParser = ToolParameterParserCLI.getInstance();
        ToolActionParameter ref;
        String testDiceInput1 = "[3-4] [2-3]";
        Assert.assertEquals(true,testParser.toolFromString(testDiceInput1));
        ref = testParser.createCommand();
        Assert.assertEquals("[3-4, 2-3]",ref.getCoords().toString());
        String testDiceInput2 = "[0-0] [5-5]";
        Assert.assertEquals(true, testParser.toolFromString(testDiceInput2));
        ref = testParser.createCommand();
        Assert.assertEquals("[0-0, 5-5]",ref.getCoords().toString());
        String testDiceInput3 = "[0-0] [5-5] [3-4] [2-3]";
        Assert.assertEquals(true, testParser.toolFromString(testDiceInput3));
        ref = testParser.createCommand();
        Assert.assertEquals("[0-0, 5-5, 3-4, 2-3]",ref.getCoords().toString());
        Assert.assertEquals(-1,ref.getPickedTool());
        String toolString3 = "[3-5] T-3";
        Assert.assertEquals(true, testParser.toolFromString(toolString3));
        ref = testParser.createCommand();
        Assert.assertEquals("[3-5]",ref.getCoords().toString());
        Assert.assertEquals(3,ref.getPickedTool());

    }

    @Test
    public void testToolPicking() {
        ToolParameterParserCLI testParser =  ToolParameterParserCLI.getInstance();
        ToolActionParameter ref;
        String toolString = "T-2";
        Assert.assertEquals(true,testParser.toolFromString(toolString));
        ref = testParser.createCommand();
        Assert.assertEquals(2,ref.getPickedTool());
        String toolString2 = "T-5 T-3";
        Assert.assertEquals(true,testParser.toolFromString(toolString2));
        ref = testParser.createCommand();
        Assert.assertEquals(3,ref.getPickedTool());
        String toolString3 = "  T-3  ";
        Assert.assertEquals(true,testParser.toolFromString(toolString3));
        ref = testParser.createCommand();
        Assert.assertEquals(3,ref.getPickedTool());

    }

    @Test
    public void testPoolPicking( ){
        ToolParameterParserCLI testParser = ToolParameterParserCLI.getInstance();
        ToolActionParameter ref;
        String toolString = "P-2";
        Assert.assertEquals(true,testParser.toolFromString(toolString));
        ref = testParser.createCommand();
        Assert.assertEquals(2,ref.getDicePoolTarget());
        Assert.assertEquals(true,ref.isWorkOnDicePool());
        String toolString2 = "P-5 P-3";
        Assert.assertEquals(true,testParser.toolFromString(toolString2));
        ref = testParser.createCommand();
        Assert.assertEquals(3,ref.getDicePoolTarget());
        Assert.assertEquals(true,ref.isWorkOnDicePool());
        Assert.assertEquals(false,ref.isWorkOnRoundTracker());
    }


    @Test
    public void testRTPicking() {
        ToolParameterParserCLI testParser = ToolParameterParserCLI.getInstance();
        ToolActionParameter ref;
        String toolString = "R-2-0";
        Assert.assertEquals(true,testParser.toolFromString(toolString));
        ref = testParser.createCommand();
        Assert.assertEquals(2,ref.getRoundTrackerTargetRound());
        Assert.assertEquals(0,ref.getRoundTrackerTargetOption());
        Assert.assertEquals(true,ref.isWorkOnRoundTracker());
        String toolString2 = "R-5-2 R-3-9";
        Assert.assertEquals(true,testParser.toolFromString(toolString2));
        ref = testParser.createCommand();
        Assert.assertEquals(3,ref.getRoundTrackerTargetRound());
        Assert.assertEquals(9,ref.getRoundTrackerTargetOption());
        Assert.assertEquals(true,ref.isWorkOnRoundTracker());
        Assert.assertEquals(false,ref.isWorkOnDicePool());

    }
    @Test
    public void testSequence() {
        ToolParameterParserCLI testParser =ToolParameterParserCLI.getInstance();
        ToolActionParameter refOld;
        ToolActionParameter ref;
        String toolString = "[3-5] T-3";
        Assert.assertEquals(true, testParser.toolFromString(toolString));
        refOld = testParser.createCommand();
        String toolString2 = "[0-0] P-1";
        Assert.assertEquals(true,testParser.toolFromString(toolString2));
        ref = testParser.createCommand();
        Assert.assertEquals("[3-5]",refOld.getCoords().toString());
        Assert.assertEquals(3,refOld.getPickedTool());
        Assert.assertEquals(false,refOld.isWorkOnDicePool());
        Assert.assertEquals("[0-0]",ref.getCoords().toString());
        Assert.assertEquals(-1,ref.getPickedTool());
        Assert.assertEquals(true,ref.isWorkOnDicePool());
        Assert.assertEquals(1,ref.getDicePoolTarget());
    }

    @Test
    public void testSinglePlayerToolPayment() {
        ToolParameterParserCLI testParser = ToolParameterParserCLI.getInstance();
        ToolActionParameter ref;
        String toolString = "T-3 D-0";
        Assert.assertEquals(true, testParser.toolFromString(toolString));
        ref = testParser.createCommand();
        Assert.assertEquals(3,ref.getPickedTool());
        Assert.assertEquals(0,ref.getPayDicePool());

        String DiceString = "D-9";
        Assert.assertEquals(true, testParser.toolFromString(DiceString));
        ref = testParser.createCommand();
        Assert.assertEquals(-1,ref.getPickedTool());
        Assert.assertEquals(9,ref.getPayDicePool());

    }
}
