
/* * 
 * This is a menu driven system that will allow users to define a data structure representing a collection of 
 * records that can be displayed both by means of a dialog that can be scrolled through and by means of a table
 * to give an overall view of the collection contents.
 * 
 * */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;

public class EmployeeDetails extends JFrame implements ActionListener, ItemListener, DocumentListener, WindowListener {
	// decimal format for inactive currency text field
	private static final DecimalFormat format = new DecimalFormat("\u20ac ###,###,##0.00");
	// decimal format for active currency text field
	private static final DecimalFormat fieldFormat = new DecimalFormat("0.00");
	/* look at this variable name */
	// hold object start position in file
	private long objectStartPosition = 0;
	private RandomFile application = new RandomFile();
	private FileNameExtensionFilter datfilter = new FileNameExtensionFilter("dat files (*.dat)", "dat");
	private File file;
	private boolean change = false;
	boolean changesMade = false;
	private JMenuItem open, save, saveAs, create, modify, delete, firstItem, lastItem, nextItem, prevItem, searchById,
			searchBySurname, listAll, closeApp;
	private JButton first, previous, next, last, add, edit, deleteButton, displayAll, searchId, searchSurname,
			saveChange, cancelChange;
	private JComboBox<String> genderCombo, departmentCombo, fullTimeCombo;
	private JTextField idField, ppsField, surnameField, firstNameField, salaryField;
	private static EmployeeDetails frame = new EmployeeDetails();
	// for labels, text fields and combo boxes
	Font font1 = new Font("SansSerif", Font.BOLD, 16);
	// holds automatically generated file name
	String generatedFileName;
	Employee currentEmployee;
	JTextField searchByIdField, searchBySurnameField;
	// gender combo box values
	String[] gender = { "", "M", "F" };
	// department combo box values
	String[] department = { "", "Administration", "Production", "Transport", "Management" };
	// full time combo box values
	String[] fullTime = { "", "Yes", "No" };

	private JMenuBar menuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu, recordMenu, navigateMenu, closeMenu;

		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		recordMenu = new JMenu("Records");
		recordMenu.setMnemonic(KeyEvent.VK_R);
		navigateMenu = new JMenu("Navigate");
		navigateMenu.setMnemonic(KeyEvent.VK_N);
		closeMenu = new JMenu("Exit");
		closeMenu.setMnemonic(KeyEvent.VK_E);

		menuBar.add(fileMenu);
		menuBar.add(recordMenu);
		menuBar.add(navigateMenu);
		menuBar.add(closeMenu);

		fileMenu.add(open = new JMenuItem("Open")).addActionListener(this);
		open.setMnemonic(KeyEvent.VK_O);
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		fileMenu.add(save = new JMenuItem("Save")).addActionListener(this);
		save.setMnemonic(KeyEvent.VK_S);
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		fileMenu.add(saveAs = new JMenuItem("Save As")).addActionListener(this);
		saveAs.setMnemonic(KeyEvent.VK_F2);
		saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, ActionEvent.CTRL_MASK));

		recordMenu.add(create = new JMenuItem("Create new Record")).addActionListener(this);
		create.setMnemonic(KeyEvent.VK_N);
		create.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		recordMenu.add(modify = new JMenuItem("Modify Record")).addActionListener(this);
		modify.setMnemonic(KeyEvent.VK_E);
		modify.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		recordMenu.add(delete = new JMenuItem("Delete Record")).addActionListener(this);

		navigateMenu.add(firstItem = new JMenuItem("First"));
		firstItem.addActionListener(this);
		navigateMenu.add(prevItem = new JMenuItem("Previous"));
		prevItem.addActionListener(this);
		navigateMenu.add(nextItem = new JMenuItem("Next"));
		nextItem.addActionListener(this);
		navigateMenu.add(lastItem = new JMenuItem("Last"));
		lastItem.addActionListener(this);
		navigateMenu.addSeparator();
		navigateMenu.add(searchById = new JMenuItem("Search by ID")).addActionListener(this);
		navigateMenu.add(searchBySurname = new JMenuItem("Search by Surname")).addActionListener(this);
		navigateMenu.add(listAll = new JMenuItem("List all Records")).addActionListener(this);

		closeMenu.add(closeApp = new JMenuItem("Close")).addActionListener(this);
		closeApp.setMnemonic(KeyEvent.VK_F4);
		closeApp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.CTRL_MASK));

		return menuBar;
	}

	private JPanel searchPanel() {
		JPanel searchPanel = new JPanel(new MigLayout());

		searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));
		searchPanel.add(new JLabel("Search by ID:"), MIG_layout.mig_design2);
		searchPanel.add(searchByIdField = new JTextField(20), "width 200:200:200," + MIG_layout.mig_design2);
		searchByIdField.addActionListener(this);
		searchByIdField.setDocument(new JTextFieldLimit(20));
		searchPanel.add(
				searchId = new JButton(new ImageIcon(
						new ImageIcon("search.png").getImage().getScaledInstance(35, 20, java.awt.Image.SCALE_SMOOTH))),
				"width 35:35:35, height 20:20:20," + MIG_layout.mig_design3);
		searchId.addActionListener(this);
		searchId.setToolTipText("Search Employee By ID");

		searchPanel.add(new JLabel("Search by Surname:"), MIG_layout.mig_design2);
		searchPanel.add(searchBySurnameField = new JTextField(20), "width 200:200:200," + MIG_layout.mig_design2);
		searchBySurnameField.addActionListener(this);
		searchBySurnameField.setDocument(new JTextFieldLimit(20));
		searchPanel.add(
				searchSurname = new JButton(new ImageIcon(
						new ImageIcon("search.png").getImage().getScaledInstance(35, 20, java.awt.Image.SCALE_SMOOTH))),
				"width 35:35:35, height 20:20:20," + MIG_layout.mig_design3);
		searchSurname.addActionListener(this);
		searchSurname.setToolTipText("Search Employee By Surname");

		return searchPanel;
	}

	private JPanel navigPanel() {
		JPanel navigPanel = new JPanel();

		navigPanel.setBorder(BorderFactory.createTitledBorder("Navigate"));
		navigPanel.add(first = new JButton(new ImageIcon(
				new ImageIcon("first.png").getImage().getScaledInstance(17, 17, java.awt.Image.SCALE_SMOOTH))));
		first.setPreferredSize(new Dimension(17, 17));
		first.addActionListener(this);
		first.setToolTipText("Display first Record");

		navigPanel.add(previous = new JButton(new ImageIcon(
				new ImageIcon("prev.png").getImage().getScaledInstance(17, 17, java.awt.Image.SCALE_SMOOTH))));
		previous.setPreferredSize(new Dimension(17, 17));
		previous.addActionListener(this);
		previous.setToolTipText("Display next Record");

		navigPanel.add(next = new JButton(new ImageIcon(
				new ImageIcon("next.png").getImage().getScaledInstance(17, 17, java.awt.Image.SCALE_SMOOTH))));
		next.setPreferredSize(new Dimension(17, 17));
		next.addActionListener(this);
		next.setToolTipText("Display previous Record");

		navigPanel.add(last = new JButton(new ImageIcon(
				new ImageIcon("last.png").getImage().getScaledInstance(17, 17, java.awt.Image.SCALE_SMOOTH))));
		last.setPreferredSize(new Dimension(17, 17));
		last.addActionListener(this);
		last.setToolTipText("Display last Record");

		return navigPanel;
	}

	private JPanel buttonPanel() {
		JPanel buttonPanel = new JPanel();

		buttonPanel.add(add = new JButton("Add Record"), MIG_layout.mig_design2);
		add.addActionListener(this);
		add.setToolTipText("Add new Employee Record");
		buttonPanel.add(edit = new JButton("Edit Record"), MIG_layout.mig_design2);
		edit.addActionListener(this);
		edit.setToolTipText("Edit current Employee");
		buttonPanel.add(deleteButton = new JButton("Delete Record"), MIG_layout.mig_design3);
		deleteButton.addActionListener(this);
		deleteButton.setToolTipText("Delete current Employee");
		buttonPanel.add(displayAll = new JButton("List all Records"), MIG_layout.mig_design2);
		displayAll.addActionListener(this);
		displayAll.setToolTipText("List all Registered Employees");

		return buttonPanel;
	}

	private JPanel detailsPanel() {
		JPanel empDetails = new JPanel(new MigLayout());
		JPanel buttonPanel = new JPanel();
		JTextField field = null;

		empDetails.setBorder(BorderFactory.createTitledBorder("Employee Details"));

		empDetails.add(new JLabel("ID:"), MIG_layout.mig_design2);
		empDetails.add(idField = new JTextField(20), MIG_layout.mig_design3);
		idField.setEditable(false);

		empDetails.add(new JLabel("PPS Number:"), MIG_layout.mig_design2);
		empDetails.add(ppsField = new JTextField(20), MIG_layout.mig_design3);

		empDetails.add(new JLabel("Surname:"), MIG_layout.mig_design2);
		empDetails.add(surnameField = new JTextField(20), MIG_layout.mig_design3);

		empDetails.add(new JLabel("First Name:"), MIG_layout.mig_design2);
		empDetails.add(firstNameField = new JTextField(20), MIG_layout.mig_design3);

		empDetails.add(new JLabel("Gender:"), MIG_layout.mig_design2);
		empDetails.add(genderCombo = new JComboBox<String>(gender), MIG_layout.mig_design3);

		empDetails.add(new JLabel("Department:"), MIG_layout.mig_design2);
		empDetails.add(departmentCombo = new JComboBox<String>(department), MIG_layout.mig_design3);

		empDetails.add(new JLabel("Salary:"), MIG_layout.mig_design2);
		empDetails.add(salaryField = new JTextField(20), MIG_layout.mig_design3);

		empDetails.add(new JLabel("Full Time:"), MIG_layout.mig_design2);
		empDetails.add(fullTimeCombo = new JComboBox<String>(fullTime), MIG_layout.mig_design3);

		buttonPanel.add(saveChange = new JButton("Save"));
		saveChange.addActionListener(this);
		saveChange.setVisible(false);
		saveChange.setToolTipText("Save changes");
		buttonPanel.add(cancelChange = new JButton("Cancel"));
		cancelChange.addActionListener(this);
		cancelChange.setVisible(false);
		cancelChange.setToolTipText("Cancel edit");

		empDetails.add(buttonPanel, MIG_layout.mig_design1);
		addListenersAndFormat(empDetails, field);
		return empDetails;
	}

	public void displayCurrentEmployee(Employee thisEmployee) {
		int countGender = 0;
		int countDep = 0;
		boolean found = false;

		searchByIdField.setText("");
		searchBySurnameField.setText("");
		if (thisEmployee != null && thisEmployee.getEmployeeId() != 0) {
			// find gender
			while (!found && countGender < gender.length - 1) {
				if (Character.toString(thisEmployee.getGender()).equalsIgnoreCase(gender[countGender]))
					found = true;
				else
					countGender++;
			}
			found = false;
			// find department
			while (!found && countDep < department.length - 1) {
				if (thisEmployee.getDepartment().trim().equalsIgnoreCase(department[countDep]))
					found = true;
				else
					countDep++;
			}
			setDisplay(thisEmployee, countGender, countDep);
		}
		change = false;
	}

	private void displayEmployeeSummaryDialog() {
		if (isSomeoneToDisplay())
			new EmployeeSummaryDialog(getAllEmloyees());
	}

	private void displaySearchByIdDialog() {
		if (isSomeoneToDisplay())
			new SearchByIdDialog(EmployeeDetails.this);
	}

	private void displaySearchBySurnameDialog() {
		if (isSomeoneToDisplay())
			new SearchBySurnameDialog(EmployeeDetails.this);
	}

	private void firstRecord() {
		if (isSomeoneToDisplay()) {
			application.openReadFile(file.getAbsolutePath());
			objectStartPosition = application.getFirstRecord();
			currentEmployee = application.readRecords(objectStartPosition);
			application.closeReadFile();
			/* look back */
			if (currentEmployee.getEmployeeId() == 0)
				nextRecord();
		}
	}

	private void previousRecord() {
		if (isSomeoneToDisplay()) {
			application.openReadFile(file.getAbsolutePath());
			objectStartPosition = application.getPrevious(objectStartPosition);
			currentEmployee = application.readRecords(objectStartPosition);
			/* look back */
			while (currentEmployee.getEmployeeId() == 0) {
				objectStartPosition = application.getPrevious(objectStartPosition);
				currentEmployee = application.readRecords(objectStartPosition);
			}
			application.closeReadFile();
		}
	}

	private void nextRecord() {
		// if any active record in file look for first record
		if (isSomeoneToDisplay()) {
			application.openReadFile(file.getAbsolutePath());
			objectStartPosition = application.getNext(objectStartPosition);
			currentEmployee = application.readRecords(objectStartPosition);
			/* look back */
			while (currentEmployee.getEmployeeId() == 0) {
				objectStartPosition = application.getNext(objectStartPosition);
				currentEmployee = application.readRecords(objectStartPosition);
			}
			application.closeReadFile();
		}
	}

	private void lastRecord() {
		if (isSomeoneToDisplay()) {
			application.openReadFile(file.getAbsolutePath());
			objectStartPosition = application.getLast();
			currentEmployee = application.readRecords(objectStartPosition);
			application.closeReadFile();
			/* look back */
			if (currentEmployee.getEmployeeId() == 0)
				previousRecord();
		}
	}

	public void searchEmployeeById() {
		boolean found = false;

		try {
			if (isSomeoneToDisplay()) {
				firstRecord();
				int firstId = currentEmployee.getEmployeeId();
				// if ID to search is already displayed do nothing else loop
				// through records
				if (searchByIdField.getText().trim().equals(idField.getText().trim()))
					found = true;
				/* look back* practically same if statement */
				else if (searchByIdField.getText().trim().equals(Integer.toString(currentEmployee.getEmployeeId()))) {
					found = true;
					displayCurrentEmployee(currentEmployee);
				} else {
					nextRecord();
					while (firstId != currentEmployee.getEmployeeId()) {
						/* look back* practically same if statement not as bad as last one */
						if (Integer.parseInt(searchByIdField.getText().trim()) == currentEmployee.getEmployeeId()) {
							found = true;
							displayCurrentEmployee(currentEmployee);
							break;
						} else
							nextRecord();
					}
				}
				if (!found)
					JOptionPane.showMessageDialog(null, "Employee not found!");
			} else {
				JOptionPane.showMessageDialog(null, "No Employees registered!");
			}
		} catch (NumberFormatException e) {
			searchByIdField.setBackground(Color_Class.color_RED);
			JOptionPane.showMessageDialog(null, "Wrong ID format!");
		}
		searchByIdField.setBackground(Color.WHITE);
		searchByIdField.setText("");
	}

	public void searchEmployeeBySurname() {
		boolean found = false;
		if (isSomeoneToDisplay()) {
			firstRecord();
			String firstSurname = currentEmployee.getSurname().trim();
			/* look back see same if statement */
			if (searchBySurnameField.getText().trim().equalsIgnoreCase(surnameField.getText().trim()))
				found = true;
			else if (searchBySurnameField.getText().trim().equalsIgnoreCase(currentEmployee.getSurname().trim())) {
				found = true;
				displayCurrentEmployee(currentEmployee);
			} else {
				nextRecord();
				while (!firstSurname.trim().equalsIgnoreCase(currentEmployee.getSurname().trim())) {
					if (searchBySurnameField.getText().trim().equalsIgnoreCase(currentEmployee.getSurname().trim())) {
						found = true;
						displayCurrentEmployee(currentEmployee);
						break;
					} else
						nextRecord();
				}
			}
			if (!found)
				JOptionPane.showMessageDialog(null, "Employee not found!");
		} else {
			JOptionPane.showMessageDialog(null, "No Employees registered!");
		}
		searchBySurnameField.setText("");
	}

	public int getNextFreeEmployeeId() {
		int nextFreeId = 0;
		/* look back on no file will be empty */
		// if file is empty or all records are empty start with ID 1 else look
		// for last active record
		if (file.length() == 0 || !isSomeoneToDisplay())
			nextFreeId++;
		else {
			lastRecord();
			nextFreeId = currentEmployee.getEmployeeId() + 1;
		}
		return nextFreeId;
	}

	private Employee getChangedDetails() {
		boolean fullTime = false;
		Employee theEmployee;
		if (((String) fullTimeCombo.getSelectedItem()).equalsIgnoreCase("Yes"))
			fullTime = true;

		theEmployee = new Employee(Integer.parseInt(idField.getText()), ppsField.getText().toUpperCase(),
				surnameField.getText().toUpperCase(), firstNameField.getText().toUpperCase(),
				genderCombo.getSelectedItem().toString().charAt(0), departmentCombo.getSelectedItem().toString(),
				Double.parseDouble(salaryField.getText()), fullTime);

		return theEmployee;
	}

	public void addEmployee(Employee newEmployee) {
		application.openWriteFile(file.getAbsolutePath());
		objectStartPosition = application.addRecords(newEmployee);
		application.closeWriteFile();
	}

	private void deleteEmployee() {
		if (isSomeoneToDisplay()) {
			int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to delete record?", "Delete",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
			if (returnVal == JOptionPane.YES_OPTION) {
				application.openWriteFile(file.getAbsolutePath());
				application.deleteRecords(objectStartPosition);
				application.closeWriteFile();
				/* look back it deletes all employees */
				if (isSomeoneToDisplay()) {
					nextRecord();
					displayCurrentEmployee(currentEmployee);
				} else {
					change = false;
				}
			}

		} else {
			JOptionPane.showMessageDialog(null, "No Employees registered!");
		}

	}

	// create vector of vectors with all Employee details
	private Vector<Object> getAllEmloyees() {
		// vector of Employee objects
		Vector<Object> allEmployee = new Vector<Object>();
		Vector<Object> empDetails;// vector of each employee details
		long byteStart = objectStartPosition;
		int firstId;

		firstRecord();
		firstId = currentEmployee.getEmployeeId();
		do {
			empDetails = new Vector<Object>();
			empDetails.addElement(new Integer(currentEmployee.getEmployeeId()));
			empDetails.addElement(currentEmployee.getPps());
			empDetails.addElement(currentEmployee.getSurname());
			empDetails.addElement(currentEmployee.getFirstName());
			empDetails.addElement(new Character(currentEmployee.getGender()));
			empDetails.addElement(currentEmployee.getDepartment());
			empDetails.addElement(new Double(currentEmployee.getSalary()));
			empDetails.addElement(new Boolean(currentEmployee.getFullTime()));

			allEmployee.addElement(empDetails);
			nextRecord();
		} while (firstId != currentEmployee.getEmployeeId());
		objectStartPosition = byteStart;

		return allEmployee;
	}

	private void editDetails() {
		if (isSomeoneToDisplay()) {
			// remove euro sign from salary text field
			salaryField.setText(fieldFormat.format(currentEmployee.getSalary()));
			change = false;
			setEnabled(true);
		}
	}

	private void cancelEditChanges() {
		setEnabled(false);
		displayCurrentEmployee(currentEmployee);
	}

	private boolean isSomeoneToDisplay() {
		boolean someoneToDisplay = false;
		application.openReadFile(file.getAbsolutePath());
		someoneToDisplay = application.isSomeoneToDisplay();
		application.closeReadFile();
		// if no records found clear all text fields and display message
		if (!someoneToDisplay) {
			currentEmployee = null;
			idField.setText("");
			ppsField.setText("");
			surnameField.setText("");
			firstNameField.setText("");
			salaryField.setText("");
			genderCombo.setSelectedIndex(0);
			departmentCombo.setSelectedIndex(0);
			fullTimeCombo.setSelectedIndex(0);
		}
		return someoneToDisplay;
	}

	// check for correct PPS format and look if PPS already in use
	public boolean correctPps(String pps, long currentByte) {
		boolean ppsExist = false;
		if (pps.length() == 7) {
			if (pps.matches("[0-9][0-9][0-9][0-9][0-9][0-9][a-zA-Z]")) {
				application.openReadFile(file.getAbsolutePath());
				// look in file is PPS already in use
				ppsExist = application.isPpsExist(pps, currentByte);
				application.closeReadFile();
			} else
				ppsExist = true;
		} else
			ppsExist = true;

		return ppsExist;
	}

	private boolean checkFileName(File fileName) {
		boolean checkFile = false;
		if (fileName.toString().endsWith(".dat"))
			checkFile = true;
		return checkFile;
	}

	private boolean checkForChanges() {
		boolean anyChanges = false;
		// if changes where made, allow user to save there changes
		if (change) {
			saveChangesToCurrentEmployee();
			anyChanges = true;
		}
		// if no changes made, set text fields as unenabled and display current Employee
		else {
			setEnabled(false);
			displayCurrentEmployee(currentEmployee);
		} 
		return anyChanges;
	}

	private boolean checkInput() {
		boolean valid = true;
		if (ppsField.isEditable() && ppsField.getText().trim().isEmpty()) {
			ppsField.setBackground(Color_Class.color_RED);
			valid = false;
		}
		if (ppsField.isEditable() && correctPps(ppsField.getText().trim(), objectStartPosition)) {
			ppsField.setBackground(Color_Class.color_RED);
			valid = false;
		}
		if (surnameField.isEditable() && surnameField.getText().trim().isEmpty()) {
			surnameField.setBackground(Color_Class.color_RED);
			valid = false;
		}
		if (firstNameField.isEditable() && firstNameField.getText().trim().isEmpty()) {
			firstNameField.setBackground(Color_Class.color_RED);
			valid = false;
		}
		if (genderCombo.getSelectedIndex() == 0 && genderCombo.isEnabled()) {
			genderCombo.setBackground(Color_Class.color_RED);
			valid = false;
		}
		if (departmentCombo.getSelectedIndex() == 0 && departmentCombo.isEnabled()) {
			departmentCombo.setBackground(Color_Class.color_RED);
			valid = false;
		}
		try {
			if (Double.parseDouble(salaryField.getText()) < 0) {
				salaryField.setBackground(Color_Class.color_RED);
				valid = false;
			}
		} 
		catch (NumberFormatException num) {
			if (salaryField.isEditable()) {
				salaryField.setBackground(Color_Class.color_RED);
				valid = false;
			}
		}
		if (fullTimeCombo.getSelectedIndex() == 0 && fullTimeCombo.isEnabled()) {
			fullTimeCombo.setBackground(Color_Class.color_RED);
			valid = false;
		}
		if (!valid)
			JOptionPane.showMessageDialog(null, "Wrong values or format! Please check!");

		return valid;
	}

	public void setEnabled(boolean booleanValue) {
		boolean search;
		/*look at this*/
		if (booleanValue)
			search = false;
		else
			search = true;
		ppsField.setEditable(booleanValue);
		surnameField.setEditable(booleanValue);
		firstNameField.setEditable(booleanValue);
		genderCombo.setEnabled(booleanValue);
		departmentCombo.setEnabled(booleanValue);
		salaryField.setEditable(booleanValue);
		fullTimeCombo.setEnabled(booleanValue);
		saveChange.setVisible(booleanValue);
		cancelChange.setVisible(booleanValue);
		searchByIdField.setEnabled(search);
		searchBySurnameField.setEnabled(search);
		searchId.setEnabled(search);
		searchSurname.setEnabled(search);
	}
	
	private void openFile() {
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Open");
		fc.setFileFilter(datfilter);
		File newFile; 
		//offers to save old file
		if (file.length() != 0 || change) {
			/*check if JOptionPane is use more than once*/
			int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes?", "Save",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
			if (returnVal == JOptionPane.YES_OPTION) {
				saveFile();// save file
			}
		}

		int returnVal = fc.showOpenDialog(EmployeeDetails.this);
		// if file been chosen, open it
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			newFile = fc.getSelectedFile();
			//delete file
			if (file.getName().equals(generatedFileName))
				file.delete();
			file = newFile;
			application.openReadFile(file.getAbsolutePath());
			firstRecord();
			displayCurrentEmployee(currentEmployee);
			application.closeReadFile();
		}
	}

	private void saveFile() {
		/*look back*/
		// if file name is generated file name, save file as 'save as' else save
		// changes to file
		if (file.getName().equals(generatedFileName))
			saveFileAs();// save file as 'save as'
		else {
			if (change) {
				int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes?", "Save",
						JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
				if (returnVal == JOptionPane.YES_OPTION) {
					if (!idField.getText().equals("")) {
						application.openWriteFile(file.getAbsolutePath());
						//make sure changes match it
						currentEmployee = getChangedDetails();
						application.changeRecords(currentEmployee, objectStartPosition);
						application.closeWriteFile();
					}
				}
			}

			displayCurrentEmployee(currentEmployee);
			setEnabled(false);
		}
	}

	private void saveChangesToCurrentEmployee() {
		int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes to current Employee?", "Save",
				JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
		if (returnVal == JOptionPane.YES_OPTION) {
			application.openWriteFile(file.getAbsolutePath());
			currentEmployee = getChangedDetails();
			application.changeRecords(currentEmployee, objectStartPosition);
			application.closeWriteFile();
			changesMade = false;
		}
		displayCurrentEmployee(currentEmployee);
		setEnabled(false);
	}

   /*look at all these saves*/
	private void saveFileAs() {
		final JFileChooser fc = new JFileChooser();
		File newFile;
		String defaultFileName = "new_Employee.dat";
		fc.setDialogTitle("Save As");
		fc.setFileFilter(datfilter);
		fc.setApproveButtonText("Save");
		fc.setSelectedFile(new File(defaultFileName));

		int returnVal = fc.showSaveDialog(EmployeeDetails.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			newFile = fc.getSelectedFile();
			if (!checkFileName(newFile)) {
				newFile = new File(newFile.getAbsolutePath() + ".dat");
				application.createFile(newFile.getAbsolutePath());
			} 
			else
				application.createFile(newFile.getAbsolutePath());

			try {// try to copy old file to new file
				Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				if (file.getName().equals(generatedFileName))
					file.delete();
				file = newFile;
			}
			catch (IOException e) {
			}
		}
		changesMade = false;
	}

	private void exitApp() {
		/*same option again for saving a file*/
		if (file.length() != 0) {
			if (changesMade) {
				int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes?", "Save",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
				if (returnVal == JOptionPane.YES_OPTION) {
					saveFile();
					deleteGeneratedFile();
				} else if (returnVal == JOptionPane.NO_OPTION) {
					deleteGeneratedFile();
				}
			} else {
				deleteGeneratedFile();
			}
		} else {
			deleteGeneratedFile();
		}
	}

	/*look at this*/
	// generate 20 character long file name
	private String getFileName() {
		String fileNameChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_-";
		StringBuilder fileName = new StringBuilder();
		Random rnd = new Random();
		while (fileName.length() < 20) {
			int index = (int) (rnd.nextFloat() * fileNameChars.length());
			fileName.append(fileNameChars.charAt(index));
		}
		String generatedfileName = fileName.toString();
		return generatedfileName;
	}

	private void createRandomFile() {
		generatedFileName = getFileName() + ".dat";
		file = new File(generatedFileName);
		application.createFile(file.getName());
	}

	// action listener for buttons, text field and menu items
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == searchId || e.getSource() == searchByIdField) {
			searchEmployeeById();
		} else if (e.getSource() == cancelChange) {
			cancelEditChanges();
		} else if (e.getSource() == searchSurname || e.getSource() == searchBySurnameField) {
			searchEmployeeBySurname();
		} else if (checkInput() && !checkForChanges()) {
			if (e.getSource() == closeApp) {
				exitApp();
			} else if (e.getSource() == open) {
				openFile();
			} else if (e.getSource() == save) {
				saveFile();
				change = false;
			} else if (e.getSource() == saveAs) {
				saveFileAs();
				change = false;
			} else if (e.getSource() == searchById) {
				displaySearchByIdDialog();
			} else if (e.getSource() == searchBySurname) {
				displaySearchBySurnameDialog();
			} else if (e.getSource() == saveChange) {
				saveChangesToCurrentEmployee();
			} else if (e.getSource() == firstItem || e.getSource() == first) {
				firstRecord();
				displayCurrentEmployee(currentEmployee);
			} else if (e.getSource() == prevItem || e.getSource() == previous) {
				previousRecord();
				displayCurrentEmployee(currentEmployee);
			} else if (e.getSource() == nextItem || e.getSource() == next) {
				nextRecord();
				displayCurrentEmployee(currentEmployee);
			} else if (e.getSource() == lastItem || e.getSource() == last) {
				lastRecord();
				displayCurrentEmployee(currentEmployee);
			} else if (e.getSource() == listAll || e.getSource() == displayAll) {
				if (isSomeoneToDisplay())
					displayEmployeeSummaryDialog();
				else {
					JOptionPane.showMessageDialog(null, "No Employees registered!");
				}
			} else if (e.getSource() == create || e.getSource() == add) {
				new AddRecordDialog(EmployeeDetails.this);
			} else if (e.getSource() == modify || e.getSource() == edit) {
				editDetails();
			} else if (e.getSource() == delete || e.getSource() == deleteButton) {
				deleteEmployee();
			} else if (e.getSource() == searchBySurname) {
				new SearchBySurnameDialog(EmployeeDetails.this);
			}
		}
	}

	// main dialog
	private void mainDialog() {
		setTitle("Employee Details");
		createRandomFile();// create random file name
		JPanel dialog = new JPanel(new MigLayout());

		// adding components to the frame
		setJMenuBar(menuBar());
		dialog.add(searchPanel(), "width 400:400:400," + MIG_layout.mig_design2);
		dialog.add(navigPanel(), "width 150:150:150, wrap");
		dialog.add(buttonPanel(), MIG_layout.mig_design1);
		dialog.add(detailsPanel(), "gap top 30, gap left 150, center");

		JScrollPane scrollPane = new JScrollPane(dialog);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		addWindowListener(this);
	}

	// create and show main dialog
	private static void createAndShowGUI() {

		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.mainDialog();
		frame.setSize(760, 600);
		frame.setLocation(250, 200);
		frame.setVisible(true);
	}

	public static void main(String args[]) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	// DocumentListener methods
	public void changedUpdate(DocumentEvent d) {
		change = true;
		new JTextFieldLimit(20);
	}

	public void insertUpdate(DocumentEvent d) {
		change = true;
		new JTextFieldLimit(20);
	}

	public void removeUpdate(DocumentEvent d) {
		change = true;
		new JTextFieldLimit(20);
	}

	// ItemListener method
	public void itemStateChanged(ItemEvent e) {
		change = true;
	}

	// WindowsListener methods
	public void windowClosing(WindowEvent e) {
		// exit application
		exitApp();
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}

	/* created to Display a user */
	public void setDisplay(Employee thisEmployee, int countGender, int countDep) {
		idField.setText(Integer.toString(thisEmployee.getEmployeeId()));
		ppsField.setText(thisEmployee.getPps().trim());
		surnameField.setText(thisEmployee.getSurname().trim());
		firstNameField.setText(thisEmployee.getFirstName());
		genderCombo.setSelectedIndex(countGender);
		departmentCombo.setSelectedIndex(countDep);
		salaryField.setText(format.format(thisEmployee.getSalary()));

		if (thisEmployee.getFullTime() == true)
			fullTimeCombo.setSelectedIndex(1);
		else
			fullTimeCombo.setSelectedIndex(2);
	}

	// loop through panel components and add listeners and format
	public void addListenersAndFormat(JPanel empDetails, JTextField field) {
		for (int i = 0; i < empDetails.getComponentCount(); i++) {
			empDetails.getComponent(i).setFont(font1);
			if (empDetails.getComponent(i) instanceof JTextField) {
				field = (JTextField) empDetails.getComponent(i);
				field.setEditable(false);
				if (field == ppsField)
					field.setDocument(new JTextFieldLimit(7));
				else
					field.setDocument(new JTextFieldLimit(20));
				field.getDocument().addDocumentListener(this);
			} else if (empDetails.getComponent(i) instanceof JComboBox) {
				empDetails.getComponent(i).setBackground(Color_Class.color_WHITE);
				empDetails.getComponent(i).setEnabled(false);
				((JComboBox<String>) empDetails.getComponent(i)).addItemListener(this);
				((JComboBox<String>) empDetails.getComponent(i)).setRenderer(new DefaultListCellRenderer() {
					// set foregroung to combo boxes
					public void paint(Graphics g) {
						setForeground(new Color(65, 65, 65));
						super.paint(g);
					}// end paint
				});
			}
		}
	}

	// delete generated file if user chooses not to save file
	public void deleteGeneratedFile() {
		if (file.getName().equals(generatedFileName))
			file.delete();
		System.exit(0);
	}

}
