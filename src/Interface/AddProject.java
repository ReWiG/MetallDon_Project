package Interface;

import Class.Material;
import Class.Project;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class AddProject extends JPanel implements ActionListener{
    private JFrame frame;
    private JPanel cards; // Панель Визарда
    private JPanel control; // Панель кнопок "Далее", "Назад", Прогресс бара
    private JButton nextBtn;
    private JButton prevBtn;
    private JTextField nameProject; // Текстовое поле Название проекта
    private JFileChooser fch;

    // Обработка события отображения панелей визарда
    private ComponentAdapter adaptPanel = new ComponentAdapter() {
        @Override
        public void componentShown(ComponentEvent e) {
            switch(e.getComponent().getName()) {
                case "p1":
                    prevBtn.setEnabled(false);
                    nextBtn.setText("Далее >");
                    break;
                case "p2":
                    prevBtn.setEnabled(true);
                    // Проверка заполнения поля Название проекта
                    if(nameProject.getText().isEmpty()) {
                        prevBtn.doClick();
                        JOptionPane.showMessageDialog(null, "Вы не ввели название проекта");
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
     * @param proj Пустой, только что добавленный проект
     * @param row Номер строки в таблице
     */
    public AddProject() {

        frame = new JFrame("Добавить проект");

        cards = new JPanel(new CardLayout());
        cards.setBorder(new EmptyBorder(20, 20, 10, 20)); // Настройка отступов

        // Панель 1
        JPanel p1 = new JPanel();
        p1.addComponentListener(adaptPanel);
        p1.setName("p1");
        p1.add(new JLabel("Введите название проекта"));
        p1.add(nameProject = addTextField(""));

        // Фокус на TextField
        frame.addWindowListener( new WindowAdapter() {
            @Override
            public void windowOpened( WindowEvent e ){
                nameProject.requestFocus();
            }
        });

        // Панель 2
        JPanel p2 = new JPanel(new GridBagLayout());
        p2.addComponentListener(adaptPanel);
        p2.setName("p2");
        p2.add(new JLabel("Выберите папку с NC - файлами:"), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.EAST,
            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0), 0);

        // настройка FileChooser
        fch = new JFileChooser();
        fch.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fch.setCurrentDirectory(new File(System.getProperty("user.home"))); // Директория, отображаемая по умолчанию
        fch.setAcceptAllFileFilterUsed(false);
        fch.setControlButtonsAreShown(false);

        p2.add(fch, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST,
            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0), 0);

        // Добавление панедей Визарда
        cards.add(p1);
        cards.add(p2);

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
                    MainWindow.frame.setEnabled(true);
                    MainWindow.frame.toFront();
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
                // Выбран ли путь к файлам?
                try {
                    String path = fch.getSelectedFile().toString() + "\\"; // Выбранный путь к папке

                    // Обновление записей в модели таблицы родительского окна
                    MainWindow.myModel.pr.add(new Project(nameProject.getText(),
                            path, null, new Date(), null, new ArrayList<Material>()));
                    MainWindow.myModel.updateData(); // обновляем таблицу родительского окна

                    // Убираем кнопки, отключаем окно
                    prevBtn.setVisible(false);
                    nextBtn.setVisible(false);
                    frame.setEnabled(false);

                    // Прогресс бар
                    JProgressBar progressBar = new JProgressBar();
                    progressBar.setPreferredSize(new Dimension(250, 20)); // Размер прогресс бара
                    progressBar.setStringPainted(true);

                    // Добавление
                    control.add(new JLabel("Пожалуйста подождите... Идет обработка  "), 0);
                    control.add(progressBar);

                    // Запускаем сортировку файлов в отдельном потоке (там же обновляется прогресс бар)
                    new Thread(new MyThread(path, progressBar)).start();

                } catch(NullPointerException exp) {
                    JOptionPane.showMessageDialog(null, "Вы не выбрали путь к файлам NC");
                    System.err.println(exp);
                }
                break;
            default:
                System.err.println("Неизвестная команда");
        }
    }

    /**
     * Реализация потока сортировки файлов NC
     */
    public class MyThread implements Runnable {

      public String path;
      public JProgressBar pb;

      // Конструктор
      public MyThread(String path, JProgressBar pb) {
          this.path = path;
          this.pb = pb;
      }

      @Override
      public void run() {
          try {
              MainClass.sortNC(path, pb); // Запуск функции сортировки

              // По окончании закрываем окно и возвращаем родитетельское окно
              frame.setVisible(false);
              MainWindow.frame.setEnabled(true);
              MainWindow.frame.toFront();
          } catch (IOException ex) {
              // В случае неверного выбора папки
              JOptionPane.showMessageDialog(null, "В выбранной папке не найдены файлы NC.\nПожалуйста, повторите выбор");
              control.remove(0); // Удаляем JLabel
              // Возвращаем элементы, убираем прогрессбар
              frame.setEnabled(true);
              prevBtn.setVisible(true);
              nextBtn.setVisible(true);
              pb.setVisible(false);

              System.err.println(ex);
          }
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
