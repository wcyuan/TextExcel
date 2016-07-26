package textExcel;

public class EmptyCell extends RootCell {

	public EmptyCell() {
		super("");
	}

	@Override
	public String abbreviatedCellText() {
		return Spreadsheet.truncateOrPad(fullCellText());
	}

	@Override
	public String cellType() {
		return "EmptyCell";
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
