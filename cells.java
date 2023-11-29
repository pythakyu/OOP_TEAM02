import javax.swing.*;
import java.awt.*;

public class ToDoListCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof ToDoItem) {
            setText(value.toString());
        }
        return this;
    }
}
