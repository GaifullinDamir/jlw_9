
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
        JButton buttonEdition = new JButton("Издания в указанном шкафу в лексиграфическом порядке");
        buttonEdition.addActionListener(this);
        JButton buttonSum = new JButton("Вывести минимальное и максимальное кол-во страниц на этаже");
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
                {"Автор", "Название издания", "Издательство", "Год Издания", "Кол-во страниц", "Год написания", "Вес", "Этаж", "Шкаф", "Полка"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableYoungModel = new DefaultTableModel(new Object[]
                {"Этаж", "Шкаф", "Полка"}, 0) {
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
            String dbURL = "jdbc:postgresql://localhost:5433/postgres";
            String user_publishing_house = "postgres";
            String user_password = "1234";
            connection = DriverManager.getConnection(dbURL, user_publishing_house, user_password);
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException err) {
            System.out.println(err.getMessage());
        }
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Библиотека");
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
        int dataToSize = 13;
        String[] dataTo = new String[dataToSize];

        for (int i = 0; i < dataToSize; i++) {
            dataTo[i] = "";
        }

        int delta_size_dialog = 20;
        if ("Добавить".equals(command)) {
            JDialog dialogContact = new JDialog(mainFrame,
                    "Новая книга", JDialog.DEFAULT_MODALITY_TYPE);

            PanelContact panelContact = new PanelContact(command, dataTo);
            dialogContact.setBounds(
                    delta_size_dialog, delta_size_dialog,
                    panelContact.getContactPanelWidth() + 3 * delta_size_dialog,
                    panelContact.getContactPanelHeight() + delta_size_dialog);
            dialogContact.add(panelContact);
            dialogContact.setVisible(true);
        }

        try {
            if ((command.equals("Редактировать") || command.equals("Просмотреть"))
                    && result != null && tableShow.getSelectedRow() > -1) {
                result.first();
                do {
                    String value = tableShowModel.getValueAt(tableShow.getSelectedRow(), 0).toString();
                    if (result.getString("author").equals(value)) {
                        dataTo[0] = result.getString("id");
                        dataTo[1] = result.getString("author");
                        dataTo[2] = result.getString("publication");
                        dataTo[3] = result.getString("publishing_house");
                        dataTo[4] = result.getString("year_public");
                        dataTo[5] = result.getString("pages");
                        dataTo[6] = result.getString("year_write");
                        dataTo[7] = result.getString("weight");
                        dataTo[8] = result.getString("place_id");
                        dataTo[9] = result.getString("id_place");
                        dataTo[10] = result.getString("floor");
                        dataTo[11] = result.getString("wardrobe");
                        dataTo[12] = result.getString("shelf");

                        String title = "";
                        if (command.equals("Редактировать")) {
                            title = "Изменить книгу";
                        }
                        if (command.equals("Просмотреть")) {
                            title = "Просмотреть книгу";
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

        if (command.equals("Издания в указанном шкафу в лексиграфическом порядке")) {
            findPublication();
        }
        if (command.equals("Вывести минимальное и максимальное кол-во страниц на этаже")) {
            findPages();
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

            SQL = "SELECT book.*, place.* FROM book JOIN place ON book.place_id = place.id_place " +
                    "WHERE book.author ILIKE '" + textFind + "%' " + orderBy;
            result = statement.executeQuery(SQL);
            while (result.next()) {
                String author = result.getString("author");
                String publication = result.getString("publication");
                String publishing_house = result.getString("publishing_house");
                String year_public = result.getString("year_public");
                String pages = result.getString("pages");
                String year_write = result.getString("year_write");
                String weight = result.getString("weight");
                String floor = result.getString("floor");
                String wardrobe = result.getString("wardrobe");
                String shelf = result.getString("shelf");
                tableShowModel.addRow(new Object[]
                        {author, publication, publishing_house, year_public, pages, year_write,weight, floor,  wardrobe, shelf});
            }
            labelFindCol.setText("Найдено записей: " + tableShowModel.getRowCount());
        } catch (SQLException err) {
            System.out.println(err.getMessage());
        }
    }

    private void findPublication() {
        if ((tableShow.getSelectedRow() == -1)) return;
        String wardrobe = tableShowModel.getValueAt(tableShow.getSelectedRow(), 8).toString();
        try {
            SQL = "select publishing_house from book join place on book.place_id = place.id_place " +
                    "where wardrobe = " + "'" + wardrobe + "'" + " order by publication";
            result = statement.executeQuery(SQL);
            String value = "";
            while (result.next()) {
                value += result.getString("publishing_house");
                value += "\n";
            }
            JOptionPane.showMessageDialog(this, value);
        } catch (SQLException err) {
            System.out.println(err.getMessage());
        }
    }

    private void findPages() {
        if ((tableShow.getSelectedRow() == -1)) return;
        String floor = tableShowModel.getValueAt(tableShow.getSelectedRow(), 7).toString();
        try {
            SQL = "select MIN(pages) from book join place on book.place_id = place.id_place " +
                    "where floor = " + "'" + floor + "'";
            result = statement.executeQuery(SQL);
            String value = "";
            if (result.next()) {
                value += "Minimum:";
                value += result.getString("min");
            }
            value += "\n";
            SQL = "select MAX(pages) from book join place on book.place_id = place.id_place " +
                    "where floor = " + "'" + floor + "'";
            result = statement.executeQuery(SQL);
            if (result.next()) {
                value += "Maximum:";
                value += result.getString("max");
            }
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

        private final JTextField txtFieldAuthor;
        private final JTextField txtFieldPublication;
        private final JTextField txtPublishingHouse;
        private final JTextField txtYearPublicated;
        private final JTextField txtPages;
        private final JTextField txtWeight;
        private final JTextField txtFloor;
        private final JTextField txtWardrobe;
        private final JTextField txtShelf;
        private final JTextField txtFielYearWrite;

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
            JLabel labelAuthor = new JLabel("Автор");
            JLabel labelPublication = new JLabel("Издание");
            JLabel labelPublishingHouse = new JLabel("Издательство");
            JLabel labelYearPublicated = new JLabel("Год публикации");
            JLabel labelPages = new JLabel("Страниц");
            JLabel labelPosition = new JLabel("Вес");
            JLabel labelFloor = new JLabel("Этаж");
            JLabel labelWardrobe = new JLabel("Шкаф");
            JLabel labelShelf = new JLabel("Полка");
            JLabel labelYearWrite = new JLabel("Год написания");

            // Fields
            txtFieldAuthor = new JTextField(dataTo[1]);
            txtFieldPublication = new JTextField(dataTo[2]);
            txtPublishingHouse = new JTextField(dataTo[3]);
            txtYearPublicated = new JTextField(dataTo[4]);
            txtPages = new JTextField(dataTo[5]);
            txtFielYearWrite = new JTextField(dataTo[6]);
            txtWeight = new JTextField(dataTo[7]);
            txtFloor = new JTextField(dataTo[10]);
            txtWardrobe = new JTextField(dataTo[11]);
            txtShelf = new JTextField(dataTo[12]);

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

            panelLabel.add(labelAuthor);
            panelLabel.add(labelPublication);
            panelLabel.add(labelPublishingHouse);
            panelLabel.add(labelYearPublicated);
            panelLabel.add(labelPages);
            panelLabel.add(labelYearWrite);
            panelLabel.add(labelPosition);
            panelLabel.add(labelFloor);
            panelLabel.add(labelWardrobe);
            panelLabel.add(labelShelf);

            panelText.setPreferredSize(new Dimension(
                    2 * width_window / 3,
                    height_window - height_button_panel - height_gap));
            panelText.setBorder(BorderFactory.createBevelBorder(1));
            panelText.setLayout(new GridLayout(8, 1));


            // Setup
            panelText.add(txtFieldAuthor);
            panelText.add(txtFieldPublication);
            panelText.add(txtPublishingHouse);
            panelText.add(txtYearPublicated);
            panelText.add(txtPages);
            panelText.add(txtFielYearWrite);
            panelText.add(txtWeight);
            panelText.add(txtFloor);
            panelText.add(txtWardrobe);
            panelText.add(txtShelf);


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
                txtFieldAuthor.setEditable(false);
                txtFieldPublication.setEditable(false);
                txtPublishingHouse.setEditable(false);
                txtYearPublicated.setEditable(false);
                txtPages.setEditable(false);
                txtWeight.setEditable(false);
                txtFloor.setEditable(false);
                txtWardrobe.setEditable(false);
                txtShelf.setEditable(false);
                txtFielYearWrite.setEditable(false);
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
                String author = txtFieldAuthor.getText();
                String publication = txtFieldPublication.getText();
                String publishing_house = txtPublishingHouse.getText();
                String year_public = txtYearPublicated.getText();
                String pages = txtPages.getText();
                String year_write = txtFielYearWrite.getText();
                String weight = txtWeight.getText();
                String floor = txtFloor.getText();
                String wardrobe = txtWardrobe.getText();
                String shelf = txtShelf.getText();

                if (isNumber(year_public) && isNumber(pages) && isNumber(weight) && isNumber(floor) && isNumber(wardrobe) && isNumber(shelf)) {
                    String updateAudience = null;
                    String updateIC = null;
                    int audience_id;

                    // max ID
                    try {
                        result = statement.executeQuery("SELECT id_place FROM place ORDER BY id_place DESC limit 1");
                        result.next();
                        audience_id = Integer.parseInt(result.getString("id_place")) + 1;
                    } catch (SQLException err) {
                        System.out.println(err.getMessage());
                        return;
                    }

                    if (mode.equals("Добавить")) {
                        updateAudience = String.format("INSERT INTO place (id_place, floor, wardrobe, shelf) " +
                                        "OVERRIDING SYSTEM VALUE VALUES (%s, %s, %s, %s)",
                                audience_id, floor, wardrobe, shelf);

                        updateIC = String.format("INSERT INTO book (author, publication, publishing_house, year_public,pages,year_write,weight,place_id) " +
                                        "VALUES ('%s', '%s', '%s', %s, %s, %s, %s, %s)",
                                author, publication, publishing_house, year_public, pages, year_write, weight, audience_id);

                    }

                    if (mode.equals("Редактировать")) {
                        updateAudience =
                                String.format("UPDATE place " +
                                                "SET floor = %s, wardrobe = %s, shelf = '%s' " +
                                                "WHERE id_place = %s",
                                        floor, wardrobe, shelf, dataTo[9]);
                        updateIC =
                                String.format("UPDATE book SET " +
                                                "author = '%s', publication = '%s', publishing_house = '%s', year_public = %s, pages = '%s', year_write = '%s', weight = %s " +
                                                "WHERE id = %s",
                                        author, publication, publishing_house, year_public, pages, year_write, weight, dataTo[0]);
                    }

                    // DB
                    try {
                        PreparedStatement audience;
                        PreparedStatement IC;
                        connection.setAutoCommit(true);

                        audience = connection.prepareStatement(updateAudience);
                        int res = audience.executeUpdate();

                        IC = connection.prepareStatement(updateIC);
                        res = IC.executeUpdate();

                        audience.close();
                        IC.close();

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