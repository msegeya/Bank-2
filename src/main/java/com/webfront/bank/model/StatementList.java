package com.webfront.bank.model;

import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.table.DefaultTableModel;

public class StatementList extends DefaultTableModel {

	private LinkedList<StatementEntry> data;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2264020760312965635L;
	private ArrayList<String> columnNames;
	private CategoryModel catModel;
	public static final int ID_COLUMN = 0;
	public static final int DATE_COLUMN = 1;
	public static final int DESC_COLUMN = 2;
	public static final int CATEGORY_COLUMN = 3;
	public static final int AMOUNT_COLUMN = 4;
	public static final int BALANCE_COLUMN = 5;
	
	public StatementList() {
		this.columnNames = new ArrayList<>();
		this.columnNames.add("ID");
		this.columnNames.add("Date");
		this.columnNames.add("Description");
		this.columnNames.add("Category");
		this.columnNames.add("Amount");
		this.columnNames.add("Balance");
		setData(new LinkedList<StatementEntry>());
	}

	@Override
	public int getRowCount() {
		if(data == null ||data.isEmpty()) {
			return 65525;
		}
		return data.size();
	}

	@Override
	public int getColumnCount() {
		return 6;
	}
	
        @Override
	public boolean isCellEditable(int row, int col) {
		return col==DESC_COLUMN || col==CATEGORY_COLUMN;
	}
	@Override
	public String getColumnName(int idx) {
		return getColumnNames().get(idx);
	}
	
        @Override
	public Class<?> getColumnClass(int col) {
		if(col==AMOUNT_COLUMN || col==BALANCE_COLUMN) {
			return Float.class;
		}
		return String.class;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		int maxRows=data.size();
		int maxCols=6;
		if (rowIndex >= 0 && rowIndex <= maxRows) {
			if(columnIndex >= 0 && columnIndex <=maxCols) {
				StatementEntry rowData=data.get(rowIndex);
				return rowData.get(columnIndex+1, 1);
			}
		}
		return null;
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		StatementEntry entry;
		if(rowIndex> data.size()) {
			entry=new StatementEntry();
		} else {
			entry=data.get(rowIndex);
			switch(columnIndex) {
			case DESC_COLUMN:
				entry.setTransDescription((String)value);
				break;
			case CATEGORY_COLUMN:
				String key=getCatModel().getCatXref().get(value.toString());
				entry.setPaymentClass(key);
				break;
			}
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}

	public LinkedList<StatementEntry> getData() {
		return data;
	}

	public void setData(LinkedList<StatementEntry> data) {
		if (data != null && data.size() >1) {
			this.fireTableDataChanged();
		}
		this.data = data;
	}

	public ArrayList<String> getColumnNames() {
		return this.columnNames;
	}

	private void setColumnNames(ArrayList<String> columnNames) {
		this.columnNames = columnNames;
	}

	private CategoryModel getCatModel() {
		return catModel;
	}

	public void setCatModel(CategoryModel catModel) {
		this.catModel = catModel;
	}

}
