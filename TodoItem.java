import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.awt.font.TextAttribute;
import net.sourceforge.jdatepicker.impl.*;

public class TodoItem {
    private String text;
    private String priority;
    private Date deadline; 
    private String notes;
    private JPanel panel;
    private JCheckBox checkBox;
    private ToDoList parent;
    private JLabel priorityLabel;
    private JButton editButton;
    private JButton deleteButton;
    private JLabel remainingDaysLabel;
    private JButton deadlineButton;
    private boolean completed; // 체크박스의 상태를 저장하는 변수
    


    public TodoItem(String text, String priority, ToDoList parent) {
        this.text = text;
        this.priority = priority;
        this.parent = parent;
        createPanel();
        this.deadline = new Date(); 
        this.notes = "";
        this.completed = false;
        updateRemainingDays();
    }

    private void createPanel() {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        checkBox = new JCheckBox(text);
        checkBox.addActionListener(e -> {
            toggleCompleted();
            parent.updateUI();
        });

        priorityLabel = new JLabel(priority);
        updatePriorityLabelStyle();
    
        panel.add(checkBox);
        panel.add(Box.createRigidArea(new Dimension(5, 0)));
        panel.add(priorityLabel);
    
        panel.add(Box.createHorizontalGlue());
    
        
        remainingDaysLabel = new JLabel();
        updateRemainingDays();
        panel.add(remainingDaysLabel);
    
        JButton detailButton = new JButton("+");
        detailButton.addActionListener(e -> openDetailWindow());
        panel.add(detailButton);
    
        editButton = new JButton("Edit");
        editButton.addActionListener(e -> editItem());
        panel.add(editButton);
    
        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> confirmDeletion());
        panel.add(deleteButton);
    
    }
    
    

    void updateRemainingDays() {
        //마감일 남은 날짜 계산 
        if (deadline == null) {
            remainingDaysLabel.setText("No Deadline");
            return;
        }

        long diff = deadline.getTime() - System.currentTimeMillis() ;
        long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

        if (days < 0) {
            remainingDaysLabel.setText("Overdue");
        } else if (days == 0) {
            remainingDaysLabel.setText("D-Day");
        } else {
            remainingDaysLabel.setText(" D-" + days+ "  ");
        }
    }


    private void editItem() {
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
    

    private void confirmDeletion() {
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
    
        UtilDateModel model = new UtilDateModel();
        if (deadline != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(deadline);
            model.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            model.setSelected(true);
        }

        JDatePanelImpl datePanel = new JDatePanelImpl(model);
        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel);
    
        JTextArea notesArea = new JTextArea(notes);
        JScrollPane notesScrollPane = new JScrollPane(notesArea);
        detailFrame.add(notesScrollPane, BorderLayout.CENTER);
        detailFrame.add(datePicker, BorderLayout.NORTH);
    
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
        // 체크박스 스타일
        Map<TextAttribute, Object> attributes = new HashMap<>(checkBox.getFont().getAttributes());
        if (checkBox.isSelected()) {
            attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
        } else {
            attributes.remove(TextAttribute.STRIKETHROUGH);
        }
        checkBox.setFont(new Font(attributes));
    }


    public JPanel getPanel() {
        return panel;
    }

    public String getPriority() {
        return priority;

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
                remainingDaysLabel.setText("");  //미선택 도 가능
            }
        }
    }

    public JCheckBox getCheckBox() {
        return checkBox;
    }
    
    
}
