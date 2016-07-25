package textExcel;

public class FormulaCell extends RealCell {

	private Spreadsheet parentSheet;
	private String errorText = null;
	private boolean isComputing = false;

	public FormulaCell(String value, Spreadsheet sheet) {
		super(value);
		parentSheet = sheet;
	}

	@Override
	protected double getDoubleValue() {
		errorText = null;
		double finalValue = 0.0;
		// Detect loops
		if (isComputing) {
			errorText = "Circular Dependency! " + fullCellText();
			return finalValue;
		}
		isComputing = true;

		String[] parts = fullCellText().split(" ");
		int idx = 0;
		int last = parts.length;
		if (parts[idx].equals("(")) {
			idx++;
			// Otherwise it's an error?
		}
		if (parts[last-1].equals(")")) {
			last--;
			// Otherwise it's an error?
		}
		if (parts[idx].equalsIgnoreCase("AVG")) {
			if (last-idx != 2) {
				errorText = "Invalid formula: " + fullCellText();
			}
			if (last > 1) {
				finalValue = getAve(getRangeValues(parts[idx+1]));
			} else {
				finalValue = 0.0;
			}
		}
		else if (parts[idx].equalsIgnoreCase("SUM")) {
			if (last-idx != 2) {
				errorText = "Invalid formula: " + fullCellText();
			}
			if (last > 1) {
				finalValue = getSum(getRangeValues(parts[idx+1]));
			} else {
				finalValue = 0.0;
			}
		}
		else {
			if ((last - idx) % 2 == 0) {
				errorText = "Invalid formula: " + fullCellText();
			}
			double arithValue = getValue(parts[idx]);
			idx++;
			for (; idx < last - 1; idx += 2) {
				double nextValue = getValue(parts[idx+1]);
				switch (parts[idx]) {
				case "+":
					arithValue += nextValue;
					break;
				case "-":
					arithValue -= nextValue;
					break;
				case "/":
					if (nextValue == 0) {
						errorText = "Divide by zero: " + fullCellText();
					} else {
						arithValue /= nextValue;
					}
					break;
				case "*":
					arithValue *= nextValue;
					break;
				default:
					errorText = "Invalid operator: " + parts[idx] + " in " + fullCellText();
					break;
				}
			}
			finalValue = arithValue;
		}

		isComputing = false;
		return finalValue;
	}

	private double getValue(String elt) {
		try {
			return Double.valueOf(elt);
		} catch (NumberFormatException e) {
		}
		SpreadsheetLocation loc = SpreadsheetLocation.fromCellName(elt);
		if (loc == null) {
			errorText = "Invalid location: " + elt;
			return 0.0;
		}
		return getLocationValue(loc);
	}

	private void addErrorText(String text) {
		if (errorText == null) {
			errorText = "";
		} else {
			errorText += ", ";
		}
		errorText += text;
	}

	private double getLocationValue(SpreadsheetLocation loc) {
		Cell cell = parentSheet.getCell(loc);
		if (cell == null) {
			addErrorText("Location out of bounds: " + loc);
			return 0.0;
		}
		try {
			double value = ((RealCell)cell).getDoubleValue();
			if (cell.errorText() != null) {
				addErrorText(cell.errorText());
			}
			return value;
		} catch (ClassCastException e) {
			addErrorText("Cell is not a RealCell: " + loc);
			return 0.0;
		}
	}

	private Double[] getRangeValues(String range) {
		String[] parts = range.split("-");
		if (parts.length != 2) {
			errorText = "Invalid range: " + range;
			return new Double[0];
		}
		SpreadsheetLocation start = SpreadsheetLocation.fromCellName(parts[0]);
		if (start == null) {
			errorText = "Invalid range: " + range;
			return new Double[0];
		}
		SpreadsheetLocation end = SpreadsheetLocation.fromCellName(parts[1]);
		if (end == null) {
			errorText = "Invalid range: " + range;
			return new Double[0];
		}
		int startRow = Math.min(start.getRow(), end.getRow());
		int endRow = Math.max(start.getRow(), end.getRow());
		int startCol = Math.min(start.getCol(), end.getCol());
		int endCol = Math.max(start.getCol(), end.getCol());
		Double[] values = new Double[(endRow - startRow + 1)*(endCol - startCol + 1)];
		for (int idx = 0, row = startRow; row <= endRow; row++) {
			for (int col = startCol; col <= endCol; col++, idx++) {
				values[idx] = getLocationValue(new SpreadsheetLocation(row, col));
			}
		}
		return values;
	}

	private double getSum(Double[] values) {
		double result = 0.0;
		for (double value : values) {
			result += value;
		}
		return result;
	}

	private double getAve(Double[] values) {
		if (values.length == 0) {
			return 0.0;
		} else {
			return getSum(values) / values.length;
		}
	}

	@Override
	public String cellType() {
		return "FormulaCell";
	}


	@Override
	public String abbreviatedCellText() {
		// getDoubleValue will set errorText;
		double value = getDoubleValue();
		if (errorText != null) {
			return errorText;
			//return Spreadsheet.truncateOrPad(errorText);
		}
		return Spreadsheet.truncateOrPad(String.valueOf(value));
	}

	@Override
	public String errorText() {
		return errorText;
	}
}
