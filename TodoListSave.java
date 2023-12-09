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

    // 할 일 목록을 파일로 저장하는 메소드
    public void saveToFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(frame);

        if (result == JFileChooser.APPROVE_OPTION) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileChooser.getSelectedFile()))) {
                for (TodoItem item : todoItems) {
                	writer.write("\n내용: " + item.getText() + "\n중요도: " + item.getPriority() + "\n메모: " + item.getNote() + 
                    		"\n날짜: " + item.getFormattedCreationDate() + "\n완료: " + item.isDone() + "\n\n"
                    		+ "===========================================\n");
                }
                JOptionPane.showMessageDialog(frame, "성공적으로 저장하였습니다.");
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "저장에 실패하였습니다.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}