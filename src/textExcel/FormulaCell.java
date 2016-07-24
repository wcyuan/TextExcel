package textExcel;

public class FormulaCell extends RealCell {

	public FormulaCell(String value) {
		super(value);
	}

	@Override
	public String abbreviatedCellText() {
		// not yet implemented
		return Spreadsheet.truncateOrPad(fullCellText());
	}

	@Override
	protected double getDoubleValue() {
		// not yet implemented
		return 0;
	}

	@Override
	public String cellType() {
		return "FormulaCell";
	}
}
