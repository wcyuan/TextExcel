# TestExcel

Notes: the spec is unclear on a few points that are required by the tests.

 - fullCellText:
   - What is returned when you inspect a cell directly.
   - This is the original string that was used to create the cell,
     unevaluated.
   - For example, strings must have double quotes around them
   - If you set a cell to a number without a trailing ".0",
     fullCellText should return it without a trailing ".0"
   - fullCellText will also be used the spreadsheet is saved/serialized
 - abbreviatedCellText.
   - What is returned when the cell is seen in the grid
   - That means it is evaluated
   - So strings do not have double quotes
   - Even if a cell has an integer value, it should be printed with ".0"
     (Though the extra credit tests seem to be inconsistent about this)
   - The string returned here must also be exactly 10 characters wide

I disagree about some of the design
 - I like the fact that the Cell has a method for the unevaluated text and
   a method for the evaluated text, but I don't think it should worry about
   formatting, like making things 10 characters wide, that should be handled
   by the Spreadsheet class.
 - The tests should allow double values to appear either with or without the
   ".0"
 - Cells shouldn't be comparable to Object, they should just be comparable
   to other Cells.  And it's a bit weird that all Text cells come before
   Real cells, why not just compare the output as text?
 - All cells share the characteristic that they save the original text used
   to create the cell, and that's what is returned by fullCellText.  So I
   created a common abstract base class for all cells (RootCell), even though
   that's not what the guide suggests.
 - The solution also uses instanceof for serialization and deserialization,
   I prefer adding a cell type to Cells.  
 - The project avoids dealing with the nuances of floating point arithmetic,
   which is probably good.

