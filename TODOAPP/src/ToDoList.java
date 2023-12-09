
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
    private List<TodoItem> todoItems;                  // 할 일 아이템들을 저장할 리스트
    private JPanel todoPanel;                          // 할 일 아이템들을 표시할 패널
    private JComboBox<String> priorityComboBox;        // 중요도 선택을 위한 콤보 박스
    private JTextField addItemField;                   // 새로운 할 일을 입력받을 텍스트 필드
    private JProgressBar progressBar;                  // 진행 상태를 표시할 프로그레스 바
    private JButton saveButton;                        // 파일 저장을 위한 버튼
    
    // 테마 색상을 저장하는 Color 객체들
    private Color lightModeBackground = Color.WHITE;
    private Color lightModeForeground = Color.BLACK;
    private Color darkModeBackground = Color.DARK_GRAY;
    private Color darkModeForeground = Color.WHITE;
    
    private boolean isDarkMode = false;	    // 현재 다크 모드가 활성화되어 있는지를 나타내는 변수

    public ToDoList() {
        todoItems = new ArrayList<>();     // 할 일 아이템 리스트 초기화
        createUI();                        // UI 생성 메소드 호출
    }

    private void createUI() {
    	
    	// 프레임의 기본 설정
        setTitle("To-Do List");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(new Dimension(600, 400));
        
        // 테마 전환 버튼 설정
        JButton changeThemeButton = new JButton("Dark Mode");
        changeThemeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isDarkMode = !isDarkMode;      // 다크 모드 상태를 반전시키고 테마 변경
                changeTheme();
                
                // 현재 모드에 따라 버튼 텍스트 변경
                if (isDarkMode) {
                    changeThemeButton.setText("Light Mode");
                } else {
                    changeThemeButton.setText("Dark Mode");
                }
            }
        });
        
        // 프로그레스 바 설정
        progressBar = new JProgressBar(0, 100);   //상단 진행률 표시
        progressBar.setStringPainted(true);
        add(progressBar, BorderLayout.NORTH);
        
        // 저장 버튼 설정
        saveButton = new JButton("Save");         //저장 버튼 
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveToFile();  
            }
        });
        
        // 상단 패널에 테마 전환 버튼, 프로그레스 바, 저장 버튼 추가
        JPanel progressPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));   // ProgressBar + saveButton
        progressPanel.add(changeThemeButton);
        progressPanel.add(progressBar);
        progressPanel.add(saveButton);
        
        add(progressPanel, BorderLayout.NORTH);  // 프레임에 상단 패널 추가
        
//--------------------------------------------------------------------------------------//
        
        // 할 일 추가를 위한 패널 생성
        JPanel addItemPanel = new JPanel(new BorderLayout());

        // 새로운 할 일을 입력받는 텍스트 필드 생성 및 패널에 추가
        addItemField = new JTextField();
        addItemPanel.add(addItemField, BorderLayout.CENTER);

        // 우선순위 선택을 위한 문자열 배열 생성 및 콤보박스 생성, 패널에 추가
        String[] priorities = {"Low", "Medium", "High"};
        priorityComboBox = new JComboBox<>(priorities);
        addItemPanel.add(priorityComboBox, BorderLayout.WEST);

        // 할 일 추가 버튼 생성, 액션 리스너 설정 및 패널에 추가
        JButton addItemButton = new JButton("Add");
        addItemButton.addActionListener(e -> addTodoItem(addItemField.getText(), (String) priorityComboBox.getSelectedItem()));
        addItemPanel.add(addItemButton, BorderLayout.EAST);

        // 우선순위별로 정렬하는 버튼 생성, 액션 리스너 설정 및 패널에 추가
        JButton sortButton = new JButton("Sort by Priority");
        sortButton.addActionListener(e -> sortByPriority());
        addItemPanel.add(sortButton, BorderLayout.NORTH);

        // 할 일 목록을 담을 패널 생성 및 레이아웃 설정
        todoPanel = new JPanel();
        todoPanel.setLayout(new BoxLayout(todoPanel, BoxLayout.Y_AXIS));

        // 스크롤 가능한 패널 생성, 스크롤 바 설정 및 프레임에 추가
        JScrollPane scrollPane = new JScrollPane(todoPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        // 할 일 추가 패널을 프레임의 아래쪽에 추가
        add(addItemPanel, BorderLayout.SOUTH);
        
        // 초기 배경색 설정
        todoPanel.setBackground(lightModeBackground);
        todoPanel.setForeground(lightModeForeground);
        addItemField.setBackground(lightModeBackground);
        addItemField.setForeground(lightModeForeground);
        priorityComboBox.setBackground(lightModeBackground);
        priorityComboBox.setForeground(lightModeForeground);
    }
    
//----------------------------------------------------------------------------------------------//
    
    // 다크모드와 라이트모드를 전환하는 메소드
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
    	
        // 프레임의 배경색을 현재 모드의 배경색으로 변경하고, 변경된 색상이 화면에 바로 반영되도록 repaint 메소드 호출
        getContentPane().setBackground(isDarkMode ? darkModeBackground : lightModeBackground);
        repaint();
    }

    // 할 일 목록을 파일로 저장하는 메소드
    private void saveToFile() {
        JFileChooser fileChooser = new JFileChooser(); //디렉토리 선
        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileChooser.getSelectedFile()))) {
                for (TodoItem item : todoItems) {
                	writer.write("\n내용: " + item.getText() + "\n중요도: " + item.getPriority() + "\n메모: " + item.getNote() + 
                    		"\n날짜: " + item.getFormattedCreationDate() + "\n완료: " + item.isDone() + "\n\n"
                    		+ "===========================================\n");
                }
                JOptionPane.showMessageDialog(this, "성공적으로 저장하였습니다.");
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "저장에 실패하였습니다.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        updateProgressBar();
    }
    
    // 프로그레스 바를 업데이트하는 메소드
    void updateProgressBar() {//progress 계산 , 완료된 아이템 / 전체 아이템 * 100
        int totalItems = todoItems.size();
        int completedItems = (int) todoItems.stream().filter(item -> item.getCheckBox().isSelected()).count();

        int progress = totalItems > 0 ? (int) ((double) completedItems / totalItems * 100) : 0;
        progressBar.setValue(progress);
    }
    
    // 할 일 목록을 우선순위 따라 정렬하는 메소드
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
    
        //선택되지 않은 목록은 내림차순 정렬 
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
    
    // 문자열로 주어진 우선순위를 정수값으로 변환하는 메소드
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
    
    // 새로운 할 일을 추가하는 메소드
    private void addTodoItem(String text, String priority) {
        if (text.isEmpty()) {
        return;
        }
    
    // 새로운 할 일 객체 생성
    TodoItem item = new TodoItem(text, priority, this);
    
    item.setDeadlineFromCalendarButton();
    //마감일 지정
    todoItems.add(item);
    todoPanel.add(item.getPanel());
    
    JCheckBox checkBox = item.getCheckBox();
    JLabel priorityLabel = item.getPriorityLabel();
    JPanel panel = item.getPanel();

    // 현재 모드에 따라 배경색과 글자색 설정
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

    // 체크리스트 항목을 리스트에 추가하고 UI 업데이트
    todoItems.add(item);
    
    updateUI();

    // 입력 필드 리셋
     addItemField.setText("");
     
     updateProgressBar();
    }

    // UI를 업데이트하는 메소드
    public void updateUI() {
        todoPanel.revalidate();
        todoPanel.repaint();
    }

    // 할 일을 삭제하는 메소드
    public void deleteTodoItem(TodoItem item) {
        todoItems.remove(item);
        todoPanel.remove(item.getPanel());
        updateUI();
        for (TodoItem items : todoItems) {
            items.updateRemainingDays();
        }
        
        updateProgressBar();
    }

    // 메인 메소드
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ToDoList().setVisible(true));
    }
}