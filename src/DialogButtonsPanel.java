import javax.swing.*;

public class DialogButtonsPanel extends JPanel {
    public JButton okButton;
    public JButton cancelButton;

    public DialogButtonsPanel() {
        add(Box.createHorizontalGlue());
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        cancelButton = new JButton("отмена");
        add(cancelButton);

        okButton = new JButton("ок");
        add(okButton);
    }

}