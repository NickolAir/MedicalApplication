import Add.*;

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
    JPanel addPanel;
    JPanel deletePanel;
    JPanel editPanel;
    JPanel queryPanel;
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

        JButton openAddPanelButton = new JButton("Открыть меню вввода");
        openAddPanelButton.addActionListener(e -> openAddPanel());
        mainPanel.add(openAddPanelButton, gbc);

        gbc.gridy = 1;

        JButton openQueryPanelButton = new JButton("Открыть меню запросов");
        openQueryPanelButton.addActionListener(e -> openQueryPanel());
        mainPanel.add(openQueryPanelButton, gbc);

        gbc.gridy = 2;

        JButton openEditPanelButton = new JButton("Открыть меню редактирования");
        openEditPanelButton.addActionListener(e -> openEditPanel());
        mainPanel.add(openEditPanelButton, gbc);

        gbc.gridy = 3;

        JButton openDeletePanelButton = new JButton("Открыть меню удаления");
        openDeletePanelButton.addActionListener(e -> openDeletePanel());
        mainPanel.add(openDeletePanelButton, gbc);
    }
    private JPanel initGoBackPanel() {
        JPanel goBackPanel = new JPanel();
        goBackPanel.setBackground(Color.GRAY);
        goBackPanel.setLayout(new BoxLayout(goBackPanel, BoxLayout.X_AXIS));
        JButton goBackButton = new JButton("назад");
        goBackButton.addActionListener(e -> openMainPanel());
        goBackPanel.add(goBackButton);

        goBackPanel.add(Box.createHorizontalGlue());
        return goBackPanel;
    }
    private void initQueryPanel() {
        queryPanel = new JPanel();
        queryPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        //gbc.gridy++;
        JPanel goBackPanel = initGoBackPanel();
        queryPanel.add(goBackPanel, gbc);
    }

    private void initDeletePanel() {
        deletePanel = new JPanel();
        deletePanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        //gbc.gridy++;
        JPanel goBackPanel = initGoBackPanel();
        deletePanel.add(goBackPanel, gbc);
    }

    private void initEditPanel() {
        editPanel = new JPanel();
        editPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        //gbc.gridy++;
        JPanel goBackPanel = initGoBackPanel();
        editPanel.add(goBackPanel, gbc);
    }
    private void initAddPanel() {
        addPanel = new JPanel();
        addPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        newHospitalButton = new JButton("Добавить больницу");
        newHospitalButton.addActionListener(e -> new NewHospitalWindow());
        addPanel.setBackground(Color.GRAY);
        addPanel.add(newHospitalButton, gbc);

        gbc.gridy++;
        newClinicButton = new JButton("Добавить клинику");
        newClinicButton.addActionListener(e -> new NewClinicWindow());
        addPanel.add(newClinicButton, gbc);

        gbc.gridy++;
        newPatientButton = new JButton("Добавить пациента");
        newPatientButton.addActionListener(e -> new NewPatientWindow());
        addPanel.add(newPatientButton, gbc);

        gbc.gridy++;
        newMedicalStaffButton = new JButton("Добавить медперсонал");
        newMedicalStaffButton.addActionListener(e -> new NewMedicalStaffWindow());
        addPanel.add(newMedicalStaffButton, gbc);

        gbc.gridy++;
        newServiceButton = new JButton("Добавить обслуживающий персонал");
        newServiceButton.addActionListener(e -> new NewServiceStaffWindow());
        addPanel.add(newServiceButton, gbc);

        gbc.gridy++;
        newBuilding = new JButton("Добавить здание");
        newBuilding.addActionListener(e -> new NewBuildingWindow());
        addPanel.add(newBuilding, gbc);

        gbc.gridy++;
        newDepartment = new JButton("Добавить отделение");
        newDepartment.addActionListener(e -> new NewDepartmentWindow());
        addPanel.add(newDepartment, gbc);

        gbc.gridy++;
        newRoom = new JButton("Добавить комнату");
        newRoom.addActionListener(e -> new NewRoomWindow());
        addPanel.add(newRoom, gbc);

        gbc.gridy++;
        newBed = new JButton("Добавить кровать");
        newBed.addActionListener(e -> new NewBedWindow());
        addPanel.add(newBed, gbc);

        gbc.gridy++;
        newOrder = new JButton("Добавить направление");
        newOrder.addActionListener(e -> new NewOrderWindow());
        addPanel.add(newOrder, gbc);

        gbc.gridy++;
        newIllness = new JButton("Добавить заболевание");
        newIllness.addActionListener(e -> new NewIllnessWindow());
        addPanel.add(newIllness, gbc);

        gbc.gridy++;
        newOperation = new JButton("Добавить операцию");
        newOperation.addActionListener(e -> new NewOperationWindow());
        addPanel.add(newOperation, gbc);

        gbc.gridy++;
        newLab = new JButton("Добавить лабораторию");
        newLab.addActionListener(e -> new NewLabWindow());
        addPanel.add(newLab, gbc);

        gbc.gridy++;
        newContract = new JButton("Добавить контракт");
        newContract.addActionListener(e -> new NewContractWindow());
        addPanel.add(newContract, gbc);

        gbc.gridy++;
        newLabType = new JButton("Добавить тип лаборатории");
        newLabType.addActionListener(e -> new NewLabTypeWindow());
        addPanel.add(newLabType, gbc);

        gbc.gridy++;
        newDoctorPatientButton = new JButton("Добавить врача к пациенту");
        newDoctorPatientButton.addActionListener(e -> new NewDoctorPatientWindow());
        addPanel.add(newDoctorPatientButton, gbc);

        gbc.gridy++;
        newScienceGrade = new JButton("Добавить научную степень");
        newScienceGrade.addActionListener(e -> new NewScienceGradeWindow());
        addPanel.add(newScienceGrade, gbc);

        gbc.gridy++;
        JPanel goBackPanel = initGoBackPanel();
        addPanel.add(goBackPanel, gbc);

        this.getContentPane().add(addPanel);
    }

    private void connectBD() {
        try (Connection connection = DriverManager.getConnection(ConnectionCnfg.url, ConnectionCnfg.username, ConnectionCnfg.password)) {
            connectButton.setBackground(Color.red);
            connectButton.setEnabled(false);
            System.out.println("Successful connected");
            openMainPanel();
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
    private void openQueryPanel() {
        this.getContentPane().removeAll();
        this.getContentPane().add(queryPanel);
        revalidate();
        repaint();
    }

    private void openAddPanel() {
        this.getContentPane().removeAll();
        this.getContentPane().add(addPanel);
        revalidate();
        repaint();
    }

    void openMainPanel() {
        this.getContentPane().removeAll();
        this.getContentPane().add(mainPanel);
        revalidate();
        repaint();
    }

    void openEditPanel() {
        this.getContentPane().removeAll();
        this.getContentPane().add(editPanel);
        revalidate();
        repaint();
    }

    void openDeletePanel() {
        this.getContentPane().removeAll();
        this.getContentPane().add(deletePanel);
        revalidate();
        repaint();
    }

    public WindowMain() {
        super("Medical");
        try {
            setSize(new Dimension(1280, 832));
            initConnectPanel();
            initMainPanel();
            initAddPanel();
            initQueryPanel();
            initEditPanel();
            initDeletePanel();
            add(connectPanel);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setVisible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}