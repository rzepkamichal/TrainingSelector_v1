package app;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class PDF {
	
	private static BaseFont helvetica = null;
	private static BaseFont helveticaBold = null;
	
	public static void generateReport(String trainingName, LocalDate from, LocalDate to) {
		
		//converting dates to nice showable strings
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		String fromString = from.format(formatter);
		String toString = to.format(formatter);
		
		//converting to date type
		Date fromDate = Date.from(from.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date toDate = Date.from(to.atStartOfDay(ZoneId.systemDefault()).plusDays(1).minusSeconds(1).toInstant());
		
		//new document
		Document document = new Document();
		
		//where to save document
		try {
			PdfWriter.getInstance(document, 
					new FileOutputStream(
							"report_" + trainingName + "_from_" + from + "_to_" + to + ".pdf"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
		//open document
		document.open();
		
		//creating a few fonts
		try {
			helvetica = BaseFont.createFont(
					BaseFont.HELVETICA, BaseFont.CP1250, BaseFont.EMBEDDED);
			helveticaBold = BaseFont.createFont(
					BaseFont.HELVETICA_BOLD, BaseFont.CP1250, BaseFont.EMBEDDED);
		} catch (DocumentException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Font helveticaNormal = new Font(helvetica, 20);
		Font helveticaBig = new Font(helveticaBold, 25);
		Font helveticaNormalBold = new Font(helveticaBold, 20);
		
		//first paragraph
		Paragraph par1 = new Paragraph("Ilo�� wej�� na treningi o nazwie", helveticaNormal); 
		par1.setAlignment(Element.ALIGN_CENTER);
		
		//second paragraph
		Paragraph name = new Paragraph(trainingName, helveticaBig); 
		name.setAlignment(Element.ALIGN_CENTER);
		
		//third paragraph
		Paragraph par2 = new Paragraph("w dniach od ", helveticaNormal);
		par2.add(new Chunk(fromString, helveticaNormalBold));
		par2.add(new Chunk(" do ", helveticaNormal));
		par2.add(new Chunk(toString, helveticaNormalBold));
		par2.setAlignment(Element.ALIGN_CENTER);
		
		//just for a little gap between paragraphs and table
		Paragraph par3 = new Paragraph(" ", new Font(helvetica, 5));
		
		//crating table
		PdfPTable table = new PdfPTable(2);
		//adding header cells
		addTableHeader(table);
		//adding cells with data
		addRows(table, trainingName, fromDate, toDate);
		  
		//adding everything to document
		try {
			document.add(par1);
			document.add(name);
			document.add(par2);
			document.add(par3);
			document.add(table);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
		//close document
		document.close();
		
	}
	
	private static int allEntrances;
	private static void addRows(PdfPTable table, String trainingName, Date from, Date to) {
		
		//reading list of trainings
		List<Training> trainings_ = Parser.readTrainings();
		
		//saving appropriate trainings to "trainingsToShow" list
		List<Training> trainingsToShow = new ArrayList<Training>();
		trainings_.forEach(training -> {
			
			//check if the date of training is in given interval
			boolean rightDate = (training.getDate().equals(from)
					|| training.getDate().equals(to)
					||(training.getDate().after(from) 
							&& training.getDate().before(to)));
			
			//check if the name of training matches the given name
			boolean rightName = Objects.equals(training.getName(), trainingName);
			
			//if everything matches - adding a training to list
			if(rightDate && rightName) {
				trainingsToShow.add(training);
			}
		});
		
		//setting sum of entrances to 0;
		allEntrances = 0;
		
		//adding cells with data about each training
		trainingsToShow.forEach(training -> {
			
			//cell with date of training - first column
			Paragraph date = new Paragraph(training.dateToDisplay(), new Font(helvetica, 14));
			PdfPCell firstCol = new PdfPCell(date);
			firstCol.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(firstCol);
			
			//cell with nr of entrances - second column
			Paragraph entrances = new Paragraph(Integer.toString(training.getEntrances()), new Font(helvetica, 14));
			PdfPCell secondCol = new PdfPCell(entrances);
			secondCol.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(secondCol);
			
			//calculating sum of entrances
			allEntrances += training.getEntrances();
		});
		
		//add cells with sum of entrances
		Paragraph all = new Paragraph("��cznie", new Font(helveticaBold, 14));
		PdfPCell allLabel = new PdfPCell(all);
		allLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(allLabel);
		Paragraph sumEntrances = new Paragraph(Integer.toString(allEntrances), new Font(helveticaBold, 14));
		PdfPCell allEntrancesLabel = new PdfPCell(sumEntrances);
		allEntrancesLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(allEntrancesLabel);
	}

	private static void addTableHeader(PdfPTable table) {
		//adding headers with some formatting
		Stream.of("Data", "Ilo�� wej��")
	      .forEach(columnTitle -> {
	    	  Paragraph p = new Paragraph(columnTitle, new Font(helvetica, 15));
	    	  PdfPCell header = new PdfPCell(p);
	    	  header.setBackgroundColor(BaseColor.LIGHT_GRAY);
	    	  header.setBorderWidth((float) 1.3);
	    	  header.setMinimumHeight(25);
	    	  header.setHorizontalAlignment(Element.ALIGN_CENTER);
	    	  table.addCell(header);
	    });
	}
}
