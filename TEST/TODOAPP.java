import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import net.sourceforge.jdatepicker.impl.*;

public class TODOAPP extends JFrame {

    private DefaultListModel<ToDoItem> listModel;
    private JList<ToDoItem> list;
    private JTextField textField;
    private JComboBox<String> priorityComboBox;
    private JProgressBar progressBar;
    private JButton saveButton;

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

        textField = new JTextField(20);

        String[] priorities = {"Low", "Medium", "High"};
        priorityComboBox = new JComboBox<>(priorities);

        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = textField.getText();
                String priority = (String) priorityComboBox.getSelectedItem();
                if (!text.isEmpty() && priority != null) {
                    listModel.addElement(new ToDoItem(text, priority, new Date(), false, TODOAPP.this));
                    textField.setText("");
                }
            }
        });

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true); // Display percentage

        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveToFile();
            }
        });

        // JPanel to hold ProgressBar and Save button horizontally
        JPanel progressPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        progressPanel.add(progressBar);
        progressPanel.add(saveButton);

        add(progressPanel, BorderLayout.NORTH);

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
        private TODOAPP parent;

        public ToDoItem(String text, String priority, Date creationDate, boolean isDone, TODOAPP parent) {
            this.text = text;
            this.priority = priority;
            this.creationDate = creationDate;
            this.isDone = isDone;
            this.parent = parent;
        }

        public void toggleDone() {
            isDone = !isDone;
            // Update progress bar when toggling the completion status
            parent.updateProgressBar();
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

    private void updateProgressBar() {
        if (listModel.isEmpty()) {
            progressBar.setValue(0);
        } else {
            // Calculate the percentage of completed tasks
            AtomicInteger completedCount = new AtomicInteger(0);
            for (int i = 0; i < listModel.getSize(); i++) {
                ToDoItem item = listModel.getElementAt(i);
                if (item.isDone()) {
                    completedCount.incrementAndGet();
                }
            }
            int percentage = (completedCount.get() * 100) / listModel.getSize();
            progressBar.setValue(percentage);
        }
    }

    private void saveToFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileChooser.getSelectedFile()))) {
                for (int i = 0; i < listModel.getSize(); i++) {
                    ToDoItem item = listModel.getElementAt(i);
                    writer.write(item.getText() + "; 중요도: " + item.getPriority() + "; " +
                            "날짜: " + item.getFormattedCreationDate() + "; 완료여부: " + item.isDone() + "\n");
                }
                JOptionPane.showMessageDialog(this, "To-Do items saved successfully!");
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving to file!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        updateProgressBar();
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

