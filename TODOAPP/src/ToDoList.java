
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
    private List<TodoItem> todoItems;                  // �� �� �����۵��� ������ ����Ʈ
    private JPanel todoPanel;                          // �� �� �����۵��� ǥ���� �г�
    private JComboBox<String> priorityComboBox;        // �߿䵵 ������ ���� �޺� �ڽ�
    private JTextField addItemField;                   // ���ο� �� ���� �Է¹��� �ؽ�Ʈ �ʵ�
    private JProgressBar progressBar;                  // ���� ���¸� ǥ���� ���α׷��� ��
    private JButton saveButton;                        // ���� ������ ���� ��ư
    
    // �׸� ������ �����ϴ� Color ��ü��
    private Color lightModeBackground = Color.WHITE;
    private Color lightModeForeground = Color.BLACK;
    private Color darkModeBackground = Color.DARK_GRAY;
    private Color darkModeForeground = Color.WHITE;
    
    private boolean isDarkMode = false;	    // ���� ��ũ ��尡 Ȱ��ȭ�Ǿ� �ִ����� ��Ÿ���� ����

    public ToDoList() {
        todoItems = new ArrayList<>();     // �� �� ������ ����Ʈ �ʱ�ȭ
        createUI();                        // UI ���� �޼ҵ� ȣ��
    }

    private void createUI() {
    	
    	// �������� �⺻ ����
        setTitle("To-Do List");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(new Dimension(600, 400));
        
        // �׸� ��ȯ ��ư ����
        JButton changeThemeButton = new JButton("Dark Mode");
        changeThemeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isDarkMode = !isDarkMode;      // ��ũ ��� ���¸� ������Ű�� �׸� ����
                changeTheme();
                
                // ���� ��忡 ���� ��ư �ؽ�Ʈ ����
                if (isDarkMode) {
                    changeThemeButton.setText("Light Mode");
                } else {
                    changeThemeButton.setText("Dark Mode");
                }
            }
        });
        
        // ���α׷��� �� ����
        progressBar = new JProgressBar(0, 100);   //��� ����� ǥ��
        progressBar.setStringPainted(true);
        add(progressBar, BorderLayout.NORTH);
        
        // ���� ��ư ����
        saveButton = new JButton("Save");         //���� ��ư 
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveToFile();  
            }
        });
        
        // ��� �гο� �׸� ��ȯ ��ư, ���α׷��� ��, ���� ��ư �߰�
        JPanel progressPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));   // ProgressBar + saveButton
        progressPanel.add(changeThemeButton);
        progressPanel.add(progressBar);
        progressPanel.add(saveButton);
        
        add(progressPanel, BorderLayout.NORTH);  // �����ӿ� ��� �г� �߰�
        
//--------------------------------------------------------------------------------------//
        
        // �� �� �߰��� ���� �г� ����
        JPanel addItemPanel = new JPanel(new BorderLayout());

        // ���ο� �� ���� �Է¹޴� �ؽ�Ʈ �ʵ� ���� �� �гο� �߰�
        addItemField = new JTextField();
        addItemPanel.add(addItemField, BorderLayout.CENTER);

        // �켱���� ������ ���� ���ڿ� �迭 ���� �� �޺��ڽ� ����, �гο� �߰�
        String[] priorities = {"Low", "Medium", "High"};
        priorityComboBox = new JComboBox<>(priorities);
        addItemPanel.add(priorityComboBox, BorderLayout.WEST);

        // �� �� �߰� ��ư ����, �׼� ������ ���� �� �гο� �߰�
        JButton addItemButton = new JButton("Add");
        addItemButton.addActionListener(e -> addTodoItem(addItemField.getText(), (String) priorityComboBox.getSelectedItem()));
        addItemPanel.add(addItemButton, BorderLayout.EAST);

        // �켱�������� �����ϴ� ��ư ����, �׼� ������ ���� �� �гο� �߰�
        JButton sortButton = new JButton("Sort by Priority");
        sortButton.addActionListener(e -> sortByPriority());
        addItemPanel.add(sortButton, BorderLayout.NORTH);

        // �� �� ����� ���� �г� ���� �� ���̾ƿ� ����
        todoPanel = new JPanel();
        todoPanel.setLayout(new BoxLayout(todoPanel, BoxLayout.Y_AXIS));

        // ��ũ�� ������ �г� ����, ��ũ�� �� ���� �� �����ӿ� �߰�
        JScrollPane scrollPane = new JScrollPane(todoPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        // �� �� �߰� �г��� �������� �Ʒ��ʿ� �߰�
        add(addItemPanel, BorderLayout.SOUTH);
        
        // �ʱ� ���� ����
        todoPanel.setBackground(lightModeBackground);
        todoPanel.setForeground(lightModeForeground);
        addItemField.setBackground(lightModeBackground);
        addItemField.setForeground(lightModeForeground);
        priorityComboBox.setBackground(lightModeBackground);
        priorityComboBox.setForeground(lightModeForeground);
    }
    
//----------------------------------------------------------------------------------------------//
    
    // ��ũ���� ����Ʈ��带 ��ȯ�ϴ� �޼ҵ�
    private void changeTheme() {
    	
    	if (isDarkMode) {
            todoPanel.setBackground(darkModeBackground);
            todoPanel.setForeground(darkModeForeground);
            //addItemField.setBackground(darkModeBackground);
            //addItemField.setForeground(darkModeForeground);
            //priorityComboBox.setBackground(darkModeBackground);
            //priorityComboBox.setForeground(darkModeForeground);
        } else {
            todoPanel.setBackground(lightModeBackground);
            todoPanel.setForeground(lightModeForeground);
            //addItemField.setBackground(lightModeBackground);
            //addItemField.setForeground(lightModeForeground);
            //priorityComboBox.setBackground(lightModeBackground);
            //priorityComboBox.setForeground(lightModeForeground);
        }
    	for (TodoItem item : todoItems) {
    		JCheckBox checkBox = item.getCheckBox();
    	    JLabel priorityLabel = item.getPriorityLabel();
    	    JPanel panel = item.getPanel();

    	    if (isDarkMode) {
    	        checkBox.setBackground(darkModeBackground);
    	        checkBox.setForeground(darkModeForeground);
    	        priorityLabel.setOpaque(true);
    	        priorityLabel.setBackground(darkModeBackground);
    	        panel.setBackground(darkModeBackground);
    	    } else {
    	        checkBox.setBackground(lightModeBackground);
    	        checkBox.setForeground(lightModeForeground);
    	        priorityLabel.setOpaque(true);
    	        priorityLabel.setBackground(lightModeBackground);
    	        panel.setBackground(lightModeBackground);
    	    }  
    	}
    	
        // �������� ������ ���� ����� �������� �����ϰ�, ����� ������ ȭ�鿡 �ٷ� �ݿ��ǵ��� repaint �޼ҵ� ȣ��
        getContentPane().setBackground(isDarkMode ? darkModeBackground : lightModeBackground);
        repaint();
    }

    // �� �� ����� ���Ϸ� �����ϴ� �޼ҵ�
    private void saveToFile() {
        JFileChooser fileChooser = new JFileChooser(); //���丮 ��
        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileChooser.getSelectedFile()))) {
                for (TodoItem item : todoItems) {
                	writer.write("\n����: " + item.getText() + "\n�߿䵵: " + item.getPriority() + "\n�޸�: " + item.getNote() + 
                    		"\n��¥: " + item.getFormattedCreationDate() + "\n�Ϸ�: " + item.isDone() + "\n\n"
                    		+ "===========================================\n");
                }
                JOptionPane.showMessageDialog(this, "���������� �����Ͽ����ϴ�.");
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "���忡 �����Ͽ����ϴ�.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        updateProgressBar();
    }
    
    // ���α׷��� �ٸ� ������Ʈ�ϴ� �޼ҵ�
    void updateProgressBar() {//progress ��� , �Ϸ�� ������ / ��ü ������ * 100
        int totalItems = todoItems.size();
        int completedItems = (int) todoItems.stream().filter(item -> item.getCheckBox().isSelected()).count();

        int progress = totalItems > 0 ? (int) ((double) completedItems / totalItems * 100) : 0;
        progressBar.setValue(progress);
    }
    
    // �� �� ����� �켱���� ���� �����ϴ� �޼ҵ�
    private void sortByPriority() { 
        List<TodoItem> selectedItems = new ArrayList<>();
        List<TodoItem> nonSelectedItems = new ArrayList<>();
        for (TodoItem item : todoItems) {
            if (item.getCheckBox().isSelected()) {
                selectedItems.add(item);
            } else {
                nonSelectedItems.add(item);
            }
        }
    
        //���õ��� ���� ����� �������� ���� 
        nonSelectedItems.sort((item1, item2) -> {
            int priority1 = getPriorityValue(item1.getPriority());
            int priority2 = getPriorityValue(item2.getPriority());
            return Integer.compare(priority2, priority1);
        });
    
        nonSelectedItems.addAll(selectedItems);

        todoPanel.removeAll();
        nonSelectedItems.forEach(item -> todoPanel.add(item.getPanel()));
        updateUI();
    }
    
    // ���ڿ��� �־��� �켱������ ���������� ��ȯ�ϴ� �޼ҵ�
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
    
    // ���ο� �� ���� �߰��ϴ� �޼ҵ�
    private void addTodoItem(String text, String priority) {
        if (text.isEmpty()) {
        return;
        }
    
    // ���ο� �� �� ��ü ����
    TodoItem item = new TodoItem(text, priority, this);
    
    item.setDeadlineFromCalendarButton();
    //������ ����
    todoItems.add(item);
    todoPanel.add(item.getPanel());
    
    JCheckBox checkBox = item.getCheckBox();
    JLabel priorityLabel = item.getPriorityLabel();
    JPanel panel = item.getPanel();

    // ���� ��忡 ���� ������ ���ڻ� ����
    if (isDarkMode) {
        checkBox.setBackground(darkModeBackground);
        checkBox.setForeground(darkModeForeground);
        priorityLabel.setOpaque(true);
        priorityLabel.setBackground(darkModeBackground);
        panel.setBackground(darkModeBackground);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
    } else {
        checkBox.setBackground(lightModeBackground);
        checkBox.setForeground(lightModeForeground);
        priorityLabel.setOpaque(true);
        priorityLabel.setBackground(lightModeBackground);
        panel.setBackground(lightModeBackground);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
    }

    // üũ����Ʈ �׸��� ����Ʈ�� �߰��ϰ� UI ������Ʈ
    todoItems.add(item);
    
    updateUI();

    // �Է� �ʵ� ����
     addItemField.setText("");
     
     updateProgressBar();
    }

    // UI�� ������Ʈ�ϴ� �޼ҵ�
    public void updateUI() {
        todoPanel.revalidate();
        todoPanel.repaint();
    }

    // �� ���� �����ϴ� �޼ҵ�
    public void deleteTodoItem(TodoItem item) {
        todoItems.remove(item);
        todoPanel.remove(item.getPanel());
        updateUI();
        for (TodoItem items : todoItems) {
            items.updateRemainingDays();
        }
        
        updateProgressBar();
    }

    // ���� �޼ҵ�
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ToDoList().setVisible(true));
    }
}