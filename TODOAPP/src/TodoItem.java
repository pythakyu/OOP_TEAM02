
import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.awt.font.TextAttribute;
import net.sourceforge.jdatepicker.impl.*;
import java.text.SimpleDateFormat;

public class TodoItem {
	
	
	// 할 일 항목의 텍스트, 우선순위, 마감일, 메모를 저장하는 변수
    private String text;
    private String priority;
    private Date deadline; 
    private String notes;
    private TodoListEdit editor;
    
    // UI 관련 변수
    private JPanel panel;
    private JCheckBox checkBox;
    private ToDoList parent;
    private JLabel priorityLabel;
    private JLabel remainingDaysLabel;
    private JButton deadlineButton;
    private boolean completed; // 체크박스의 상태를 저장하는 변수
    private Date creationDate;
    
    
    public TodoItem(String text, String priority, ToDoList parent) {
        this.text = text;                    // 할 일 항목의 텍스트를 저장
        this.priority = priority;            // 할 일 항목의 우선순위를 저장
        this.parent = parent;                // 부모 객체 저장
        createPanel();                       // UI 패널을 생성
        this.deadline = new Date();          // 마감일을 현재 날짜로 설정
        this.notes = "";                     // 메모를 빈 문자열로 초기화
        this.completed = false;              // 완료 상태를 false로 초기화
        this.creationDate = new Date();      // 생성 날짜를 현재 날짜
        updateRemainingDays();               // 남은 일수를 업데이트
        editor = new TodoListEdit(this);
    }
    

    private void createPanel() {
    	// 패널을 생성하고 레이아웃을 설정
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        
        // 체크박스 생성하고 액션 리스너 등록
        checkBox = new JCheckBox(text);
        checkBox.addActionListener(e -> {
            toggleCompleted();
            parent.updateUI();
        });

        // 우선순위 레이블 생성 및 스타일 업데이트
        priorityLabel = new JLabel(priority);
        updatePriorityLabelStyle();
    
        // 패널에 체크박스와 우선순위 레이블을 추가
        panel.add(checkBox);
        panel.add(Box.createRigidArea(new Dimension(5, 0)));
        panel.add(priorityLabel);
    
        panel.add(Box.createHorizontalGlue());
        
    	// 남은 일수 레이블 생성 및 업데이트 후 패널에 추가
        remainingDaysLabel = new JLabel();
        updateRemainingDays();
        panel.add(remainingDaysLabel);
    
        // 각 버튼 생성, 액션 리스너 등록 후 패널에 추가
        JButton detailButton = new JButton("+");
        detailButton.addActionListener(e -> openDetailWindow());
        panel.add(detailButton);
    }
    
    // 할 일 항목의 생성 날짜 반환
    public Date getCreationDate() {
        return creationDate;
    }

    //마감일 남은 날짜 계산
    void updateRemainingDays() {
    	
    	// 마감일이 없는 경우
        if (deadline == null) {
            remainingDaysLabel.setText("No Deadline");
            return;
        }
    
        // 현재 날짜
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        
        // 마감일을 Calendar 객체로 변환
        Calendar deadlineCalendar = Calendar.getInstance();
        deadlineCalendar.setTime(deadline);
        deadlineCalendar.set(Calendar.HOUR_OF_DAY, 0);
        deadlineCalendar.set(Calendar.MINUTE, 0);
        deadlineCalendar.set(Calendar.SECOND, 0);
        deadlineCalendar.set(Calendar.MILLISECOND, 0);
    
        // 남은 일수를 계산
        long diff = deadlineCalendar.getTimeInMillis() - today.getTimeInMillis();
        long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    
        days++;
        
        // 남은 일수에 따라 레이블 텍스트를 설정
        if (days <= 0) {
            remainingDaysLabel.setText("Overdue");
        } else if (days == 1) {
            remainingDaysLabel.setText("D-Day");
        } else {
            remainingDaysLabel.setText(" D-" + (days - 1) + "  ");
        }
    }
    
    public void editItem() {
        //텍스트와 우선순위 변경가능한 대화상자 표시
        JTextField editText = new JTextField(text);
        JComboBox<String> editPriority = new JComboBox<>(new String[]{"Low", "Medium", "High"});
        editPriority.setSelectedItem(priority);
        Object[] message = {
            "Text:", editText,
            "Priority:", editPriority
        };
        int option = JOptionPane.showConfirmDialog(null, message, "Edit Item", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            this.text = editText.getText();
            this.priority = (String) editPriority.getSelectedItem();
            checkBox.setText(text);
            priorityLabel.setText(priority);  // Update the text of the priority label
            updatePriorityLabelStyle();       // Update the style of the priority label
            parent.updateUI();
        }
    }

    public void confirmDeletion() {
        //삭제 확인하는 대화상자
        int response = JOptionPane.showConfirmDialog(
                panel,
                "Are you sure you want to delete this item?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (response == JOptionPane.YES_OPTION) {
            parent.deleteTodoItem(this);
        }
    }

    private void openDetailWindow() {
        //마강일와 세부메모 설정가능한 상세 창
        JFrame detailFrame = new JFrame("Set Deadline and Notes");
        detailFrame.setSize(400, 300);
        detailFrame.setLayout(new BorderLayout());
        
        // 이미 설정된 마감일이 있다면, 그 값을 초기값으로 설정
        UtilDateModel model = new UtilDateModel();
        if (deadline != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(deadline);
            model.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            model.setSelected(true);
        }

        // 날짜 선택 패널 생성
        JDatePanelImpl datePanel = new JDatePanelImpl(model);
        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel);
    
        // 세부 메모를 입력할 수 있는 텍스트 영역 생성
        JTextArea notesArea = new JTextArea(notes);
        JScrollPane notesScrollPane = new JScrollPane(notesArea);
        detailFrame.add(notesScrollPane, BorderLayout.CENTER);
        detailFrame.add(datePicker, BorderLayout.NORTH);
    
        // 확인 버튼 생성하고 액션 리스너 등록
        JButton confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(e -> {
            deadline = (Date) datePicker.getModel().getValue();
            notes = notesArea.getText();
            updateRemainingDays();
            detailFrame.dispose();
        });
    
        detailFrame.add(confirmButton, BorderLayout.SOUTH);
    
        detailFrame.setVisible(true);
    }
    

    private void updatePriorityLabelStyle() {
        //우선순위에 따라 스타일 업데이트
        Font font = priorityLabel.getFont();
    float size = font.getSize2D();

    switch (priority) {
        case "High":
            priorityLabel.setFont(font.deriveFont(Font.BOLD, size));
            priorityLabel.setForeground(Color.RED);
            break;
        case "Medium":
            priorityLabel.setFont(font.deriveFont(Font.PLAIN, size));
            priorityLabel.setForeground(Color.ORANGE);
            break;
        case "Low":
            priorityLabel.setFont(font.deriveFont(Font.PLAIN, size));
            priorityLabel.setForeground(Color.GREEN);
            break;
    }
    }
    

    private void toggleCompleted() {
    	// 체크박스 선택 상태에 따라 취소선 스타일을 적용하거나 제거
        Map<TextAttribute, Object> attributes = new HashMap<>(checkBox.getFont().getAttributes());
        if (checkBox.isSelected()) {
            attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
            completed = true;
        } else {
            attributes.remove(TextAttribute.STRIKETHROUGH);
            completed = false;
        }
        checkBox.setFont(new Font(attributes));
        parent.updateProgressBar();
    }


    // 할 일 항목을 나타내는 패널을 반환
    public JPanel getPanel() {
        return this.panel;
    }

    // 우선순위에 따른 한글 텍스트 반환
    public String getPriority() {
    	if (priority == "high") {
    		return "높음";
    	}
    	else if (priority == "medium") {
    		return "보통";
    	}
    	else {
    		return "낮음";
    	}
    }
    
    // 체크박스 반환
    public JCheckBox getCheckBox() {
        return checkBox;
    }
    
    // 할 일 항목의 텍스트 반환
    public String getText() {
        return text;
    }

    // 완료 상태를 "O" 또는 "X"로 반환
    public String isDone() {
        return completed ? "O" : "X";
    }

    // 마감일 반환
   public Date getDeadline() {
	   return deadline;
   }
   
   // 메모 반환
  public String getNote() {
	  return notes;
  }
  
  public JLabel getPriorityLabel() {
      return priorityLabel;
  }
   
    public void setDeadlineFromCalendarButton() {
        //마감일 선택할 수 있는 달력 
        UtilDateModel model = new UtilDateModel();
        // if (deadline != null) {
        //     model.setDate(deadline.getYear() + 1900, deadline.getMonth(), deadline.getDate());
        //     model.setSelected(true);
        // }
        JDatePanelImpl datePanel = new JDatePanelImpl(model);
        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel);
    
        int option = JOptionPane.showConfirmDialog(null, datePicker, "Set Deadline", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            deadline = (Date) datePicker.getModel().getValue();
            if (deadline != null) {
                updateRemainingDays();
            } else {
                remainingDaysLabel.setText("");  //미선택도 가능
            }
        }
    }

    // 생성일자를 "yyyy-MM-dd" 형식의 문자열로 변환하여 반환
    public String getFormattedCreationDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(creationDate);
    }
    
}