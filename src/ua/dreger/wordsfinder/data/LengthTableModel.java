/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ua.dreger.wordsfinder.data;

import ua.dreger.wordsfinder.data.Word;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Sergey
 */
public class LengthTableModel extends AbstractTableModel{
	private List<Word> modelList;

	public LengthTableModel(List<Word> modelList) {
		sortModelList(modelList);
	}
	public void setModelList(List<Word> modelList) {
		sortModelList(modelList);
	}
	private void sortModelList(List<Word> modelList){
		this.modelList = new ArrayList<Word>(modelList);
		Collections.sort(this.modelList, new Comparator<Word>() {
			@Override
			public int compare(Word o1, Word o2) {
				//for reverce order
				return o2.getWord().length() - o1.getWord().length();
			}
		});
	}

	@Override
	public int getRowCount() {
		return this.modelList.size();
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(columnIndex == 0){
			return (modelList.get(rowIndex).getWord());
		}
		if(columnIndex == 1){
			return modelList.get(rowIndex).getWord().length();
		}else{
			return modelList.get(rowIndex).getType();
		}
	}
	@Override
	public String getColumnName(int column) {
		if(column == 0) return "Слово";
		if(column == 1 ) return "Длина";
		else return "Часть речи";
	}
}