package com.anupya.anupya_pamidimukkala.picmyallergy;
/**
 * Created by Anupya_Pamidimukkala on 3/27/2018.
 */

public class KeyPairBoolData {

    int index;
    String name;
    boolean isSelected;

    Object object;

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    /**
     * @return the id
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param id the id to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the isSelected
     */
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * @param isSelected the isSelected to set
     */
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
