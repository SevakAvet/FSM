package lr.entity;

import java.util.Set;

/**
 * Created by savetisyan on 24/10/16
 */
public class Component {
    private Integer id;
    private Set<Item> items;

    public Component(Integer name, Set<Item> items) {
        this.id = name;
        this.items = items;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Set<Item> getItems() {
        return items;
    }

    public void setItems(Set<Item> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Component component = (Component) o;

        return items.equals(component.items);

    }

    @Override
    public int hashCode() {
        return items.hashCode();
    }

    @Override
    public String toString() {
        return "Component{" +
                "id='" + id + '\'' +
                ", items=" + items +
                '}';
    }
}
