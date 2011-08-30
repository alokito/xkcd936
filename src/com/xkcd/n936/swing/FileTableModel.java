package com.xkcd.n936.swing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class FileTableModel extends AbstractTableModel {
	private List<SelectableFile> files = new ArrayList<SelectableFile>();
	private String[] columnNames = new String[]{"Selected", "Filename"};
	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public int getRowCount() {
		return files.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		SelectableFile file = files.get(row);
		if (col == 0)
			return file.getSelected();
		else return file.getFile().getName();
	}
	
	public void addFile(File file) {
		files.add(new SelectableFile(file, true));
		fireTableRowsInserted(files.size()-1, files.size()-1);
	}
	public boolean isCellEditable(int row, int col) { 
		return col == 0;
	}
	public String getColumnName(int col) {
        return columnNames[col].toString();
    }
	public void setValueAt(Object value, int row, int col) {
		if (col == 0) {
			files.get(row).setSelected((Boolean) value);
			fireTableCellUpdated(row, col);
		}
    }
	@Override
	public Class getColumnClass(int columnIndex) {
		if (columnIndex == 0)
			return Boolean.class; 
		return super.getColumnClass(columnIndex);
	}
	public List<File> getSelectedFiles() {
		List<File> selected = new ArrayList<File>();
		for (SelectableFile file : files) {
			if (file.getSelected())
				selected.add(file.getFile());
		}
		return selected;
	}
	
	private class SelectableFile  {
		public SelectableFile(File file, Boolean selected) {
			super();
			this.file = file;
			this.selected = selected;
		}
		private File file;
		private Boolean selected;
		public File getFile() {
			return file;
		}
		public void setSelected(Boolean selected) {
			this.selected = selected;
		}
		public Boolean getSelected() {
			return selected;
		}
	}

	public void removeAll() {
		int deleted = files.size();
		files.clear();
		fireTableRowsDeleted(0, deleted);
	}

}
