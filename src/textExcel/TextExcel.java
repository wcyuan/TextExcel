package textExcel;

import java.io.FileNotFoundException;
import java.util.Scanner;

// Update this file with your own code.

public class TextExcel
{

	public static void main(String[] args)
	{
	    // Add your command loop here
		Spreadsheet sheet = new Spreadsheet();
		Scanner sc = new Scanner(System.in);
        while (true) {
            String input = sc.nextLine();
            if (input.equals("quit")) {
                break;
            } else {
            	System.out.println(sheet.processCommand(input));
            }
        }
        sc.close();
	}
}
