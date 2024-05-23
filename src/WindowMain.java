import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class WindowMain extends JFrame {
    private ImageIcon img;
    private JLabel background;
    JButton connectButton;
    JPanel connectPanel;
    JPanel mainPanel;
    JButton newHospitalButton;
    JButton newClinicButton;
    JButton newPatientButton;
    JButton newMedicalStaffButton;
    JButton newServiceButton;
    JButton newBuilding;
    JButton newDepartment;
    JButton newRoom;
    JButton newBed;
    JButton newOrder;
    JButton newIllness;
    JButton newOperation;
    JButton newLab;
    JButton newContract;
    JButton newLabType;
    JButton newDoctorPatientButton;
    JButton newScienceGrade;

    private void initMainPanel() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        newHospitalButton = new JButton("Добавить больницу");
        newHospitalButton.addActionListener(e -> new NewHospitalWindow());
        mainPanel.setBackground(Color.GRAY);
        mainPanel.add(newHospitalButton, gbc);

        gbc.gridy++;
        newClinicButton = new JButton("Добавить клинику");
        newClinicButton.addActionListener(e -> new NewClinicWindow());
        mainPanel.add(newClinicButton, gbc);

        gbc.gridy++;
        newPatientButton = new JButton("Добавить пациента");
        newPatientButton.addActionListener(e -> new NewPatientWindow());
        mainPanel.add(newPatientButton, gbc);

        gbc.gridy++;
        newMedicalStaffButton = new JButton("Добавить медперсонал");
        newMedicalStaffButton.addActionListener(e -> new NewMedicalStaffWindow());
        mainPanel.add(newMedicalStaffButton, gbc);

        gbc.gridy++;
        newServiceButton = new JButton("Добавить обслуживающий персонал");
        newServiceButton.addActionListener(e -> new NewServiceStaffWindow());
        mainPanel.add(newServiceButton, gbc);

        gbc.gridy++;
        newBuilding = new JButton("Добавить здание");
        newBuilding.addActionListener(e -> new NewBuildingWindow());
        mainPanel.add(newBuilding, gbc);

        gbc.gridy++;
        newDepartment = new JButton("Добавить отделение");
        newDepartment.addActionListener(e -> new NewDepartmentWindow());
        mainPanel.add(newDepartment, gbc);

        gbc.gridy++;
        newRoom = new JButton("Добавить комнату");
        newRoom.addActionListener(e -> new NewRoomWindow());
        mainPanel.add(newRoom, gbc);

        gbc.gridy++;
        newBed = new JButton("Добавить кровать");
        newBed.addActionListener(e -> new NewBedWindow());
        mainPanel.add(newBed, gbc);

        gbc.gridy++;
        newOrder = new JButton("Добавить направление");
        newOrder.addActionListener(e -> new NewOrderWindow());
        mainPanel.add(newOrder, gbc);

        gbc.gridy++;
        newIllness = new JButton("Добавить заболевание");
        newIllness.addActionListener(e -> new NewIllnessWindow());
        mainPanel.add(newIllness, gbc);

        gbc.gridy++;
        newOperation = new JButton("Добавить операцию");
        newOperation.addActionListener(e -> new NewOperationWindow());
        mainPanel.add(newOperation, gbc);

        gbc.gridy++;
        newLab = new JButton("Добавить лабораторию");
        newLab.addActionListener(e -> new NewLabWindow());
        mainPanel.add(newLab, gbc);

        gbc.gridy++;
        newContract = new JButton("Добавить контракт");
        newContract.addActionListener(e -> new NewContractWindow());
        mainPanel.add(newContract, gbc);

        gbc.gridy++;
        newLabType = new JButton("Добавить тип лаборатории");
        newLabType.addActionListener(e -> new NewLabTypeWindow());
        mainPanel.add(newLabType, gbc);

        gbc.gridy++;
        newDoctorPatientButton = new JButton("Добавить врача к пациенту");
        newDoctorPatientButton.addActionListener(e -> new NewDoctorPatientWindow());
        mainPanel.add(newDoctorPatientButton, gbc);

        gbc.gridy++;
        newScienceGrade = new JButton("Добавить научную степень");
        newScienceGrade.addActionListener(e -> new NewScienceGradeWindow());
        mainPanel.add(newScienceGrade, gbc);

        this.getContentPane().add(mainPanel);
    }

    private void connectBD() {
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            connectButton.setBackground(Color.red);
            connectButton.setEnabled(false);
            System.out.println("Successful connected");
            this.getContentPane().remove(connectPanel);
            initMainPanel();
            revalidate(); // Обновляем содержимое JFrame
            repaint();   // Перерисовываем JFrame
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Ошибка подключения к базе данных\n" + e.getMessage());
            connectButton.setText("Подключиться к базе данных");
            connectButton.setEnabled(true);
        }
    }

    private void initConnectPanel() {
        img = new ImageIcon("/Users/nikolayratushnyak/IdeaProjects/MedicalApplication/res/medical_organization.png");
        background = new JLabel("", img, JLabel.CENTER);

        connectPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(img.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        connectPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;

        connectButton = new JButton("Подключиться к базе данных");
        connectButton.addActionListener(e -> {
            connectButton.setText("Подключение...");
            connectBD();
        });
        connectPanel.add(connectButton, gbc);

        this.getContentPane().add(connectPanel);
    }

    public WindowMain() {
        super("Medical");
        try {
            setSize(new Dimension(640, 480));
            initConnectPanel();
            add(connectPanel);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setVisible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}