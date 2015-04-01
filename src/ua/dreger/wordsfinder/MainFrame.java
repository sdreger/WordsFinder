/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ua.dreger.wordsfinder;

import ua.dreger.wordsfinder.data.Database;
import ua.dreger.wordsfinder.data.LengthTableModel;
import ua.dreger.wordsfinder.data.PercentTableModel;
import ua.dreger.wordsfinder.data.Word;
import ua.dreger.wordsfinder.utils.Settings;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

/**
 *
 * @author Sergey
 */
public class MainFrame extends JFrame{
	private JCheckBox[] checkBoxes = new JCheckBox[13];
	private JButton queryBytton = new JButton("Найти слова");
	private JTextField queryText = new JTextField(10);
	private Database db = new Database();
	private PercentTableModel model1;
	private LengthTableModel model2;
	private JTable table1;
	private JTable table2;
	private Box leftTableBox;
	private Box rightTableBox;
	private JLabel leftLabel;
	private JLabel rightLabel;
	private JLabel total;
//---------------------------------------------------------------------------//		
	/**
	 * Возвращает массив чекбоксов, для определения их состояние, при выполнении запрса к БД
	 * @return массив чекбоксов
	 */
	public JCheckBox[] getCheckBoxes() {
		return checkBoxes;
	}
//---------------------------------------------------------------------------//	
	public MainFrame() {
		initComponents();
	}
//---------------------------------------------------------------------------//	
	/**
	 * Отрисовывает весь GUI
	 */
	private void initComponents(){
		//загрузка размеров фрейма, положения на экране, и сост. флажков
		Settings settings = new Settings(this);
		settings.setup();
		//панель для управляющих элементов
		JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
		getContentPane().add(rightPanel, BorderLayout.EAST);
		//панель для флажков
		Box rightBox = new Box(BoxLayout.Y_AXIS);
		rightPanel.add(rightBox, BorderLayout.CENTER);
		//панель для поля ввода, JLabel кол-ва слов и кнопки запроса
		Box rightBotBox = new Box(BoxLayout.Y_AXIS);
		rightPanel.add(rightBotBox, BorderLayout.SOUTH);
		//создание массива флажков
		createCheckBoxes(settings.getCheckBoxStatus());
		//добавление из на панель
		for (int i = 0; i < checkBoxes.length; i++) {
			rightBox.add(checkBoxes[i]);
		}
		rightBox.add(Box.createVerticalStrut(10));
		rightBox.add(new JSeparator(JSeparator.HORIZONTAL));
		rightBox.add(Box.createVerticalStrut(10));
		//поле вывода кол-ва слов, которые вернул запрос
		total = new JLabel();
		total.setVisible(false);
		total.setBorder(BorderFactory.createEtchedBorder());
		rightBox.add(total);
		rightBox.add(Box.createVerticalStrut(10));
		//поле для ввода исходного слова
		rightBotBox.add(queryText);
		rightBotBox.add(Box.createVerticalStrut(10));
		//кнопка выполняющая запрос
		rightBotBox.add(queryBytton);
		queryBytton.setToolTipText("Выполнить запрос");
		rightBotBox.add(Box.createVerticalStrut(10));
		queryText.setHorizontalAlignment(JTextField.CENTER);
		queryBytton.setAlignmentX(0.5f);
		//привязка обработчиков событий
		bindListeners();
		//панель для таблиц
		Box tableBox = Box.createHorizontalBox();
		//панель для левой таблицы
		leftTableBox = Box.createVerticalBox();
		//панель для правой таблицы
		rightTableBox = Box.createVerticalBox();
		tableBox.add(leftTableBox);
		tableBox.add(rightTableBox);
		leftLabel = new JLabel("Таблица процентного соответствия");
		leftLabel.setVisible(false);
		leftLabel.setAlignmentX(0.5f);
		rightLabel = new JLabel("Таблица с сортировкой по длине");
		rightLabel.setVisible(false);
		rightLabel.setAlignmentX(0.5f);
		leftTableBox.add(leftLabel);
		rightTableBox.add(rightLabel);
		
		getContentPane().add(tableBox, BorderLayout.CENTER);
		setVisible(true);
	}
//---------------------------------------------------------------------------//		
	/**
	 * Назначает обработчики для управляющих элементов
	 */
	private void bindListeners(){
		queryText.addKeyListener(new EnterListener());
		queryBytton.addActionListener(new ButtonListener());
		CheckboxesListener checkboxesListener = new CheckboxesListener();
		for (JCheckBox jCheckBox : checkBoxes) {
			jCheckBox.addItemListener(checkboxesListener);
		}
	}
//---------------------------------------------------------------------------//		
	/**
	 * Запускает в отдельном потоке запрос к БД, создает,отрисовывает и обновляет таблицы результатов звпроса
	 * @param baseWord слово, по буквам которого, делается выборка (может содержать пробелы и быть в любом регистре)
	 */
	private void createTables(String baseWord){
		new Thread(new TablesCreator(baseWord.toLowerCase())).start();
	}
//---------------------------------------------------------------------------//		
	/**
	 * Создает массив чекбоксов с именами из БД, и состоянием и файла настроек
	 * в случае отсутствия файла настроек статус всек чекбокосв устанавливается checked
	 * в случае отсутствия соединения с БД выдает окно с сообщение о невозможности
	 * установки соединения и закрывает приложение
	 * @param checkBoxStatus 
	 */
	private void createCheckBoxes(boolean[] checkBoxStatus){
		List<String> labels;
		try {
			labels = db.getTypesNeames();
			for (int i = 0; i < checkBoxes.length; i++) {
			checkBoxes[i] = new JCheckBox(labels.get(i), checkBoxStatus[i]);
		}
		} catch (SQLException ex) {
//			Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
			System.out.println("Error Code: "+ex.getErrorCode()+"; Error Messge: "+ex.getMessage());
			emergencyExit();
		}
	}
//---------------------------------------------------------------------------//	
	/**
	 * Аварийно завершает приложение, возвращая код ошибки: -1
	 */
	private void emergencyExit(){
		JOptionPane.showMessageDialog(rootPane, "Ошибка соединения с БД, проложение будет закрыто!", "Ошибка соединения с БД", JOptionPane.ERROR_MESSAGE);
		System.exit(-1);
	}
//---------------------------------------------------------------------------//	
	/**
	 * Обработчик нажатия на кнопку "Выполнить запрос"
	 */
	private class ButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			createTables(queryText.getText());
		}
	}
//---------------------------------------------------------------------------//	
	/**
	 * Обработчик нажатия на кнопку "Enter" на клавиатуре
	 */
	private class EnterListener extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_ENTER)
				createTables(queryText.getText());
		}
	}
//---------------------------------------------------------------------------//	
	/**
	 * Обработчик изменения состояния чекбоксов
	 */
	private class CheckboxesListener implements ItemListener{
		@Override
		public void itemStateChanged(ItemEvent e) {
			createTables(queryText.getText());
		}
	}
//---------------------------------------------------------------------------//	
	/**
	 * Вополняет запрос к БД и, в случае успеха, на его основании формирует модели таблиц
	 * при первом вызове создаются таблицы и их модели, при последующих только 
	 * устанавлевается новый List<Word> в модеди таблиц и выполняется revalidate();
	 */
	private class TablesCreator implements Runnable{
		private String baseWord;

		public TablesCreator(String baseWord) {
			this.baseWord = baseWord;
		}
		private void setColumnSettings(){
			table1.setDefaultRenderer(table1.getColumnClass(1), new CenterAlignmentRenderer());
			table2.setDefaultRenderer(table2.getColumnClass(1), new CenterAlignmentRenderer());
			TableColumn leftTableColumn1 = table1.getColumnModel().getColumn(1);
			int ltabMidColumnWidth = 85;
			leftTableColumn1.setPreferredWidth(ltabMidColumnWidth);
			leftTableColumn1.setMinWidth(ltabMidColumnWidth);
			leftTableColumn1.setMaxWidth(ltabMidColumnWidth);
			TableColumn rightTableColumn1 = table2.getColumnModel().getColumn(1);
			int rtabMidColumnWidth = 70;
			rightTableColumn1.setPreferredWidth(rtabMidColumnWidth);
			rightTableColumn1.setMinWidth(rtabMidColumnWidth);
			rightTableColumn1.setMaxWidth(rtabMidColumnWidth);
//				table1.setFont(new Font("Sans-serif", Font.PLAIN, 18));
//				table1.setRowHeight(18);
//				JTableHeader header1 = table1.getTableHeader();
//				header1.setFont(new Font("Sans-serif", Font.PLAIN, 18));
//				table1.setTableHeader(header1);
		}
		@Override
		public void run() {
			final List<Word> wordList;
			try {
				wordList = db.getWords(baseWord.trim(), checkBoxes);
				//этот блок выполняется только при первом запросе
				if(model1 == null){
					model1 = new PercentTableModel(wordList);
					model2 = new LengthTableModel(wordList);
					table1 = new JTable(model1);
					table2 = new JTable(model2);
					setColumnSettings();
					JScrollPane scroll1 = new JScrollPane(table1);
					JScrollPane scroll2 = new JScrollPane(table2);
					leftTableBox.add(scroll1);
					rightTableBox.add(scroll2);
					leftLabel.setVisible(true);
					rightLabel.setVisible(true);
					total.setVisible(true);
				} else {
					model1.setModelList(wordList);
					model2.setModelList(wordList);
				}
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						total.setText("<html><p style='text-align:center;padding-left:15px;'>Запрос вернул<br> "+wordList.size()+" слов(а)</p></html>");
						table1.revalidate();
						table2.revalidate();
						revalidate();
					}
				});
			} catch (SQLException ex) {
//				Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
				System.out.println("Error Code: "+ex.getErrorCode()+"; Error Messge: "+ex.getMessage());
				emergencyExit();
			}
		}
	}
//---------------------------------------------------------------------------//	
	/**
	 * Рендер-класс, используемый для центрирования средних ячеек таблиц
	 */
	private class CenterAlignmentRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			setHorizontalAlignment(SwingConstants.CENTER);
			return this;
		}
    }
}