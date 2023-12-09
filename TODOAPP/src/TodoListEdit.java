import javax.swing.*;
import java.awt.*;

//TodoListEdit 클래스는 할 일 항목에 대한 수정 및 삭제 버튼을 관리
public class TodoListEdit {
	private TodoItem todoItem;        // 할 일 항목에 대한 참조
    private JPanel panel;  		      // 할 일 항목의 UI 패널에 대한 참조
    private JButton editButton;       // 할 일 항목을 수정하는 버튼
    private JButton deleteButton;     // 할 일 항목을 삭제하는 버튼
    
 // 할 일 항목에 대한 참조를 받아 필드를 초기화하고, 버튼 생성
    public TodoListEdit(TodoItem todoItem) {
        this.todoItem = todoItem;
        this.panel = todoItem.getPanel();
        createButtons();
    }
    
    // 수정 및 삭제 버튼을 생성하고, 이벤트 리스너를 등록
    private void createButtons() {
    	// 'Edit' 버튼을 생성하고, 클릭 시 할 일 항목의 editItem 메소드를 호출
        editButton = new JButton("Edit");
        editButton.addActionListener(e -> todoItem.editItem());
        panel.add(editButton);

     // 'Delete' 버튼을 생성하고, 클릭 시 할 일 항목의 confirmDeletion 메소드를 호출
        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> todoItem.confirmDeletion());
        panel.add(deleteButton);
    }
}
