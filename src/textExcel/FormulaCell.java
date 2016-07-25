package textExcel;

public class FormulaCell extends RealCell {

	Spreadsheet parentSheet;
	String errorText = null;
	public FormulaCell(String value, Spreadsheet sheet) {
		super(value);
		parentSheet = sheet;
	}

	@Override
	protected double getDoubleValue() {
		errorText = null;
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
			if (last != 2) {
				errorText = "Invalid formula: " + fullCellText();
			}
			if (last > 1) {
				return getAve(getRange(parts[idx+1]));
			}
			return 0.0;
		}
		else if (parts[idx].equalsIgnoreCase("SUM")) {
			if (last != 2) {
				errorText = "Invalid formula: " + fullCellText();
			}
			if (last > 1) {
				return getSum(getRange(parts[idx+1]));
			}
			return 0.0;
		}
		else {
			if (last % 2 == 0) {
				errorText = "Invalid formula: " + fullCellText();
			}
			double value = getValue(parts[idx]);
			idx++;
			for (; idx < last - 1; idx += 2) {
				double nextValue = getValue(parts[idx+1]);
				switch (parts[idx]) {
				case "+":
					value += nextValue;
					break;
				case "-":
					value -= nextValue;
					break;
				case "/":
					if (nextValue == 0) {
						errorText = "Divide by zero: " + fullCellText();
					} else {
						value /= nextValue;
					}
					break;
				case "*":
					value *= nextValue;
					break;
				default:
					errorText = "Invalid operator: " + parts[idx] + " in " + fullCellText();
					break;
				}
			}
			return value;
		}
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
		Cell cell = parentSheet.getCell(loc);
		if (cell == null) {
			errorText = "Location out of bounds: " + elt;
			return 0.0;
		}
		// TODO: detect cycles...
		return ((RealCell)cell).getDoubleValue();
	}

	private Double[] getRange(String range) {
		String[] parts = range.split("-");
		if (parts.length != 2) {
			errorText = "";
		}
		Double[] values = new Double[0];
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
}
