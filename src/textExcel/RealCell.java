package textExcel;

public abstract class RealCell extends RootCell {

	public RealCell(String value) {
		super(value);
	}

	protected abstract double getDoubleValue();
}
