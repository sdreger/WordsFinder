/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ua.dreger.wordsfinder.utils;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import ua.dreger.wordsfinder.Main;
import ua.dreger.wordsfinder.MainFrame;

/**
 *
 * @author Sergey
 */
public class Settings {
	private MainFrame mainFrame;
	//дефолтные состояния чекбоксов
	private boolean[] checkBoxStatus = {true,true,true,true,true,true,true,true,true,true,true,true,true};

	/**
	 * Возвращает массив чекбоксов, считанных из файла настроек
	 * @return массив чекбоксов
	 */
	public boolean[] getCheckBoxStatus() {
		return checkBoxStatus;
	}
	
	public Settings(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}
	/**
	 * Установка положения и размеров окна, и установка состояния чекбоксов
	 */
	public void setup(){
//		Image img = Toolkit.getDefaultToolkit().getImage("pic/search.png");
//		mainFrame.setI/conImage(img);
		ImageIcon img = new ImageIcon(Settings.class.getResource("/ua/dreger/resources/search.png"),"Label");
		mainFrame.setIconImage(img.getImage());
		
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setTitle("Подбор слов v1.0");
		mainFrame.setMinimumSize(new Dimension(630, 485));
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Properties settings = loadSettings();
		if(settings == null){
			mainFrame.setBounds(10, 10, 850, 490);
		}else{
			try {
				int xPos = Integer.parseInt(settings.getProperty("xPos", "20"));
				int yPos = Integer.parseInt(settings.getProperty("yPos", "20"));
				int frameWidth = Integer.parseInt(settings.getProperty("frameWidth", "850"));
				int frameHeight = Integer.parseInt(settings.getProperty("frameHeight", "490"));
				mainFrame.setBounds(xPos, yPos, frameWidth, frameHeight);
			} catch (NumberFormatException e) {
				System.out.println("Wrong settings file");
				//установка размера фрейма в четверть экрана и вывод его в центре
				mainFrame.setBounds(screen.width/4, screen.height/4, screen.width/2, screen.height/2);
			}
			for (int i = 0; i < checkBoxStatus.length; i++) {
				checkBoxStatus[i] = (settings.getProperty("Checkbox"+i, "1").equals("1"));
			}
		}
		mainFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				saveSettings();
			}
		});
		
	}
	/**
	 * Загрузка установок их XML файла
	 * @return объект Properties инуапсулирующий считанные установки
	 */	
	private Properties loadSettings(){
		Properties prop = null;
		InputStream is = null;
		try {
			File inputFile = new File("settings.xml");
			is = new FileInputStream(inputFile);
			prop = new Properties();
			prop.loadFromXML(is);

		} catch (IOException e) {
			System.out.println("Error reading file: " + new File("settings.xml").getAbsolutePath());
		} finally {
			if(is != null){
				try {
					is.close();
				} catch (IOException ex) {
					Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
		return prop;
	}
	/**
	 * Сохранение положения и размеров окна, и состояний чекбоксов
	 */
	public void saveSettings() {
		Dimension frameSize = mainFrame.getSize();
		int frameWidth = frameSize.width;
		int frameHeight = frameSize.height;
		Properties prop = new Properties();
		prop.put("frameWidth", String.valueOf(frameWidth));
		prop.put("frameHeight", String.valueOf(frameHeight));
		prop.put("xPos", String.valueOf(mainFrame.getX()));
		prop.put("yPos", String.valueOf(mainFrame.getY()));
		JCheckBox[] checkBoxes = mainFrame.getCheckBoxes();
		if(checkBoxes[0] != null){
			for (int i = 0; i < checkBoxes.length; i++) {
				prop.put("Checkbox"+i, (checkBoxes[i].isSelected()) ? "1" : "0");
			}
		}
		OutputStream os = null;
		try{
			File outputFile = new File("settings.xml");
			os = new FileOutputStream(outputFile);
			prop.storeToXML(os, "Settings of Words Finder programm");
		} catch(IOException e){
			System.out.println("Error writing file");
		} finally {
			try {
				os.close();
			} catch (IOException ex) {
				Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
}
