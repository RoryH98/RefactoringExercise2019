/*
 * 
 * This is a dialog for adding new Employees and saving Employees to file
 * 
 * */
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class AddEmployeeDialog extends JDialog implements ActionListener {
	JTextField idField, ppsField, surnameField, firstNameField, salaryField;
	JComboBox<String> genderCombo, departmentCombo, fullTimeCombo;
	JButton save, cancel;
	EmployeeDetails parent;
	Validate validate = new Validate();
	public AddEmployeeDialog(EmployeeDetails parent) {
		setTitle("Add Employee");
		setModal(true);
		this.parent = parent;
		this.parent.setEnabled(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JScrollPane scrollPane = new JScrollPane(dialogPane());
		setContentPane(scrollPane);
		
		getRootPane().setDefaultButton(save);
		
		setSize(500, 370);
		setLocation(350, 250);
		setVisible(true);
	}

	// initialize dialog container
	public Container dialogPane() {
		JPanel empDetails, buttonPanel;
		empDetails = new JPanel(new MigLayout());
		buttonPanel = new JPanel();
		JTextField field;

		empDetails.setBorder(BorderFactory.createTitledBorder("Employee Details"));

		empDetails.add(new JLabel("ID:"), MIG_layout.mig_design2);
		empDetails.add(idField = new JTextField(20), MIG_layout.mig_design3);
		idField.setEditable(false);
		

		empDetails.add(new JLabel("PPS Number:"), MIG_layout.mig_design2);
		empDetails.add(ppsField = new JTextField(20), MIG_layout.mig_design3);

		empDetails.add(new JLabel("Surname:"), MIG_layout.mig_design2);
		empDetails.add(surnameField = new JTextField(20), MIG_layout.mig_design3);

		empDetails.add(new JLabel("First Name:"), MIG_layout.mig_design2);
		empDetails.add(firstNameField = new JTextField(20),MIG_layout.mig_design3);

		empDetails.add(new JLabel("Gender:"), MIG_layout.mig_design2);
		empDetails.add(genderCombo = new JComboBox<String>(this.parent.gender), MIG_layout.mig_design3);

		empDetails.add(new JLabel("Department:"), MIG_layout.mig_design2);
		empDetails.add(departmentCombo = new JComboBox<String>(this.parent.department), MIG_layout.mig_design3);

		empDetails.add(new JLabel("Salary:"),MIG_layout.mig_design2);
		empDetails.add(salaryField = new JTextField(20), MIG_layout.mig_design3);

		empDetails.add(new JLabel("Full Time:"), MIG_layout.mig_design2);
		empDetails.add(fullTimeCombo = new JComboBox<String>(this.parent.fullTime),MIG_layout.mig_design3);

		buttonPanel.add(save = new JButton("Save"));
		save.addActionListener(this);
		save.requestFocus();
		buttonPanel.add(cancel = new JButton("Cancel"));
		cancel.addActionListener(this);

		empDetails.add(buttonPanel,MIG_layout.mig_design1);
		// loop through all panel components and add fonts and listeners
		for (int i = 0; i < empDetails.getComponentCount(); i++) {
			empDetails.getComponent(i).setFont(this.parent.font1);
			if (empDetails.getComponent(i) instanceof JComboBox) {
				empDetails.getComponent(i).setBackground(Color_Class.color_WHITE);
			}
			else if(empDetails.getComponent(i) instanceof JTextField){
				field = (JTextField) empDetails.getComponent(i);
				if(field == ppsField)
					field.setDocument(new JTextFieldLimit(7));
				else
				field.setDocument(new JTextFieldLimit(20));
			}
		}
		idField.setText(Integer.toString(this.parent.getNextFreeEmployeeId()));
		return empDetails;
	}

	// add Employee to file
	public void addEmployee() {
		boolean fullTime = false;
		Employee theEmployee;

		if (((String) fullTimeCombo.getSelectedItem()).equalsIgnoreCase("Yes"))
			fullTime = true;
		theEmployee = new Employee(Integer.parseInt(idField.getText()), ppsField.getText().toUpperCase(), surnameField.getText().toUpperCase(),
				firstNameField.getText().toUpperCase(), genderCombo.getSelectedItem().toString().charAt(0),
				departmentCombo.getSelectedItem().toString(), Double.parseDouble(salaryField.getText()), fullTime);
		this.parent.currentEmployee = theEmployee;
		this.parent.addEmployee(theEmployee);
		this.parent.displayCurrentEmployee(theEmployee);
	}

	// check for input in text fields
	public boolean checkInput() {
		boolean valid = validate.validate2(ppsField, surnameField, firstNameField, genderCombo, departmentCombo, salaryField, fullTimeCombo, -1, this.parent.getApplication(), this.parent.getFile());
        
		return valid;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == save) {
			// if inputs correct, save Employee
			if (checkInput()) {
				addEmployee();
				dispose();
				this.parent.changesMade = true;
			}
			else {
				JOptionPane.showMessageDialog(null, "Wrong values or format! Please check!");
			}
		}
		else if (e.getSource() == cancel)
			dispose();
	}
}