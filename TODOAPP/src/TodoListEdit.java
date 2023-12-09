import javax.swing.*;
import java.awt.*;

//TodoListEdit Ŭ������ �� �� �׸� ���� ���� �� ���� ��ư�� ����
public class TodoListEdit {
	private TodoItem todoItem;        // �� �� �׸� ���� ����
    private JPanel panel;  		      // �� �� �׸��� UI �гο� ���� ����
    private JButton editButton;       // �� �� �׸��� �����ϴ� ��ư
    private JButton deleteButton;     // �� �� �׸��� �����ϴ� ��ư
    
 // �� �� �׸� ���� ������ �޾� �ʵ带 �ʱ�ȭ�ϰ�, ��ư ����
    public TodoListEdit(TodoItem todoItem) {
        this.todoItem = todoItem;
        this.panel = todoItem.getPanel();
        createButtons();
    }
    
    // ���� �� ���� ��ư�� �����ϰ�, �̺�Ʈ �����ʸ� ���
    private void createButtons() {
    	// 'Edit' ��ư�� �����ϰ�, Ŭ�� �� �� �� �׸��� editItem �޼ҵ带 ȣ��
        editButton = new JButton("Edit");
        editButton.addActionListener(e -> todoItem.editItem());
        panel.add(editButton);

     // 'Delete' ��ư�� �����ϰ�, Ŭ�� �� �� �� �׸��� confirmDeletion �޼ҵ带 ȣ��
        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> todoItem.confirmDeletion());
        panel.add(deleteButton);
    }
}
