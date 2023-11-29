import java.text.SimpleDateFormat;
import java.util.Date;

public class ToDoItem {
    private String text;
    private boolean completed;
    private Date date;

    public ToDoItem(String text) {
        this.text = text;
        this.completed = false;
        this.date = new Date();
    }

    public String getText() {
        return text;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return "[" + (completed ? "X" : " ") + "] " + text + " - " + dateFormat.format(date);
    }
}
