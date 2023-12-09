import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class TodoListEdit {
	private TodoItem todoItem;
    private JPanel panel;
    private JButton editButton;
    private JButton deleteButton;
    private JFrame frame;
    private List<TodoItem> todoItems;
    
    public TodoListEdit(TodoItem todoItem) {
        this.todoItem = todoItem;
        this.panel = todoItem.getPanel();
        createButtons();
    }

    private void createButtons() {
        editButton = new JButton("Edit");
        editButton.addActionListener(e -> todoItem.editItem());
        panel.add(editButton);

        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> todoItem.confirmDeletion());
        panel.add(deleteButton);
    }
    
    
}
