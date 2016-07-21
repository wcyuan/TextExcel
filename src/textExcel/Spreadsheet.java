package textExcel;

// Update this file with your own code.

public class Spreadsheet implements Grid
{
	static int NUM_ROWS = 20;
	static int NUM_COLS = 12;
	static int CELL_WIDTH = 10;
	static String DELIMITER = "|";
	
	Cell data[][];
	Spreadsheet() {
		data = new Cell[NUM_ROWS][NUM_COLS];
		for (int row = 0; row < NUM_ROWS; row++) {
			//data[row] = new Cell[NUM_COLS];
			for (int col = 0; col < NUM_COLS; col++) {
				data[row][col] = new EmptyCell();
			}
		}
	}
	
	@Override
	public String processCommand(String command)
	{
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public int getRows()
	{
		// TODO Auto-generated method stub
		return data.length;
	}

	@Override
	public int getCols()
	{
		// TODO Auto-generated method stub
		if (data.length <= 0) {
			return 0;
		}
		return data[0].length;
	}

	@Override
	public Cell getCell(Location loc)
	{
		// TODO Auto-generated method stub
		// What should we do if loc is outside of our grid?
		return data[loc.getRow()][loc.getCol()];
	}

	private String makeRow(String header, String[] values) {
		String[] newValues = new String[values.length+2];
		newValues[0] = header.substring(0, 3);
		for (int ii = 0; ii < values.length; ii++) {
			newValues[ii+1] = values[ii].substring(0, CELL_WIDTH);
		}
		newValues[values.length+1] = "";
		return String.join(DELIMITER, newValues);
	}
	
	@Override
	public String getGridText()
	{
		// TODO Auto-generated method stub
		String output = "";
		String[] colHeaders = new String[getCols()];
		for (int col = 0; col < getCols(); col++) {
			colHeaders[col] = String.valueOf(SpreadsheetLocation.colIdxToChar(col));
		}
		output += makeRow("", colHeaders);
		for (int row = 0; row < getRows(); row++) {
			String[] values = new String[getCols()];
			for (int col = 0; col < getCols(); col++) {
				values[col] = data[row][col].abbreviatedCellText();
			}
			output += makeRow(String.valueOf(row), values);
		}
		return output;
	}

}
