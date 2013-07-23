package Interface;

import Class.Project;
import Class.Detail;
import Class.Material;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import javax.swing.JProgressBar;
import org.apache.commons.io.FileUtils;

public class MainClass {
    // Список папок\оборудования
    static public String paths[] = {
        "/result",
        "/result/VB1250",
        "/result/V630",
        "/result/V550-4",
        "/result/VB1550",
        "/result/V505",
        "/result/V250",
        "/result/FICEP HP12T4",
        "result/FALSE"
    };
    // Расширение файлов для чтения
    static String fileExtension = ".nc";
    // Файл проектов
    static public String dbProject = "C:/dbProject.db";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
        // Раскомментировать при сборке
        //System.setOut(new PrintStream(System.out, true, "cp866"));

//        ArrayList<Project> listProject = new ArrayList<Project>();
//        ArrayList<Material> listMaterial = new ArrayList<Material>();
//        listMaterial.add(new Material(dbProject, Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE));
//        listMaterial.add(new Material(dbProject, Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE));
//        listMaterial.add(new Material(dbProject, Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE));
//        listMaterial.add(new Material(dbProject, Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE));
//        listMaterial.add(new Material(dbProject, Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE));
//        listMaterial.add(new Material(dbProject, Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE));
//        listMaterial.add(new Material(dbProject, Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE));
//
//        listProject.add(new Project(dbProject, dbProject, dbProject, new Date(), new Date(), null));
//        listProject.add(new Project(dbProject, dbProject, dbProject, new Date(2012, 12, 1, 13, 49, 1), new Date(), null));
//        listProject.add(new Project(dbProject, dbProject, dbProject, new Date(2012, 12, 2, 13, 49, 2), new Date(), null));
//        listProject.add(new Project(dbProject, dbProject, "2535232436", new Date(2012, 12, 3, 13, 49, 3), new Date(), listMaterial));
//        listProject.add(new Project(dbProject, dbProject, dbProject, new Date(2012, 12, 4, 13, 49, 4), null, null));
//        listProject.add(new Project(dbProject, dbProject, dbProject, new Date(2012, 12, 5, 13, 50, 5), new Date(), null));
//        serializable(listProject);


        // Чтение из файла и передача в окно
        ArrayList<Project> pr2 = deserializable();
        MainWindow.main(pr2);
    }

    /**
     * Сериализация объекта
     * @param pr Объект
     * @throws IOException
     */
    public static void serializable(ArrayList<Project> pr) throws IOException {
        FileOutputStream fos = new FileOutputStream(dbProject);
        try (ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(pr);
            oos.flush();
        }
    }

    /**
     * Десериализация объекта
     * @return Объект
     */
    public static ArrayList<Project> deserializable() throws IOException, ClassNotFoundException {
        try {
            FileInputStream fis = new FileInputStream(dbProject);
            ObjectInputStream oin = new ObjectInputStream(fis);
            return (ArrayList<Project>) oin.readObject();
        } catch(Exception e) {
            System.err.println("Ошибка чтения файла проектов dbProject.db: " + e);
            return new ArrayList<Project>();
        }
    }

    /**
     * Сортировка NC-файлов, создание объектов
     * @throws IOException
     */
    public static Boolean sortNC(String workDir, JProgressBar pb) throws IOException{
         // Фильтр файлов (.NC)
        FilenameFilter fil = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                try {
                    return name.endsWith(fileExtension);
                } catch (UnsupportedOperationException e) {
                    System.out.println("NO FILE or Not supported yet." + e);
                    return false;
                }
            }
        };

        // Список имён файлов
        String listNameFiles[] = new File(workDir).list(fil);
        if (listNameFiles.length == 0) {
            throw new FileNotFoundException("NC Files Found");
        }

        // Удаляем и создаем директории
        for (int i = 0; i < paths.length; i++) {
            if (new File(workDir + paths[i]).exists()) {
                deleteDirectory(new File(workDir + paths[i]));
            }
            // Создаем
            Files.createDirectories(Paths.get(workDir + paths[i]));
        }

        /**
         * Список объектов Detail
         */
        List<Detail> det = new ArrayList<Detail>();

        // Настройка минимума и максимума прогресс бара
        pb.setMinimum(0);
        pb.setMaximum(listNameFiles.length);

        // Читаем файлы и создаём объекты
        for (int i = 0; i < listNameFiles.length; i++) {

            // Обновление прогресс бара
            pb.setString("Обработано " + (i+1) + " из " + listNameFiles.length + " файлов");
            pb.setValue(i);

            det.add(createObject(workDir, listNameFiles[i]));

            Boolean arr[];
            arr = new Boolean[7];
            arr[0] = det.get(i).VB1250();
            arr[1] = det.get(i).V630();
            arr[2] = det.get(i).V5504();
            arr[3] = det.get(i).VB1550();
            arr[4] = det.get(i).V505();
            arr[5] = det.get(i).V250();
            arr[6] = det.get(i).HP12T4();
            if(
                    (arr[0] == Boolean.FALSE)&&
                    (arr[1] == Boolean.FALSE)&&
                    (arr[2] == Boolean.FALSE)&&
                    (arr[3] == Boolean.FALSE)&&
                    (arr[4] == Boolean.FALSE)&&
                    (arr[5] == Boolean.FALSE)&&
                    (arr[6] == Boolean.FALSE)
            ) {
                det.get(i).writeFile(8);
            }
        }

        return true;
    }

    /**
     * Создает объекты по данным NC файлов
     * @param dir Файл NC
     * @return Объект Detail
     */
    public static Detail createObject(String workdir, String nameFile) throws IOException{
        try {
            File f = new File(workdir+nameFile);
            List<String> fileList = FileUtils.readLines(f);

            return new Detail(nameFile,
                    fileList.get(7).trim(),
                    fileList.get(8).trim(),
                    fileList.get(9).trim(),
                    fileList.get(10).trim(),
                    fileList.get(11).trim(),
                    fileList.get(12).trim(),
                    fileList.get(13).trim(),
                    fileList.get(14).trim(),
                    fileList.get(15).trim(),
		    fileList.get(17).trim(),
		    fileList.get(18).trim(),
		    fileList.get(19).trim(),
		    fileList.get(20).trim(),
                    workdir);
        } catch(IOException e) {
            throw new IOException("Error creating object 'Details': " + e);
        }
    }

    /**
     * Удаляет директорию вместе с подпапками и файлами
     * @param dir Директория для удаления
     */
    public static void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                File f = new File(dir, children[i]);
                deleteDirectory(f);
            }
            dir.delete();
        } else {
            dir.delete();
        }
    }
}