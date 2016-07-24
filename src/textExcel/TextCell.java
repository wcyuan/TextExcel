package textExcel;

public class TextCell implements Cell {

	String value;
	
	@Override
	public String abbreviatedCellText() {
		// TODO Auto-generated method stub
		return Spreadsheet.truncateOrPad(value);
	}

	@Override
	public String fullCellText() {
		// TODO Auto-generated method stub
		return '"' + value + '"';
	}

	/**
	 * @param value
	 */
	public TextCell(String value) {
		super();
		this.value = value;
	}
}
