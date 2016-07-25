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

	@Override
	public int compareTo(Object o) {
		if (o instanceof TextCell) {
			TextCell cell = (TextCell)o;
			return fullCellText().compareTo(cell.fullCellText());
		} else if (o instanceof EmptyCell) {
			return 1;
		} else if (o instanceof RealCell) {
			return -1;
		} else {
			return -1;
		}
	}
}
