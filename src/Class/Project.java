package Class;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * @author asup
 */
public class Project implements Serializable, Cloneable{
    /*
     * Наименование
     */
    public String name;
    /**
     * Путь к NC файлам
     */
    public String path;
    /**
     * Оприходованные остатки
     */
    public String debitBalances;
    /**
     * Дата
     */
    public Date date;
    /**
     * Дата подготовки материалов
     */
    public Date dateMaterials;
    /**
     * Список материалов
     */
    public ArrayList<Material> listMaterial;
    /**
     * Выделение
     */
    public Boolean checkBox;

    /**
     * Конструктор
     * @param Name Название
     * @param Path Путь к NC файлам
     * @param Debit Оприходованные остатки
     * @param Date Дата
     * @param DateM Дата подготовки материалов
     * @param list ArrayList<Material> Список материалов
     */
    public Project(String Name,
            String Path,
            String Debit,
            Date Date,
            Date DateM,
            ArrayList<Material> list) {
        name = Name;
        path = Path;
        debitBalances = Debit;
        date = Date;
        dateMaterials = DateM;
        listMaterial = list;
        checkBox = false;
    }

    // Пустой конструктор для добавления новой строки
    public Project() {
        listMaterial = new ArrayList<Material>();
    }

    /**
     * Клонирование объекта для передачи в дочернее окно
     * @return Project
     * @throws CloneNotSupportedException
     */
    @Override
    public Project clone() throws CloneNotSupportedException {
        Project obj = new Project();
        obj.name = name;
        obj.path = path;
        obj.checkBox = checkBox;
        obj.date = date;
        obj.dateMaterials = dateMaterials;
        obj.debitBalances = debitBalances;

        // Итератор для последовательного клонирования элементов ArrayList-a
        if(listMaterial != null) {
            Iterator<Material> iter = listMaterial.iterator();
            if (iter.hasNext()) {
                while(iter.hasNext()){
                    obj.listMaterial.add(iter.next().clone());
                }
            }
        } else {
            obj.listMaterial = new ArrayList<Material>();
        }
        return obj;
    }
}