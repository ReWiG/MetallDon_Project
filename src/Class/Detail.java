package Class;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import Interface.MainClass;

/**
 * Класс Деталь
 * @author asup
 */
public class Detail {
    /**
     * Название файла
     */
    public String nameFile;
    /**
     * Идентификация заказа
     */
    public String orderIdentifications;
    public int drawingIdentifications;
    public int phaseIdentifications;
    public int pieceIdentifications;
    /**
     * Качество стали
     */
    public String steelQuality;
    /**
     * Количество штук
     */
    public int quantityOfPieces;
    /**
     * Профиль
     */
    public String profile;
    /**
     * Код профиля
     */
    public String codeProfile;
    /**
     * Длина, длина пилы
     */
    public String[] length_saw;
    /**
     * Высота профиля
     */
    public double profileHeight;
    /**
     * Ширина фланца
     */
    public double flangeWidth;
    /**
     * Толщина фланца
     */
    public double flangeThickness;
    /**
     * Толщина листа
     */
    public double webThickness;
    /**
     * Радиус
     */
    public double radius;
    /**
     * Вес на метр
     */
    public double weightByMeter;
    /**
     * Поверхность метр(Площадь?)
     */
    public double paintingSurfaceByMeter;
    /**
     * Угол (Начало детали)
     */
    public double webStartCut;
    /**
     * Угол (Начало детали)
     */
    public double webEndCut;
    /**
     * Угол (конец детали)
     */
    public double flangeStartCut;
    /**
     * Угол (Конец детали)
     */
    public double flangeEndCut;
    /**
     * Рабочая папка
     */
    public String workDir;
    /**
     * Конструктор
     * @param nameFile Название файла
     * @param profile Профиль
     * @param codeProfile Код профиля
     * @param length_saw Длина, длина пилы
     * @param profileHeight Высота профиля
     * @param flangeWidth Ширина фланга
     * @param flangeThickness Толщина фланга
     * @param webThickness Толщина листа
     * @param radius Радиус
     * @param weightByMeter Вес на метр
     * @param WorkDir Рабочая папка
     */
    public Detail(String nameFile,
            String profile,
            String codeProfile,
            String length_saw,
            String profileHeight,
            String flangeWidth,
            String flangeThickness,
            String webThickness,
            String radius,
            String weightByMeter,
	    String webStartCut,
	    String webEndCut,
	    String flangeStartCut,
	    String flangeEndCut,
            String WorkDir) {
        this.nameFile = nameFile;
        this.profile = profile;
        this.codeProfile = codeProfile;
        this.length_saw = length_saw.split(",");
        this.profileHeight = Double.valueOf(profileHeight);
        this.flangeWidth = Double.valueOf(flangeWidth);
        this.flangeThickness = Double.valueOf(flangeThickness);
        this.webThickness = Double.valueOf(webThickness);
        this.radius = Double.valueOf(radius);
        this.weightByMeter = Double.valueOf(weightByMeter);
	this.webStartCut = Double.valueOf(webStartCut);
	this.webEndCut = Double.valueOf(webEndCut);
	this.flangeStartCut = Double.valueOf(flangeStartCut);
	this.flangeEndCut = Double.valueOf(flangeEndCut);
        this.workDir = WorkDir;
    }

    /**
     * Вывод данных на экран
     */
    public void objPrint(){
        System.out.print("\nНазвание файла " + this.nameFile +
                "\n Профиль " + this.profile +
                "\n Код профиля " + this.codeProfile +
                "\n Длина, длина пилы ");

        for (String string : length_saw) {
            System.out.print(string.trim() + " ");
        }

        System.out.println("\n Высота профиля " + this.profileHeight +
                "\n Ширина фланца " + this.flangeWidth +
                "\n Толщина фланца " + this.flangeThickness +
                "\n Толщина листа " + this.webThickness +
                "\n Радиус " + this.radius +
                "\n Вес на метр " + this.weightByMeter);
    }

    /**
     * Пиление
     * @return
     */
    public Boolean VB1250() throws FileNotFoundException, IOException {
        switch(this.codeProfile) {
            case "I":
            case "U":
            case "RU":
            case "RO":
            case "M":
            case "C":
            case "T":
            case "SO":
                if(((this.flangeWidth > 50)&&(this.flangeWidth < 1250))&&
                        ((this.profileHeight > 10)&&(this.profileHeight < 600))) {
                        System.out.println("Материал подходит для обработки на станке VB1250");
                        this.writeFile(1);
                        return Boolean.TRUE;
                } else {
                    System.out.println("Материал НЕ подходит для обработки на станке VB1250 по параметрам");
                    return Boolean.FALSE;
                }
            default:
                System.err.println("Профиль не подходит для VB1250");
                return Boolean.FALSE;
        }
    }

    /**
     * Сверление и метка, нарезание резьбы
     * @return
     */
    public Boolean V630() throws FileNotFoundException, IOException {
        switch(this.codeProfile) {
            case "I":
            case "U":
            case "RU":
            case "RO":
            case "M":
            case "C":
            case "T":
            case "SO":
            case "B":
                if((Double.valueOf(this.length_saw[0]) < 15500)&&
                        ((this.flangeWidth > 60)&&(this.flangeWidth < 1050))&&
                        ((this.profileHeight > 10)&&(this.profileHeight < 450))) {
                        System.out.println("Материал подходит для обработки на станке V630");
                        this.writeFile(2);
                        return Boolean.TRUE;
                } else {
                    System.out.println("Материал НЕ подходит для обработки на станке V630 по параметрам");
                    return Boolean.FALSE;
                }
            default:
                System.err.println("Профиль не подходит для V630");
                return Boolean.FALSE;
        }
    }

    /**
     * Резка, вертикальная и горизонтальная пробивка
     * @return
     */
    public Boolean V5504() throws FileNotFoundException, IOException {
        switch(this.codeProfile) {
            case "B":
                // Резка и вертикальная пробивка
                if((Double.valueOf(this.length_saw[0]) < 12200)&&
                        ((this.flangeWidth > 50)&&(this.flangeWidth < 500))) {
                        System.out.println("Возможна резка и вертикальная пробивка пластин на станке V5504");
                        this.writeFile(3);
                        return Boolean.TRUE;
                } else {
                    System.out.println("НЕ возможна резкаи пробивка пластин на станке V5504 по параметрам");
                    return Boolean.FALSE;
                }
            case "L":
                // Резка и вертикальная и горизонтальная пробивка
                if((Double.valueOf(this.length_saw[0]) < 12200)&&
                        (this.flangeWidth == 160)&&
                        (this.profileHeight == 160)&&
			checkAngles()) {
                        System.out.println("Возможна резка и пробивка уголков на станке V5504");
                        this.writeFile(3);
                        return Boolean.TRUE;
                } else {
                    System.out.println("НЕ возможна резка и пробивка уголков станке V5504 по параметрам");
                    return Boolean.FALSE;
                }
            default:
                System.err.println("Профиль не подходит для V5504");
                return Boolean.FALSE;
        }
    }

    /**
     * Пиление
     * @return
     */
    public Boolean VB1550() throws FileNotFoundException, IOException {
        switch(this.codeProfile) {
            case "I":
            case "U":
            case "RU":
            case "RO":
            case "M":
            case "C":
            case "T":
            case "SO":
                if(((this.flangeWidth > 50)&&(this.flangeWidth < 1550))&&
                        ((this.profileHeight > 10)&&(this.profileHeight < 600))) {
                        System.out.println("Материал подходит для обработки на станке VB1550");
                        this.writeFile(4);
                        return Boolean.TRUE;
                } else {
                    System.out.println("Материал НЕ подходит для обработки на станке VB1550 по параметрам");
                    return Boolean.FALSE;
                }
            default:
                System.err.println("Профиль не подходит для VB1550");
                return Boolean.FALSE;
        }
    }

    /**
     * Резка и пробивка
     * @return
     */
    public Boolean V505() throws FileNotFoundException, IOException {
        switch(this.codeProfile) {
            case "L":
                // Резка и вертикальная и горизонтальная пробивка
                if((this.flangeWidth > 50)&&(this.flangeWidth < 160)&&checkAngles()) {
                        System.out.println("Возможна резка и пробивка уголков на станке V505");
                        this.writeFile(5);
                        return Boolean.TRUE;
                } else {
                    System.out.println("НЕ возможна резка и пробивка уголков станке V505 по параметрам");
                    return Boolean.FALSE;
                }
            default:
                System.err.println("Профиль не подходит для V505");
                return Boolean.FALSE;
        }
    }

     /**
     * Сверление, пробивка, метка,
     * нарезание резьбы, зенковка
     *
     * НЕ учитывалась ддина
     * @return
     */
    public Boolean V250() throws FileNotFoundException, IOException {
        switch(this.codeProfile) {
            case "B":
                // Резка и вертикальная и горизонтальная пробивка
                if(this.flangeWidth <= 1000) {
                        System.out.println("Возможна обработка пластин на станке V250");
                        this.writeFile(6);
                        return Boolean.TRUE;
                } else {
                    System.out.println("НЕ возможна обработка пластин станке V250 по параметрам");
                    return Boolean.FALSE;
                }
            default:
                System.err.println("Профиль не подходит для V250");
                return Boolean.FALSE;
        }
    }

    /**
     * Резка, пробивка, долбление
     * @return
     */
    public Boolean HP12T4() throws FileNotFoundException, IOException {
        switch(this.codeProfile) {
            case "L":
                // Резка и вертикальная и горизонтальная пробивка
                if((this.flangeWidth > 50)&&(this.flangeWidth < 160)&&checkAngles()) {
                        System.out.println("Возможна резка и пробивка уголков на станке HP12T4");
                        this.writeFile(7);
                        return Boolean.TRUE;
                } else {
                    System.out.println("НЕ возможна резка и пробивка уголков станке HP12T4 по параметрам");
                    return Boolean.FALSE;
                }
            default:
                System.err.println("Профиль не подходит для HP12T4");
                return Boolean.FALSE;
        }
    }

    /**
     * Проверяет что углы резки равны 0
     * @return TRUE если равны 0
     */
    private Boolean checkAngles(){
	if((this.webStartCut == 0)&&
		(this.webEndCut == 0)&&
		(this.flangeStartCut == 0)&&
		(this.flangeEndCut == 0)){
	    return Boolean.TRUE;
	} else {
	    return Boolean.FALSE;
	}
    }

    /**
     * Копирует файлы по соответствующим папкам
     * @param num is the record number in the array patchs []
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void writeFile(int num) throws FileNotFoundException, IOException {
        FileOutputStream out;
        FileChannel srcChannel;
        FileChannel destChannel;
        try (FileInputStream in = new FileInputStream(this.workDir + this.nameFile)) {
            out = new FileOutputStream(this.workDir + MainClass.paths[num] + "//" +  this.nameFile);
            srcChannel = in.getChannel();
            destChannel = out.getChannel();
            srcChannel.transferTo(0, srcChannel.size(), destChannel);
        }
        out.close();
        srcChannel.close();
        destChannel.close();
    }
}
