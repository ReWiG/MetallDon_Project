package Interface;

import Class.Material;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class AddMaterial extends JPanel implements ActionListener{
    private JFrame frame;
    private JPanel cards; // Панель Визарда
    private JPanel control; // Панель кнопок "Далее", "Назад", Прогресс бара
    private JButton nextBtn;
    private JButton prevBtn;
    private JComboBox comboBox; // Код профиля
    private JTextField lengthMaterialTf; // Текстовое поле Длина материала
    private JTextField widthMaterialTf; // Текстовое поле Ширина проекта
    private JTextField heightMaterialTf; // Текстовое поле Высота проекта

    // Обработка события отображения панелей визарда
    private ComponentAdapter adaptPanel = new ComponentAdapter() {
        @Override
        public void componentShown(ComponentEvent e) {
            switch(e.getComponent().getName()) {
                case "p1":
                    prevBtn.setEnabled(false);
                    nextBtn.setText("Далее >");
                    // Фокус на Combobox
                    frame.addWindowListener( new WindowAdapter() {
                        @Override
                        public void windowOpened( WindowEvent e ){
                            comboBox.requestFocus();
                        }
                    });
                    break;
                case "p2":
                    prevBtn.setEnabled(true);
                    // Фокус на TextBox
                    frame.addWindowListener( new WindowAdapter() {
                        @Override
                        public void windowOpened( WindowEvent e ){
                            lengthMaterialTf.requestFocusInWindow();
                        }
                    });
                    break;
                case "p3":
                    // Фокус на TextBox
                    frame.addWindowListener( new WindowAdapter() {
                        @Override
                        public void windowOpened( WindowEvent e ){
                            widthMaterialTf.requestFocus();
                        }
                    });
                    // Проверка заполнения поля Длина материала
                    if(lengthMaterialTf.getText().isEmpty()) {
                        prevBtn.doClick();
                        JOptionPane.showMessageDialog(null, "Вы не ввели Длину материала");
                    }
                    break;
                case "p4":
                    // Фокус на TextBox
                    frame.addWindowListener( new WindowAdapter() {
                        @Override
                        public void windowOpened( WindowEvent e ){
                            heightMaterialTf.requestFocus();
                        }
                    });
                    nextBtn.setText("Далее >");
                    // Проверка заполнения поля Ширина материала
                    if(widthMaterialTf.getText().isEmpty()) {
                        prevBtn.doClick();
                        JOptionPane.showMessageDialog(null, "Вы не ввели Ширину материала");
                    } else {
                        nextBtn.setText("Готово");
                    }
                    break;
                default:
                    System.err.println("Неизвестное имя панели");
            }
        }
    };

    /**
     * Конструктор
     * @param proj Пустой, только что добавленный материал
     * @param row Номер строки в таблице
     */
    public AddMaterial() {

        frame = new JFrame("Добавить материал");

        cards = new JPanel(new CardLayout());
        cards.setBorder(new EmptyBorder(20, 20, 10, 20)); // Настройка отступов

        // Панель 1
        JPanel p1 = new JPanel();
        p1.addComponentListener(adaptPanel);
        p1.setName("p1");
        comboBox = new JComboBox();
        comboBox.addItem("I - профиль");
        comboBox.addItem("U - профиль");
        comboBox.addItem("RU - профиль");
        comboBox.addItem("RO - профиль");
        comboBox.addItem("M - профиль");
        comboBox.addItem("L - профиль");
        comboBox.addItem("C - профиль");
        comboBox.addItem("T - профиль");
        comboBox.addItem("SO - профиль");
        comboBox.addItem("B - профиль");
        p1.add(new JLabel("Выберите код профиля"));
        p1.add(comboBox);

        // Панель 2
        JPanel p2 = new JPanel();
        p2.addComponentListener(adaptPanel);
        p2.setName("p2");
        p2.add(new JLabel("Введите длину материала"));
        p2.add(lengthMaterialTf = addTextField(""));

        // Панель 3
        JPanel p3 = new JPanel();
        p3.addComponentListener(adaptPanel);
        p3.setName("p3");
        p3.add(new JLabel("Введите ширину материала"));
        p3.add(widthMaterialTf = addTextField(""));

        // Панель 4
        JPanel p4 = new JPanel();
        p4.addComponentListener(adaptPanel);
        p4.setName("p4");
        p4.add(new JLabel("Введите высоту материала"));
        p4.add(heightMaterialTf = addTextField(""));

        // Добавление панедей Визарда
        cards.add(p1);
        cards.add(p2);
        cards.add(p3);
        cards.add(p4);

        // Панель кнопок
        control = new JPanel();
        control.setBorder(new EmptyBorder(20, 20, 10, 20)); // Настройка отступов

        control.add(prevBtn = addButton("< Назад"));
        control.add(nextBtn = addButton("Далее >"));
        prevBtn.setEnabled(false); // По умолчанию кнопка назад не активна

        // Добавление панелей на фрейм
        frame.add(cards, BorderLayout.NORTH);
        frame.add(control, BorderLayout.SOUTH);

        // настройка и отображение окна
        // отключаем операцию закрытия
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // добавляем слушателя событий от окна
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // потверждение выхода
                int res = JOptionPane.showConfirmDialog(null, "Все изменения будут утеряны, закрыть?",
                        "Question", JOptionPane.YES_NO_OPTION);
                if (res == JOptionPane.YES_OPTION) {
                    // Закрытие окна
                    frame.setVisible(false);
                    PrepareMaterials.frame.setEnabled(true);
                    PrepareMaterials.frame.toFront();
                }
            }
        });

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Обработка событий
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        CardLayout cl = (CardLayout) cards.getLayout();
        switch(command) {
            case "< Назад":
                cl.previous(cards);
                break;
            case "Далее >":
                cl.next(cards);
                break;
            case "Готово":
                // Заполнение последнего поля проверяем тут
                // Проверка заполнения поля Высота материала
                if(heightMaterialTf.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Вы не ввели Высоту материала");
                } else {
                    // Обновление записей в модели таблицы родительского окна
                    PrepareMaterials.prepModel.material.add(new Material(comboBox.getSelectedItem().toString(),
                            Double.valueOf(lengthMaterialTf.getText()),
                            Double.valueOf(widthMaterialTf.getText()),
                            Double.valueOf(heightMaterialTf.getText())));
                    PrepareMaterials.prepModel.updateData(); // обновляем таблицу родительского окна

                    // Убираем окно, возвращаем родительское
                    frame.setVisible(false);
                    PrepareMaterials.frame.setEnabled(true);
                    PrepareMaterials.frame.toFront();
                }
                break;
            default:
                System.err.println("Неизвестная команда");
        }
    }

    /**
     * Добавление кнопок
     * @param text Название
     * @return Кнопка
     */
    public JButton addButton(String text) {
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
    private JTextField addTextField(String text) {
        JTextField tf = new JTextField(20);
        tf.setText(text);
        return tf;
    }
}
