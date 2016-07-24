package textExcel;

public abstract class RealCell extends RootCell {

	public RealCell(String value) {
		super(value);
	}

	@Override
	public String abbreviatedCellText() {
		return Spreadsheet.truncateOrPad(String.valueOf(getDoubleValue()));
	}

	protected abstract double getDoubleValue();
}
