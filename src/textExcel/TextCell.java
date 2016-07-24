package textExcel;

public class TextCell implements Cell {

	// value should begin and end with double quotes (")
	String value;
	
	@Override
	public String abbreviatedCellText() {
		// print it without the quotes
		return Spreadsheet.truncateOrPad(value.substring(1, value.length()-1));
	}

	@Override
	public String fullCellText() {
		return value;
	}

	/**
	 * @param value
	 */
	public TextCell(String value) {
		super();
		this.value = value;
	}
}
