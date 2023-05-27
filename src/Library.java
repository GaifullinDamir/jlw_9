
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Library extends JPanel implements ActionListener {
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

    public Library() {
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
        JButton buttonOrderBuilding = new JButton("Упорядочить по 1 столбцу");
        buttonOrderBuilding.addActionListener(this);
        JButton buttonOrderNumber = new JButton("Упорядочить по 2 столбцу");
        buttonOrderNumber.addActionListener(this);

        panelControl.add(buttonShow);
        panelControl.add(buttonCreate);
        panelControl.add(buttonEdit);
        panelControl.add(buttonDelete);
        panelControl.add(buttonEdition);
        panelControl.add(buttonYoungest);
        panelControl.add(buttonSum);
        panelControl.add(buttonOrderBuilding);
        panelControl.add(buttonOrderNumber);
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
                {"учебное здание", "номер аудит.", "наименование", "площадь", "ФИО ответственного", "должность", "телефон", "возраст"}, 0) {
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
        JComponent componentPanelAddressBook = new Library();
        frame.setContentPane(componentPanelAddressBook);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(Library::createAndShowGUI);
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

            SQL = "SELECT Classroom.*, Employee.* FROM Classroom JOIN Employee ON Classroom.IdEmp = Employee.IdEmployee";
            result = statement.executeQuery(SQL);
            if ((command.equals("Редактировать") || command.equals("Просмотреть"))
                    && result != null && tableShow.getSelectedRow() > -1) {
                result.first();
                do {
                    String value = tableShowModel.getValueAt(tableShow.getSelectedRow(), 0).toString();
                    if (result.getString("EducationalBuilding").equals(value)) {
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

        if (command.equals("Упорядочить по 1 столбцу")) {
            findByString("", 1);
        }

        if (command.equals("Упорядочить по 2 столбцу")) {
            findByString("", 2);
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

        try {
            if (command.equals("Удалить") && result != null && tableShow.getSelectedRow() > -1) {
                result.first();
                do {
                    String value = tableShowModel.getValueAt(tableShow.getSelectedRow(), 0).toString();
                    if (result.getString("author").equals(value)) {
                        String deleteIC = "DELETE FROM book WHERE place_id = " + result.getString("place_id");
                        String deleteAudience = "DELETE FROM place WHERE id_place = " + result.getString("id_place");

                        PreparedStatement IC;
                        PreparedStatement audience;
                        connection.setAutoCommit(true);

                        IC = connection.prepareStatement(deleteIC);
                        audience = connection.prepareStatement(deleteAudience);
                        IC.executeUpdate();
                        audience.executeUpdate();
                        IC.close();
                        audience.close();
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
                String educationalBuilding = result.getString("EducationalBuilding");
                String audienceNumber = result.getString("AudienceNumber");
                String audienceName = result.getString("AudienceName");
                String audienceSquare = result.getString("AudienceSquare");
                String fullName = result.getString("FullName");
                String post = result.getString("Post");
                String phoneNumber = result.getString("PhoneNumber");
                String age = result.getString("Age");
                tableShowModel.addRow(new Object[]
                        {educationalBuilding, audienceNumber, audienceName, audienceSquare, fullName, post, phoneNumber, age});
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
        String fullName = tableShowModel.getValueAt(tableShow.getSelectedRow(), 4).toString();
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

    class PanelContact extends JPanel implements ActionListener {
        private final int width_window = 300;
        private final int height_window = 300;
        private final String mode;
        private final String[] dataTo;

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
            JLabel labelEducationalBuilding = new JLabel("EducationalBuilding");
            JLabel labelAudienceNumber = new JLabel("AudienceNumber");
            JLabel labelAudienceName = new JLabel("AudienceName");
            JLabel labelAudienceSquare = new JLabel("AudienceSquare");
            JLabel labelFullName = new JLabel("FullName");
            JLabel labelPost = new JLabel("Post");
            JLabel labelPhoneNumber = new JLabel("PhoneNumber");
            JLabel labelAge = new JLabel("Age");

            // Fields
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

                if (isNumber(audienceSquare)  && isNumber(audienceNumber) && isNumber(phoneNumber) && isNumber(age)) {
                    String updateEmployee = null;
                    String updateAudience = null;
                    int idEmployee;

                    // max ID
                    try {
                        result = statement.executeQuery("SELECT TOP 1 IdEmployee FROM Employee ORDER BY IdEmployee DESC");
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
                                String.format("UPDATE place " +
                                                "SET floor = %s, wardrobe = %s, shelf = '%s' " +
                                                "WHERE id_place = %s",
                                        post, phoneNumber, age, dataTo[9]);
                        updateAudience =
                                String.format("UPDATE book SET " +
                                                "author = '%s', publication = '%s', publishing_house = '%s', year_public = %s, pages = '%s', year_write = '%s', weight = %s " +
                                                "WHERE id = %s",
                                        educationalBuilding, audienceNumber, audienceName, audienceSquare,  fullName, dataTo[0]);
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