
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Classroom extends JPanel implements ActionListener {
    private static JFrame mainFrame = null;
    private static Connection connection = null;
    private static ResultSet result = null;
    private static Statement statement = null;
    private static String SQL = null;
    private final JTextField textFieldFind;
    private final DefaultTableModel tableShowModel;
    private final DefaultTableModel tableYoungModel;
    private final JTable tableShow;
    private final JTable tableYoung;
    private final JLabel labelFindCol;
    private final JScrollPane paneYoung;

    public Classroom() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        //Создание панели "Управление".
        JPanel panelControl = new JPanel();
        int width_window = 1400;
        panelControl.setPreferredSize(new Dimension(width_window, 100));
        panelControl.setBorder(BorderFactory.createTitledBorder("Управление"));
        add(Box.createRigidArea(new Dimension(0, 10))); // Отступ 10 пикселей
        panelControl.setLayout(new FlowLayout());

        JButton buttonShow = new JButton("Просмотреть");
        buttonShow.addActionListener(this);
        JButton buttonCreate = new JButton("Добавить");
        buttonCreate.addActionListener(this);
        JButton buttonEdit = new JButton("Редактировать");
        buttonEdit.addActionListener(this);
        JButton buttonDelete = new JButton("Удалить");
        buttonDelete.addActionListener(this);
        JButton buttonEdition = new JButton("Номера аудиторий");
        buttonEdition.addActionListener(this);
        JButton buttonYoungest = new JButton("Самые младшие");
        buttonYoungest.addActionListener(this);
        JButton buttonSum = new JButton("Общая площадь аудиторий");
        buttonSum.addActionListener(this);
        JButton buttonSetDefault = new JButton("Сбросить данные");
        buttonSetDefault.addActionListener(this);

        panelControl.add(buttonShow);
        panelControl.add(buttonCreate);
        panelControl.add(buttonEdit);
        panelControl.add(buttonDelete);
        panelControl.add(buttonEdition);
        panelControl.add(buttonYoungest);
        panelControl.add(buttonSum);
        panelControl.add(buttonSetDefault);
        add(panelControl);

        //Создание панели "Поиск".
        JPanel panelFind = new JPanel();
        panelFind.setPreferredSize(new Dimension(width_window, 50));
        panelFind.setBorder(BorderFactory.createTitledBorder("Поиск"));
        panelFind.setLayout(new GridLayout());
        textFieldFind = new JTextField();
        JButton buttonFind = new JButton("Поиск");
        buttonFind.addActionListener(this);
        panelFind.add(textFieldFind);
        panelFind.add(buttonFind);
        add(Box.createRigidArea(new Dimension(0, 10))); // Отступ сверху вниз на 10 пикселей
        add(panelFind);

        //Создание панели "Список аудиторий".
        JPanel audienceShow = new JPanel();
        audienceShow.setPreferredSize(new Dimension(width_window, 130));
        audienceShow.setLayout(new BoxLayout(audienceShow, BoxLayout.Y_AXIS));
        audienceShow.setBorder(BorderFactory.createTitledBorder("Список аудиторий"));
        add(Box.createRigidArea(new Dimension(0, 10))); // Отступ сверху вниз на 10 пикселей

        tableShowModel = new DefaultTableModel(new Object[]
                {"ID аудитории", "учебное здание", "номер аудит.", "наименование", "площадь", "ID ответственного","ФИО ответственного", "должность", "телефон", "возраст"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableYoungModel = new DefaultTableModel(new Object[]
                {"ФИО ответственного", "должность", "телефон", "возраст"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableShow = new JTable();
        tableShow.setModel(tableShowModel);

        tableYoung = new JTable();
        tableYoung.setModel(tableYoungModel);

        JScrollPane paneShow = new JScrollPane(tableShow);
        paneYoung = new JScrollPane(tableYoung);
        audienceShow.add(paneShow);
        labelFindCol = new JLabel("Найдено записей: 0");
        audienceShow.add(labelFindCol);
        audienceShow.add(paneYoung);
        paneYoung.setVisible(false);
        add(audienceShow);

        // DB connection
        try {
            String dbURL = "jdbc:sqlserver://localhost:1433;"
                    + "databaseName=ClassroomDB;"
                    + "user=admin;"
                    + "password=admin;"
                    + "encrypt=true;"
                    + "trustServerCertificate=true;"
                    + "loginTimeout=30;";
            connection = DriverManager.getConnection(dbURL);

            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            System.out.println(ResultSet.TYPE_SCROLL_INSENSITIVE);
            System.out.println(ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException err) {
            System.out.println(err.getMessage());
        }
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Аудитория");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame = frame;
        JComponent componentPanelAddressBook = new Classroom();
        frame.setContentPane(componentPanelAddressBook);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(Classroom::createAndShowGUI);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        int dataToSize = 11;
        String[] dataTo = new String[dataToSize];

        for (int i = 0; i < dataToSize; i++) {
            dataTo[i] = "";
        }

        int delta_size_dialog = 20;
        if ("Добавить".equals(command)) {
            JDialog dialogContact = new JDialog(mainFrame,
                    "Новая аудитория", JDialog.DEFAULT_MODALITY_TYPE);

            PanelContact panelContact = new PanelContact(command, dataTo);
            dialogContact.setBounds(
                    delta_size_dialog, delta_size_dialog,
                    panelContact.getContactPanelWidth() + 3 * delta_size_dialog,
                    panelContact.getContactPanelHeight() + delta_size_dialog);
            dialogContact.add(panelContact);
            dialogContact.setVisible(true);
        }

        try {

//            SQL = "SELECT Classroom.*, Employee.* FROM Classroom JOIN Employee ON Classroom.IdEmp = Employee.IdEmployee";
//            result = statement.executeQuery(SQL);
            if ((command.equals("Редактировать") || command.equals("Просмотреть"))
                    && result != null && tableShow.getSelectedRow() > -1) {
                result.first();
                do {
                    String value = tableShowModel.getValueAt(tableShow.getSelectedRow(), 0).toString();
                    if (result.getString("IdClassroom").equals(value)) {
                        System.out.println(result);
                        dataTo[0] = result.getString("IdClassroom");
                        dataTo[1] = result.getString("EducationalBuilding");
                        dataTo[2] = result.getString("AudienceNumber");
                        dataTo[3] = result.getString("AudienceName");
                        dataTo[4] = result.getString("AudienceSquare");
                        dataTo[5] = result.getString("IdEmp");
                        dataTo[6] = result.getString("IdEmployee");
                        dataTo[7] = result.getString("FullName");
                        dataTo[8] = result.getString("Post");
                        dataTo[9] = result.getString("PhoneNumber");
                        dataTo[10] = result.getString("Age");

                        String title = "";
                        if (command.equals("Редактировать")) {
                            title = "Изменить аудиторию";
                        }
                        if (command.equals("Просмотреть")) {
                            title = "Просмотреть аудиторию";
                        }

                        JDialog dialogContact = new JDialog(
                                mainFrame,
                                title,
                                JDialog.DEFAULT_MODALITY_TYPE);
                        PanelContact panelContact = new PanelContact(command, dataTo);
                        dialogContact.setBounds(
                                delta_size_dialog,
                                delta_size_dialog,
                                panelContact.getContactPanelWidth() + 3 * delta_size_dialog,
                                panelContact.getContactPanelHeight() + delta_size_dialog);
                        dialogContact.add(panelContact);
                        dialogContact.setVisible(true);
                        break;
                    }
                } while (result.next());
            }
        } catch (SQLException err1) {
            System.out.println(err1.getMessage());
        }

        if (command.equals("Поиск")) {
            findByString(textFieldFind.getText(), 0);
        }

        if (command.equals("Номера аудиторий")) {
            findAudiences();
        }
        if (command.equals("Самые младшие")){
            findYoungest();
        }
        if (command.equals("Общая площадь аудиторий")) {
            findSquareSum();
        }
        if(command.equals("Сбросить данные")){
            setDefault();
        }

        try {
            if (command.equals("Удалить") && result != null && tableShow.getSelectedRow() > -1) {
                SQL = "SELECT Classroom.*, Employee.* FROM Classroom JOIN Employee ON Classroom.IdEmp = Employee.IdEmployee";
                result = statement.executeQuery(SQL);
                result.first();
                do {
                    String value = tableShowModel.getValueAt(tableShow.getSelectedRow(), 0).toString();
                    if (result.getString("IdClassroom").equals(value)) {
                        String deleteEmployee = "DELETE FROM Employee WHERE IdEmployee = " + result.getString("IdEmployee");
                        String deleteClassroom = "DELETE FROM Classroom WHERE IdClassroom = " + result.getString("IdClassroom");

                        PreparedStatement employee;
                        PreparedStatement classroom;
                        connection.setAutoCommit(true);

                        classroom = connection.prepareStatement(deleteClassroom);
                        employee = connection.prepareStatement(deleteEmployee);

                        classroom.executeUpdate();
                        employee.executeUpdate();

                        classroom.close();
                        employee.close();

                        findByString("", 0);

                        break;
                    }
                } while (result.next());
            }
        } catch (SQLException err1) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
                JOptionPane.showMessageDialog(
                        this, "Транзакция на удаление не выполнена.\nСмотрите сообщения в консоли.");
                System.out.println(err1.getMessage());
            } catch (SQLException err2) {
                System.out.println(err2.getMessage());
            }
        }
    }

    private void findByString(String textFind, int column) {
        try {
            while (tableShowModel.getRowCount() > 0) {
                tableShowModel.removeRow(0);
            }

            String orderBy = "";

            if (column == 1)
                orderBy = "ORDER BY author";
            else if (column == 2)
                orderBy = "ORDER BY publication ";

            SQL = "SELECT Classroom.*, Employee.* FROM Classroom JOIN Employee ON Classroom.IdEmp = Employee.IdEmployee " +
                    "WHERE Classroom.AudienceNumber LIKE '" + textFind + "%' " + orderBy;
            result = statement.executeQuery(SQL);
            while (result.next()) {
                String idClassroom = result.getString("IdClassroom");
                String educationalBuilding = result.getString("EducationalBuilding");
                String audienceNumber = result.getString("AudienceNumber");
                String audienceName = result.getString("AudienceName");
                String audienceSquare = result.getString("AudienceSquare");
                String idEmployee = result.getString("IdEmployee");
                String fullName = result.getString("FullName");
                String post = result.getString("Post");
                String phoneNumber = result.getString("PhoneNumber");
                String age = result.getString("Age");
                tableShowModel.addRow(new Object[]
                        {idClassroom, educationalBuilding, audienceNumber, audienceName, audienceSquare, idEmployee, fullName, post, phoneNumber, age});
            }
            labelFindCol.setText("Найдено записей: " + tableShowModel.getRowCount());
        } catch (SQLException err) {
            System.out.println(err.getMessage());
        }
    }

    private void findAudiences() {
        try {
            SQL = "SELECT Classroom.AudienceNumber from Classroom join Employee on Classroom.IdEmp = Employee.IdEmployee " +
                    "ORDER BY AudienceNumber";
            result = statement.executeQuery(SQL);
            String value = "";
            while (result.next()) {
                value += result.getString("audienceNumber");
                value += "\n";
            }
            JOptionPane.showMessageDialog(this, value);
        } catch (SQLException err) {
            System.out.println(err.getMessage());
        }
    }

    private void findYoungest() {
        try {
//            SQL = "SELECT Employee.Age, Employee.FullName from Classroom join Employee on Classroom.IdEmp = Employee.IdEmployee " +
//                    "ORDER BY Employee.Age DESC WHERE Employee.Age = MIN(Employee.Age)";
            SQL = " WITH DuplicateValue AS (\n" +
                    "        SELECT MIN(Age) AS MinAge\n" +
                    "        FROM Employee\n" +
                    "   )\n" +
                    "   SELECT FullName, Age\n" +
                    "   FROM Employee\n" +
                    "   WHERE Age IN (SELECT * FROM DuplicateValue)\n" +
                    "   ORDER BY FullName";
            result = statement.executeQuery(SQL);
            String value = "";
            while (result.next()) {
                value += "ФИО: " + result.getString("fullName") + " ";
                value +=  "Возраст: " + result.getString("age") ;

                value += "\n";
            }
            JOptionPane.showMessageDialog(this, value);
        } catch (SQLException err) {
            System.out.println(err.getMessage());
        }
    }

    private void findSquareSum() {
        if ((tableShow.getSelectedRow() == -1)) return;
        String fullName = tableShowModel.getValueAt(tableShow.getSelectedRow(), 6).toString();
        try {
            SQL = "SELECT FullName, AudienceSquare FROM Employee JOIN Classroom on Employee.IdEmployee=Classroom.IdEmp";
            result = statement.executeQuery(SQL);
            String value = "";
            var sumSquare = 0.0;

            while (result.next()){
                var name = result.getString("fullName");
                if(fullName.equals(name)){
                    sumSquare += Double.parseDouble(result.getString("AudienceSquare"));
                }
                System.out.println(sumSquare);

             };
            value += "Площадь кабинетов " + fullName + " " + Double.toString(sumSquare);
            JOptionPane.showMessageDialog(this, value);
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void setDefault(){
        try{
            if(result!= null){
                String truncateClassroom = "TRUNCATE TABLE Classroom\n" +
                        "ALTER TABLE Classroom\n" +
                        "DROP CONSTRAINT [FK__Classroom__IdEmp__267ABA7A]\n";
                String truncateEmployee = "TRUNCATE TABLE Employee\n";

                String addConstraintClassroom = "ALTER TABLE Classroom\n" +
                        "ADD CONSTRAINT [FK__Classroom__IdEmp__267ABA7A] FOREIGN KEY (IdEmp)\n" +
                        "REFERENCES Employee (IdEmployee)\n";


                PreparedStatement employee;
                PreparedStatement classroom;
                connection.setAutoCommit(true);

                classroom = connection.prepareStatement(truncateClassroom);
                employee = connection.prepareStatement(truncateEmployee);

                classroom.executeUpdate();
                employee.executeUpdate();

                classroom = connection.prepareStatement(addConstraintClassroom);
                classroom.executeUpdate();

                String setDefaultValueEmployee = "INSERT Employee \n" +
                        "\tVALUES\n" +
                        "\t\t('Горохов Андрей Сергеевич', 'Программист', '1111111', 20),\n" +
                        "\t\t('Зигангирова Булат Мисбахович', 'Программист', '1111112', 21),\n" +
                        "\t\t('Калеева Данил Андреевич', 'Программист', '1111113', 21),\n" +
                        "\t\t('Гайфуллин Дамир Равильевич', 'Программист', '1111114', 21),\n" +
                        "\t\t('Галлямов Ильсур Рамисович', 'Аналитик', '1111115', 21)";

                String setDefaultValueClassroom = "INSERT Classroom\n" +
                        "\tVALUES\n" +
                        "\t\t('Building_1', 111, 'Programmig_audience_1', 101, 1 ),\n" +
                        "\t\t('Building_2', 222, 'Programmig_audience_2', 202, 2 ),\n" +
                        "\t\t('Building_3', 333, 'Programmig_audience_3', 303, 3 ),\n" +
                        "\t\t('Building_4', 444, 'Programmig_audience_4', 404, 4 ),\n" +
                        "\t\t('Building_4', 447, 'Programmig_audience_4', 570, 4 ),\n" +
                        "\t\t('Building_4', 430, 'Programmig_audience_4', 435, 4 ),\n" +
                        "\t\t('Building_5', 555, 'Programmig_audience_5', 505, 5 )";

                employee = connection.prepareStatement(setDefaultValueEmployee);
                classroom = connection.prepareStatement(setDefaultValueClassroom);

                employee.executeUpdate();
                classroom.executeUpdate();

                employee.close();
                classroom.close();

                findByString("", 0);
            }
        }catch(SQLException err1){
            try {
                connection.rollback();
                connection.setAutoCommit(true);
                JOptionPane.showMessageDialog(
                        this, "Транзакция на удаление не выполнена.\nСмотрите сообщения в консоли.");
                System.out.println(err1.getMessage());
            } catch (SQLException err2) {
                System.out.println(err2.getMessage());
            }
        }
    }

    class PanelContact extends JPanel implements ActionListener {
        private final int width_window = 300;
        private final int height_window = 300;
        private final String mode;
        private final String[] dataTo;

        private final JTextField txtFieldIdEmployee;
        private final JTextField txtFielEducationalBuilding;
        private final JTextField txtFieldAudienceNumber;
        private final JTextField txtFieldAudienceName;
        private final JTextField txtFieldAudienceSquare;
        private final JTextField txtFieldFullName;
        private final JTextField txtFieldPost;
        private final JTextField txtFieldPhoneNumber;
        private final JTextField txtFieldAge;

        public PanelContact(String mode, String[] dataTo) {
            super();
            this.mode = mode;
            this.dataTo = dataTo;

            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            setPreferredSize(new Dimension(width_window, height_window));
            JPanel panelUp = new JPanel(); //Панель для размещения панелей
            JPanel panelLabel = new JPanel();
            JPanel panelText = new JPanel();
            JPanel panelButton = new JPanel();

            // Labels
            JLabel labelIdEmployee =new JLabel("IdEmp");
            JLabel labelEducationalBuilding = new JLabel("EducationalBuilding");
            JLabel labelAudienceNumber = new JLabel("AudienceNumber");
            JLabel labelAudienceName = new JLabel("AudienceName");
            JLabel labelAudienceSquare = new JLabel("AudienceSquare");
            JLabel labelFullName = new JLabel("FullName");
            JLabel labelPost = new JLabel("Post");
            JLabel labelPhoneNumber = new JLabel("PhoneNumber");
            JLabel labelAge = new JLabel("Age");

            // Fields
            txtFieldIdEmployee = new JTextField(dataTo[5]);
            txtFielEducationalBuilding = new JTextField(dataTo[1]);
            txtFieldAudienceNumber = new JTextField(dataTo[2]);
            txtFieldAudienceName = new JTextField(dataTo[3]);
            txtFieldAudienceSquare = new JTextField(dataTo[4]);
            txtFieldFullName = new JTextField(dataTo[7]);
            txtFieldPost = new JTextField(dataTo[8]);
            txtFieldPhoneNumber = new JTextField(dataTo[9]);
            txtFieldAge = new JTextField(dataTo[10]);

            JButton buttonApply = new JButton("Принять");
            buttonApply.addActionListener(this);
            JButton buttonCancel = new JButton("Отменить");
            buttonCancel.addActionListener(this);

            int height_button_panel = 40;
            int height_gap = 3;
            panelUp.setPreferredSize(new Dimension(width_window,
                    height_window - height_button_panel - height_gap));
            panelUp.setBorder(BorderFactory.createBevelBorder(1));
            add(panelUp);
            panelUp.setLayout(new BoxLayout(panelUp, BoxLayout.LINE_AXIS));
            panelLabel.setPreferredSize(new Dimension(
                    width_window / 3,
                    height_window - height_button_panel - height_gap));
            panelLabel.setBorder(BorderFactory.createBevelBorder(1));
            panelLabel.setLayout(new GridLayout(8, 1));

            panelLabel.add(labelEducationalBuilding);
            panelLabel.add(labelAudienceNumber);
            panelLabel.add(labelAudienceName);
            panelLabel.add(labelAudienceSquare);
            panelLabel.add(labelFullName);
            panelLabel.add(labelPost);
            panelLabel.add(labelPhoneNumber);
            panelLabel.add(labelAge);

            panelText.setPreferredSize(new Dimension(
                    2 * width_window / 3,
                    height_window - height_button_panel - height_gap));
            panelText.setBorder(BorderFactory.createBevelBorder(1));
            panelText.setLayout(new GridLayout(8, 1));

            // Setup
            panelText.add(txtFielEducationalBuilding);
            panelText.add(txtFieldAudienceNumber);
            panelText.add(txtFieldAudienceName);
            panelText.add(txtFieldAudienceSquare);
            panelText.add(txtFieldFullName);
            panelText.add(txtFieldPost);
            panelText.add(txtFieldPhoneNumber);
            panelText.add(txtFieldAge);


            panelUp.add(panelLabel);
            panelUp.add(panelText);
            add(Box.createRigidArea(new Dimension(0, height_gap)));
            panelButton.setPreferredSize(new Dimension(width_window, height_button_panel));
            panelButton.setBorder(BorderFactory.createBevelBorder(1));
            add(panelButton);
            panelButton.setLayout(new FlowLayout());
            panelButton.add(buttonApply);
            panelButton.add(buttonCancel);

            if ("Просмотреть".equals(mode)) {
                buttonApply.setEnabled(false);
                txtFielEducationalBuilding.setEditable(false);
                txtFieldAudienceNumber.setEditable(false);
                txtFieldAudienceName.setEditable(false);
                txtFieldAudienceSquare.setEditable(false);
                txtFieldFullName.setEditable(false);
                txtFieldPost.setEditable(false);
                txtFieldPhoneNumber.setEditable(false);
                txtFieldAge.setEditable(false);
            }
        }

        public int getContactPanelWidth() {
            return width_window;
        }

        public int getContactPanelHeight() {
            return height_window;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            JDialog dialog = (JDialog) SwingUtilities.getWindowAncestor(this);
            if (command.equals("Отменить")) {
                dialog.dispose();
            }
            if (command.equals("Принять")) {
                String educationalBuilding = txtFielEducationalBuilding.getText();
                String audienceNumber = txtFieldAudienceNumber.getText();
                String audienceName = txtFieldAudienceName.getText();
                String audienceSquare = txtFieldAudienceSquare.getText();
                String fullName = txtFieldFullName.getText();
                String post = txtFieldPost.getText();
                String phoneNumber = txtFieldPhoneNumber.getText();
                String age = txtFieldAge.getText();

                if (isNumber(audienceNumber) && isNumber(phoneNumber) && isNumber(age)) {
                    String updateEmployee = null;
                    String updateAudience = null;
                    int idEmployee;

                    // max ID
                    try {
                        result = statement.executeQuery("SELECT IDENT_CURRENT('Employee') as IdEmployee");
                        result.next();
                        idEmployee = Integer.parseInt(result.getString("IdEmployee")) + 1;
                    } catch (SQLException err) {
                        System.out.println(err.getMessage());
                        return;
                    }

                    if (mode.equals("Добавить")) {
                        updateEmployee = String.format("SET IDENTITY_INSERT dbo.Employee ON;\n INSERT Employee(IdEmployee, FullName, Post, PhoneNumber, Age) VALUES(%s, '%s', '%s', '%s', %s)",
                                idEmployee, fullName, post, phoneNumber, age);

                        updateAudience = String.format("INSERT Classroom VALUES('%s', %s, '%s', %s, %s)",
                                educationalBuilding, audienceNumber, audienceName, audienceSquare, idEmployee);

                    }

                    if (mode.equals("Редактировать")) {
                        updateEmployee =
                                String.format("UPDATE Employee " +
                                                "SET Post = '%s', PhoneNumber = '%s', Age = %s, FullName= '%s' " +
                                                "WHERE IdEmployee = %s",
                                        post, phoneNumber, age, fullName, dataTo[5]);
                        updateAudience =
                                String.format("UPDATE Classroom SET " +
                                                "EducationalBuilding = '%s', AudienceNumber = %s, AudienceName = '%s', AudienceSquare = %s  " +
                                                "WHERE IdClassroom = %s",
                                        educationalBuilding, audienceNumber, audienceName, audienceSquare,  dataTo[0]);
                    }

                    // DB
                    try {
                        PreparedStatement employee;
                        PreparedStatement audience;
                        connection.setAutoCommit(true);

                        employee = connection.prepareStatement(updateEmployee);
                        int res = employee.executeUpdate();

                        audience = connection.prepareStatement(updateAudience);
                        res = audience.executeUpdate();

                        employee.close();
                        audience.close();

                    } catch (SQLException err1) {
                        try {
                            connection.setAutoCommit(false);
                            JOptionPane.showMessageDialog(
                                    this,
                                    "Транзакция на создание/изменение не выполнена.\nСмотрите сообщения в консоли.");
                            System.out.println(err1.getMessage());
                            connection.rollback();
                            connection.setAutoCommit(true);
                        } catch (SQLException err2) {
                            System.out.println(err2.getMessage());
                        }
                    }
                    findByString("", 0);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Исправьте введённые данные");
                }
            }
        }

        private boolean isNumber(String text) {
            try {
                Integer.parseInt(text);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }
}