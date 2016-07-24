package textExcel;

public class ValueCell extends RealCell {
	
	double doubleValue;
	public ValueCell(String stringValue) {
		super(stringValue);
		this.doubleValue = Double.valueOf(stringValue);
	}

	@Override
	public String abbreviatedCellText() {
		return Spreadsheet.truncateOrPad(String.valueOf(doubleValue));
	}

	@Override
	public double getDoubleValue() {
		return doubleValue;
	}
}
