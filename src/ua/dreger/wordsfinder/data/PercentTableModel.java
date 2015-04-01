/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ua.dreger.wordsfinder.data;

import ua.dreger.wordsfinder.data.Word;
import java.util.Collections;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Sergey
 * Модель для таблицы с сортировкой слов по процентной схожести с исходным словом
 */
public class PercentTableModel extends AbstractTableModel{
	private List<Word> modelList;

	public PercentTableModel(List<Word> modelList) {
		this.modelList = modelList;
		sortModelList();
	}

	public void setModelList(List<Word> modelList) {
		this.modelList = modelList;
		sortModelList();
	}
	private void sortModelList(){
		Collections.sort(modelList);
	}
	
	@Override
	public int getRowCount() {
		return modelList.size();
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
			return modelList.get(rowIndex).getSimilarity() + " %";
		}else{
			return modelList.get(rowIndex).getType();
		}
	}

	@Override
	public String getColumnName(int column) {
		if(column == 0) return "Слово";
		if(column == 1 ) return "% схожести";
		else return "Часть речи";
	}
}