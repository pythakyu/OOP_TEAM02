
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
	
	
	// �� �� �׸��� �ؽ�Ʈ, �켱����, ������, �޸� �����ϴ� ����
    private String text;
    private String priority;
    private Date deadline; 
    private String notes;
    private TodoListEdit editor;
    
    // UI ���� ����
    private JPanel panel;
    private JCheckBox checkBox;
    private ToDoList parent;
    private JLabel priorityLabel;
    private JLabel remainingDaysLabel;
    private JButton deadlineButton;
    private boolean completed; // üũ�ڽ��� ���¸� �����ϴ� ����
    private Date creationDate;
    
    
    public TodoItem(String text, String priority, ToDoList parent) {
        this.text = text;                    // �� �� �׸��� �ؽ�Ʈ�� ����
        this.priority = priority;            // �� �� �׸��� �켱������ ����
        this.parent = parent;                // �θ� ��ü ����
        createPanel();                       // UI �г��� ����
        this.deadline = new Date();          // �������� ���� ��¥�� ����
        this.notes = "";                     // �޸� �� ���ڿ��� �ʱ�ȭ
        this.completed = false;              // �Ϸ� ���¸� false�� �ʱ�ȭ
        this.creationDate = new Date();      // ���� ��¥�� ���� ��¥
        updateRemainingDays();               // ���� �ϼ��� ������Ʈ
        editor = new TodoListEdit(this);
    }
    

    private void createPanel() {
    	// �г��� �����ϰ� ���̾ƿ��� ����
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        
        // üũ�ڽ� �����ϰ� �׼� ������ ���
        checkBox = new JCheckBox(text);
        checkBox.addActionListener(e -> {
            toggleCompleted();
            parent.updateUI();
        });

        // �켱���� ���̺� ���� �� ��Ÿ�� ������Ʈ
        priorityLabel = new JLabel(priority);
        updatePriorityLabelStyle();
    
        // �гο� üũ�ڽ��� �켱���� ���̺��� �߰�
        panel.add(checkBox);
        panel.add(Box.createRigidArea(new Dimension(5, 0)));
        panel.add(priorityLabel);
    
        panel.add(Box.createHorizontalGlue());
        
    	// ���� �ϼ� ���̺� ���� �� ������Ʈ �� �гο� �߰�
        remainingDaysLabel = new JLabel();
        updateRemainingDays();
        panel.add(remainingDaysLabel);
    
        // �� ��ư ����, �׼� ������ ��� �� �гο� �߰�
        JButton detailButton = new JButton("+");
        detailButton.addActionListener(e -> openDetailWindow());
        panel.add(detailButton);
    }
    
    // �� �� �׸��� ���� ��¥ ��ȯ
    public Date getCreationDate() {
        return creationDate;
    }

    //������ ���� ��¥ ���
    void updateRemainingDays() {
    	
    	// �������� ���� ���
        if (deadline == null) {
            remainingDaysLabel.setText("No Deadline");
            return;
        }
    
        // ���� ��¥
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        
        // �������� Calendar ��ü�� ��ȯ
        Calendar deadlineCalendar = Calendar.getInstance();
        deadlineCalendar.setTime(deadline);
        deadlineCalendar.set(Calendar.HOUR_OF_DAY, 0);
        deadlineCalendar.set(Calendar.MINUTE, 0);
        deadlineCalendar.set(Calendar.SECOND, 0);
        deadlineCalendar.set(Calendar.MILLISECOND, 0);
    
        // ���� �ϼ��� ���
        long diff = deadlineCalendar.getTimeInMillis() - today.getTimeInMillis();
        long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    
        days++;
        
        // ���� �ϼ��� ���� ���̺� �ؽ�Ʈ�� ����
        if (days <= 0) {
            remainingDaysLabel.setText("Overdue");
        } else if (days == 1) {
            remainingDaysLabel.setText("D-Day");
        } else {
            remainingDaysLabel.setText(" D-" + (days - 1) + "  ");
        }
    }
    
    public void editItem() {
        //�ؽ�Ʈ�� �켱���� ���氡���� ��ȭ���� ǥ��
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
        //���� Ȯ���ϴ� ��ȭ����
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
        //�����Ͽ� ���θ޸� ���������� �� â
        JFrame detailFrame = new JFrame("Set Deadline and Notes");
        detailFrame.setSize(400, 300);
        detailFrame.setLayout(new BorderLayout());
        
        // �̹� ������ �������� �ִٸ�, �� ���� �ʱⰪ���� ����
        UtilDateModel model = new UtilDateModel();
        if (deadline != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(deadline);
            model.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            model.setSelected(true);
        }

        // ��¥ ���� �г� ����
        JDatePanelImpl datePanel = new JDatePanelImpl(model);
        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel);
    
        // ���� �޸� �Է��� �� �ִ� �ؽ�Ʈ ���� ����
        JTextArea notesArea = new JTextArea(notes);
        JScrollPane notesScrollPane = new JScrollPane(notesArea);
        detailFrame.add(notesScrollPane, BorderLayout.CENTER);
        detailFrame.add(datePicker, BorderLayout.NORTH);
    
        // Ȯ�� ��ư �����ϰ� �׼� ������ ���
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
        //�켱������ ���� ��Ÿ�� ������Ʈ
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
    	// üũ�ڽ� ���� ���¿� ���� ��Ҽ� ��Ÿ���� �����ϰų� ����
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


    // �� �� �׸��� ��Ÿ���� �г��� ��ȯ
    public JPanel getPanel() {
        return this.panel;
    }

    // �켱������ ���� �ѱ� �ؽ�Ʈ ��ȯ
    public String getPriority() {
    	if (priority == "high") {
    		return "����";
    	}
    	else if (priority == "medium") {
    		return "����";
    	}
    	else {
    		return "����";
    	}
    }
    
    // üũ�ڽ� ��ȯ
    public JCheckBox getCheckBox() {
        return checkBox;
    }
    
    // �� �� �׸��� �ؽ�Ʈ ��ȯ
    public String getText() {
        return text;
    }

    // �Ϸ� ���¸� "O" �Ǵ� "X"�� ��ȯ
    public String isDone() {
        return completed ? "O" : "X";
    }

    // ������ ��ȯ
   public Date getDeadline() {
	   return deadline;
   }
   
   // �޸� ��ȯ
  public String getNote() {
	  return notes;
  }
  
  public JLabel getPriorityLabel() {
      return priorityLabel;
  }
   
    public void setDeadlineFromCalendarButton() {
        //������ ������ �� �ִ� �޷� 
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
                remainingDaysLabel.setText("");  //�̼��õ� ����
            }
        }
    }

    // �������ڸ� "yyyy-MM-dd" ������ ���ڿ��� ��ȯ�Ͽ� ��ȯ
    public String getFormattedCreationDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(creationDate);
    }
    
}