package textExcel;

public class PercentCell extends ValueCell {

	public PercentCell(String value) {
		super(value);
	}

	@Override
	public String abbreviatedCellText() {
		return Spreadsheet.truncateOrPad(String.format("%.9s", (int)(doubleValue * 100)) + "%");
	}
}
