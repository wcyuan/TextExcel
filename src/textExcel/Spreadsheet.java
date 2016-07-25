package textExcel;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

// Update this file with your own code.

public class Spreadsheet implements Grid
{
	private static int NUM_ROWS = 20;
	private static int NUM_COLS = 12;
	private static int CELL_WIDTH = 10;
	private static int LABEL_WIDTH = 3;
	private static String DELIMITER = "|";
	private static Cell EMPTY = new EmptyCell();
	
	private Cell data[][];
	Spreadsheet() {
		data = new Cell[NUM_ROWS][NUM_COLS];
		clearAll();
	}

	private boolean setCell(SpreadsheetLocation loc, Cell value) {
		if (loc == null || value == null) {
			return false;
		}
		if (loc.getRow() < 0 || loc.getRow() >= getRows()) {
			return false;
		}
		if (loc.getCol() < 0 || loc.getCol() >= getCols()) {
			return false;
		}
		data[loc.getRow()][loc.getCol()] = value;
		return true;
	}
	private boolean clearCell(SpreadsheetLocation loc) {
		return setCell(loc, EMPTY);
	}
	private void clearAll() {
		for (int row = 0; row < NUM_ROWS; row++) {
			for (int col = 0; col < NUM_COLS; col++) {
				data[row][col] = EMPTY;
			}
		}
	}

	private static int firstNonalphnumericCharIdx(String command)
	{
		int idx = 0;
		while (idx < command.length() && Character.isLetterOrDigit(command.charAt(idx))) {
			idx++;
		}
		return idx;
	}

	@Override
	public String processCommand(String command)
	{
		command = command.trim();
		if (command.equals("")) {
			return "";
		}
		
		int space_idx = firstNonalphnumericCharIdx(command);
		String firstWord = command.substring(0, space_idx).toLowerCase();
		String rest = command.substring(space_idx).trim();

		if (firstWord.equals("clear")) {
			// COMMAND: Clear the whole grid
			if (rest.equals("")) {
				clearAll();
				return getGridText();
			}
			// COMMAND: Clear a single cell
			return clearCellCommand(rest, command);
		}

		if (firstWord.equals("save")) {
			return saveFileCommand(rest, command);
		}
		if (firstWord.equals("open")) {
			return openFileCommand(rest, command);
		}
		if (firstWord.equals("sorta")) {
			return sortCommand(true, rest, command);
		}
		if (firstWord.equals("sortd")) {
			return sortCommand(false, rest, command);
		}

		SpreadsheetLocation loc = SpreadsheetLocation.fromCellName(firstWord);
		if (loc != null) {
			// COMMAND: Get the contents of a single cell
			if (rest.equals("")) {
				return getCellCommand(loc, command);
			}
			// COMMAND: Set a cell
			else if (rest.charAt(0) == '=') {
				return setCellCommand(loc, rest.substring(1).trim(), command);
			}
		} else {
			return "ERROR: Invalid Cell Location: " + command;
		}

		return "ERROR: Unrecognized command: " + command;
	}

	private String sortCommand(boolean isAscending, String range, String command) {
		SpreadsheetLocation[] locs = getRange(range);
		if (locs == null) {
			return "ERROR: Invalid range: " + command;
		}
		
		return getGridText();
	}

	private String getCellCommand(SpreadsheetLocation loc, String command) {
		if (loc.getRow() < 0 || loc.getRow() >= getRows() ||
			loc.getCol() < 0 || loc.getCol() >= getCols()) {
			return "ERROR: Location Out Of Bounds: " + loc + " " + command;
		}
		return getCell(loc).fullCellText();
	}

	private String clearCellCommand(String cellName, String command) {
		SpreadsheetLocation loc = SpreadsheetLocation.fromCellName(cellName);
		if (loc == null) {
			return "ERROR: Invalid Cell: " + command;
		}
		if (!clearCell(loc)) {
			return "ERROR: Cell Out Of Bounds: " + command;
		}
		return getGridText();
	}

	private String saveFileCommand(String filename, String command) {
		try (PrintWriter writer = new PrintWriter(filename)) {
			for (int row = 0; row < NUM_ROWS; row++) {
				for (int col = 0; col < NUM_COLS; col++) {
					// Cell should really have an isEmpty method
					Cell cell = data[row][col];
					SpreadsheetLocation loc = new SpreadsheetLocation(row, col);
					if (!cell.cellType().equals("EmptyCell")) {
						// Instead of cellType, could use
						// cell.getClass().getName(), but that would include 
						// the package name
						// (e.g. textExcel.FormulaCell instead of just FormulaCell)
						writer.println(loc + "," + cell.cellType() + "," + cell.fullCellText());
					}
				}
			}
			writer.close();
			return getGridText();
		} catch (FileNotFoundException e) {
			return "ERROR: Invalid File: " + e;
		}
	}

	private Cell makeCell(String cellType, String value) {
		// If, instead of storing cellType, we stored the
		// name of the class, we could construct an object
		// with something like
		// return (Cell)(Class.forName(cellType).getConstructor(String.class).newInstance(value));
		// But we'd have to handle the fact that FormulaCell's constructor
		// also takes a link back to the Spreadsheet.
		if (cellType.equals("TextCell")) {
			return new TextCell(value);
		}
		else if (cellType.equals("ValueCell")) {
			return new ValueCell(value);
		}
		else if (cellType.equals("PercentCell")) {
			return new PercentCell(value);
		}
		else if (cellType.equals("FormulaCell")) {
			return new FormulaCell(value, this);
		}
		return null;
	}

	private String openFileCommand(String filename, String command) {
		try(BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String line;
			String errors = "";
		    while ((line = br.readLine()) != null) {
		    	String parts[] = line.split(",");
		    	if (parts.length != 3) {
					errors += "ERROR: Invalid File Line: " + line + "\n";
					continue;
		    	}
				SpreadsheetLocation loc = SpreadsheetLocation.fromCellName(parts[0]);
				if (loc == null) {
					errors += "ERROR: Bad Location: " + line + " " + loc + "\n";
					continue;
				}
				
				Cell cell = makeCell(parts[1], parts[2]);
				if (cell == null) {
					errors += "ERROR: Bad Cell Type: " + line + "\n";
					continue;
				}
				if (!setCell(loc, cell)) {
					errors += "ERROR: Location Out Of Bounds: " + line + " " + loc + "\n";
					continue;
				}
		    }
		    if (errors.equals("")) {
		    	return getGridText();
		    } else {
		    	return errors;
		    }
		} catch (IOException e) {
			// ERROR! Invalid file
			return "ERROR: Invalid File: " + e;
		}
	}

	private String setCellCommand(SpreadsheetLocation loc, String value, String command) {
		Cell cell;
		if (value.startsWith("\"") && value.endsWith("\"")) {
			cell = new TextCell(value);
		}
		else if (value.endsWith("%")) {
			try {
				double dval = Double.valueOf(value.substring(0, value.length()-1));
				cell = new PercentCell(String.valueOf(dval / 100));
			} catch (NumberFormatException e) {
				return "ERROR: Invalid Number: " + e; 
			}
		}
		else if (value.startsWith("(") && value.endsWith(")")) {
			cell = new FormulaCell(value, this);
		}
		else {
			try {
				// Try to convert to a Double.
				Double.valueOf(value);
				cell = new ValueCell(value);
			} catch (NumberFormatException e) {
				return "ERROR: Invalid Number: " + e;
			}
		}
		if (!setCell(loc, cell)) {
			return "ERROR: Cell Out Of Bounds: " + command;
		}
		return getGridText();
	}

	@Override
	public int getRows()
	{
		return data.length;
	}

	@Override
	public int getCols()
	{
		if (data.length <= 0) {
			return 0;
		}
		return data[0].length;
	}

	@Override
	public Cell getCell(Location loc)
	{
		if (loc.getRow() < 0 || loc.getRow() >= getRows() ||
			loc.getCol() < 0 || loc.getCol() >= getCols()) {
			return null;
		}
		// What should we do if loc is outside of our grid?
		return data[loc.getRow()][loc.getCol()];
	}

	private String makeRow(String label, String[] values) {
		return truncateOrPad(label, LABEL_WIDTH) + DELIMITER + String.join(DELIMITER, values) + DELIMITER;
	}

	private static String truncateOrPad(String value, int length) {
		String format = "%-" + length + "." + length + "s";
		return String.format(format, value);
	}

	public static String truncateOrPad(String value) {
		return truncateOrPad(value, CELL_WIDTH);
	}

	@Override
	public String getGridText()
	{
		String output = "";
		String[] colHeaders = new String[getCols()];
		for (int col = 0; col < getCols(); col++) {
			colHeaders[col] = truncateOrPad(String.valueOf(SpreadsheetLocation.colIdxToChar(col)));
		}
		output += makeRow("", colHeaders) + "\n";
		for (int row = 0; row < getRows(); row++) {
			String[] values = new String[getCols()];
			for (int col = 0; col < getCols(); col++) {
				values[col] = truncateOrPad(data[row][col].abbreviatedCellText());
			}
			output += makeRow(String.valueOf(row+1), values) + "\n";
		}
		return output;
	}

	public SpreadsheetLocation[] getRange(String range) {
		String[] parts = range.split("-");
		if (parts.length != 2) {
			return null;
		}
		SpreadsheetLocation start = SpreadsheetLocation.fromCellName(parts[0]);
		if (start == null) {
			return null;
		}
		SpreadsheetLocation end = SpreadsheetLocation.fromCellName(parts[1]);
		if (end == null) {
			return null;
		}
		int startRow = Math.min(start.getRow(), end.getRow());
		int endRow = Math.max(start.getRow(), end.getRow());
		int startCol = Math.min(start.getCol(), end.getCol());
		int endCol = Math.max(start.getCol(), end.getCol());
		SpreadsheetLocation[] locs = new SpreadsheetLocation[(endRow - startRow + 1)*(endCol - startCol + 1)];
		for (int idx = 0, row = startRow; row <= endRow; row++) {
			for (int col = startCol; col <= endCol; col++, idx++) {
				locs[idx] = new SpreadsheetLocation(row, col);
			}
		}
		return locs;
	}
}
