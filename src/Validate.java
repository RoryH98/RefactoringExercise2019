import java.io.File;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class Validate {
	public boolean validate2(JTextField ppsField, JTextField surnameField, JTextField firstNameField,
			JComboBox<String> genderCombo, JComboBox<String> departmentCombo, JTextField salaryField,
			JComboBox<String> fullTimeCombo,long currentPosition,FileManager application,File file) {
		boolean valid = true;
		EmployeeDetails ed = new EmployeeDetails();
		if (ppsField.isEditable() && ppsField.getText().trim().isEmpty()) {
			ppsField.setBackground(Color_Class.color_RED);
			valid = false;
		}
		if(ppsField.isEditable() && correctPps(ppsField.getText().trim(), currentPosition, application, file)) {
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
		try {// try to get values from text field
			// check if salary is greater than 0
			if (Double.parseDouble(salaryField.getText()) < 0) {
				salaryField.setBackground(Color_Class.color_RED);
				valid = false;
			}
		} catch (NumberFormatException num) {
			if (salaryField.isEditable()) {
				salaryField.setBackground(Color_Class.color_RED);
				valid = false;
			} // end if
		} // end catch
		if (fullTimeCombo.getSelectedIndex() == 0 && fullTimeCombo.isEnabled()) {
			fullTimeCombo.setBackground(Color_Class.color_RED);
			valid = false;
		}

		if (!valid)
			JOptionPane.showMessageDialog(null, "Wrong values or format! Please check!");
		// set text field to white colour if text fields are editable
		if (ppsField.isEditable()) {
			setToWhite(ppsField, surnameField, firstNameField, salaryField, genderCombo, departmentCombo,
					fullTimeCombo);

		}
		return valid;
	}

	private void setToWhite(JTextField ppsField, JTextField surnameField, JTextField firstNameField,
			JTextField salaryField, JComboBox<String> genderCombo, JComboBox<String> departmentCombo,
			JComboBox<String> fullTimeCombo) {
		ppsField.setBackground(UIManager.getColor("TextField.background"));
		surnameField.setBackground(UIManager.getColor("TextField.background"));
		firstNameField.setBackground(UIManager.getColor("TextField.background"));
		salaryField.setBackground(UIManager.getColor("TextField.background"));
		genderCombo.setBackground(UIManager.getColor("TextField.background"));
		departmentCombo.setBackground(UIManager.getColor("TextField.background"));
		fullTimeCombo.setBackground(UIManager.getColor("TextField.background"));
	}
	
	public boolean correctPps(String pps, long currentPosition,FileManager application, File file) {
		boolean ppsExist = false;
		if (pps.length() == 7) {
			if (pps.matches("[0-9][0-9][0-9][0-9][0-9][0-9][a-zA-Z]")) {
				application.openReadFile(file.getAbsolutePath());
				// look in file is PPS already in use
				ppsExist = application.isPpsExist(pps, currentPosition);
				application.closeReadFile();
			} else
				ppsExist = true;
		} else
			ppsExist = true;

		return ppsExist;
	}
	
	
}
