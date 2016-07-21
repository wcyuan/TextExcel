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
    
    public static int colIdxToChar(int idx) {
    	return LETTERS.charAt(idx);
    }
    
    public SpreadsheetLocation(String cellName)
    {
        // TODO: Fill this out with your own code
    	int firstNumberIdx = 0;
    	for (firstNumberIdx = 0; firstNumberIdx < cellName.length(); firstNumberIdx++) {
    		char c = cellName.charAt(firstNumberIdx);
    		if (Character.isDigit(c)) {
    			break;
    		} else if (!Character.isAlphabetic(c)) {
    			// non-digit, non-character found
    			// throw an error?  Print a warning?
    		}
    	}
    	if (firstNumberIdx <= 0 || firstNumberIdx >= cellName.length()) {
    		// either there were no characters, or no digits
    		// throw an error?  print a warning?
    	}
    	row = Integer.valueOf(cellName.substring(firstNumberIdx)) - 1;
    	for (int ii = 0; ii < firstNumberIdx; ii++) {
    		col = 26*col + colCharToIdx(cellName.charAt(ii));
    	}
    }
}
