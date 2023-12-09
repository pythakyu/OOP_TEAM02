import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class TodoListSave {
    private JFrame frame;
    private List<TodoItem> todoItems;

    public TodoListSave(JFrame frame, List<TodoItem> todoItems) {
        this.frame = frame;
        this.todoItems = todoItems;
    }

    // �� �� ����� ���Ϸ� �����ϴ� �޼ҵ�
    public void saveToFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(frame);

        if (result == JFileChooser.APPROVE_OPTION) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileChooser.getSelectedFile()))) {
                for (TodoItem item : todoItems) {
                    writer.write("\n����: " + item.getText() + "\n�߿䵵: " + item.getPriority() + "\n�޸�: " + item.getNote() + 
                    		"\n��¥: " + item.getFormattedCreationDate() + "\n�Ϸ�: " + item.isDone() + "\n\n"
                    		+ "===========================================\n");
                }
                JOptionPane.showMessageDialog(frame, "���������� �����Ͽ����ϴ�.");
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "���忡 �����Ͽ����ϴ�.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
