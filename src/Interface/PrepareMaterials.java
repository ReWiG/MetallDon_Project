package Interface;

import Class.Material;
import Class.Project;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class PrepareMaterials extends JPanel implements ActionListener{

    static public JFrame frame;
    private JPanel panel;
    private JTable tablePrepare;
    private JScrollPane paneTable;
    static public PrepareTableModel prepModel;
    private JTextField tfName;
    private JTextField tfDebit;
    private JTextField tfPath;
    private JDialog modalDebitDialog;
    private JTextField modalDebitTextField;
    private JButton modalDebitBtn;
    private Integer row;
    private Project proj;
    public PrepareMaterials(Project proj, int Row) {
        this.row = Row;
        this.proj = proj;

        frame = new JFrame("Подготовить материал");

        panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 5, 20)); // Настройка отступов

        // добавление лэйблов
        panel.add(new JLabel("Название"), new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.NORTH,
            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));
        panel.add(new JLabel("Папка проекта"), new GridBagConstraints(3, 1, 1, 1, 0, 0, GridBagConstraints.NORTH,
            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));
        panel.add(new JLabel("Оприходованныые остатки"), new GridBagConstraints(3, 2, 1, 1, 0, 0, GridBagConstraints.NORTH,
            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));

        // Добавление текстовых полей
        tfName = addTextField(proj.name, true);
        tfPath = addTextField(proj.path, false);
        tfDebit = addTextField(proj.debitBalances, true);
        panel.add(tfName, new GridBagConstraints(4, 0, 1, 1, 0, 0, GridBagConstraints.BASELINE,
            GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
        panel.add(tfPath, new GridBagConstraints(4, 1, 1, 1, 0, 0, GridBagConstraints.BASELINE,
            GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
        panel.add(tfDebit, new GridBagConstraints(4, 2, 1, 1, 0, 0, GridBagConstraints.BASELINE,
            GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

        // Добавление кнопок
        JButton addMat = addButton("Добавить материал");
        panel.add(addMat, new GridBagConstraints(1, 5, 1, 1, 0, 0, GridBagConstraints.WEST,
            GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
        panel.add(addButton("Удалить материалы"), new GridBagConstraints(2, 5, 1, 1, 0, 0, GridBagConstraints.WEST,
            GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
        panel.add(addButton("Сохранить изменения"), new GridBagConstraints(4, 5, 1, 1, 0, 0, GridBagConstraints.WEST,
            GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

        // Создание модели, таблицы и заполнение значениями
        prepModel = new PrepareTableModel();
        tablePrepare = new JTable(prepModel);
        prepModel.setValue((ArrayList<Material>) proj.listMaterial.clone()); // Клонируем (используется для сохранения)

        // Настройка таблицы
        tablePrepare.setPreferredScrollableViewportSize(new Dimension(900, 150));
        tablePrepare.setFillsViewportHeight(true);

        // Добавление таблицы на Scroll Pane
        paneTable = new JScrollPane(tablePrepare);

        // Добавление панелей на фрейм
        frame.add(panel, BorderLayout.NORTH);
        frame.add(paneTable, BorderLayout.SOUTH);

        // настройка и отображение окна
        // отключаем операцию закрытия
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // добавляем слушателя событий от окна
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // потверждение выхода
                int res = JOptionPane.showConfirmDialog(null, "Все не сохранённые данные будут утеряны!",
                        "Действительно выйти?", JOptionPane.OK_CANCEL_OPTION);
                if (res == JOptionPane.YES_OPTION) {
                    frame.setVisible(false);
                    MainWindow.frame.setEnabled(true);
                    MainWindow.frame.toFront();
                }
            }
        });

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Если Оприходованные остатки не заполнены, то заполняем в отдельном окне
        if(proj.debitBalances == null) {
            modalDebitTextField = new JTextField(10);
            ((AbstractDocument)modalDebitTextField.getDocument()).setDocumentFilter(new DocumentFilter () {
                @Override
                public void insertString(FilterBypass fb, int offset, String str, AttributeSet attr) throws BadLocationException {
                    //fb.insertString(offset, str.replaceAll("\\D", ""), attr);
                    try {
                        if (str.equals(",") && !fb.getDocument().getText(0, fb.getDocument().getLength()).contains(",")) {
                            super.insertString(fb, offset, str, attr);
                            return;
                        }
                        Double.parseDouble(str);
                        super.insertString(fb, offset, str, attr);
                    } catch(Exception e) {
                        System.err.println(e);
                    }
                }

                @Override
                public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                    //fb.replace(offset, length, text.replaceAll("\\D", ""), attrs);
                    try {
                        if (text.equals(",")&& !fb.getDocument().getText(0, fb.getDocument().getLength()).contains(",")) {
                            super.insertString(fb, offset, text, attrs);
                            return;
                        }
                        Double.parseDouble(text);
                        super.replace(fb, offset, length, text, attrs);
                    } catch(Exception e) {
                        System.err.println(e);
                    }
                }
            });

            JPanel p = new JPanel();
            p.setLayout(new GridBagLayout());
            p.setBorder(new EmptyBorder(10, 10, 10, 10));
            p.add(new JLabel("Введите оприходованные остатки"), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.EAST,
            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0), 0);
            p.add(modalDebitTextField, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.EAST,
            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0), 0);
            modalDebitBtn = new JButton("OK");
            modalDebitBtn.setMinimumSize(new Dimension(30, 30));
            modalDebitBtn.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if(!"".equals(modalDebitTextField.getText())) {
                        tfDebit.setText(modalDebitTextField.getText());
                        tfDebit.setEnabled(false);
                        modalDebitDialog.setVisible(false);
                    } else {
                        System.err.println("Пустое значение");
                    }
                }
            });
            p.add(modalDebitBtn, new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.EAST,
            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0), 0);

            modalDebitDialog = new JDialog(frame, "Введите данные", false);
            modalDebitDialog.setContentPane(p);
            modalDebitDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            modalDebitDialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent we) {
                    // закрываем диалог и окно добавления материалов
                    modalDebitDialog.setVisible(false);
                    frame.setVisible(false);
                    MainWindow.frame.setEnabled(false);
                    MainWindow.frame.setEnabled(true);

                    System.err.println(we);
                    MainWindow.frame.toFront();
                }
            });

            modalDebitDialog.pack();
            modalDebitDialog.setLocationRelativeTo(null);
            modalDebitDialog.setVisible(true);

        }
    }

    /**
     * Добавление кнопок
     * @param text Название
     * @return Кнопка
     */
    private JButton addButton(String text) {
        JButton bt = new JButton(text);
        bt.addActionListener(this);
        bt.setHorizontalAlignment(SwingConstants.LEFT);
        return bt;
    }

    /**
     * Добавление TextField шириной 20 знаков
     * @param text Текст
     * @return TextField
     */
    private JTextField addTextField(String text, Boolean enabled) {
        JTextField tf = new JTextField(20);
        tf.setText(text);
        tf.setEnabled(enabled);
        return tf;
    }

    // Обработка событий
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        switch(command) {
            case "Добавить материал":
                // Открываем новое окно
                int row = prepModel.material.size(); // Номер добавляемой в будущем строки
                frame.setEnabled(false); // Делаем окно неактивным
                AddMaterial addMt = new AddMaterial();
                addMt.setVisible(true);
                System.out.println("Добавлена\n");
                break;
            case "Удалить материалы":
                Iterator<Material> iter = prepModel.material.iterator(); // Итератор
                ArrayList<Material> selectItems = new ArrayList<Material>(); // Список выделенных элементов
                // Есть ли элементы в таблице?
                if (iter.hasNext()) {
                    // Кидаем в список выделенные элементы
                    while(iter.hasNext()){
                        Material next = iter.next();
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
                                prepModel.material.remove(selectItems.get(i));
                            }
                            prepModel.updateData(); // Обновление данных
                            System.out.println("Удалены");
                            JOptionPane.showMessageDialog(null, "Удаление выполнено успешно! Сохраните результат!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Нет выделенных элементов для удаления.");
                    }

                } else {
                    System.out.println("Нет элементов для удаления\n");
                }
                break;
            case "Сохранить изменения":
                // Проверка, добавдлены ли материалы? (Для установки даты)
                Date dMat = null;
                if(!proj.listMaterial.equals(prepModel.material)) {
                    dMat = new Date();
                }
                // Обновление записей в модели таблицы родительского окна
                MainWindow.myModel.pr.set(this.row,
                        new Project(tfName.getText(), tfPath.getText(), tfDebit.getText(), proj.date,
                        dMat, prepModel.material));

                // MessageBox
                JOptionPane.showMessageDialog(null, "Сохраненение выполнено успешно!",
                        "Сохранено", JOptionPane.INFORMATION_MESSAGE);
                MainWindow.myModel.updateData(); // обновляем таблицу родительского окна

                // Закрываем окно после сохранения
                frame.setVisible(false);
                MainWindow.frame.setEnabled(true);
                MainWindow.frame.toFront();
                break;
            default:
                System.err.println("Неизвестная команда");
        }
    }

    /**
     * Модель таблицы PrepareMaterials
     */
    public class PrepareTableModel extends AbstractTableModel {
        private String[] columnNames = {"Код профиля",
                                        "Длина",
                                        "Ширина",
                                        "Высота",
                                        "Выделить"};

        protected ArrayList<Material> material;

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public int getRowCount() {
            try {
                return material.size();
            } catch (NullPointerException e) {
                System.err.println("В таблице Prepare нет элементов для отображения: " + e);
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
                    case 4: return Boolean.class;
                    default: return Object.class;
                }
            } catch(NullPointerException n) {
                System.err.println("NullPoint, ячейка не заполнена");
                if (c == 4){
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
                    return material.get(r).codeProfile;
                case 1:
                    return material.get(r).length;
                case 2:
                    return material.get(r).width;
                case 3:
                    return material.get(r).heigth;
                case 4:
                    return material.get(r).checkBox;
                default:
                    return "";
            }
        }

        /*
         * Определяет, какие столбцы разрешено редактировать
         */
        @Override
        public boolean isCellEditable(int row, int col) {
            if (col < 4) {
                return false;
            } else {
                return true;
            }
        }

        /**
         * Переопределение метода изменения значений
         * @param value True или False
         * @param row Не используется
         * @param col Не используется
         */
        @Override
        public void setValueAt(Object value, int row, int col) {
            switch (col) {
                case 0:
                    material.get(row).codeProfile = (String)value;
                    break;
                case 1:
                    material.get(row).length = (Double)value;
                    break;
                case 2:
                    material.get(row).width = (Double)value;
                    break;
                case 3:
                    material.get(row).heigth = (Double)value;
                    break;
                case 4:
                    material.get(row).checkBox = (Boolean)value;
                    break;
                default:
                    break;
            }
            fireTableCellUpdated(row, col);
        }

        /**
         * Добавление строки в таблицу
         * @param newPr Material
         */
        public void setValueAt(Material newMt) {
            material.add(newMt);//добавляем в наш ArrayList новые данные
            fireTableDataChanged();//обновляем данные
        }

        /**
         * Заполнение таблицы из ArrayList-a
         * @param value ArrayList<Material>
         */
        public void setValue(ArrayList<Material> value) {
           material = value;
        }

        /**
         * Обновление таблицы
         */
        public void updateData() {
           fireTableDataChanged();//обновляем данные
        }

    }
}
