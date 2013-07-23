package Interface;

import Class.Project;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

public class MainWindow extends JPanel
                                implements ActionListener {
    // Таблица с проектами
    private JTable table;
    private JTextArea output;
    private JPanel buttonPanel;
    private JPanel labelPanel;
    static public MyTableModel myModel;
    static public JFrame frame;
    SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy"); // Формат даты, отображаемый в таблице

    // Конструктор
    public MainWindow(ArrayList<Project> pr) {
        super();

        // Добавлениие кнопок
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        addButton("Добавить проект", buttonPanel).setFocusable(false);
        buttonPanel.add(Box.createHorizontalStrut(5));
        addButton("Удалить проекты", buttonPanel).setFocusable(false);
        buttonPanel.add(Box.createHorizontalStrut(5));
        addButton("Сохранить проекты", buttonPanel).setFocusable(false);
        add(buttonPanel);

        // Создание таблицы
        myModel = new MyTableModel();
        table = new JTable(myModel); // Создаем таблицу
        myModel.setValue(pr); // Заполняем значениями

        // Настройка таблицы
        table.addMouseListener(new myMouseAdapter());
        table.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        table.setPreferredScrollableViewportSize(new Dimension(1000, 150));
        table.setFillsViewportHeight(true);
        add(new JScrollPane(table));

        // Подсказка под таблицей
        labelPanel = new JPanel(new GridBagLayout());
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.LINE_AXIS));
        JLabel jl = new JLabel("* Двойной клик по элементу таблицы открывает добавление материалов");
        labelPanel.add(Box.createHorizontalStrut(600));
        labelPanel.add(jl);
        add(labelPanel);

        // Окно вывода
        output = new JTextArea(5, 90);
        output.setEditable(false);
        add(new JScrollPane(output));
    }

    /**
     * Добавление кнопки
     * @param text Текст кнопки
     * @param p Панель
     * @return Кнопка
     */
    private JButton addButton(String text, JPanel p) {
        JButton bt = new JButton(text);
        bt.addActionListener(this);
        p.add(bt);
        return bt;
    }

    // Обработка событий формы
    @Override
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();

        switch(command) {
            case "Добавить проект":
                // Открываем новое окно
                frame.setEnabled(false); // Делаем окно неактивным
                AddProject addPr = new AddProject();
                addPr.setVisible(true);
                output.append("Добавлена\n");
                break;
            case "Удалить проекты":
                Iterator<Project> iter = myModel.pr.iterator(); // Итератор
                ArrayList<Project> selectItems = new ArrayList<Project>(); // Список выделенных элементов
                // Есть ли элементы в таблице?
                if (iter.hasNext()) {
                    // Кидаем в список выделенные элементы
                    while(iter.hasNext()){
                        Project next = iter.next();
                        if(next.checkBox == Boolean.TRUE) {
                            selectItems.add(next);
                        }
                    }

                    // Есть ли выделенные элементы?
                    if (!selectItems.isEmpty()) {
                        // подтверждение удаления
                        int res = JOptionPane.showConfirmDialog(null, "Вы действительно хотите удалить выделенные элементы?",
                                "Question", JOptionPane.YES_NO_OPTION);
                        if (res == JOptionPane.YES_OPTION) {
                            // Удаление из таблицы
                            for (int i = 0; i < selectItems.size(); i++) {
                                myModel.pr.remove(selectItems.get(i));
                            }
                            myModel.updateData(); // Обновление данных
                            output.append("Удалены\n");
                            JOptionPane.showMessageDialog(null, "Удаление выполнено успешно! Сохраните результат!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Нет выделенных элементов для удаления.");
                    }

                } else {
                    output.append("Нет элементов для удаления\n");
                }
                break;
            case "Сохранить проекты":
                try {
                    MainClass.serializable(myModel.pr);
                    JOptionPane.showMessageDialog(null, "Сохраненение выполнено успешно!",
                            "Сохранено", JOptionPane.INFORMATION_MESSAGE);
                    System.out.println("Сохранено");
                    output.append("Сохранено\n");
                } catch (IOException e) {
                    System.err.println("Ошибка записи в файл проектов dbProject.db: " + e);
                    JOptionPane.showMessageDialog(null, "Текст ошибки: " + e,
                            "Ошибка сохранения, повторите попытку!", JOptionPane.ERROR_MESSAGE);
                }
                break;
            default:
                System.err.println("Неизвестная команда");
        }
    }

    // Обработка двойного клика по таблице
    private class myMouseAdapter extends MouseAdapter{
        @Override
        public void mouseClicked(MouseEvent e)
        {
            if (e.getClickCount() == 2)
            {
                Point p = e.getPoint();
                int row = table.rowAtPoint(p);
                int column = table.columnAtPoint(p);
                output.append("Double click on " + column + " column, " + row + " row\n");

                // Открываем новое окно(клонируем и передаем объект Project, номер строки)
                PrepareMaterials prep;
                try {
                    prep = new PrepareMaterials(myModel.pr.get(row).clone(), row);
                } catch (CloneNotSupportedException ex) {
                    System.err.println("Ошибка клонирования объекта Project:" + ex);
                }


                frame.setEnabled(false);
            }
        }
    }

    /**
     * Модель таблицы
     */
    class MyTableModel extends AbstractTableModel {
        private String[] columnNames = {"Наименование",
                                        "Папка проекта",
                                        "Оприходованные остатки",
                                        "Дата",
                                        "Дата подготовки материалов",
                                        "Выделить"};

        protected ArrayList<Project> pr;

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public int getRowCount() {
            try {
                return pr.size();
            } catch (NullPointerException e) {
                System.err.println("В таблице Project нет элементов для отображения: " + e);
                return 0;
            }
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }

        @Override
        public Class getColumnClass(int c) {
            try {
                switch(c) {
                    case 0: return String.class;
                    case 1: return String.class;
                    case 2: return String.class;
                    case 3: return String.class;
                    case 4: return String.class;
                    case 5: return Boolean.class;
                    default: return Object.class;
                }
            } catch(NullPointerException n) {
                System.err.println("NullPoint, ячейка не заполнена");
                if (c == 5){
                    return Boolean.class;
                } else {
                    return Object.class;
                }
            }
        }

        @Override
        public Object getValueAt(int r, int c) {
            switch (c) {
                case 0:
                    return pr.get(r).name;
                case 1:
                    return pr.get(r).path;
                case 2:
                    return pr.get(r).debitBalances;
                case 3:
                    if(pr.get(r).date != null) {
                        return dateFormat.format(pr.get(r).date);
                    } else {
                        return "";
                    }
                case 4:
                    if(pr.get(r).dateMaterials != null) {
                        return dateFormat.format(pr.get(r).dateMaterials);
                    } else {
                        return "";
                    }
                case 5:
                    return pr.get(r).checkBox;
                default:
                    return "";
            }
        }

        /*
         * Определяет, какие столбцы разрешено редактировать
         */
        @Override
        public boolean isCellEditable(int row, int col) {
            if (col < 5) {
                return false;
            } else {
                return true;
            }
        }

        /**
         * Переопределение метода, используется для выделения CheckBox-ов
         * @param value True или False
         * @param row Не используется
         * @param col Не используется
         */
        @Override
        public void setValueAt(Object value, int row, int col) {
            if(col == 5) {
                pr.get(row).checkBox = (Boolean)value;
            }
            fireTableCellUpdated(row, col);
        }

        /**
         * Добавление строки в таблицу
         * @param newPr Project
         */
        public void setValueAt(Project newPr) {
            pr.add(newPr);//добавляем в наш ArrayList новые данные
            fireTableDataChanged();//обновляем данные
        }

        /**
         * Заполнение таблицы из ArrayList-a
         * @param value ArrayList<Project>
         */
        public void setValue(ArrayList<Project> value) {
            pr = value;
        }

        /**
         * Обновление таблицы
         */
        public void updateData() {
            fireTableDataChanged();//обновляем данные
        }

    }

    public static void main(ArrayList<Project> data) {
        //Disable boldface controls.
        UIManager.put("swing.boldMetal", Boolean.FALSE);

        //Создание и настройка окна.
        frame = new JFrame("Список проектов");
        frame.setPreferredSize(new Dimension(1050, 400));

        // отключаем операцию закрытия
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // добавляем слушателя событий от окна
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // потверждение выхода
                int res = JOptionPane.showConfirmDialog(null, "Все не сохранённые данные будут утеряны!",
                        "Действительно выйти?", JOptionPane.OK_CANCEL_OPTION);
                if (res == JOptionPane.YES_OPTION)
                    System.exit(0);
            }
        });

        //Создание и настройка панели контента.
        MainWindow newContentPane = new MainWindow(data);
        frame.setContentPane(newContentPane);

        //Отображение окна.
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
