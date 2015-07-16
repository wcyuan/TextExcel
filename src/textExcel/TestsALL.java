package textExcel;

//*******************************************************
//DO NOT MODIFY THIS FILE!!!
//*******************************************************

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    TestsALL.A_Checkpoint1.class,
    TestsALL.A_Checkpoint2.class,
    TestsALL.A_Checkpoint3.class,
    TestsALL.A_Final.class,
    TestsALL.B_Checkpoint1.class,
    TestsALL.B_Final.class,
    TestsALL.C_Checkpoint1.class,
    TestsALL.C_Final.class,
    TestsALL.ExtraCreditCommandErrors.class,
    TestsALL.ExtraCreditCommandHistory.class,
    TestsALL.ExtraCreditCircularReferenceErrors.class,
    TestsALL.ExtraCreditEvaluationErrors.class,
    TestsALL.ExtraCreditOperationOrder.class,
    TestsALL.C_ExtraCreditHeterogeneousSorting.class,
})

public class TestsALL
{
    public static class TestLocation implements Location
    {
        // Simple implementation of Location interface for use only by tests.
        private int row;
        private int col;

        public TestLocation(int row, int col)
        {
            this.row = row;
            this.col = col;
        }

        @Override
        public int getRow() {
            return row;
        }

        @Override
        public int getCol() {
            return col;
        }
    }

    public static class Helper
    {
        // For use only by test code, which uses it carefully.
        private String[][] items;

        public Helper()
        {
            items = new String[20][12];
            for (int i = 0; i < 20; i++)
                for (int j = 0; j < 12; j++)
                    items[i][j] = format("");
        }

        public static String format(String s)
        {
            return String.format(String.format("%%-%d.%ds", 10, 10),  s);
        }

        public void setItem(int row, int col, String text)
        {
            items[row][col] = format(text);
        }

        public String getText()
        {
            String ret = "   |";
            for (int j = 0; j < 12; j++)
                ret = ret + format(Character.toString((char)('A' + j))) + "|";
            ret = ret + "\n";
            for (int i = 0; i < 20; i++)
            {
                ret += String.format("%-3d|", i + 1);
                for (int j = 0; j < 12; j++)
                {
                    ret += items[i][j] + "|";
                }
                ret += "\n";
            }
            return ret;
        }
    }

    public static class A_Checkpoint1
    {
        // Tests for checkpoint 1.
        // Pass them all, plus ensure main loop until quit works, for full credit on checkpoint 1.
        // Note these must also pass for all subsequent checkpoints including final project.
        Grid grid;

        @Before
        public void initializeGrid()
        {
            grid = new Spreadsheet();
        }

        @Test
        public void testGetRows()
        {
            assertEquals("getRows", 20, grid.getRows());
        }

        @Test
        public void testGetCols()
        {
            assertEquals("getCols", 12, grid.getCols());
        }

        @Test
        public void testProcessCommand()
        {
            String str = grid.processCommand("");
            assertEquals("output from empty command", "", str);
        }

        @Test
        public void testLongShortStringCell()
        {
            SpreadsheetLocation loc = new SpreadsheetLocation("L20");
            assertEquals("SpreadsheetLocation column", loc.getCol(), 11);
            assertEquals("SpreadsheetLocation row", loc.getRow(), 19);

            loc = new SpreadsheetLocation("D5");
            assertEquals("SpreadsheetLocation column", loc.getCol(), 3);
            assertEquals("SpreadsheetLocation row", loc.getRow(), 4);

            loc = new SpreadsheetLocation("A1");
            assertEquals("SpreadsheetLocation column", loc.getCol(), 0);
            assertEquals("SpreadsheetLocation row", loc.getRow(), 0);
        }

        @Test
        public void testProcessCommandNonliteralEmpty()
        {
            String input = " ".trim();
            String output = grid.processCommand(input);
            assertEquals("output from empty command", "", output);
        }
    }

    public static class A_Checkpoint2
    {
        // Tests for checkpoint 2.
        // Note these must also pass for all subsequent checkpoints including final project.
        Grid grid;

        @Before
        public void initializeGrid()
        {
            grid = new Spreadsheet();
        }

        @Test
        public void testEmptyGridCells()
        {
            for (int i = 0; i < grid.getRows(); i++)
                for (int j = 0; j < grid.getCols(); j++)
                {
                    Cell cell = grid.getCell(new TestLocation(i, j));
                    assertEquals("empty cell text", Helper.format(""), cell.abbreviatedCellText());
                    assertEquals("empty inspection text", "", cell.fullCellText());
                }
        }

        @Test
        public void testEmptyGridText()
        {
            Helper helper = new Helper();
            assertEquals("empty grid", helper.getText(), grid.getGridText());
        }

        @Test
        public void testShortStringCell()
        {
            String hello = "Hello";
            grid.processCommand("A1 = \"" + hello + "\"");
            Cell helloCell = grid.getCell(new TestLocation(0,0));
            assertEquals("hello cell text", Helper.format(hello), helloCell.abbreviatedCellText());
            assertEquals("hello inspection text", "\"" + hello + "\"", helloCell.fullCellText());
        }

        @Test
        public void testLongShortStringCell()
        {
            String greeting = "Hello, world!";
            grid.processCommand("L20 = \"" + greeting + "\"");
            Cell greetingCell = grid.getCell(new TestLocation(19,11));
            assertEquals("greeting cell text", Helper.format(greeting), greetingCell.abbreviatedCellText());
            assertEquals("greeting inspection text", "\"" + greeting + "\"", greetingCell.fullCellText());
        }

        @Test
        public void testEmptyStringCell()
        {
            grid.processCommand("B2 = \"\"");
            Cell emptyStringCell = grid.getCell(new TestLocation(1,1));
            assertEquals("empty string cell text", Helper.format(""), emptyStringCell.abbreviatedCellText());
            assertEquals("empty string inspection text", "\"\"", emptyStringCell.fullCellText());
        }

        @Test
        public void testDifferentCellTypes()
        {
            grid.processCommand("C11 = \"hi\"");
            Cell stringCell = grid.getCell(new TestLocation(10, 2));
            Cell emptyCell = grid.getCell(new TestLocation(0,0));
            assertTrue("string cell implementation class must be different from empty cell",
                    !emptyCell.getClass().equals(stringCell.getClass()));
        }

        @Test
        public void testClear()
        {
            grid.processCommand("A1 = \"first\"");
            grid.processCommand("D8 = \"second\"");
            grid.processCommand("clear");
            Cell cellFirst = grid.getCell(new TestLocation(0,0));
            Cell cellSecond = grid.getCell(new TestLocation(7, 3));
            assertEquals("cellFirst inspection text after clear", "", cellFirst.fullCellText());
            assertEquals("cellSecond inspection text after clear", "", cellSecond.fullCellText());
        }

        @Test
        public void testClearLocation()
        {
            grid.processCommand("A1 = \"first\"");
            grid.processCommand("D8 = \"second\"");
            grid.processCommand("clear A1");
            Cell cellFirst = grid.getCell(new TestLocation(0,0));
            Cell cellSecond = grid.getCell(new TestLocation(7, 3));
            assertEquals("cellFirst inspection text after clear", "", cellFirst.fullCellText());
            assertEquals("cellSecond inspection text after clear", "\"second\"", cellSecond.fullCellText());
        }

        @Test
        public void testProcessCommandInspection()
        {
            String empty = grid.processCommand("A1");
            assertEquals("inspection of empty cell", "", empty);
            grid.processCommand("A1 = \"first\"");
            String first = grid.processCommand("A1");
            assertEquals("inspection of string cell", "\"first\"", first);
        }

        @Test
        public void testProcessCommand()
        {
            Helper helper = new Helper();
            String gridOne = grid.processCommand("A1 = \"oNe\"");
            helper.setItem(0, 0, "oNe");
            assertEquals("grid with one string cell", helper.getText(), gridOne);
            String accessorOne = grid.getGridText();
            assertEquals("grid from accessor with one string cell", helper.getText(), accessorOne);
            String gridTwo = grid.processCommand("L20 = \"TWo\"");
            helper.setItem(19, 11, "TWo");
            assertEquals("grid from accessor with two string cells", helper.getText(), gridTwo);
            String gridOnlyTwo = grid.processCommand("clear A1");
            helper.setItem(0, 0, "");
            assertEquals("grid with only the second string cell", helper.getText(), gridOnlyTwo);
            String gridEmpty = grid.processCommand("clear");
            helper.setItem(19, 11, "");
            assertEquals("empty grid", helper.getText(), gridEmpty);
        }

        @Test
        public void testProcessCommandSpecialStrings()
        {
            String stringSpecial1 = "A1 = ( avg A2-A3 )";
            String stringSpecial2 = "A1 = ( 1 * 2 / 1 + 3 - 5 )";
            Helper helper = new Helper();
            String grid1 = grid.processCommand("B7 = \"" + stringSpecial1 + "\"");
            helper.setItem(6, 1, stringSpecial1);
            assertEquals("grid with one special string", helper.getText(), grid1);
            String grid2 = grid.processCommand("F13 = \"" + stringSpecial2 + "\"");
            helper.setItem(12, 5, stringSpecial2);
            assertEquals("grid with two special strings", helper.getText(), grid2);
            String inspectedSpecial1 = grid.getCell(new TestLocation(6,1)).fullCellText();
            assertEquals("inspected first special string", "\"" + stringSpecial1 + "\"", inspectedSpecial1);
            String inspectedSpecial2 = grid.getCell(new TestLocation(12,5)).fullCellText();
            assertEquals("inspected second special string", "\"" + stringSpecial2 + "\"", inspectedSpecial2);
        }

        @Test
        public void testLongStringCellNoSpaces()
        {
            String greeting = "ThisIsALongString";
            grid.processCommand("L2 = \"" + greeting + "\"");
            Cell greetingCell = grid.getCell(new TestLocation(1,11));
            assertEquals("greeting cell text", Helper.format(greeting), greetingCell.abbreviatedCellText());
            assertEquals("greeting inspection text", "\"" + greeting + "\"", greetingCell.fullCellText());
        }

        @Test
        public void testLowerCaseCellAssignment()
        {
            String text = "Cell";
            grid.processCommand("b5 = \"" + text + "\"");
            Cell cell = grid.getCell(new TestLocation(4, 1));
            assertEquals("cell text", Helper.format(text), cell.abbreviatedCellText());
            assertEquals("inspection text", "\"" + text + "\"", cell.fullCellText());
            String processText = grid.processCommand("b5");
            assertEquals("processed inspection text", "\"" + text + "\"", processText);
            String processText2 = grid.processCommand("B5");
            assertEquals("processed inspection text 2", "\"" + text + "\"", processText2);
        }

        @Test
        public void testLowerCaseCellProcessInspection()
        {
            grid.processCommand("B2 = \"\"");
            String processText = grid.processCommand("b2");
            assertEquals("processed inspection text", "\"\"", processText);
            grid.processCommand("c18 = \"3.1410\"");
            String processText2 = grid.processCommand("c18");
            assertEquals("processed inspection text 2", "\"3.1410\"", processText2);
        }

        @Test
        public void testMixedCaseClear()
        {
            grid.processCommand("A1 = \"first\"");
            grid.processCommand("D8 = \"second\"");
            grid.processCommand("CleaR");
            Cell cellFirst = grid.getCell(new TestLocation(0,0));
            Cell cellSecond = grid.getCell(new TestLocation(7, 3));
            assertEquals("cellFirst inspection text after clear", "", cellFirst.fullCellText());
            assertEquals("cellSecond inspection text after clear", "", cellSecond.fullCellText());
        }

        @Test
        public void textNonliteralClear()
        {
            String clear = " clear ".trim();
            grid.processCommand("A1 = \"first\"");
            grid.processCommand("D8 = \"second\"");
            grid.processCommand(clear);
            Cell cellFirst = grid.getCell(new TestLocation(0,0));
            Cell cellSecond = grid.getCell(new TestLocation(7, 3));
            assertEquals("cellFirst inspection text after clear", "", cellFirst.fullCellText());
            assertEquals("cellSecond inspection text after clear", "", cellSecond.fullCellText());
            String finalGrid = grid.getGridText();
            Helper th = new Helper();
            String emptyGrid = th.getText();
            assertEquals("empty grid", emptyGrid, finalGrid);
        }

        @Test
        public void testMixedCaseClearLocation()
        {
            grid.processCommand("A18 = \"first\"");
            grid.processCommand("D8 = \"second\"");
            grid.processCommand("clEAr a18");
            Cell cellFirst = grid.getCell(new TestLocation(17,0));
            Cell cellSecond = grid.getCell(new TestLocation(7, 3));
            assertEquals("cellFirst inspection text after clear", "", cellFirst.fullCellText());
            assertEquals("cellSecond inspection text after clear", "\"second\"", cellSecond.fullCellText());
            String processedCleared = grid.processCommand("A18");
            assertEquals("processed inspection after clear", "", processedCleared);
        }

        @Test
        public void testProcessCommandMoreSpecialStrings()
        {
            String[] specialStrings = new String[] { "clear", "(", " = ", "5", "4.3", "12/28/1998", "A1 = ( 1 / 1 )", "A20 = 1/1/2000", "A9 = 4.3", "abcdefgh", "abcdefghi", "abcdefghijk" };

            Helper helper = new Helper();
            for (int col = 0; col < specialStrings.length; col++)
            {
                for (int row = 5; row < 20; row += 10)
                {
                    String cellName = Character.toString((char)('A' + col)) + (row + 1);
                    helper.setItem(row,  col, specialStrings[col]);
                    String sheet = grid.processCommand(cellName + " = \"" + specialStrings[col] + "\"");
                    assertEquals("grid after setting cell " + cellName, helper.getText(), sheet);
                    String inspected = grid.getCell(new TestLocation(row, col)).fullCellText();
                    assertEquals("inspected cell " + cellName, "\"" + specialStrings[col] + "\"", inspected);
                }
            }
            assertEquals("final sheet", helper.getText(), grid.getGridText());
        }
    }

    public static class A_Checkpoint3
    {
        // Tests for checkpoint 3.
        // Note these must also pass for all subsequent checkpoints including final project.
        Grid grid;

        @Before
        public void initializeGrid()
        {
            grid = new Spreadsheet();
        }

        @Test
        public void testPercentCell()
        {
            String percent = "11.25%";
            grid.processCommand("A1 = " + percent);
            Cell dateCell = grid.getCell(new TestLocation(0,0));
            assertEquals("date cell text", "11%", dateCell.abbreviatedCellText().trim());
            assertEquals("date inspection text", "0.1125", dateCell.fullCellText());
        }

        @Test
        public void testBasicRealCell()
        {
            String real = "3.14";
            grid.processCommand("D18 = " + real);
            Cell realCell = grid.getCell(new TestLocation(17, 3));
            assertEquals("real cell text", Helper.format(real), realCell.abbreviatedCellText());
            assertEquals("real inspection text", real, realCell.fullCellText());
        }

        @Test
        public void testMoreRealCells()
        {
            String zero = "0.0";
            grid.processCommand("A1 = " + zero);
            Cell zeroCell = grid.getCell(new TestLocation(0, 0));
            assertEquals("real cell 0", Helper.format(zero), zeroCell.abbreviatedCellText());
            assertEquals("real inspection 0", zero, zeroCell.fullCellText());
            String negativeTwo = "-2.0";
            grid.processCommand("B1 = " + negativeTwo);
            Cell negativeTwoCell = grid.getCell(new TestLocation(0, 1));
            assertEquals("real cell -2", Helper.format(negativeTwo), negativeTwoCell.abbreviatedCellText());
            assertEquals("real inspection -2", negativeTwo, negativeTwoCell.fullCellText());
        }

        @Test
        public void testDifferentCellTypes()
        {
            grid.processCommand("H4 = 12.281998%");
            grid.processCommand("G3 = \"5\"");
            grid.processCommand("F2 = -123.456");
            Cell dateCell = grid.getCell(new TestLocation(3, 7));
            Cell stringCell = grid.getCell(new TestLocation(2, 6));
            Cell realCell = grid.getCell(new TestLocation(1, 5));
            Cell emptyCell = grid.getCell(new TestLocation(0, 4));
            Cell[] differentCells = { dateCell, stringCell, realCell, emptyCell };
            for (int i = 0; i < differentCells.length - 1; i++)
            {
                for (int j = i + 1; j < differentCells.length; j++)
                {
                    assertTrue("percent, string, real, empty cells must all have different class types",
                            !differentCells[i].getClass().equals(differentCells[j].getClass()));
                }
            }
        }

        @Test
        public void testProcessCommand()
        {
            Helper helper = new Helper();
            String first = grid.processCommand("A1 = 1.021822%");
            helper.setItem(0, 0, "1%");
            assertEquals("grid with date", helper.getText(), first);
            String second = grid.processCommand("B2 = -5");
            helper.setItem(1, 1, "-5.0");
            assertEquals("grid with date and number", helper.getText(), second);
            String third = grid.processCommand("C3 = 2.718");
            helper.setItem(2, 2, "2.718");
            assertEquals("grid with date and two numbers", helper.getText(), third);
            String fourth = grid.processCommand("D4 = 0");
            helper.setItem(3, 3, "0.0");
            assertEquals("grid with date and three numbers", helper.getText(), fourth);
        }

        @Test
        public void testRealCellFormat()
        {
            // NOTE spec not totally consistent on inspection format, allow anything that parses to within epsilon of as entered
            String[] realsEntered = { "3.00", "-74.05000", "400", "400.0" };
            String[] realsFormatted = { "3.0       ", "-74.05    ", "400.0     ", "400.0     " };
            Helper helper = new Helper();
            for (int col = 0; col < realsEntered.length; col++)
            {
                for (int row = 6; row < 20; row += 10)
                {
                    String cellName = Character.toString((char)('A' + col)) + (row + 1);
                    String sheet = grid.processCommand(cellName + " = " + realsEntered[col]);
                    helper.setItem(row,  col, realsFormatted[col]);
                    assertEquals("sheet after setting cell " + cellName, helper.getText(), sheet);
                    String inspected = grid.getCell(new TestLocation(row, col)).fullCellText();
                    double expected = Double.parseDouble(realsEntered[col]);
                    double actual = Double.parseDouble(inspected);
                    assertEquals("inspected real value", actual, expected, 1e-6);
                }
            }
            assertEquals("final sheet", helper.getText(), grid.getGridText());
        }

        @Test
        public void testRealCellTruncation()
        {
            String big = "-9876543212345";
            grid.processCommand("A1 = " + big);
            Cell bigCell = grid.getCell(new TestLocation(0, 0));
            assertEquals("real big cell length", 10, bigCell.abbreviatedCellText().length());
            assertEquals("real big inspection ", Double.parseDouble(big), Double.parseDouble(bigCell.fullCellText()), 1e-6);

            String precise = "3.14159265358979";
            grid.processCommand("A2 = " + precise);
            Cell preciseCell = grid.getCell(new TestLocation(1, 0));
            assertEquals("real precise cell length", 10, preciseCell.abbreviatedCellText().length());
            assertEquals("real precise cell", Double.parseDouble(precise), Double.parseDouble(preciseCell.abbreviatedCellText()), 1e-6);
            assertEquals("real precise inspection ", Double.parseDouble(precise), Double.parseDouble(preciseCell.fullCellText()), 1e-6);

            String moderate = "123456";
            grid.processCommand("A3 = " + moderate);
            Cell moderateCell = grid.getCell(new TestLocation(2, 0));
            assertEquals("real moderate cell length", 10, moderateCell.abbreviatedCellText().length());
            assertEquals("real moderate cell", moderate + ".0", moderateCell.abbreviatedCellText().trim());
            assertEquals("real moderate inspection", moderate, moderateCell.fullCellText());

            String precisePerc = "7.87878%";
            grid.processCommand("A4 = " + precisePerc);
            Cell precisePerCell = grid.getCell(new TestLocation(3, 0));
            assertEquals("real precise percent cell length", 10, precisePerCell.abbreviatedCellText().length());
            assertEquals("real precise percent cell", "7%", precisePerCell.abbreviatedCellText().trim());
            assertEquals("real precise percent inspection", "0.0787878", precisePerCell.fullCellText());
        }        
    }

    public static class A_Final
    {
        // Additional tests for final project.
        Grid grid;

        @Before
        public void initializeGrid()
        {
            grid = new Spreadsheet();
        }

        private void assertListContains(Iterable<String> list, String text)
        {
            for (String line : list)
            {
                if (line.equals(text))
                {
                    return;
                }
            }

            assertEquals("Unable to find '" + text + "' in saved file", "0", "1");
        }

        @Test
        public void testSaveFormat()
        {
            // Generate the saved file
            grid.processCommand("A1 = 23.521822%");             // Percent
            grid.processCommand("B3 = -52.5");                  // Value
            grid.processCommand("J6 = 2.888");                  // Value
            grid.processCommand("L20 = 0");                     // Value
            grid.processCommand("D10 = \"ChocolateChocolateChipCrustedCookie\"");    // Text            
            grid.processCommand("F4 = ( 2 + 1 * 7 )");         // Formula            
            grid.processCommand("save TestSaveFormat.csv");

            // Open the file manually with a scanner to inspect its contents
            Scanner file;
            try
            {
                file = new Scanner(new File("TestSaveFormat.csv"));
            }
            catch (FileNotFoundException e)
            {
                assertEquals("Unable to open TestSaveFormat.csv: " + e.getMessage(), "0", "1");
                return;
            }

            ArrayList<String> contents = new ArrayList<String>();
            while (file.hasNextLine())
            {
                contents.add(file.nextLine());
            }
            file.close();

            assertListContains(contents, "A1,PercentCell,0.23521822");
            assertListContains(contents, "B3,ValueCell,-52.5");
            assertListContains(contents, "F4,FormulaCell,( 2 + 1 * 7 )");
            assertListContains(contents, "J6,ValueCell,2.888");
            assertListContains(contents, "D10,TextCell,\"ChocolateChocolateChipCrustedCookie\"");
            assertListContains(contents, "L20,ValueCell,0");
        }

        @Test
        public void testFileIOSimple()
        {
            Helper helper = new Helper();

            // Cells of each type (do formula in separate test, since can't compare
            // sheet texts with formulas until Part B)
            grid.processCommand("A1 = 1.021822%");              // Percent
            helper.setItem(0, 0, "1%");
            grid.processCommand("A2 = -5");                     // Value
            helper.setItem(1, 0, "-5.0");
            grid.processCommand("K19 = 2.718");                 // Value
            helper.setItem(18, 10, "2.718");
            grid.processCommand("L20 = 0");                     // Value
            helper.setItem(19, 11, "0.0");
            String d8 = "ChocolateChocolateChipCrustedCookie";
            grid.processCommand("D8 = " + "\"" + d8 + "\"");    // Text
            helper.setItem(7, 3, d8.substring(0, 10));

            // Save and clear
            grid.processCommand("save TestFileIOSimple.csv");
            grid.processCommand("clear");

            // Verify grid is cleared
            Cell cell = grid.getCell(new TestLocation(0, 0));
            assertEquals("cell inspection after clear", "", cell.fullCellText());
            cell = grid.getCell(new TestLocation(1, 0));
            assertEquals("cell inspection after clear", "", cell.fullCellText());
            cell = grid.getCell(new TestLocation(18, 10));
            assertEquals("cell inspection after clear", "", cell.fullCellText());
            cell = grid.getCell(new TestLocation(19, 11));
            assertEquals("cell inspection after clear", "", cell.fullCellText());
            cell = grid.getCell(new TestLocation(7, 3));
            assertEquals("cell inspection after clear", "", cell.fullCellText());

            // Read back in the file, verify sheet looks correct
            String gridText = grid.processCommand("open TestFileIOSimple.csv");

            assertEquals("grid after save and open", helper.getText(), gridText);
        }

        private String getConstantFormulaString(int col)
        {
            String ret = "( 0.2";
            String[] operators = {" + ", " - ", " * ", " / "};
            int operator = 0;
            String colS = "" + col;

            for (int row = 1; row <= 18; row++)
            {
                ret += operators[operator] + colS + row;
                operator = (operator + 1) % 4;
            }

            ret += " )";
            return ret;
        }


        @Test
        public void testFileIOComplex()
        {
            // Fills out all cells, and tests them individually.  Includes formulas,
            // so grid text cannot be used for comparisons

            // Fill out all but last two rows with different double values
            double value = 0.1;
            for (int col = 0; col < 12; col++)
            {
                for (int row = 0; row < 18; row++)
                {
                    grid.processCommand(((char) ('A' + col)) + "" + (row + 1) + " = " + value);
                    value++;
                }
            }

            // Next row combines the columns via FormulaCell
            for (int col = 0; col < 12; col++)
            {
                grid.processCommand(((char) ('A' + col)) + "19 = " + getConstantFormulaString(col));
            }

            // Final row contains special strings (NOT formulas, but they look like them)
            String odds = "\"( 1 * 2 / 1 + 3 - 5 )\"";
            String evens = "\"B4 = ( avg A2-A3 )\"";
            for (int col = 0; col < 12; col++)
            {
                grid.processCommand(((char) ('A' + col)) + "20 = " + ((col % 2 == 0) ? evens : odds));
            }

            // Save and clear
            grid.processCommand("save TestFileIOComplex.csv");
            grid.processCommand("clear");

            // Verify grid is cleared
            for (int row=0; row < 20; row++)
            {
                for (int col=0; col < 12; col++)
                {
                    Cell cell = grid.getCell(new TestLocation(row, col));
                    assertEquals("cell inspection after clear", "", cell.fullCellText());
                }
            }

            // Read back in the file
            String gridText = grid.processCommand("open TestFileIOComplex.csv");

            // Redo the loops, this time verifying the cells' text

            // Doubles in all but last two rows
            value = 0.1;
            for (int col = 0; col < 12; col++)
            {
                for (int row = 0; row < 18; row++)
                {
                    Cell cell = grid.getCell(new TestLocation(row, col));
                    assertEquals("value cell inspection after reload", "" + value, cell.fullCellText());
                    value++;
                }
            }

            // Next row's formulas that combine the columns
            for (int col = 0; col < 12; col++)
            {
                Cell cell = grid.getCell(new TestLocation(18, col));
                assertEquals("formula cell inspection after reload", "" + getConstantFormulaString(col), cell.fullCellText());
            }

            // Final row contains special strings
            for (int col = 0; col < 12; col++)
            {
                Cell cell = grid.getCell(new TestLocation(19, col));
                assertEquals("formula cell inspection after reload", ((col % 2 == 0) ? evens : odds), cell.fullCellText());
            }
        }
    }

    public static class B_Checkpoint1
    {
        // Tests for Part B, Checkpoint 1
        // Note these must also pass for all subsequent checkpoints including final project.
        private Grid grid;

        @Before
        public void initializeGrid()
        {
            grid = new Spreadsheet();
        }

        @Test
        public void testConstant()
        {
            grid.processCommand("A1 = ( -43.5 )");
            Cell cell = grid.getCell(new TestLocation(0, 0));
            assertEquals("constant formula value", Helper.format("-43.5"), cell.abbreviatedCellText());
            assertEquals("constant formula inspection", "( -43.5 )", cell.fullCellText());
        }

        @Test
        public void testMultiplicative()
        {
            String formula = "( 2 * 3 * 4 * 5 / 2 / 3 / 2 )";
            grid.processCommand("A1 = " + formula);
            Cell cell = grid.getCell(new TestLocation(0, 0));
            assertEquals("multiplicative formula value", Helper.format("10.0"), cell.abbreviatedCellText());
            assertEquals("multiplicative formula inspection", formula, cell.fullCellText());
        }

        @Test
        public void testAdditive()
        {
            String formula = "( 1 + 3 + 5 - 2 - 4 - 6 )";
            grid.processCommand("L20 = " + formula);
            Cell cell = grid.getCell(new TestLocation(19, 11));
            assertEquals("additive formula value", Helper.format("-3.0"), cell.abbreviatedCellText());
            assertEquals("additive formula inspection", formula, cell.fullCellText());
        }

        @Test
        public void testMixed()
        {
            String formula = "( 5.4 * 3.5 / -1.4 + 27.4 - 11.182 )";
            grid.processCommand("E1 = " + formula);
            Cell cell = grid.getCell(new TestLocation(0, 4));
            assertEquals("mixed formula value length", 10, cell.abbreviatedCellText().length());
            assertEquals("mixed formula value", 2.718, Double.parseDouble(cell.abbreviatedCellText()), 1e-6);
            assertEquals("mixed formula inspection", formula, cell.fullCellText());
        }



        @Test
        public void testMultiplicativeWithNegative()
        {
            String formula = "( -2 * -3.0 * 4 * 5 / -2 / 3 / 2 )";
            grid.processCommand("A1 = " + formula);
            Cell cell = grid.getCell(new TestLocation(0, 0));
            assertEquals("multiplicative formula value", Helper.format("-10.0"), cell.abbreviatedCellText());
            assertEquals("multiplicative formula inspection", formula, cell.fullCellText());
        }

        @Test
        public void testAdditiveWithNegatives()
        {
            String formula = "( -1 + 3 + 5.0 - -2 - 4 - 6 + -2 )";
            grid.processCommand("L20 = " + formula);
            Cell cell = grid.getCell(new TestLocation(19, 11));
            assertEquals("additive formula value", Helper.format("-3.0"), cell.abbreviatedCellText());
            assertEquals("additive formula inspection", formula, cell.fullCellText());
        }

        @Test
        public void testSimpleMixedWithOrWithoutPrecedence()
        {
            // Accept either the left-to-right basic result 13 or the precedence extra credit result 11
            String formula = "( 1 + 2 * 3 + 4 )";
            grid.processCommand("A20 = " + formula);
            Cell cell = grid.getCell(new TestLocation(19, 0));
            String text = cell.abbreviatedCellText();
            assertEquals("length", 10, text.length());
            String result = text.trim();
            assertTrue("result " + result + " should be either 13 (left-to-right) or 11 (with precedence)",
                    result.equals("13.0") || result.equals("11.0"));
        }

        @Test
        public void testMixedWithOrWithoutPrecedence()
        { // Accept either the left-to-right basic result 24 or the precedence extra credit result 20
            String formula = "( 1 + 2 * 3 + 4.5 - 5 * 6 - 3.0 / 2 )";
            grid.processCommand("E1 = " + formula);
            Cell cell = grid.getCell(new TestLocation(0, 4));
            assertEquals("mixed formula value length", 10, cell.abbreviatedCellText().length());
            double result = Double.parseDouble(cell.abbreviatedCellText());
            if (result > 0)
            {
                assertEquals("mixed formula value (without precedence)", 24, result, 1e-6);
            } else
            {
                assertEquals("mixed formula value (with precedence)", -20, result, 1e-6);
            }
            assertEquals("mixed formula inspection", formula, cell.fullCellText());
        }
    }

    public static class B_Final
    {
        // Tests for Part B, Final submission

        private Grid grid;

        @Before
        public void initializeGrid()
        {
            grid = new Spreadsheet();
        }

        @Test
        public void testProcessCommand()
        {
            Helper helper = new Helper();
            helper.setItem(0, 5,  "1.0");
            helper.setItem(1, 5, "1.0");
            helper.setItem(2,  5,  "2.0");
            helper.setItem(3,  5, "3.0");
            helper.setItem(4, 5, "5.0");
            grid.processCommand("F1 = 1");
            grid.processCommand("F2 = ( 1 )");
            grid.processCommand("F3 = ( F2 + F1 )");
            grid.processCommand("F4 = ( F2 + F3 )");
            String actual = grid.processCommand("F5 = ( F3 + F4 )");
            assertEquals("grid", helper.getText(), actual);
            String inspected = grid.processCommand("F4");
            assertEquals("inspected", "( F2 + F3 )", inspected);
            String updated = grid.processCommand("F3 = 11.5");
            helper.setItem(2, 5, "11.5");
            helper.setItem(3, 5, "12.5");
            helper.setItem(4, 5, "24.0");
            assertEquals("updated grid", helper.getText(), updated);
            String updatedInspected = grid.processCommand("F4");
            assertEquals("updated inspected", "( F2 + F3 )", updatedInspected);
        }

        @Test
        public void testFormulaAssignment()
        {
            for (int row = 1; row < 11; row++)
            {
                for (int col = 1; col < 7; col++)
                {
                    String cellName = Character.toString((char)('A' + col)) + (row + 1);
                    grid.processCommand(cellName + " = 1");
                }
            }
            String formula1 = "( 4 * 5.5 / 2 + 1 - -11.5 )";
            String formula2 = "( sUm B6-g11 )";
            String formula3 = "( AvG f8-F9 )";
            grid.processCommand("K9 = " + formula1);
            grid.processCommand("J10 = " + formula2);
            grid.processCommand("I11 = " + formula3);
            Cell cell1 = grid.getCell(new TestLocation(8, 10));
            Cell cell2 = grid.getCell(new TestLocation(9, 9));
            Cell cell3 = grid.getCell(new TestLocation(10, 8));
            assertEquals("cell length 1", 10, cell1.abbreviatedCellText().length());
            assertEquals("inspection 1", formula1, cell1.fullCellText());
            assertEquals("cell length 2", 10, cell2.abbreviatedCellText().length());
            assertEquals("inspection 2", formula2, cell2.fullCellText());
            assertEquals("cell length 3", 10, cell3.abbreviatedCellText().length());
            assertEquals("inspection 3", formula3, cell3.fullCellText());
        }

        @Test
        public void testReferences()
        {
            String formula = "( A1 * A2 / A3 + A4 - A5 )";
            grid.processCommand("A1 = 5.4");
            grid.processCommand("A2 = 3.5");
            grid.processCommand("A3 = -1.4");
            grid.processCommand("A4 = 27.4");
            grid.processCommand("A5 = 11.182");
            grid.processCommand("L18 = " + formula);
            Cell cell = grid.getCell(new TestLocation(17, 11));
            assertEquals("reference formula value length", 10, cell.abbreviatedCellText().length());
            assertEquals("reference formula value", 2.718, Double.parseDouble(cell.abbreviatedCellText()), 1e-6);
            assertEquals("reference formula inspection", formula, cell.fullCellText());
            grid.processCommand("A4 = 25.4");
            assertEquals("updated value length", 10, cell.abbreviatedCellText().length());
            assertEquals("updated value", 0.718, Double.parseDouble(cell.abbreviatedCellText()), 1e-6);
            assertEquals("updated inspection", formula, cell.fullCellText());
        }

        @Test
        public void testTransitiveReferences()
        {
            grid.processCommand("F1 = 1");
            grid.processCommand("F2 = ( 1 )");
            grid.processCommand("F3 = ( F2 + F1 )");
            grid.processCommand("F4 = ( F2 + F3 )");
            grid.processCommand("F5 = ( F3 + F4 )");
            Cell cell = grid.getCell(new TestLocation(4, 5));
            assertEquals("Fib(5)", Helper.format("5.0"), cell.abbreviatedCellText());
            assertEquals("inspection", "( F3 + F4 )", cell.fullCellText());
        }

        @Test
        public void testSaveFormatB()
        {
            // Generate the saved file
            grid.processCommand("A1 = 23.521822%");             // Percent
            grid.processCommand("B3 = -52.5");                  // Value
            grid.processCommand("J6 = 2.888");                  // Value
            grid.processCommand("L20 = 0");                     // Value
            grid.processCommand("D10 = \"ChocolateChocolateChipCrustedCookie\"");    // Text            
            grid.processCommand("F4 = ( 2 + A1 * 7 )");         // Formula            
            grid.processCommand("save TestSaveFormat.csv");

            // Open the file manually with a scanner to inspect its contents
            Scanner file;
            try
            {
                file = new Scanner(new File("TestSaveFormat.csv"));
            }
            catch (FileNotFoundException e)
            {
                assertEquals("Unable to open TestSaveFormat.csv: " + e.getMessage(), "0", "1");
                return;
            }

            ArrayList<String> contents = new ArrayList<String>();
            while (file.hasNextLine())
            {
                contents.add(file.nextLine());
            }
            file.close();

            assertListContains(contents, "A1,PercentCell,0.23521822");
            assertListContains(contents, "B3,ValueCell,-52.5");
            assertListContains(contents, "F4,FormulaCell,( 2 + A1 * 7 )");
            assertListContains(contents, "J6,ValueCell,2.888");
            assertListContains(contents, "D10,TextCell,\"ChocolateChocolateChipCrustedCookie\"");
            assertListContains(contents, "L20,ValueCell,0");
        }

        private void assertListContains(Iterable<String> list, String text)
        {
            for (String line : list)
            {
                if (line.equals(text))
                {
                    return;
                }
            }

            assertEquals("Unable to find '" + text + "' in saved file", "0", "1");
        }

        @Test
        public void testMixedReferencesAndConstantsWithOrWithoutPrecedence()
        { // Initially with precedence, 39.264.  without precedence, 9.564
            // Then with precedence 37.264, 13.564 without precedence
            String formula = "( 3.0 + A1 * A2 / -1.4 + A4 - A5 * -2.0 )";
            grid.processCommand("A1 = 5.4");
            grid.processCommand("A2 = 3.5");
            grid.processCommand("A4 = 27.4");
            grid.processCommand("A5 = 11.182");
            grid.processCommand("L18 = " + formula);
            Cell cell = grid.getCell(new TestLocation(17, 11));
            assertEquals("reference formula value length", 10, cell.abbreviatedCellText().length());
            double resultInitial = Double.parseDouble(cell.abbreviatedCellText());
            if (resultInitial > 10)
            {
                assertEquals("initial value", 39.264, resultInitial, 1e-6);
            } else
            {
                assertEquals("initial value", 9.564, resultInitial, 1e-6);
            }
            assertEquals("initial formula inspection", formula, cell.fullCellText());
            grid.processCommand("A4 = 25.4");
            assertEquals("updated value length", 10, cell.abbreviatedCellText().length());
            double resultUpdated = Double.parseDouble(cell.abbreviatedCellText());
            if (resultUpdated > 15)
            {
                assertEquals("updated value", 37.264, resultUpdated, 1e-6);
            } else
            {
                assertEquals("updated value", 13.564, resultUpdated, 1e-6);
            }
            assertEquals("updated inspection", formula, cell.fullCellText());
        }

        private String getReferenceFormulaString(int col)
        {
            String ret = "( 0.2";
            String[] operators = {" + ", " - ", " * ", " / "};
            int operator = 0;
            String colS = "" + (char) ('A' + col);

            for (int row = 1; row <= 18; row++)
            {
                ret += operators[operator] + colS + row;
                operator = (operator + 1) % 4;
            }

            ret += " )";
            return ret;
        }


        @Test
        public void testFileIOComplexB()
        {
            // Fills out all cells, and tests them individually.  Includes formulas,
            // so grid text cannot be used for comparisons

            // Fill out all but last two rows with different double values
            double value = 0.1;
            for (int col = 0; col < 12; col++)
            {
                for (int row = 0; row < 18; row++)
                {
                    grid.processCommand(((char) ('A' + col)) + "" + (row + 1) + " = " + value);
                    value++;
                }
            }

            // Next row combines the columns via FormulaCell
            for (int col = 0; col < 12; col++)
            {
                grid.processCommand(((char) ('A' + col)) + "19 = " + getReferenceFormulaString(col));
            }

            // Final row contains special strings (NOT formulas, but they look like them)
            String odds = "\"( 1 * 2 / 1 + 3 - 5 )\"";
            String evens = "\"B4 = ( avg A2-A3 )\"";
            for (int col = 0; col < 12; col++)
            {
                grid.processCommand(((char) ('A' + col)) + "20 = " + ((col % 2 == 0) ? evens : odds));
            }

            // Save and clear
            grid.processCommand("save TestFileIOComplex.csv");
            grid.processCommand("clear");

            // Verify grid is cleared
            for (int row=0; row < 20; row++)
            {
                for (int col=0; col < 12; col++)
                {
                    Cell cell = grid.getCell(new TestLocation(row, col));
                    assertEquals("cell inspection after clear", "", cell.fullCellText());
                }
            }

            // Read back in the file
            String gridText = grid.processCommand("open TestFileIOComplex.csv");

            // Redo the loops, this time verifying the cells' text

            // Doubles in all but last two rows
            value = 0.1;
            for (int col = 0; col < 12; col++)
            {
                for (int row = 0; row < 18; row++)
                {
                    Cell cell = grid.getCell(new TestLocation(row, col));
                    assertEquals("value cell inspection after reload", "" + value, cell.fullCellText());
                    value++;
                }
            }

            // Next row's formulas that combine the columns
            for (int col = 0; col < 12; col++)
            {
                Cell cell = grid.getCell(new TestLocation(18, col));
                assertEquals("formula cell inspection after reload", "" + getReferenceFormulaString(col), cell.fullCellText());
            }

            // Final row contains special strings
            for (int col = 0; col < 12; col++)
            {
                Cell cell = grid.getCell(new TestLocation(19, col));
                assertEquals("formula cell inspection after reload", ((col % 2 == 0) ? evens : odds), cell.fullCellText());
            }
        }

        @Test
        public void testTransitiveNontrivialReferences()
        {
            grid.processCommand("F1 = 1");
            grid.processCommand("F2 = ( 1 )");
            grid.processCommand("F3 = ( 1 + 3 + F2 + F1 - 3 - 1 )");
            grid.processCommand("F4 = ( 1.0 * F2 + F3 - 0.0 )");
            String outerFormula = "( 1.0 - 1 + F3 + F4 * 1.0 )";
            grid.processCommand("F5 = " + outerFormula);
            Cell cell = grid.getCell(new TestLocation(4, 5));
            assertEquals("Fib(5)", Helper.format("5.0"), cell.abbreviatedCellText());
            assertEquals("inspection", outerFormula, cell.fullCellText());
        }

        @Test
        public void testSumSingle()
        {
            grid.processCommand("A15 = 37.05");
            grid.processCommand("A16 = ( SuM A15-A15 )");
            Cell cell = grid.getCell(new TestLocation(15, 0));
            assertEquals("sum single cell", Helper.format("37.05"), cell.abbreviatedCellText());
        }

        @Test
        public void testAvgSingle()
        {
            grid.processCommand("A1 = -9");
            grid.processCommand("A2 = ( 3 * A1 )");
            grid.processCommand("B1 = ( avg A2-A2 )");
            Cell cell = grid.getCell(new TestLocation(0, 1));
            assertEquals("avg single cell", Helper.format("-27.0"), cell.abbreviatedCellText());
        }

        @Test
        public void testVertical()
        {
            grid.processCommand("C3 = 1");
            grid.processCommand("C4 = ( C3 * 2 )"); // 2
            grid.processCommand("C5 = ( C4 - C3 )"); // 1
            grid.processCommand("C6 = ( 32 - C4 )"); // 30
            grid.processCommand("K20 = ( SUM c3-c6 )"); // 34
            grid.processCommand("L20 = ( avg C3-C6 )"); // 8.5
            Cell cellSum = grid.getCell(new TestLocation(19, 10));
            Cell cellAvg = grid.getCell(new TestLocation(19, 11));
            assertEquals("sum vertical", Helper.format("34.0"), cellSum.abbreviatedCellText());
            assertEquals("avg vertical", Helper.format("8.5"), cellAvg.abbreviatedCellText());
        }

        @Test
        public void testHorizontal()
        {
            grid.processCommand("F8 = 3");
            grid.processCommand("G8 = ( 5 )");
            grid.processCommand("H8 = ( -1 * F8 + G8 )"); // 2
            grid.processCommand("I8 = ( sum F8-H8 )"); // 10
            grid.processCommand("J8 = ( AVG F8-I8 )"); // 5
            Cell cellSum = grid.getCell(new TestLocation(7, 8));
            Cell cellAvg = grid.getCell(new TestLocation(7, 9));
            assertEquals("sum horizontal", Helper.format("10.0"), cellSum.abbreviatedCellText());
            assertEquals("avg horizontal", Helper.format("5.0"), cellAvg.abbreviatedCellText());
        }

        @Test
        public void testRectangular()
        {
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 5; j++)
                {
                    String cellId = "" + (char)('A' + j) + (i + 1);
                    grid.processCommand(cellId + " = " + (i * j));
                }
            grid.processCommand("G8 = ( sum A1-E4 )");
            grid.processCommand("G9 = ( avg A1-E4 )");
            Cell cellSum = grid.getCell(new TestLocation(7, 6));
            Cell cellAvg = grid.getCell(new TestLocation(8, 6));
            assertEquals("sum rectangular", Helper.format("60.0"), cellSum.abbreviatedCellText());
            assertEquals("avg rectangular", Helper.format("3.0"), cellAvg.abbreviatedCellText());
        }

        @Test
        public void testProcessCommandWithFunctions()
        {
            Helper helper = new Helper();
            for (int i = 0; i < 4; i++)
            {
                for (int j = 0; j < 5; j++)
                {
                    String cellId = "" + (char)('A' + j) + (i + 1);
                    grid.processCommand(cellId + " = " + (i * j));
                    helper.setItem(i, j, (i * j) + ".0");
                }
            }
            String first = grid.processCommand("G8 = ( sum A1-E4 )");
            helper.setItem(7, 6, "60.0");
            assertEquals("grid with sum", helper.getText(), first);
            String second = grid.processCommand("G9 = ( avg A1-E4 )");
            helper.setItem(8, 6, "3.0");
            assertEquals("grid with sum and avg", helper.getText(), second);
            String updated = grid.processCommand("E4 = ( sum A4-D4 )");
            helper.setItem(3, 4, "18.0");
            helper.setItem(7, 6, "66.0");
            helper.setItem(8, 6, "3.3");
            assertEquals("updated grid", helper.getText(), updated);
        }

        @Test
        public void testSumSingleNegative()
        {
            grid.processCommand("A15 = -37.05");
            grid.processCommand("A16 = ( SuM A15-A15 )");
            Cell cell = grid.getCell(new TestLocation(15, 0));
            assertEquals("sum single cell", Helper.format("-37.05"), cell.abbreviatedCellText());
        }

        @Test
        public void testAvgSingleNontrivial()
        {
            grid.processCommand("A1 = -9");
            grid.processCommand("A2 = ( 14 - 7 + -4 - 3 + 3 * A1 )");
            grid.processCommand("b1 = ( avG A2-a2 )");
            Cell cell = grid.getCell(new TestLocation(0, 1));
            assertEquals("avg single cell", Helper.format("-27.0"), cell.abbreviatedCellText());
        }

        @Test
        public void testVerticalNontrivial()
        {
            grid.processCommand("C13 = 1.0");
            grid.processCommand("C14 = ( 7 + 2 - 3 + -6 + C13 * 2 )"); // 2
            grid.processCommand("C15 = ( C14 - C13 )"); // 1
            grid.processCommand("C16 = ( 32 - C14 )"); // 30
            grid.processCommand("K20 = ( SuM c13-C16 )"); // 34
            grid.processCommand("L20 = ( Avg c13-C16 )"); // 8.5
            Cell cellSum = grid.getCell(new TestLocation(19, 10));
            Cell cellAvg = grid.getCell(new TestLocation(19, 11));
            assertEquals("sum vertical", Helper.format("34.0"), cellSum.abbreviatedCellText());
            assertEquals("avg vertical", Helper.format("8.5"), cellAvg.abbreviatedCellText());
        }

        @Test
        public void testHorizontalNontrivial()
        {
            grid.processCommand("F8 = 3");
            grid.processCommand("G8 = ( 5 )");
            grid.processCommand("H8 = ( 2 * -3 + 4 - -2 + -1 * F8 + G8 )"); // 2
            grid.processCommand("I8 = ( sum F8-H8 )"); // 10
            grid.processCommand("J8 = ( AVG F8-I8 )"); // 5
            Cell cellSum = grid.getCell(new TestLocation(7, 8));
            Cell cellAvg = grid.getCell(new TestLocation(7, 9));
            assertEquals("sum horizontal", Helper.format("10.0"), cellSum.abbreviatedCellText());
            assertEquals("avg horizontal", Helper.format("5.0"), cellAvg.abbreviatedCellText());
        }

        @Test
        public void testRectangularNontrivial()
        {
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 5; j++)
                {
                    String cellId = "" + (char)('A' + j) + (i + 1);
                    grid.processCommand(cellId + " = ( 3 * 2 - 4 + -2 + " + i + " * " + j + " )");
                }
            grid.processCommand("G8 = ( sum A1-E4 )");
            grid.processCommand("G9 = ( avg A1-E4 )");
            Cell cellSum = grid.getCell(new TestLocation(7, 6));
            Cell cellAvg = grid.getCell(new TestLocation(8, 6));
            assertEquals("sum rectangular", Helper.format("60.0"), cellSum.abbreviatedCellText());
            assertEquals("avg rectangular", Helper.format("3.0"), cellAvg.abbreviatedCellText());
        }

        @Test
        public void testMultipleNesting()
        {
            grid.processCommand("A1 = ( 1 + 2 + 3 + 4 )"); // 10, then 9
            grid.processCommand("A2 = ( 1 * 2 * 3 * 4 )"); // 24
            grid.processCommand("B1 = ( Sum a1-a2 )"); // 34, then 33
            grid.processCommand("B2 = ( avG a1-A2 )"); // 17, then 16.5
            grid.processCommand("C1 = ( sum A1-B2 )"); // 85, then 82.5
            grid.processCommand("C2 = ( avg a1-b2 )"); // 21.25, then 20.625
            grid.processCommand("d1 = ( c1 / 5.0 )"); // 17, then 16.5
            grid.processCommand("d2 = ( c2 + 1.75 + a1 )"); // 33, then 31.375
            grid.processCommand("e2 = 18");
            grid.processCommand("d3 = 29");
            grid.processCommand("A20 = ( SUM A1-D2 )"); // 241.25, then 233.5
            grid.processCommand("B20 = ( AVG A1-D2 )"); // 30.15625, then 29.1875
            Cell cellSum = grid.getCell(new TestLocation(19, 0));
            Cell cellAvg = grid.getCell(new TestLocation(19, 1));
            double resultSum = Double.parseDouble(cellSum.abbreviatedCellText());
            double resultAvg = Double.parseDouble(cellAvg.abbreviatedCellText());
            assertEquals("sum nested", 241.25, resultSum, 1e-6);
            assertEquals("avg nested", 30.15625, resultAvg, 1e-6);
            grid.processCommand("a1 = 9");
            cellSum = grid.getCell(new TestLocation(19, 0));
            cellAvg = grid.getCell(new TestLocation(19, 1));
            resultSum = Double.parseDouble(cellSum.abbreviatedCellText());
            resultAvg = Double.parseDouble(cellAvg.abbreviatedCellText());
            assertEquals("updated sum nested", 233.5, resultSum, 1e-6);
            assertEquals("updated avg nested", 29.1875, resultAvg, 1e-6);
        }
    }
    
    public static class C_Checkpoint1
    {
        private Grid grid;

        @Before
        public void initializeGrid()
        {
            grid = new Spreadsheet();
        }

        private static String getCellName(int row, int col)
        {
            return "" + (Character.toString((char) ('A' + col))) + (row + 1);
        }

        @Test
        public void testSortaTextCol() 
        {
            grid.processCommand("A1 = \"rocks\"");
            grid.processCommand("A2 = \"babies\"");
            grid.processCommand("A3 = \"zest\"");
            grid.processCommand("A4 = \"airplanes\"");

            grid.processCommand("soRTa A1-A4");

            assertEquals("\"airplanes\"", grid.getCell(new TestLocation(0,0)).fullCellText());
            assertEquals("\"babies\"", grid.getCell(new TestLocation(1,0)).fullCellText());
            assertEquals("\"rocks\"", grid.getCell(new TestLocation(2,0)).fullCellText());
            assertEquals("\"zest\"", grid.getCell(new TestLocation(3,0)).fullCellText());
        }


        @Test
        public void testSortaTextRow()
        {
            grid.processCommand("A1 = \"rocking\"");
            grid.processCommand("B1 = \"bae\"");
            grid.processCommand("C1 = \"xylophone\"");
            grid.processCommand("D1 = \"aerospace\"");

            grid.processCommand("soRTa A1-D1");

            assertEquals("\"aerospace\"", grid.getCell(new TestLocation(0,0)).fullCellText());
            assertEquals("\"bae\"", grid.getCell(new TestLocation(0,1)).fullCellText());
            assertEquals("\"rocking\"", grid.getCell(new TestLocation(0,2)).fullCellText());
            assertEquals("\"xylophone\"", grid.getCell(new TestLocation(0,3)).fullCellText());
        }

        @Test
        public void testSortaTextRowExtraValues() 
        {

            grid.processCommand("A1 = \"branded\"");
            grid.processCommand("B1 = \"acting\"");
            grid.processCommand("C1 = \"branding\"");
            grid.processCommand("D1 = \"acted\"");
            grid.processCommand("E1 = 17.4");
            grid.processCommand("A2 = 3.14159");
            grid.processCommand("B2 = \"extras!\"");

            grid.processCommand("soRTa A1-D1");

            assertEquals("\"acted\"", grid.getCell(new TestLocation(0,0)).fullCellText());
            assertEquals("\"acting\"", grid.getCell(new TestLocation(0,1)).fullCellText());
            assertEquals("\"branded\"", grid.getCell(new TestLocation(0,2)).fullCellText());
            assertEquals("\"branding\"", grid.getCell(new TestLocation(0,3)).fullCellText());

            assertEquals("17.4", grid.getCell(new TestLocation(0,4)).fullCellText());
            assertEquals("3.14159", grid.getCell(new TestLocation(1,0)).fullCellText());
            assertEquals("\"extras!\"", grid.getCell(new TestLocation(1,1)).fullCellText());
        }

        @Test
        public void testSortaMultidigit2DText() 
        {
            grid.processCommand("A9 = \"fiddle\"");
            grid.processCommand("A10 = \"arduous\"");
            grid.processCommand("A11 = \"zaza\"");
            grid.processCommand("A12 = \"boos\"");
            grid.processCommand("A13 = \"zazzaz\"");
            grid.processCommand("B9 = \"aardvark\"");
            grid.processCommand("B10 = \"rara\"");
            grid.processCommand("B11 = \"yaya\"");
            grid.processCommand("B12 = \"azteca\"");
            grid.processCommand("B13 = \"boo\"");

            grid.processCommand("SoRTA A9-B13");

            String[] sortedVals = { "\"aardvark\"", "\"arduous\"", "\"azteca\"", "\"boo\"", "\"boos\"", "\"fiddle\"", "\"rara\"", "\"yaya\"", "\"zaza\"", "\"zazzaz\"" };
            int iSortedVals = 0;

            for (int row = 8; row <= 12; row++)
            {
                for (int col = 0; col <= 1; col++)
                {
                    assertEquals("Inspecting cell " + getCellName(row, col), sortedVals[iSortedVals++], grid.getCell(new TestLocation(row,col)).fullCellText());
                }
            }
        }       
    }
    
    public static class C_Final
    {
        private Grid grid;

        @Before
        public void initializeGrid()
        {
            grid = new Spreadsheet();
        }

        private static String getCellName(int row, int col)
        {
            return "" + (Character.toString((char) ('A' + col))) + (row + 1);
        }

        @Test
        public void testSortaCol() 
        {
            grid.processCommand("A1 = 19.1");
            grid.processCommand("A2 = 2.1");
            grid.processCommand("A3 = 607.1");
            grid.processCommand("A4 = 0.01");

            grid.processCommand("soRTa A1-A4");

            assertEquals("0.01", grid.getCell(new TestLocation(0,0)).fullCellText());
            assertEquals("2.1", grid.getCell(new TestLocation(1,0)).fullCellText());
            assertEquals("19.1", grid.getCell(new TestLocation(2,0)).fullCellText());
            assertEquals("607.1", grid.getCell(new TestLocation(3,0)).fullCellText());
        }


        @Test
        public void testSortaRow()
        {
            grid.processCommand("A1 = 19.2");
            grid.processCommand("B1 = 2.2");
            grid.processCommand("C1 = 607.2");
            grid.processCommand("D1 = 0.01");

            grid.processCommand("soRTa A1-D1");

            assertEquals("0.01", grid.getCell(new TestLocation(0,0)).fullCellText());
            assertEquals("2.2", grid.getCell(new TestLocation(0,1)).fullCellText());
            assertEquals("19.2", grid.getCell(new TestLocation(0,2)).fullCellText());
            assertEquals("607.2", grid.getCell(new TestLocation(0,3)).fullCellText());
        }

        @Test
        public void testSortaRowExtraValues() 
        {

            grid.processCommand("A1 = 19.4");
            grid.processCommand("B1 = 2.4");
            grid.processCommand("C1 = 607.4");
            grid.processCommand("D1 = 0.01");
            grid.processCommand("E1 = 17.4");
            grid.processCommand("A2 = 3.14159");
            grid.processCommand("B2 = \"extras!\"");

            grid.processCommand("soRTa A1-D1");

            assertEquals("0.01", grid.getCell(new TestLocation(0,0)).fullCellText());
            assertEquals( "2.4", grid.getCell(new TestLocation(0,1)).fullCellText());
            assertEquals("19.4", grid.getCell(new TestLocation(0,2)).fullCellText());
            assertEquals("607.4", grid.getCell(new TestLocation(0,3)).fullCellText());

            assertEquals("17.4", grid.getCell(new TestLocation(0,4)).fullCellText());
            assertEquals("3.14159", grid.getCell(new TestLocation(1,0)).fullCellText());
            assertEquals("\"extras!\"", grid.getCell(new TestLocation(1,1)).fullCellText());
        }

        @Test
        public void testSortaTwoDimensionalOffset() 
        {

            grid.processCommand("C3 = 19.5");
            grid.processCommand("C4 = 2.5");
            grid.processCommand("D3 = 607.5");
            grid.processCommand("D4 = 0.01");
            grid.processCommand("E1 = 17.5");
            grid.processCommand("A2 = 3.14159");

            grid.processCommand("soRTa C3-D4");

            assertEquals("0.01", grid.getCell(new TestLocation(2,2)).fullCellText());
            assertEquals("2.5", grid.getCell(new TestLocation(2,3)).fullCellText());
            assertEquals("19.5", grid.getCell(new TestLocation(3,2)).fullCellText());
            assertEquals("607.5", grid.getCell(new TestLocation(3,3)).fullCellText());

            assertEquals("17.5", grid.getCell(new TestLocation(0,4)).fullCellText());
            assertEquals("3.14159", grid.getCell(new TestLocation(1,0)).fullCellText());
        }

        @Test
        public void testSortaTwoDimensional() 
        {

            grid.processCommand("A1 = 19.3");
            grid.processCommand("A2 = 2.3");
            grid.processCommand("B1 = 607.3");
            grid.processCommand("B2 = 0.01");

            grid.processCommand("SoRTa A1-B2");

            assertEquals("0.01", grid.getCell(new TestLocation(0,0)).fullCellText());
            assertEquals("2.3", grid.getCell(new TestLocation(0,1)).fullCellText());
            assertEquals("19.3", grid.getCell(new TestLocation(1,0)).fullCellText());
            assertEquals("607.3", grid.getCell(new TestLocation(1,1)).fullCellText());
        }

        @Test
        public void testSortdCol() 
        {
            grid.processCommand("A1 = 19.2");
            grid.processCommand("A2 = 2.2");
            grid.processCommand("A3 = 607.2");
            grid.processCommand("A4 = 0.01");

            grid.processCommand("SoRTd A1-A4");

            assertEquals("0.01", grid.getCell(new TestLocation(3,0)).fullCellText());
            assertEquals("2.2", grid.getCell(new TestLocation(2,0)).fullCellText());
            assertEquals("19.2", grid.getCell(new TestLocation(1,0)).fullCellText());
            assertEquals("607.2", grid.getCell(new TestLocation(0,0)).fullCellText());
        }

        @Test
        public void testSortdMultidigit2DValue() 
        {
            grid.processCommand("A9 = -13.2");
            grid.processCommand("A10 = 19.2");
            grid.processCommand("A11 = 2.2");
            grid.processCommand("A12 = 607.1");
            grid.processCommand("A13 = 0.01");
            grid.processCommand("B9 = 88.2");
            grid.processCommand("B10 = -190.1");
            grid.processCommand("B11 = 1.2");
            grid.processCommand("B12 = 607.2");
            grid.processCommand("B13 = -0.02");

            grid.processCommand("SoRTd A9-B13");

            String[] sortedVals = { "-190.1", "-13.2", "-0.02", "0.01", "1.2", "2.2", "19.2", "88.2", "607.1", "607.2" };
            int iSortedVals = 0;

            for (int row = 12; row >= 8; row--)
            {
                for (int col = 1; col >= 0; col--)
                {
                    assertEquals("Inspecting cell " + getCellName(row, col), sortedVals[iSortedVals++], grid.getCell(new TestLocation(row,col)).fullCellText());
                }
            }
        }  

        @Test
        public void testSortDMultiType2D()
        {
            grid.processCommand("A9 = -13.2");
            grid.processCommand("A10 = 19.2");
            grid.processCommand("A11 = 2.2");
            grid.processCommand("A12 = 60710%");
            grid.processCommand("A13 = 1%");
            grid.processCommand("B9 = 88.2");
            grid.processCommand("B10 = -190.1");
            grid.processCommand("B11 = 101%");
            grid.processCommand("B12 = 607.2");
            grid.processCommand("B13 = -0.02");

            grid.processCommand("SoRTd A9-B13");

            String[] sortedVals = { "-190.1", "-13.2", "-0.02", "0.01", "1.01", "2.2", "19.2", "88.2", "607.1", "607.2" };
            int iSortedVals = 0;

            for (int row = 12; row >= 8; row--)
            {
                for (int col = 1; col >= 0; col--)
                {
                    assertEquals("Inspecting cell " + getCellName(row, col), sortedVals[iSortedVals++], grid.getCell(new TestLocation(row,col)).fullCellText());
                }
            }
        }


        @Test
        public void testSortMultiType2DMultipleTimes()
        {
            grid.processCommand("A9 = -13.2");
            grid.processCommand("A10 = 19.2");
            grid.processCommand("A11 = 2.2");
            grid.processCommand("A12 = 60710%");
            grid.processCommand("A13 = 1%");
            grid.processCommand("B9 = 88.2");
            grid.processCommand("B10 = -190.1");
            grid.processCommand("B11 = 101%");
            grid.processCommand("B12 = 607.2");
            grid.processCommand("B13 = -0.02");

            grid.processCommand("SoRTa A11-B12");
            grid.processCommand("SoRTd A12-B13");
            grid.processCommand("sorta A9-B10");
            grid.processCommand("SoRTd A9-B13");

            String[] sortedVals = { "-190.1", "-13.2", "-0.02", "0.01", "1.01", "2.2", "19.2", "88.2", "607.1", "607.2" };
            int iSortedVals = 0;

            for (int row = 12; row >= 8; row--)
            {
                for (int col = 1; col >= 0; col--)
                {
                    assertEquals("Inspecting cell " + getCellName(row, col), sortedVals[iSortedVals++], grid.getCell(new TestLocation(row,col)).fullCellText());
                }
            }
        }
        
        @Test
        public void testComparableText()
        {
            grid.processCommand("A1 = \"chocolate\"");
            grid.processCommand("B1 = \"chocolate\"");
            grid.processCommand("C1 = \"sauce\"");
            
            Cell a1 = grid.getCell(new TestLocation(0, 0));
            Cell b1 = grid.getCell(new TestLocation(0, 1));
            Cell c1 = grid.getCell(new TestLocation(0, 2));
            
            // If any of these CRASH (RED), then you did not properly implement the
            // Comparable interface on your TextCell class
            Comparable<Object> comparableA1 = (Comparable<Object>) a1;
            Comparable<Object> comparableC1 = (Comparable<Object>) c1;
            
            assertEquals("Comparing string with itself should give 0", 0, comparableA1.compareTo(b1));
            assertTrue("Comparing string with later string should return negative", comparableA1.compareTo(c1) < 0);
            assertTrue("Comparing string with earlier string should return positive", comparableC1.compareTo(a1) > 0);
        }
        
        @Test
        public void testComparableValue()
        {
            grid.processCommand("A1 = 5.5");
            grid.processCommand("B1 = 5.5");
            grid.processCommand("C1 = 5.6");
            grid.processCommand("D1 = -5.7");
            
            Cell a1 = grid.getCell(new TestLocation(0, 0));
            Cell b1 = grid.getCell(new TestLocation(0, 1));
            Cell c1 = grid.getCell(new TestLocation(0, 2));
            Cell d1 = grid.getCell(new TestLocation(0, 3));
            
            // If any of these CRASH (RED), then you did not properly implement the
            // Comparable interface on your ValueCell class
            Comparable<Object> comparableA1 = (Comparable<Object>) a1;
            Comparable<Object> comparableC1 = (Comparable<Object>) c1;
            
            assertEquals("Comparing value with itself should give 0", 0, comparableA1.compareTo(b1));
            assertTrue("Comparing value with larger value should return negative", comparableA1.compareTo(c1) < 0);
            assertTrue("Comparing value with smaller value should return positive", comparableC1.compareTo(a1) > 0);
            assertTrue("Comparing value with smaller value should return positive", comparableA1.compareTo(d1) > 0);
        }        
        
        @Test
        public void testComparablePercent()
        {
            grid.processCommand("A1 = 55%");
            grid.processCommand("B1 = 55%");
            grid.processCommand("C1 = 56%");
            grid.processCommand("D1 = 157%");
            
            Cell a1 = grid.getCell(new TestLocation(0, 0));
            Cell b1 = grid.getCell(new TestLocation(0, 1));
            Cell c1 = grid.getCell(new TestLocation(0, 2));
            Cell d1 = grid.getCell(new TestLocation(0, 3));
            
            // If any of these CRASH (RED), then you did not properly implement the
            // Comparable interface on your PercentCell class
            Comparable<Object> comparableA1 = (Comparable<Object>) a1;
            Comparable<Object> comparableC1 = (Comparable<Object>) c1;
            Comparable<Object> comparableD1 = (Comparable<Object>) d1;
            
            assertEquals("Comparing percent with itself should give 0", 0, comparableA1.compareTo(b1));
            assertTrue("Comparing percent with larger percent should return negative", comparableA1.compareTo(c1) < 0);
            assertTrue("Comparing percent with smaller percent should return positive", comparableD1.compareTo(a1) > 0);
        }       
        
        @Test
        public void testComparableFormula()
        {
            grid.processCommand("A1 = ( 1 + 2 + 2 )");
            grid.processCommand("B1 = ( 5 * 1 )");
            grid.processCommand("C1 = ( 0 * 7 + 100 )");
            
            Cell a1 = grid.getCell(new TestLocation(0, 0));
            Cell b1 = grid.getCell(new TestLocation(0, 1));
            Cell c1 = grid.getCell(new TestLocation(0, 2));
            
            // If any of these CRASH (RED), then you did not properly implement the
            // Comparable interface on your FormulaCell class
            Comparable<Object> comparableA1 = (Comparable<Object>) a1;
            Comparable<Object> comparableC1 = (Comparable<Object>) c1;
            
            assertEquals("Comparing formulas with same value should give 0", 0, comparableA1.compareTo(b1));
            assertTrue("Comparing formula to one with larger value should return negative", comparableA1.compareTo(c1) < 0);
            assertTrue("Comparing formula to one with smaller value should return positive", comparableC1.compareTo(a1) > 0);
        }
        
        @Test
        public void testComparableRealMixed()
        {
            grid.processCommand("A1 = ( 1 + 2 + 2 )");
            grid.processCommand("B1 = 5");
            grid.processCommand("C1 = 500%");
            grid.processCommand("A2 = ( 3 * 2 )");
            grid.processCommand("B2 = 100");
            grid.processCommand("C2 = 501%");
            
            Cell a1 = grid.getCell(new TestLocation(0, 0));
            Cell b1 = grid.getCell(new TestLocation(0, 1));
            Cell c1 = grid.getCell(new TestLocation(0, 2));
            Cell a2 = grid.getCell(new TestLocation(1, 0));
            Cell b2 = grid.getCell(new TestLocation(1, 1));
            Cell c2 = grid.getCell(new TestLocation(1, 2));
            
            // If any of these CRASH (RED), then you did not properly implement the
            // Comparable interface on one or more of your RealCell subclasses
            Comparable<Object> comparableA1 = (Comparable<Object>) a1;
            Comparable<Object> comparableB1 = (Comparable<Object>) b1;
            Comparable<Object> comparableC1 = (Comparable<Object>) c1;
            Comparable<Object> comparableA2 = (Comparable<Object>) a2;
            Comparable<Object> comparableB2 = (Comparable<Object>) b2;
            Comparable<Object> comparableC2 = (Comparable<Object>) c2;
            
            // RealCells with same getDoubleValue
            
            assertEquals("Comparing formula with matching value should give 0", 0, comparableA1.compareTo(b1));
            assertEquals("Comparing value with matching formula should give 0", 0, comparableB1.compareTo(a1));
            assertEquals("Comparing formula with matching percent should give 0", 0, comparableA1.compareTo(c1));
            assertEquals("Comparing percent with matching formula should give 0", 0, comparableC1.compareTo(a1));
            assertEquals("Comparing value with matching percent should give 0", 0, comparableB1.compareTo(c1));
            assertEquals("Comparing percent with matching value should give 0", 0, comparableC1.compareTo(b1));

            // RealCells with different getDoubleValue

            assertTrue("Comparing formula with larger value should return negative", comparableA1.compareTo(b2) < 0);
            assertTrue("Comparing value with smaller formula should return positive", comparableB2.compareTo(a1) > 0);

            assertTrue("Comparing formula with larger percent should return negative", comparableA1.compareTo(c2) < 0);
            assertTrue("Comparing percent with smaller formula should return positive", comparableC2.compareTo(a1) > 0);

            assertTrue("Comparing value with larger percent should return negative", comparableB1.compareTo(c2) < 0);
            assertTrue("Comparing percent with smaller value should return positive", comparableC2.compareTo(b1) > 0);
            
            assertTrue("Comparing formula with smaller value should return positive", comparableA2.compareTo(b1) > 0);
            assertTrue("Comparing value with larger formula should return negative", comparableB1.compareTo(a2) < 0);
            
            assertTrue("Comparing formula with smaller percent should return positive", comparableA2.compareTo(c1) > 0);
            assertTrue("Comparing percent with larger formula should return negative", comparableC1.compareTo(a2) < 0);
            
            assertTrue("Comparing value with smaller percent should return positive", comparableB2.compareTo(c1) > 0);
            assertTrue("Comparing percent with larger value should return negative", comparableC1.compareTo(b2) < 0);
        }        
    }

    public static class ExtraCreditCommandErrors
    {
        // Tests for command errors extra credit
        Grid grid;

        @Before
        public void initializeGrid()
        {
            grid = new Spreadsheet();
        }

        @Test
        public void testInvalidCommand()
        {
            String before = grid.processCommand("A1 = \"thrang\"");
            String error = grid.processCommand("lesnerize");
            String after = grid.getGridText();
            assertTrue("error message starts with ERROR: ", error.startsWith("ERROR: "));
            assertEquals("grid contents unchanged", before, after);
        }

        @Test
        public void testInvalidCellAssignment()
        {
            String before = grid.processCommand("A1 = \"hello\"");
            String error1 = grid.processCommand("A37 = 5");
            String error2 = grid.processCommand("M1 = 3");
            String error3 = grid.processCommand("A-5 = 2");
            String error4 = grid.processCommand("A0 = 17");
            String after = grid.getGridText();
            assertTrue("error1 message starts with ERROR: ", error1.startsWith("ERROR: "));
            assertTrue("error2 message starts with ERROR: ", error2.startsWith("ERROR: "));
            assertTrue("error3 message starts with ERROR: ", error3.startsWith("ERROR: "));
            assertTrue("error4 message starts with ERROR: ", error4.startsWith("ERROR: "));
            assertEquals("grid contents unchanged", before, after);
        }

        @Test
        public void testInvalidConstants()
        {
            String before = grid.processCommand("A1 = \"hello\"");
            String error1 = grid.processCommand("A2 = 5...");
            String error2 = grid.processCommand("A3 = 4p");
            String error3 = grid.processCommand("A4 = \"he");
            String error4 = grid.processCommand("A5 = 1/2/aughtfour");
            String error5 = grid.processCommand("A6 = *9");
            String after = grid.getGridText();
            assertTrue("error1 message starts with ERROR: ", error1.startsWith("ERROR: "));
            assertTrue("error2 message starts with ERROR: ", error2.startsWith("ERROR: "));
            assertTrue("error3 message starts with ERROR: ", error3.startsWith("ERROR: "));
            assertTrue("error4 message starts with ERROR: ", error4.startsWith("ERROR: "));
            assertTrue("error5 message starts with ERROR: ", error5.startsWith("ERROR: "));
            assertEquals("grid contents unchanged", before, after);
        }

        @Test
        public void testInvalidFormulaAssignment()
        {
            grid.processCommand("A1 = 1");
            String before = grid.processCommand("A2 = 2");
            String error1 = grid.processCommand("A3 = 5 + 2");
            String error2 = grid.processCommand("A4 = ( avs A1-A2 )");
            String error3 = grid.processCommand("A5 = ( sum A0-A2 )");
            String error4 = grid.processCommand("A6 = ( 1 + 2");
            String error5 = grid.processCommand("A7 = ( avg A1-B )");
            String error6 = grid.processCommand("A8 = M80");
            String after = grid.getGridText();
            assertTrue("error1 message starts with ERROR: ", error1.startsWith("ERROR: "));
            assertTrue("error2 message starts with ERROR: ", error2.startsWith("ERROR: "));
            assertTrue("error3 message starts with ERROR: ", error3.startsWith("ERROR: "));
            assertTrue("error4 message starts with ERROR: ", error4.startsWith("ERROR: "));
            assertTrue("error5 message starts with ERROR: ", error5.startsWith("ERROR: "));
            assertTrue("error6 message starts with ERROR: ", error6.startsWith("ERROR: "));
            assertEquals("grid contents unchanged", before, after);
        }

        @Test
        public void testWhitespaceTolerance()
        {
            // OK to either treat as error or as valid, just don't crash
            String before = grid.getGridText();
            grid.processCommand("L20=5");
            grid.processCommand(" A1  =     -14 ");
            grid.processCommand("A1=-14");
            grid.processCommand("A1=(3+5*4/2)");
            grid.processCommand("A1=(sum L20-L20)");
            grid.processCommand("clear    A1");
            String after = grid.processCommand("clear");
            assertEquals("end with empty grid", before, after);
        }
    }

    public static class ExtraCreditCommandHistory
    {
        // Tests for command history extra credit
        Grid grid;

        @Before
        public void initializeGrid()
        {
            grid = new Spreadsheet();
        }

        @Test
        public void testEmptyCommandHistory()
        {
            startHistory(3);

            checkHistory(new String[]{});

            stopHistory();
        }

        @Test
        public void testPartiallyEmptyCommandHistory()
        {
            startHistory(5);

            executeCommands(new String[]
                    {
                    "A1 = 5",
                    "A2 = \"Test\""
                    });

            checkHistory(new String[]
                    {
                    "A2 = \"Test\"",
                    "A1 = 5"
                    });

            executeCommands(new String[]
                    {
                    "clear A1",
                    "A3 = 10"
                    });

            checkHistory(new String[]
                    {
                    "A3 = 10",
                    "clear A1",            		
                    "A2 = \"Test\"",
                    "A1 = 5"
                    });

            stopHistory();   	
        }

        @Test
        public void testFullCommandHistory()
        {
            startHistory(6);

            executeCommands(new String[]
                    {
                    "A1 = 5",
                    "A2 = \"Test\"",
                    "clear A1"
                    });

            checkHistory(new String[]
                    {
                    "clear A1",
                    "A2 = \"Test\"",
                    "A1 = 5",
                    });

            executeCommands(new String[]
                    {
                    "A1 = 6",
                    "B1 = 7",
                    "C1 = 8"
                    });

            checkHistory(new String[]
                    {
                    "C1 = 8",
                    "B1 = 7",
                    "A1 = 6",
                    "clear A1",
                    "A2 = \"Test\"",
                    "A1 = 5",
                    });

            stopHistory();   	
        }

        @Test
        public void testOverflowingCommandHistory()
        {
            startHistory(4);

            executeCommands(new String[]
                    {
                    "A1 = 10",
                    "A2 = \"Test2\"",
                    "clear A2"
                    });

            checkHistory(new String[]
                    {
                    "clear A2",
                    "A2 = \"Test2\"",
                    "A1 = 10",
                    });

            executeCommands(new String[]
                    {
                    "A1 = 60",
                    "B1 = 70",
                    "C1 = 80"
                    });

            checkHistory(new String[]
                    {
                    "C1 = 80",
                    "B1 = 70",
                    "A1 = 60",
                    "clear A2"
                    });

            executeCommands(new String[]
                    {
                    "clear B1",
                    "clear C1"
                    });


            checkHistory(new String[]
                    {
                    "clear C1",
                    "clear B1",
                    "C1 = 80",
                    "B1 = 70"
                    });

            stopHistory();   	
        }

        @Test
        public void testClearHistory()
        {
            startHistory(5);

            executeCommands(new String[]
                    {
                    "A1 = 8",
                    "A2 = \"Test\"",
                    "clear A1",
                    "clear A2"
                    });

            checkHistory(new String[]
                    {
                    "clear A2",
                    "clear A1",
                    "A2 = \"Test\"",
                    "A1 = 8"			
                    });

            clearHistory(2);

            checkHistory(new String[]
                    {
                    "clear A2",
                    "clear A1",		
                    });

            clearHistory(1);

            checkHistory(new String[]
                    {
                    "clear A2"		
                    });

            clearHistory(1);

            checkHistory(new String[] 
                    {
                    });

            executeCommands(new String[]
                    {
                    "C1 = 8",
                    "D2 = \"Test\"",
                    "clear C1",
                    "clear D2",
                    "C1 = 20",
                    "E1 = 40",
                    "F3 = 60"
                    });

            checkHistory(new String[] 
                    {
                    "F3 = 60",
                    "E1 = 40",
                    "C1 = 20",
                    "clear D2",	
                    "clear C1"	
                    });

            clearHistory(3);

            checkHistory(new String[] 
                    {
                    "F3 = 60",
                    "E1 = 40"	
                    });

            executeCommands(new String[]
                    {
                    "A5 = 6",
                    "A6 = 7",
                    "A7 = 8",
                    "A8 = 9"
                    });

            checkHistory(new String[] 
                    {
                    "A8 = 9",
                    "A7 = 8",
                    "A6 = 7",
                    "A5 = 6",
                    "F3 = 60",
                    });

            clearHistory(2);


            checkHistory(new String[] 
                    {
                    "A8 = 9",
                    "A7 = 8",
                    "A6 = 7"
                    });

            clearHistory(5);
            checkHistory(new String[] 
                    {
                    });

            stopHistory();
        }

        @Test
        public void testAllOnSameSheet()
        {
            testClearHistory();

            grid.processCommand("A1 = 1");

            testEmptyCommandHistory();

            grid.processCommand("B2 = 2");

            testPartiallyEmptyCommandHistory();

            grid.processCommand("C3 = 3");

            testFullCommandHistory();

            grid.processCommand("D4 = 4");

            testOverflowingCommandHistory();
        }

        private void startHistory(int historySize)
        {
            String historyStart = grid.processCommand("history start " + historySize);
            assertEquals("", historyStart);
        }

        private void executeCommands(String[] commands)
        {
            for (String command : commands)
            {
                String output = grid.processCommand(command);
                if (command.startsWith("history"))
                {
                    assertEquals("", output);
                }
            }
        }

        private void checkHistory(String[] expectedHistory)
        {          	
            String historyDisplay = grid.processCommand("history display");
            if (historyDisplay.equals(""))
            {
                assertEquals(0, expectedHistory.length);
            }
            else
            {
                String[] historyCommands = historyDisplay.split("\n");

                assertTrue(Arrays.equals(expectedHistory, historyCommands));
            }
        }

        private void clearHistory(int numToClear)
        {
            String historyClear = grid.processCommand("history clear " + numToClear);
            assertEquals("", historyClear);
        }

        private void stopHistory()
        {
            String historyStop = grid.processCommand("history stop");
            assertEquals("", historyStop);
        }
    }

    public static class ExtraCreditCircularReferenceErrors
    {
        // Tests for circular reference errors extra credit
        private Grid grid;
        private final String expectedError = "#ERROR    ";
        private final String expectedOne = "1         ";

        @Before
        public void initializeGrid()
        {
            grid = new Spreadsheet();
        }

        private void assertEvalError(int row, int col, String formula, String description)
        {
            Cell cell = grid.getCell(new TestLocation(row, col));
            assertEquals(description, expectedError, cell.abbreviatedCellText());
            assertEquals("formula", formula, cell.fullCellText());
        }

        private void assertEvalOK(int row, int col, String expected, String formula, String description)
        {
            Cell cell = grid.getCell(new TestLocation(row, col));
            assertEquals(description, expected, cell.abbreviatedCellText());
            assertEquals("formula", formula, cell.fullCellText());
        }

        @Test
        public void testCircularReference()
        {
            Helper thErrors = new Helper();
            Helper thOnes = new Helper();
            for (int col = 0; col < 3; col++)
            {
                thErrors.setItem(0,  col, expectedError);
                thOnes.setItem(0,  col,  expectedOne);
            }
            grid.processCommand("A1 = ( B1 )");
            grid.processCommand("b1 = ( c1 )");
            try
            {
                String gridErrors = grid.processCommand("C1 = ( a1 )");
                assertEquals("grid with circular reference errors", thErrors.getText(), gridErrors);
                String gridOnes = grid.processCommand("B1 = 1");
                assertEquals("grid with ones", thOnes.getText(), gridOnes);
                assertEvalOK(0, 2, expectedOne, "( a1 )", "noncircular");
                gridErrors = grid.processCommand("b1 = ( a1 )");
                assertEquals("second grid with circular reference errors", thErrors.getText(), gridErrors);
                assertEvalError(0, 2, "( a1 )", "circular");
            }
            catch (StackOverflowError e)
            {
                fail("Circular reference not handled, caught stack overflow error");
            }
        }
    }
    public static class ExtraCreditEvaluationErrors
    {
        // Tests for evaluation errors extra credit
        private Grid grid;
        private final String expectedError = "#ERROR    ";

        @Before
        public void initializeGrid()
        {
            grid = new Spreadsheet();
        }

        @Test
        public void testSimpleError()
        {
            String formula = "( A2 )";
            grid.processCommand("A1 = " + formula);
            Cell cell = grid.getCell(new TestLocation(0, 0));
            assertEquals("evaluation error", expectedError, cell.abbreviatedCellText());
            assertEquals("formula", formula, cell.fullCellText());
        }

        @Test
        public void testDivideByZero()
        {
            String formula = "( 1 / 0 )";
            grid.processCommand("A1 = " + formula);
            Cell cell = grid.getCell(new TestLocation(0, 0));
            assertEquals("evaluation error", expectedError, cell.abbreviatedCellText());
            assertEquals("formula", formula, cell.fullCellText());
        }

        private void assertEvalError(int row, int col, String formula, String description)
        {
            Cell cell = grid.getCell(new TestLocation(row, col));
            assertEquals(description, expectedError, cell.abbreviatedCellText());
            assertEquals("formula", formula, cell.fullCellText());
        }

        private void assertEvalOK(int row, int col, String expected, String formula, String description)
        {
            Cell cell = grid.getCell(new TestLocation(row, col));
            assertEquals(description, expected, cell.abbreviatedCellText());
            assertEquals("formula", formula, cell.fullCellText());
        }

        @Test
        public void testSimpleTypeErrors()
        {
            String formula = "( avg A1-A1 )";
            grid.processCommand("A2 = " + formula);
            assertEvalError(1, 0, formula, "empty ref error");
            grid.processCommand("A1 = 1");
            assertEvalOK(1, 0, "1         ", formula, "valid ref");
            grid.processCommand("A1 = \"hello\"");
            assertEvalError(1, 0, formula, "string ref error");
            grid.processCommand("A1 = 2");
            assertEvalOK(1, 0, "2         ", formula, "valid ref");
            grid.processCommand("A1 = 11/20/2013");
            assertEvalError(1, 0, formula, "date ref error");
            grid.processCommand("A1 = 3");
            assertEvalOK(1, 0, "3         ", formula, "valid ref");
        }

        @Test
        public void testErrorPropagation()
        {
            String formulaA2 = "( sum A1-A1 )";
            String formulaA3 = "( 1 / A2 )";
            String formulaA4 = "( A3 + A3 )";
            String formulaB3 = "( A2 / 1 )";
            String formulaB4 = "( B3 + B3 )";
            String formulaC3 = "( avg A2-A3 )";
            String formulaC4 = "( sum C3-C3 )";
            grid.processCommand("A2 = " + formulaA2);
            grid.processCommand("A3 = " + formulaA3);
            grid.processCommand("A4 = " + formulaA4);
            grid.processCommand("B3 = " + formulaB3);
            grid.processCommand("B4 = " + formulaB4);
            grid.processCommand("C3 = " + formulaC3);
            grid.processCommand("C4 = " + formulaC4);
            assertEvalError(1, 0, formulaA2, "direct");
            assertEvalError(2, 0, formulaA3, "indirect");
            assertEvalError(3, 0, formulaA4, "indirect");
            assertEvalError(2, 1, formulaB3, "indirect");
            assertEvalError(3, 1, formulaB4, "indirect");
            assertEvalError(2, 2, formulaC3, "indirect");
            assertEvalError(3, 2, formulaC4, "indirect");
            grid.processCommand("A1 = 1");
            assertEvalOK(1, 0, "1         ", formulaA2, "direct");
            assertEvalOK(2, 0, "1         ", formulaA3, "indirect");
            assertEvalOK(3, 0, "2         ", formulaA4, "indirect");
            assertEvalOK(2, 1, "1         ", formulaB3, "indirect");
            assertEvalOK(3, 1, "2         ", formulaB4, "indirect");
            assertEvalOK(2, 2, "1         ", formulaC3, "indirect");
            assertEvalOK(3, 2, "1         ", formulaC4, "indirect");
            grid.processCommand("A1 = 0");
            assertEvalOK(1, 0, "0         ", formulaA2, "direct");
            assertEvalError(2, 0, formulaA3, "direct");
            assertEvalError(3, 0, formulaA4, "indirect");
            assertEvalOK(2, 1, "0         ", formulaB3, "indirect");
            assertEvalOK(3, 1, "0         ", formulaB4, "indirect");
            assertEvalError(2, 2, formulaC3, "indirect");
            assertEvalError(3, 2, formulaC4, "indirect");
        }
    }

    public static class ExtraCreditOperationOrder
    {
        // Tests for operation order extra credit
        Grid grid;

        @Before
        public void initializeGrid()
        {
            grid = new Spreadsheet();
        }

        @Test
        public void testSimplePrecedence()
        {
            String formula = "( 1 + 2 * 3 )";
            grid.processCommand("A1 = " + formula);
            String result = grid.getCell(new TestLocation(0, 0)).abbreviatedCellText();
            assertEquals(formula, "7         ", result);
        }

        @Test
        public void testComplexPrecedence()
        {
            String formula = "( 1 - 3.0 / 5 + 7 / 2 - 4 * -18.5 + 1 )";
            grid.processCommand("L20 = " + formula);
            String result = grid.getCell(new TestLocation(19, 11)).abbreviatedCellText();
            assertEquals(formula, 78.9, Double.parseDouble(result), 1e-6);
        }

        @Test
        public void testReferencePrecedence()
        {
            String formulaA1 = "( 1 - 3 / -2 )";
            String formulaA2 = "( 4 * A1 / 2.5 - 3 / A1 )";
            String formulaA3 = "( A2 - A1 * 1.2 )";
            grid.processCommand("A1 = " + formulaA1);
            grid.processCommand("A2 = " + formulaA2);
            grid.processCommand("A3 = " + formulaA3);
            String result = grid.getCell(new TestLocation(2, 0)).abbreviatedCellText();
            assertEquals("formula with references and precedence", -0.2, Double.parseDouble(result), 1e-6);
        }

    }


    public static class C_ExtraCreditHeterogeneousSorting
    {
        // Tests for operation order extra credit
        Grid grid;

        @Before
        public void initializeGrid()
        {
            grid = new Spreadsheet();
        }

        private static String getCellName(int row, int col)
        {
            return "" + (Character.toString((char) ('A' + col))) + (row + 1);
        }

        @Test
        public void testSortaHeterogeneous() 
        {
            grid.processCommand("H14 = \"fiddle\"");
            grid.processCommand("I14 = \"arduous\"");
            // empty
            grid.processCommand("K14 = \"zaza\"");
            grid.processCommand("L14 = -0.02");
            grid.processCommand("H15 = \"zazzaz\"");
            grid.processCommand("I15 = -13.2");
            // empty
            grid.processCommand("K15 = -190.1");
            grid.processCommand("L15 = \"yaya\"");
            grid.processCommand("H16 = 19.2");
            // empty
            // empty
            // empty
            grid.processCommand("L16 = \"azteca\"");
            grid.processCommand("H17 = 1%");
            grid.processCommand("I17 = \"boos\"");
            grid.processCommand("J17 = 2.2");
            // empty
            grid.processCommand("L17 = 60710%");
            // empty
            grid.processCommand("I18 = \"rara\"");
            grid.processCommand("J18 = 88.2");
            grid.processCommand("K18 = \"aardvark\"");
            grid.processCommand("L18 = 101%");
            // empty
            grid.processCommand("I19 = 607.2");
            // empty
            grid.processCommand("K19 = \"boo\"");
            // empty
            
            grid.processCommand("SoRTA H14-L19");

            String[] sortedVals = { "", "", "", "", "", "", "", "", "", "",  
                                    "\"aardvark\"", "\"arduous\"", "\"azteca\"", "\"boo\"", "\"boos\"", "\"fiddle\"", "\"rara\"", "\"yaya\"", "\"zaza\"", "\"zazzaz\"",
                                    "-190.1", "-13.2", "-0.02", "0.01", "1.01", "2.2", "19.2", "88.2", "607.1", "607.2" };
            int iSortedVals = 0;
            
            for (int row = 13; row <= 18; row++)
            {
                for (int col = 7; col <= 11; col++)
                {
                    assertEquals("Inspecting cell " + getCellName(row, col), sortedVals[iSortedVals++], grid.getCell(new TestLocation(row,col)).fullCellText());
                }
            }
        }             
    }
}
