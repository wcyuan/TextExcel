package textExcel;

public abstract class RootCell implements Cell {

	private String stringValue;
	public RootCell(String value) {
		super();
		stringValue = value;
	}

	@Override
	public String fullCellText() {
		return stringValue;
	}

	@Override
	public String errorText() {
		return null;
	}
}
