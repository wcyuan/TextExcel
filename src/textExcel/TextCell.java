package textExcel;

public class TextCell extends RootCell {

	@Override
	public String abbreviatedCellText() {
		// print it without the quotes
		return Spreadsheet.truncateOrPad(fullCellText().substring(1, fullCellText().length()-1));
	}

	/**
	 * @param value
	 */
	// value should begin and end with double quotes (")
	public TextCell(String value) {
		super(value);
	}

	@Override
	public String cellType() {
		return "TextCell";
	}
}
