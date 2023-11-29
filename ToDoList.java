import javax.swing.*;
import java.awt.*;

public class ToDoListApp extends JFrame {

    private DefaultListModel<ToDoItem> toDoListModel;
    private JList<ToDoItem> toDoList;
    private JTextField newItemTextField;

    public ToDoListApp() {
        setTitle("To-Do List");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        toDoListModel = new DefaultListModel<>();
        toDoList = new JList<>(toDoListModel);
        toDoList.setCellRenderer(new ToDoListCellRenderer());
        JScrollPane scrollPane = new JScrollPane(toDoList);
        add(scrollPane, BorderLayout.CENTER);

        newItemTextField = new JTextField();
        JButton addButton = new JButton("추가");
        addButton.addActionListener(e -> addItem());

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(newItemTextField, BorderLayout.CENTER);
        panel.add(addButton, BorderLayout.EAST);

        add(panel, BorderLayout.SOUTH);

        toDoList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                ToDoItem selectedItem = toDoList.getSelectedValue();
                if (selectedItem != null) {
                    confirmAndRemoveItem(selectedItem);
                }
            }
        });

        setVisible(true);
    }

    private void addItem() {
        String newItemText = newItemTextField.getText();
        if (!newItemText.isEmpty()) {
            ToDoItem newItem = new ToDoItem(newItemText);
            toDoListModel.addElement(newItem);
            newItemTextField.setText("");
        }
    }

    private void removeItem(ToDoItem item) {
        toDoListModel.removeElement(item);
    }

    private void confirmAndRemoveItem(ToDoItem item) {
        int option = JOptionPane.showConfirmDialog(this, "완료하였습니까?", "확인", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            removeItem(item);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ToDoListApp());
    }
}
