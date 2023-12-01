import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TODOAPP extends JFrame {

    private DefaultListModel<ToDoItem> listModel;
    private JList<ToDoItem> list;
    private JTextField textField;
    private JComboBox<String> priorityComboBox;

    public TODOAPP() {
        setTitle("To-Do List");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);
        list.setCellRenderer(new ToDoItemRenderer());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(list);
        add(scrollPane, BorderLayout.CENTER);

        textField = new JTextField(20); // 설정된 크기

        String[] priorities = {"Low", "Medium", "High"};
        priorityComboBox = new JComboBox<>(priorities);

        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = textField.getText();
                String priority = (String) priorityComboBox.getSelectedItem();
                if (!text.isEmpty() && priority != null) {
                    listModel.addElement(new ToDoItem(text, priority, new Date(), false));
                    textField.setText("");
                }
            }
        });

        JPanel inputPanel = new JPanel();
        inputPanel.add(textField);
        inputPanel.add(priorityComboBox);
        inputPanel.add(addButton);
        add(inputPanel, BorderLayout.SOUTH);

        list.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int index = list.locationToIndex(evt.getPoint());
                ToDoItem item = list.getModel().getElementAt(index);
                item.toggleDone();
                list.repaint();
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new TODOAPP();
            }
        });
    }

    private static class ToDoItem {
        private String text;
        private String priority;
        private Date creationDate;
        private boolean isDone;

        public ToDoItem(String text, String priority, Date creationDate, boolean isDone) {
            this.text = text;
            this.priority = priority;
            this.creationDate = creationDate;
            this.isDone = isDone;
        }

        public void toggleDone() {
            isDone = !isDone;
        }

        public String getText() {
            return text;
        }

        public String getPriority() {
            return priority;
        }

        public Date getCreationDate() {
            return creationDate;
        }

        public boolean isDone() {
            return isDone;
        }

        public String getFormattedCreationDate() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.format(creationDate);
        }
    }

    private static class ToDoItemRenderer extends JPanel implements ListCellRenderer<ToDoItem> {
        private JCheckBox checkBox;
        private JLabel infoLabel;

        public ToDoItemRenderer() {
            setLayout(new BorderLayout());
            checkBox = new JCheckBox();
            infoLabel = new JLabel();
            add(checkBox, BorderLayout.WEST);
            add(infoLabel, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends ToDoItem> list, ToDoItem value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            checkBox.setSelected(value.isDone());
            String text = value.getText();
            if (value.isDone()) {
                text = "<html><strike>" + text + "</strike></html>";
            }
            infoLabel.setText("<html>" + text + "<br><small>Priority: " + value.getPriority() + 
                              "<br>Date: " + value.getFormattedCreationDate() + "</small></html>");
            return this;
        }
    }
}