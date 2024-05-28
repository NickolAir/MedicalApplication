import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class WindowMain extends JFrame {
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
    JButton deleteHospitalButton;
    JButton deleteClinicButton;
    JButton deletePatientButton;
    JButton deleteMedicalStaffButton;
    JButton deleteServiceButton;
    JButton deleteBuilding;
    JButton deleteDepartment;
    JButton deleteRoom;
    JButton deleteBed;
    JButton deleteOrder;
    JButton deleteIllness;
    JButton deleteOperation;
    JButton deleteLab;
    JButton deleteContract;
    JButton deleteLabType;
    JButton deleteDoctorPatientButton;
    JButton deleteScienceGrade;
    JButton editHospitalButton;
    JButton editClinicButton;
    JButton editPatientButton;
    JButton editMedicalStaffButton;
    JButton editServiceButton;
    JButton editBuilding;
    JButton editDepartment;
    JButton editRoom;
    JButton editBed;
    JButton editOrder;
    JButton editIllness;
    JButton editOperation;
    JButton editLab;
    JButton editContract;
    JButton editLabType;
    JButton editDoctorPatientButton;
    JButton editScienceGrade;
    JButton queryDoctorsProfile;
    JButton queryServiceStaffSpecialty;
    JButton queryDoctorsOperations;
    JButton queryDoctorsExperience;
    JButton queryDoctorsDegree;
    JButton queryPatientsDetails;
    JButton queryPatientsTreatment;
    JButton queryPatientsClinic;
    JButton queryBedsAvailability;
    JButton queryClinicVisits;
    JButton queryDoctorWorkload;
    JButton queryCurrentPatients;
    JButton queryPatientsSurgery;
    JButton queryLabWorkload;

    private void initMainPanel() {

        mainPanel = new JPanel() {
            ImageIcon img = new ImageIcon("/Users/nikolayratushnyak/IdeaProjects/MedicalApplication/res/medical_organization.png");
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(img.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
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
        ImageIcon img = new ImageIcon("/Users/nikolayratushnyak/IdeaProjects/MedicalApplication/res/query.png");
        queryPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(img.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        queryPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        queryDoctorsProfile = new JButton("Врачи по профилю");
        queryDoctorsProfile.addActionListener(e -> new executeQueryDoctorsProfile());
        queryPanel.add(queryDoctorsProfile, gbc);

        gbc.gridy++;
        queryServiceStaffSpecialty = new JButton("Персонал по специальности");
        queryServiceStaffSpecialty.addActionListener(e -> new executeQueryServiceStaffSpecialty());
        queryPanel.add(queryServiceStaffSpecialty, gbc);

        gbc.gridy++;
        queryDoctorsOperations = new JButton("Врачи по операциям");
        queryDoctorsOperations.addActionListener(e -> new executeQueryDoctorsOperations());
        queryPanel.add(queryDoctorsOperations, gbc);

        gbc.gridy++;
        queryDoctorsExperience = new JButton("Врачи по стажу");
        queryDoctorsExperience.addActionListener(e -> new executeQueryDoctorsExperience());
        queryPanel.add(queryDoctorsExperience, gbc);

        gbc.gridy++;
        queryDoctorsDegree = new JButton("Врачи по степени");
        queryDoctorsDegree.addActionListener(e -> new executeQueryDoctorsDegree());
        queryPanel.add(queryDoctorsDegree, gbc);

        gbc.gridy++;
        queryPatientsDetails = new JButton("Пациенты больницы");
        queryPatientsDetails.addActionListener(e -> new executeQueryPatientsDetails());
        queryPanel.add(queryPatientsDetails, gbc);

        gbc.gridy++;
        queryPatientsTreatment = new JButton("Пациенты по лечению");
        queryPatientsTreatment.addActionListener(e -> new executeQueryPatientsTreatment());
        queryPanel.add(queryPatientsTreatment, gbc);

        gbc.gridy++;
        queryPatientsClinic = new JButton("Пациенты клиники");
        queryPatientsClinic.addActionListener(e -> new executeQueryPatientsClinic());
        queryPanel.add(queryPatientsClinic, gbc);

        gbc.gridy++;
        queryBedsAvailability = new JButton("Доступность коек");
        queryBedsAvailability.addActionListener(e -> new executeQueryBedsAvailability());
        queryPanel.add(queryBedsAvailability, gbc);

        gbc.gridy++;
        queryCurrentPatients = new JButton("Загрузка врача");
        queryCurrentPatients.addActionListener(e -> new executeQueryCurrentPatients());
        queryPanel.add(queryCurrentPatients, gbc);

        gbc.gridy++;
        queryPatientsSurgery = new JButton("Пациенты по операциям");
        queryPatientsSurgery.addActionListener(e -> new executeQueryPatientsSurgery());
        queryPanel.add(queryPatientsSurgery, gbc);

        gbc.gridy++;
        queryLabWorkload = new JButton("Выработка лаборатории");
        queryLabWorkload.addActionListener(e -> new executeQueryLabWorkload());
        queryPanel.add(queryLabWorkload, gbc);

        gbc.gridy++;
        JPanel goBackPanel = initGoBackPanel();
        queryPanel.add(goBackPanel, gbc);
    }

    private void initDeletePanel() {
        ImageIcon img = new ImageIcon("/Users/nikolayratushnyak/IdeaProjects/MedicalApplication/res/delete.png");
        deletePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(img.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        deletePanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        deleteHospitalButton = new JButton("Удалить больницу");
        deleteHospitalButton.addActionListener(e -> new DeleteHospitalWindow());
        deletePanel.add(deleteHospitalButton, gbc);

        gbc.gridy++;
        deleteClinicButton = new JButton("Удалить клинику");
        deleteClinicButton.addActionListener(e -> new DeleteClinicWindow());
        deletePanel.add(deleteClinicButton, gbc);

        gbc.gridy++;
        deletePatientButton = new JButton("Удалить пациента");
        deletePatientButton.addActionListener(e -> new DeletePatientWindow());
        deletePanel.add(deletePatientButton, gbc);

        gbc.gridy++;
        deleteMedicalStaffButton = new JButton("Удалить медперсонал");
        deleteMedicalStaffButton.addActionListener(e -> new DeleteMedicalStaffWindow());
        deletePanel.add(deleteMedicalStaffButton, gbc);

        gbc.gridy++;
        deleteServiceButton = new JButton("Удалить обслуживающий персонал");
        deleteServiceButton.addActionListener(e -> new DeleteServiceStaffWindow());
        deletePanel.add(deleteServiceButton, gbc);

        gbc.gridy++;
        deleteBuilding = new JButton("Удалить здание");
        deleteBuilding.addActionListener(e -> new DeleteBuildingWindow());
        deletePanel.add(deleteBuilding, gbc);

        gbc.gridy++;
        deleteDepartment = new JButton("Удалить отделение");
        deleteDepartment.addActionListener(e -> new DeleteDepartmentWindow());
        deletePanel.add(deleteDepartment, gbc);

        gbc.gridy++;
        deleteRoom = new JButton("Удалить комнату");
        deleteRoom.addActionListener(e -> new DeleteRoomWindow());
        deletePanel.add(deleteRoom, gbc);

        gbc.gridy++;
        deleteBed = new JButton("Удалить кровать");
        deleteBed.addActionListener(e -> new DeleteBedWindow());
        deletePanel.add(deleteBed, gbc);

        gbc.gridy++;
        deleteIllness = new JButton("Удалить заболевание");
        deleteIllness.addActionListener(e -> new DeleteIllnessWindow());
        deletePanel.add(deleteIllness, gbc);

        gbc.gridy++;
        deleteLab = new JButton("Удалить лабораторию");
        deleteLab.addActionListener(e -> new DeleteLabWindow());
        deletePanel.add(deleteLab, gbc);

        gbc.gridy++;
        deleteContract = new JButton("Удалить контракт");
        deleteContract.addActionListener(e -> new DeleteContractWindow());
        deletePanel.add(deleteContract, gbc);

        gbc.gridy++;
        deleteLabType = new JButton("Удалить тип лаборатории");
        deleteLabType.addActionListener(e -> new DeleteLabTypeWindow());
        deletePanel.add(deleteLabType, gbc);

        gbc.gridy++;
        deleteDoctorPatientButton = new JButton("Удалить врача от пациента");
        deleteDoctorPatientButton.addActionListener(e -> new DeleteDoctorPatientWindow());
        deletePanel.add(deleteDoctorPatientButton, gbc);

        gbc.gridy++;
        deleteScienceGrade = new JButton("Удалить научную степень");
        deleteScienceGrade.addActionListener(e -> new DeleteScienceGradeWindow());
        deletePanel.add(deleteScienceGrade, gbc);

        gbc.gridy++;
        JPanel goBackPanel = initGoBackPanel();
        deletePanel.add(goBackPanel, gbc);
    }

    private void initEditPanel() {
        ImageIcon img = new ImageIcon("/Users/nikolayratushnyak/IdeaProjects/MedicalApplication/res/edit.png");
        editPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(img.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        editPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        editHospitalButton = new JButton("Редактировать больницу");
        editHospitalButton.addActionListener(e -> new EditHospitalWindow());
        editPanel.add(editHospitalButton, gbc);

        gbc.gridy++;
        editClinicButton = new JButton("Редактировать клинику");
        editClinicButton.addActionListener(e -> new EditClinicWindow());
        editPanel.add(editClinicButton, gbc);

        gbc.gridy++;
        editPatientButton = new JButton("Редактировать пациента");
        editPatientButton.addActionListener(e -> new EditPatientWindow());
        editPanel.add(editPatientButton, gbc);

        gbc.gridy++;
        editMedicalStaffButton = new JButton("Редактировать медперсонал");
        editMedicalStaffButton.addActionListener(e -> new EditMedicalStaffWindow());
        editPanel.add(editMedicalStaffButton, gbc);

        gbc.gridy++;
        editServiceButton = new JButton("Редактировать обслуживающий персонал");
        editServiceButton.addActionListener(e -> new EditServiceStaffWindow());
        editPanel.add(editServiceButton, gbc);

        gbc.gridy++;
        editBuilding = new JButton("Редактировать здание");
        editBuilding.addActionListener(e -> new EditBuildingWindow());
        editPanel.add(editBuilding, gbc);

        gbc.gridy++;
        editDepartment = new JButton("Редактировать отделение");
        editDepartment.addActionListener(e -> new EditDepartmentWindow());
        editPanel.add(editDepartment, gbc);

        gbc.gridy++;
        editRoom = new JButton("Редактировать комнату");
        editRoom.addActionListener(e -> new EditRoomWindow());
        editPanel.add(editRoom, gbc);

        gbc.gridy++;
        editBed = new JButton("Редактировать кровать");
        editBed.addActionListener(e -> new EditBedWindow());
        editPanel.add(editBed, gbc);

        gbc.gridy++;
        editIllness = new JButton("Редактировать заболевание");
        editIllness.addActionListener(e -> new EditIllnessWindow());
        editPanel.add(editIllness, gbc);

        gbc.gridy++;
        editLabType = new JButton("Редактировать тип лаборатории");
        editLabType.addActionListener(e -> new EditLabTypeWindow());
        editPanel.add(editLabType, gbc);

        gbc.gridy++;
        editScienceGrade = new JButton("Редактировать научную степень");
        editScienceGrade.addActionListener(e -> new EditScienceGradeWindow());
        editPanel.add(editScienceGrade, gbc);

        gbc.gridy++;
        JPanel goBackPanel = initGoBackPanel();
        editPanel.add(goBackPanel, gbc);
    }
    private void initAddPanel() {
        ImageIcon img = new ImageIcon("/Users/nikolayratushnyak/IdeaProjects/MedicalApplication/res/add.png");
        addPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(img.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        addPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        newHospitalButton = new JButton("Добавить больницу");
        newHospitalButton.addActionListener(e -> new NewHospitalWindow());
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
        ImageIcon img = new ImageIcon("/Users/nikolayratushnyak/IdeaProjects/MedicalApplication/res/medical_organization.png");
        JLabel background = new JLabel("", img, JLabel.CENTER);

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