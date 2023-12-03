import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
// import java.util.Collections;
import java.util.List;

public class ToDoList extends JFrame {
    private List<TodoItem> todoItems;
    private JPanel todoPanel;
    private JComboBox<String> priorityComboBox;
    private JTextField addItemField;

    public ToDoList() {
        todoItems = new ArrayList<>();
        createUI();

        
    }

    private void createUI() {
        setTitle("To-Do List");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(new Dimension(600, 400));

        addItemField = new JTextField();
        String[] priorities = {"Low", "Medium", "High"};
        priorityComboBox = new JComboBox<>(priorities);

        JButton addItemButton = new JButton("Add");
        addItemButton.addActionListener(e -> addTodoItem(addItemField.getText(), (String) priorityComboBox.getSelectedItem()));

        

        JPanel addItemPanel = new JPanel(new BorderLayout());
        addItemPanel.add(addItemField, BorderLayout.CENTER);
        addItemPanel.add(priorityComboBox, BorderLayout.WEST);
        addItemPanel.add(addItemButton, BorderLayout.EAST);

        todoPanel = new JPanel();
        todoPanel.setLayout(new BoxLayout(todoPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(todoPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        add(scrollPane, BorderLayout.CENTER);
        add(addItemPanel, BorderLayout.SOUTH);

        JButton sortButton = new JButton("Sort by Priority");
        sortButton.addActionListener(e -> sortByPriority());

        addItemPanel.add(sortButton, BorderLayout.NORTH);
        


    }
    private void sortByPriority() {
        List<TodoItem> selectedItems = new ArrayList<>();
        List<TodoItem> nonSelectedItems = new ArrayList<>();
    
        // Separate selected and non-selected items
        for (TodoItem item : todoItems) {
            if (item.getCheckBox().isSelected()) {
                selectedItems.add(item);
            } else {
                nonSelectedItems.add(item);
            }
        }
    
        // Sort the non-selected items by priority
        nonSelectedItems.sort((item1, item2) -> {
            int priority1 = getPriorityValue(item1.getPriority());
            int priority2 = getPriorityValue(item2.getPriority());
            return Integer.compare(priority2, priority1); // 내림차순 정렬
        });
    
        // Combine selected and non-selected items with selected items at the bottom
        nonSelectedItems.addAll(selectedItems);
    
        todoPanel.removeAll();
        nonSelectedItems.forEach(item -> todoPanel.add(item.getPanel()));
        updateUI();
    }
    

    private int getPriorityValue(String priority) {
        switch (priority) {
            case "High":
                return 3;
            case "Medium":
                return 2;
            case "Low":
                return 1;
            default:
                return 0;
        }
    }
    // private void addTodoItem(String text, String priority) {
    //     if (text.isEmpty()) {
    //         return;
    //     }
    //     TodoItem item = new TodoItem(text, priority, this);
        
    //     // Set the initial deadline for the new item
    //     item.setDeadlineFromCalendarButton();
        
    //     todoItems.add(item);
    //     todoPanel.add(item.getPanel());
    //     updateUI();
    // }

    




    private void addTodoItem(String text, String priority) {
        if (text.isEmpty()) {
        return;
    }
    TodoItem item = new TodoItem(text, priority, this);
    
    // Set the initial deadline for the new item
    item.setDeadlineFromCalendarButton();
    
    todoItems.add(item);
    todoPanel.add(item.getPanel());
    updateUI();

        // 입력 필드 리셋
        addItemField.setText("");

    }

    public void updateUI() {
        todoPanel.revalidate();
        todoPanel.repaint();
    }

    public void deleteTodoItem(TodoItem item) {
        todoItems.remove(item);
        todoPanel.remove(item.getPanel());
        updateUI();
        // Update D-Day for all items
        for (TodoItem items : todoItems) {
            items.updateRemainingDays();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ToDoList().setVisible(true));
    }
}
