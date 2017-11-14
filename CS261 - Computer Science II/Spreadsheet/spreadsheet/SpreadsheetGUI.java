package spreadsheet;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

/** 
 * The SpreadsheetGUI class displays spreadsheet data graphically, and
 * allows the user to edit the data in the cells.  SpreadsheetGUI does
 * not store any data, or evaluate expressions -- it is simply a display
 * and interface to your spreadsheet object.  Your spreadsheet must
 * therefore implement the Sheetable interface, which provides methods
 * for retrieving the values and formulas in specific cells.
 * 
 * You do NOT need to know how any of the code in this class works.
 * 
 * @author Brad Richards
 */
public class SpreadsheetGUI extends JPanel implements MouseListener, Runnable {
	protected Sheetable data;      // The spreadsheet we're displaying
	// The following three constants have to do with table layout
	private static final int ROW_HEADER_WIDTH = 30;
	private static final int COL_WIDTH = 80;
	private static final int ROW_HEIGHT = 16;
	// We use a JTable to present the data.  The JTable assumes that the
	// "database" whose data is being displayed implements a variety of
	// methods (getColumnCount, getRowCount, getColumnClass, etc).  To
	// shield the user of SpreadsheetGUI from these details, we define
	// the MyTableModel class which provides these methods, and declare
	// model, an instance of it.  
	private MyTableModel model;  // Implements access methods for JTable
	JTable table = null;         // The table itself
	protected int rows;
	protected int cols;
	private static final long serialVersionUID = -8968585244087732722L;

	/**
	 * The constructor takes a Sheetable object whose data is to be
	 * displayed, and the number of rows and cols to display.
	 * @param data The Sheetable object to display
	 * @param rows The number of rows in the spreadsheet
	 * @param cols The number of columns in the spreadsheet
	 */
	public SpreadsheetGUI(Sheetable data, int rows, int cols) {
		super(new GridLayout(1,0));
		this.data = data;
		this.rows = rows;
		this.cols = cols;
	}

	public void run() {
		// Build the table model for our JTable
		model = new MyTableModel(data, rows, cols);
		table = new JTable(model);
		// Set up display parameters
		table.setPreferredScrollableViewportSize(
				new Dimension(COL_WIDTH*cols, ROW_HEIGHT*rows));
		TableColumn rowHeader = table.getColumnModel().getColumn(0);
		rowHeader.setMinWidth(ROW_HEADER_WIDTH);
		rowHeader.setMaxWidth(ROW_HEADER_WIDTH);
		table.setGridColor(java.awt.Color.BLACK);
		table.setShowGrid(true);

		// Allow user to select just a single cell, and register a
		// listener so we can catch mouse clicks
		table.setCellSelectionEnabled(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.addMouseListener(this);

		//Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(table);

		//Add the scroll pane to this panel.
		add(scrollPane);

		//Create and set up the window.
		JFrame frame = new JFrame("Spreadsheet");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setOpaque(true);   //content panes must be opaque
		frame.setContentPane(this);

		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * Calling this method causes the JTable display to be
	 * updated.
	 */
	public void refreshDisplay() {
		model.fireTableChanged(new TableModelEvent(model));
	}

	/**
	 * The object being displayed by JTable must support the methods
	 * in TableModel.  The user of the SpreadsheetGUI doesn't need to
	 * know about many of these, so we define MyTableModel to shield
	 * them from these details.  MyTableModel implements methods like
	 * getColumnClass, but gets the actual data it needs from the user's
	 * Sheetable object via getValue.
	 */
	class MyTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		private int rows;
		private int cols;
		private Sheetable data;

		public MyTableModel(Sheetable data, int rows, int cols) {
			this.data = data;
			this.rows = rows;
			this.cols = cols;
		}      

		public int getColumnCount() {
			return cols+1;
		}

		public int getRowCount() {
			return rows;
		}

		public String getColumnName(int col) {
			if (col == 0)
				return "";
			else 
				return "["+(char)('A'+col-1)+"]";
		}

		public Object getValueAt(int row, int col) {
			if (col == 0) 
				return "["+(row+1)+"]";
			else if (data.isEmpty(row, col-1))
				return null;
			else
				return data.getValue(row, col-1);
		}

		/*
		 * JTable uses this method to determine the default renderer/
		 * editor for each cell.
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Class getColumnClass(int c) {
			if (c==0)
				return String.class;
			else
				return Double.class;
		}
	}

	/*
	 * We need these methods so we can claim to implement the MouseListener
	 * interface.  We really only care about mouseClicked events.  We ask
	 * the event for its coordinates, figure out which cell it corresponds
	 * to, and present its formula for modification.
	 */
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
	public void mouseClicked(MouseEvent e) {				
		int col = table.columnAtPoint(e.getPoint())-1;
		int row = table.rowAtPoint(e.getPoint());
		if (col >= 0) {
			String oldFormula = "";
			if (!data.isEmpty(row, col))
				oldFormula = data.getFormula(row, col);
			String newFormula = JOptionPane.showInputDialog("Formula: ", oldFormula);
			if (newFormula != null)
			{
				data.setFormula(newFormula, row, col);
				refreshDisplay();
			}
		}
	}
}
