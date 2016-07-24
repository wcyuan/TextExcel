package textExcel;

public abstract class RealCell implements Cell {

	String stringValue;
	public RealCell(String value) {
		super();
		stringValue = value;
	}

	@Override
	public String fullCellText() {
		return stringValue;
	}

	protected abstract double getDoubleValue();
}
