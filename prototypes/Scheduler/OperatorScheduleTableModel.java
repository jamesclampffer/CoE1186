package TLTTC;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

public class OperatorScheduleTableModel extends DefaultTableModel
{
  boolean sortedHighToLow;
	int sortColumn;

	public OperatorScheduleTableModel()
	{
		super();

		addColumn("First Name");
		addColumn("Last Name");
		addColumn("Train Number");
		addColumn("Shift Start");
		addColumn("Break Start");
		addColumn("Break End");
		addColumn("Shift End");
		addColumn("Status");

		sortColumn = 2;
		sortedHighToLow = false;		
	}

	public boolean isCellEditable(int row, int column)
	{
		return false;
	}

	public void update(OperatorSchedule schedule)
	{
		Iterator<Operator> i;
		boolean[] rowMatches;
		Operator operator;

		rowMatches = new boolean[getRowCount()];
		i = schedule.getIterator();

		while(i.hasNext())
		{
			boolean match = false;

			operator = i.next();

			for(int row = 0; row < rowMatches.length; row++)
			{
				if(operator.trainNumber == (int)getValueAt(row ,2))
				{
					match = true;
					rowMatches[row] = true;
					setValueAt(operator.status.toString(), row, 7);
					break;
				}
			}

			if(!match)
			{
				addRow(new Object[]{operator.firstName, operator.lastName, operator.trainNumber, new Time(operator.shiftStart), new Time(operator.breakStart), new Time(operator.breakEnd), new Time(operator.shiftEnd), operator.status.toString()});
			}
		}

		for(int row = rowMatches.length - 1; row >= 0; row--)
		{
			if(!rowMatches[row])
			{
				removeRow(row);
			}
		}
	}
}
