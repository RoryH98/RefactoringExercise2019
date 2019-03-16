
import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class FileManager {
	private RandomAccessFile output;
	private RandomAccessFile input;

	public void createFile(String fileName) {
		RandomAccessFile file = null;

		try {
			file = new RandomAccessFile(fileName, "rw");

		} catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error processing file!");
			System.exit(1);
		}

		finally {
			try {
				if (file != null)
					file.close();
			} catch (IOException ioException) {
				JOptionPane.showMessageDialog(null, "Error closing file!");
				System.exit(1);
			}
		}
	}

	public void openWriteFile(String fileName) {
		try {
			output = new RandomAccessFile(fileName, "rw");
		} catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "File does not exist!");
		}
	}

	public void closeWriteFile() {
		try {
			if (output != null)
				output.close();
		} 
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error closing file!");
			System.exit(1);
		}
	}

	// Add Employees to file
	public long addEmployees(Employee employeeToAdd) {
		Employee newEmployee = employeeToAdd;
		long currentEmployeeStart = 0;

		// object to be written to file
		AccessEmployeeFileDetails Employee;

		try {
			Employee = new AccessEmployeeFileDetails(newEmployee.getEmployeeId(), newEmployee.getPps(),
					newEmployee.getSurname(), newEmployee.getFirstName(), newEmployee.getGender(),
					newEmployee.getDepartment(), newEmployee.getSalary(), newEmployee.getFullTime());

			output.seek(output.length());
			Employee.write(output);
			currentEmployeeStart = output.length();
		} catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error writing to file!");
		}

		return currentEmployeeStart - AccessEmployeeFileDetails.SIZE;
	}

	// Change details for existing object
	public void changeEmployees(Employee newDetails, long startPosition) {
		long currentEmployeeStart = startPosition;
		AccessEmployeeFileDetails Employee;
		Employee oldDetails = newDetails;
		try {
			Employee = new AccessEmployeeFileDetails(oldDetails.getEmployeeId(), oldDetails.getPps(),
					oldDetails.getSurname(), oldDetails.getFirstName(), oldDetails.getGender(),
					oldDetails.getDepartment(), oldDetails.getSalary(), oldDetails.getFullTime());

			output.seek(currentEmployeeStart);
			Employee.write(output);
		} 
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error writing to file!");
		} 
	}

	public void deleteEmployees(long startPosition) {
		long currentEmployeeStart = startPosition;

		AccessEmployeeFileDetails Employee;
		

		try 
		{
			Employee = new AccessEmployeeFileDetails();
			output.seek(currentEmployeeStart);
			Employee.write(output);
		} 
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error writing to file!");
		}
	}

	public void openReadFile(String fileName) {
		try 
		{
			input = new RandomAccessFile(fileName, "r");
		}
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "File is not suported!");
		} 
	}

	public void closeReadFile() {
		try {
			if (input != null)
				input.close();
		} catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error closing file!");
			System.exit(1);
		}
	}

	public long getFirstEmployee() {
		long startPosition = 0;

		try {// try to get file
			input.length();
		} catch (IOException e) {
		}

		return startPosition;
	}

	
	public long getLast() {
		long startPosition = 0;

		try {
			startPosition = input.length() - AccessEmployeeFileDetails.SIZE;
		} 
		catch (IOException e) {
		} 

		return startPosition;
	}

	
	public long getNext(long readFrom) {
		long startPosition = readFrom;

		try {
			input.seek(startPosition);
			if (startPosition + AccessEmployeeFileDetails.SIZE == input.length())
				startPosition = 0;
			else
				startPosition = startPosition + AccessEmployeeFileDetails.SIZE;
		} 
		catch (NumberFormatException e) {
		} 
		catch (IOException e) {
		}
		return startPosition;
	}

	public long getPrevious(long readFrom) {
		long startPosition = readFrom;

		try {
			input.seek(startPosition);
		
			if (startPosition == 0)
				startPosition = input.length() - AccessEmployeeFileDetails.SIZE;
			else
				startPosition = startPosition - AccessEmployeeFileDetails.SIZE;
		} 
		catch (NumberFormatException e) {
		} 
		catch (IOException e) {
		} 
		return startPosition;
	}

	// Get object from file in specified position
	public Employee readEmployees(long startPosition) {
		Employee thisEmp = null;
		AccessEmployeeFileDetails Employee = new AccessEmployeeFileDetails();

		try {
			input.seek(startPosition);
			Employee.read(input);
		} catch (IOException e) {
		}

		thisEmp = Employee;

		return thisEmp;
	}

	public boolean isPpsExist(String pps, long position) {
		AccessEmployeeFileDetails Employee = new AccessEmployeeFileDetails();
		boolean ppsExist = false;
		long currentPosition = 0;

		try {
			while (currentPosition != input.length() && !ppsExist) {
				if (currentPosition != position) {
					input.seek(currentPosition);
					Employee.read(input);
					if (Employee.getPps().trim().equalsIgnoreCase(pps)) {
						ppsExist = true;
						JOptionPane.showMessageDialog(null, "PPS number already exist!");
					}
				}
				currentPosition = currentPosition + AccessEmployeeFileDetails.SIZE;
			}
		} catch (IOException e) {
		}

		return ppsExist;
	}

	public boolean isSomeoneToDisplay() {
		boolean someoneToDisplay = false;
		long currentPosition = 0;
		AccessEmployeeFileDetails Employee = new AccessEmployeeFileDetails();

		try {
			while (currentPosition != input.length() && !someoneToDisplay) {
				input.seek(currentPosition);
				Employee.read(input);
				if (Employee.getEmployeeId() > 0)
					someoneToDisplay = true;
				currentPosition = currentPosition + AccessEmployeeFileDetails.SIZE;
			}
		} catch (IOException e) {
		}

		return someoneToDisplay;
	}
}
