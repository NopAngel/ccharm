import javax.swing.JLabel;
import javax.swing.JPanel;

public class StatusBar extends JPanel {
    private JLabel statusLabel;

    public StatusBar() {
        statusLabel = new JLabel("Ready");
        add(statusLabel);
    }

    public void setStatus(String status) {
        statusLabel.setText(status);
    }
}
