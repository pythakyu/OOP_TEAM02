package APP;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class ToDoList extends JFrame {
    private List<TodoItem> todoItems;
    private JPanel todoPanel;
    private JComboBox<String> priorityComboBox;
    private JTextField addItemField;
    private JProgressBar progressBar;
    private JButton saveButton; 

    public ToDoList() {
        todoItems = new ArrayList<>();
        createUI();
    }

    private void createUI() {
        setTitle("To-Do List");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(new Dimension(600, 400));
        
        progressBar = new JProgressBar(0, 100);//상단 진행률 표시
        progressBar.setStringPainted(true);
        add(progressBar, BorderLayout.NORTH);
        
        saveButton = new JButton("Save");//저장 버튼 
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveToFile();  
            }
        });
        
        JPanel progressPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));//ProgressBar + saveButton
        progressPanel.add(progressBar);
        progressPanel.add(saveButton);
        
        add(progressPanel, BorderLayout.NORTH);

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
    
    //파일로 저장 
    private void saveToFile() {
        JFileChooser fileChooser = new JFileChooser(); //디렉토리 선
        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileChooser.getSelectedFile()))) {
                for (TodoItem item : todoItems) {
                    writer.write("내용: " + item.getText() + "\n중요도: " + item.getPriority() +
                             "\n완료여부: " + item.isDone() + "\n\n");
                }
                JOptionPane.showMessageDialog(this, "성공적으로 저장하였습니다.");
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "저장에 실패하였습니다.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        updateProgressBar();
    }
    
    void updateProgressBar() {//progress 계산 , 완료된 아이템 / 전체 아이템 * 100
        int totalItems = todoItems.size();
        int completedItems = (int) todoItems.stream().filter(item -> item.getCheckBox().isSelected()).count();

        int progress = totalItems > 0 ? (int) ((double) completedItems / totalItems * 100) : 0;
        progressBar.setValue(progress);
      
    }

    private void sortByPriority() { 
        // 할 일 목록을 우선순위 따라 정렬
        List<TodoItem> selectedItems = new ArrayList<>();
        List<TodoItem> nonSelectedItems = new ArrayList<>();
        for (TodoItem item : todoItems) {
            if (item.getCheckBox().isSelected()) {
                selectedItems.add(item);
            } else {
                nonSelectedItems.add(item);
            }
        }
    
        nonSelectedItems.sort((item1, item2) -> {
            int priority1 = getPriorityValue(item1.getPriority());
            int priority2 = getPriorityValue(item2.getPriority());
            return Integer.compare(priority2, priority1);
            //선택되지 않은 목록은 내림차순 정렬 
        });
    
        nonSelectedItems.addAll(selectedItems);

        todoPanel.removeAll();
        nonSelectedItems.forEach(item -> todoPanel.add(item.getPanel()));
        updateUI();
    }
    

    private int getPriorityValue(String priority) {
        //우선순위를 문자열로 변환
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
    
    private void addTodoItem(String text, String priority) {
        if (text.isEmpty()) {
        return;
    }
    TodoItem item = new TodoItem(text, priority, this);
    
    item.setDeadlineFromCalendarButton();
    //마감일 지정
    todoItems.add(item);
    todoPanel.add(item.getPanel());
    updateUI();

    // 입력 필드 리셋
     addItemField.setText("");
     
     updateProgressBar();

    }

    public void updateUI() {
        todoPanel.revalidate();
        todoPanel.repaint();
    }

    public void deleteTodoItem(TodoItem item) {
        todoItems.remove(item);
        todoPanel.remove(item.getPanel());
        updateUI();
        for (TodoItem items : todoItems) {
            items.updateRemainingDays();
        }
        
        updateProgressBar();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ToDoList().setVisible(true));
    }
}