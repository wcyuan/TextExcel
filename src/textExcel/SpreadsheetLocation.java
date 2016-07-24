package textExcel;

//Update this file with your own code.

public class SpreadsheetLocation implements Location
{
	int row;
	int col;

	static String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	
    @Override
    public int getRow()
    {
        // TODO Auto-generated method stub
        return row;
    }

    @Override
    public int getCol()
    {
        // TODO Auto-generated method stub
        return col;
    }
    
    public static int colCharToIdx(char c) {
    	return LETTERS.indexOf(Character.toUpperCase(c));
    }
    
    public static char colIdxToChar(int idx) {
    	return LETTERS.charAt(idx);
    }

    @Override
    public String toString() {
    	return String.valueOf(colIdxToChar(col)) + (row+1);
    }

    public boolean parseCellName(String cellName) {
    	row = 0;
    	col = 0;
    	int firstNumberIdx = 0;
    	for (firstNumberIdx = 0; firstNumberIdx < cellName.length(); firstNumberIdx++) {
    		char c = cellName.charAt(firstNumberIdx);
    		if (Character.isDigit(c)) {
    			break;
    		} else if (!Character.isLetter(c)) {
    			// non-digit, non-character found
    			// throw an error?  Print a warning?
    			return false;
    		}
    	}
    	if (firstNumberIdx <= 0 || firstNumberIdx >= cellName.length()) {
    		// either there were no characters, or no digits
    		// throw an error?  print a warning?
    		return false;
    	}
    	try {
    		row = Integer.valueOf(cellName.substring(firstNumberIdx)) - 1;
    	} catch (NumberFormatException e) {
    		return false;
    	}
    	for (int ii = 0; ii < firstNumberIdx; ii++) {
    		col = 26*col + colCharToIdx(cellName.charAt(ii));
    	}
    	return true;
    }

    public static SpreadsheetLocation fromCellName(String cellName) {
    	SpreadsheetLocation loc = new SpreadsheetLocation(0, 0);
    	if (loc.parseCellName(cellName)) {
    		return loc;
    	} else {
    		return null;
    	}
    }

    public SpreadsheetLocation(String cellName)
    {
        // TODO: Fill this out with your own code
    	parseCellName(cellName);
    }

    public SpreadsheetLocation(int row, int col)
    {
        // TODO: Fill this out with your own code
    	this.row = row;
    	this.col = col;
    }
}
