/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ua.dreger.wordsfinder.data;

import ua.dreger.wordsfinder.utils.StringProcessing;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;

/**
 * Класс для работы с БД SQLite
 * @author Sergey
 */
public class Database {
	private Connection con = null;
	//---------------------------------------------------------------------------//	
	public Database() {
		try {
			Class.forName("org.sqlite.JDBC");
			con = openConnection();
		} catch (ClassNotFoundException e) {
			System.out.println("Database driver was not found!");
		}
	}
//---------------------------------------------------------------------------//
	private Connection openConnection() {
		try {
			String url = "jdbc:sqlite:HagenDict.db";
			con = DriverManager.getConnection(url);
		} catch (SQLException  e) {
			System.out.println("Database connection error!");
			System.out.println(e.getMessage());
		}
		return con;
	}
//---------------------------------------------------------------------------//
	/**
	 * Выполняет запрос списка частей речи, присутствующих в базе
	 * @return возвращает список строк, содержащих названия частей речи
	 * @throws SQLException
	 */
	public List<String> getTypesNeames() throws SQLException{
		ArrayList<String> names = new ArrayList<String>();
		Statement typesStatement = con.createStatement();
		ResultSet typesSet = typesStatement.executeQuery("SELECT description FROM word_types WHERE 1");
		while(typesSet.next()){
			names.add(typesSet.getString("description"));
		}
		typesStatement.close();
		return names;
	}
//---------------------------------------------------------------------------//
	/**
	 * Вополняет основной запрос слов которые можно составить из букв базового слова 
	 * @param baseWord базвое слово, на основании которого выполняется запрос
	 * @param checkBoxes массив чекбоксов, на основании которого фильтруются результаты запроса
	 * @return список объектов Word инкапсулирующих в себе слово, часть речи, процент схожести с базовым словом
	 * @throws SQLException 
	 * @see Word
	 */
	public List<Word> getWords(String baseWord, JCheckBox[] checkBoxes) throws SQLException{
		List<Word> wordList = new ArrayList<Word>();
		StringBuilder request = new StringBuilder("SELECT words.word, word_types.description ");
				request.append("FROM `letters_count` ");
				request.append("INNER JOIN words ON words.word_id = letters_count.word_id ");
				request.append("INNER JOIN word_types ON word_types.type_id = words.type ");
				request.append("WHERE a <=? ");
				request.append("AND b <=? ");
				request.append("AND v <=? ");
				request.append("AND g <=? ");
				request.append("AND d <=? ");
				request.append("AND je <=? ");
				request.append("AND zh <=? ");
				request.append("AND z <=? ");
				request.append("AND i <=? ");
				request.append("AND jj <=? ");
				request.append("AND k <=? ");
				request.append("AND l <=? ");
				request.append("AND m <=? ");
				request.append("AND n <=? ");
				request.append("AND o <=? ");
				request.append("AND p <=? ");
				request.append("AND r <=? ");
				request.append("AND s <=? ");
				request.append("AND t <=? ");
				request.append("AND u <=? ");
				request.append("AND f <=? ");
				request.append("AND kh <=? ");
				request.append("AND c <=? ");
				request.append("AND ch <=? ");
				request.append("AND sh <=? ");
				request.append("AND shch <=? ");
				request.append("AND tvzn <=? ");
				request.append("AND y <=? ");
				request.append("AND mzn <=? ");
				request.append("AND e <=? ");
				request.append("AND ju <=? ");
				request.append("AND ja <=? ");
				request.append("AND defis <=? AND (");
		boolean oneOrMoreCheckBoxSet = false;
		for (JCheckBox checkBox : checkBoxes) {
			if(checkBox.isSelected()){
				oneOrMoreCheckBoxSet = true;
				request.append("description = '"+checkBox.getText()+"' OR ");
			}
		}
		//если хотя бы один чекбокс был отмечен
		if(oneOrMoreCheckBoxSet)
			request.replace(request.length()-4, request.length(),")");
		//если ни один чекбокс не был отмечен
		else 
			request.replace(request.length()-6, request.length()," AND 1 = 0");
//			System.out.println(request);
		PreparedStatement wordsListStatement = con.prepareStatement(request.toString());
		//составление массива кол-ва символов в исходном слове
		//пример: слово "кошка"
		//[а,б,в,г,д,е,ж,з,и,й,к,л,м,н,о,п,р,с,и,у,ф,х,ц,ч,ш,щ,ъ,ы,ь,э,ю,я]
		//[1,0,0,0,0,0,0,0,0,0,2,0,0,0,1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0]
		int[] wordChars = StringProcessing.charsCount(baseWord, false);
		//биндинг параметров подготовленного запроса
		for (int i = 0; i < wordChars.length; i++) {
			wordsListStatement.setInt(i+1, wordChars[i]);
		}
		ResultSet wordsSet = wordsListStatement.executeQuery();
		//создание списка объектов типа Word
		while(wordsSet.next()){
			wordList.add(new Word(wordsSet.getString("word"), wordsSet.getString("description"), baseWord));
		}
		return wordList;
	}
//---------------------------------------------------------------------------//
	
	
}
/*
SELECT words.word, word_types.description
FROM `letters_count`
INNER JOIN words ON words.word_id = letters_count.word_id
INNER JOIN word_types ON word_types.type_id = words.type
WHERE a <=0
AND b <=0
AND v <=1
AND g <=0
AND d <=0
AND je <=2
AND zh <=0
AND z <=0
AND i <=0
AND jj <=0
AND k <=0
AND l <=1
AND m <=1
AND n <=0
AND o <=1
AND p <=0
AND r <=0
AND s <=0
AND t <=0
AND u <=0
AND f <=0
AND kh <=0
AND c <=0
AND ch <=0
AND sh <=0
AND shch <=0
AND tvzn <=0
AND y <=0
AND mzn <=0
AND e <=0
AND ju <=0
AND ja <=0
AND defis <=0
AND (description = 'Существительное'
OR description = 'Предлог')
LIMIT 0 , 50

*/