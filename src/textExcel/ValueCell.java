package textExcel;

public class ValueCell extends RealCell {
	
	private double doubleValue;
	public ValueCell(String stringValue) {
		super(stringValue);
		this.doubleValue = Double.valueOf(stringValue);
	}

	@Override
	public double getDoubleValue() {
		return doubleValue;
	}

	@Override
	public String cellType() {
		return "ValueCell";
	}
}
