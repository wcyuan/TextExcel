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
	
	@Override
	public int compareTo(Object o) {
		if (o instanceof RealCell) {
			RealCell cell = (RealCell)o;
			double thisValue = getDoubleValue();
			double thatValue = cell.getDoubleValue();
			if (thisValue < thatValue) {
				return -1;
			} else if (thisValue > thatValue) {
				return 1;
			} else {
				return 0;
			}
		} else if (o instanceof EmptyCell) {
			return 1;
		} else if (o instanceof TextCell) {
			return 1;
		} else {
			return -1;
		}
	}
}
