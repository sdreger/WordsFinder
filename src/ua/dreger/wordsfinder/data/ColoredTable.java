/*
 * How to control font style, color, and size inside a JTable?
 * http://stackoverflow.com/questions/7212114/how-to-control-font-style-color-and-size-inside-a-jtable
 */

package ua.dreger.wordsfinder.data;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableModel;

/**
 *
 * @author Sergey
 */
public class ColoredTable extends JTable{

	public ColoredTable(TableModel dm) {
		super(dm);
		setOpaque(false);
        ((JComponent) getDefaultRenderer(Object.class)).setOpaque(false);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Color background = new Color(168, 210, 241);
        Color controlColor = new Color(230, 240, 230);
        int width = getWidth();
        int height = getHeight();
        Graphics2D g2 = (Graphics2D) g;
        Paint oldPaint = g2.getPaint();
        g2.setPaint(new GradientPaint(0, 0, background, width, 0, controlColor));
        g2.fillRect(0, 0, width, height);
        g2.setPaint(oldPaint);
        for (int row : getSelectedRows()) {
            Rectangle start = getCellRect(row, 0, true);
            Rectangle end = getCellRect(row, getColumnCount() - 1, true);
            g2.setPaint(new GradientPaint(start.x, 0, controlColor, (int) ((end.x + end.width - start.x) * 1.25), 0, Color.orange));
            g2.fillRect(start.x, start.y, end.x + end.width - start.x, start.height);
        }
		super.paintComponent(g); //To change body of generated methods, choose Tools | Templates.
	}
	
}


/*
					//пример сортеров колонок таблицы
					table1.setAutoCreateRowSorter(true);
					TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(model1);
					table1.setRowSorter(sorter);
					sorter.setComparator(1, new Comparator<Float>() {
						@Override
						public int compare(Float o1, Float o2) {
							float tmp = o1 - o2;
							if(tmp == 0) return 0;
							else return (tmp > 0) ? 1 : -1;
						}
					});
					sorter.setComparator(0, new Comparator<String>() {
						@Override
						public int compare(String o1, String o2) {
							return o1.length() - o2.length();
						}
					});
*/