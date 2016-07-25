package textExcel;

public class EmptyCell implements Cell {

	@Override
	public String abbreviatedCellText() {
		// TODO Auto-generated method stub
		return Spreadsheet.truncateOrPad(fullCellText());
	}

	@Override
	public String fullCellText() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String cellType() {
		return "EmptyCell";
	}

	@Override
	public String errorText() {
		return null;
	}

	@Override
	public int compareTo(Object o) {
		if (o instanceof EmptyCell) {
			return 0;
		} else {
			return -1;
		}
	}
}
