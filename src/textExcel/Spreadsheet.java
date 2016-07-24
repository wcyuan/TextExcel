package textExcel;

// Update this file with your own code.

public class Spreadsheet implements Grid
{
	static int NUM_ROWS = 20;
	static int NUM_COLS = 12;
	static int CELL_WIDTH = 10;
	static int LABEL_WIDTH = 3;
	// PAD must be at least as long as max(CELL_WIDTH, LABEL_WIDTH)
	static String PAD = "          ";
	static String DELIMITER = "|";
	static Cell EMPTY = new EmptyCell();
	
	Cell data[][];
	Spreadsheet() {
		data = new Cell[NUM_ROWS][NUM_COLS];
		clearAll();
	}

	private void setCell(SpreadsheetLocation loc, Cell value) {
		data[loc.getRow()][loc.getCol()] = value;
	}
	private void clearCell(SpreadsheetLocation loc) {
		setCell(loc, EMPTY);
	}
	private void clearAll() {
		for (int row = 0; row < NUM_ROWS; row++) {
			for (int col = 0; col < NUM_COLS; col++) {
				data[row][col] = EMPTY;
			}
		}
	}

	public static String trimleft(String orig)
	{
		int start = 0;
		while (start < orig.length() && Character.isWhitespace(orig.charAt(start))) {
			start++;
		}
		return orig.substring(start);
	}

	public static int firstNonalphnumericCharIdx(String command)
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
		command = trimleft(command);
		int space_idx = firstNonalphnumericCharIdx(command);
		String firstWord = command.substring(0, space_idx);
		String rest = trimleft(command.substring(space_idx));

		if (firstWord.toLowerCase().equals("clear")) {
			// COMMAND: Clear the whole grid
			if (rest.equals("")) {
				clearAll();
				return getGridText();
			}
			// COMMAND: Clear a single cell
			SpreadsheetLocation loc = SpreadsheetLocation.fromCellName(rest);
			clearCell(loc);
			return getGridText();
		}

		SpreadsheetLocation loc = SpreadsheetLocation.fromCellName(firstWord);
		if (loc != null) {
			// COMMAND: Get the contents of a single cell
			if (rest.equals("")) {
				return getCell(loc).fullCellText();
			}
			// COMMAND: Set a cell
			else if (rest.charAt(0) == '=') {
				String value = trimleft(rest.substring(1));
				if (value.startsWith("\"") && value.endsWith("\"")) {
					setCell(loc, new TextCell(value.substring(1, value.length()-1)));
					return getGridText();
				}
				// ERROR! for now until we implement more cell types
			}
		}

		// ERROR! unrecognized command
		return "";
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
		// What should we do if loc is outside of our grid?
		return data[loc.getRow()][loc.getCol()];
	}

	private String makeRow(String label, String[] values) {
		return truncateOrPad(label, LABEL_WIDTH) + DELIMITER + String.join(DELIMITER, values) + DELIMITER;
	}

	public static String truncateOrPad(String value, int length) {
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

}
