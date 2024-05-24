import javax.swing.*;
import java.util.HashMap;

public class Select {
    private final String selectorLabel;
    private final HashMap<String, Integer> map;
    private final JComboBox<String> selector;

    public Select(String selectorLabel, HashMap<String, Integer> map) {
        this.selectorLabel = selectorLabel;
        this.map = map;
        selector = new JComboBox<>(map.keySet().toArray(new String[0]));
    }

    public Integer getSelectedID() {
        return map.get((String) selector.getSelectedItem());
    }

    public String getSelectorLabel() {
        return selectorLabel;
    }

    public String getSelectedName() {
        return (String)selector.getSelectedItem();
    }

    public JPanel getPanel() {
        JPanel selectorPanel = new JPanel();
        selectorPanel.setLayout(new BoxLayout(selectorPanel, BoxLayout.X_AXIS));
        selectorPanel.add(Box.createHorizontalGlue());

        JLabel selectorLabel = new JLabel(getSelectorLabel());
        selectorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        selectorPanel.add(selectorLabel);

        selectorPanel.add(selector);

        return selectorPanel;
    }
}