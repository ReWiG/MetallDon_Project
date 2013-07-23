package Class;

import java.io.Serializable;

/**
 * @author asup
 */
public class Material implements Serializable, Cloneable {
    /**
     * Код профиля
     */
    public String codeProfile;
    /**
     * Длина
     */
    public Double length;
    /**
     * Ширина
     */
    public Double width;
    /**
     * Высота
     */
    public Double heigth;
    /**
     * Выделение
     */
    public Boolean checkBox;

    public Material(String CodeProfie,
            Double Length,
            Double Width,
            Double Height) {
        codeProfile = CodeProfie;
        length = Length;
        width = Width;
        heigth = Height;
        checkBox = false;
    }

    // Пустой конструктор для создания новой строки
    public Material() {

    }

    // Клонирование объекта
    @Override
    public Material clone() throws CloneNotSupportedException {
        Material obj = new Material();
        obj.checkBox = checkBox;
        obj.codeProfile = codeProfile;
        obj.heigth = heigth;
        obj.length = length;
        obj.width = length;
        return obj;
    }
}
