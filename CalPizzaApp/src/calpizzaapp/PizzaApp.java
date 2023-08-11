package calpizzaapp;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PizzaApp extends Application {

	private CustomerDAO customerDAO;
	private OrderDAO orderDAO;

	private TextField firstNameTextField;
	private TextField lastNameTextField;
	private TextField phoneNumberTextField;
	private TextField emailTextField;
	private TextField searchCustomerField;
	private TextField searchOrderField;
	private ListView<Customer> customerListView;
	private ListView<Order> orderListView;
	private ComboBox<String> itemsComboBox;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		// Initialize Hibernate session factory
		SessionFactory sessionFactory = new Configuration().configure("hibernate.cfg.xml")
				.addAnnotatedClass(Customer.class).addAnnotatedClass(Order.class).buildSessionFactory();

		// Initialize DAO classes
		customerDAO = new CustomerDAO(sessionFactory);
		orderDAO = new OrderDAO(sessionFactory);

		// Initialize UI components
		firstNameTextField = new TextField();
		lastNameTextField = new TextField();
		phoneNumberTextField = new TextField();
		emailTextField = new TextField();
		searchCustomerField = new TextField();
		searchOrderField = new TextField();
		customerListView = new ListView<>();
		orderListView = new ListView<>();
		itemsComboBox = new ComboBox<>();
		itemsComboBox.getItems().addAll("Caesar Salad", "Greek Salad", "Cobb Salad");

		// Create menu
		MenuBar menuBar = new MenuBar();
		Menu customerMenu = new Menu("Customer");
		MenuItem searchCustomerItem = new MenuItem("Search Customer");
		MenuItem addCustomerItem = new MenuItem("Add Customer");
		MenuItem updateCustomerItem = new MenuItem("Update Customer");
		MenuItem deleteCustomerItem = new MenuItem("Delete Customer");
		customerMenu.getItems().addAll(searchCustomerItem, addCustomerItem, updateCustomerItem, deleteCustomerItem);

		Menu orderMenu = new Menu("Order");
		MenuItem searchOrderItem = new MenuItem("Search Order");
		MenuItem listOrderItem = new MenuItem("List Order");
		MenuItem addOrderItem = new MenuItem("Add Order");
		MenuItem updateOrderItem = new MenuItem("Update Order");
		MenuItem deleteOrderItem = new MenuItem("Delete Order");
		orderMenu.getItems().addAll(searchOrderItem, listOrderItem, addOrderItem, updateOrderItem, deleteOrderItem);

		menuBar.getMenus().addAll(customerMenu, orderMenu);

		// Set actions for menu items
		searchCustomerItem.setOnAction(event -> searchCustomer());
		addCustomerItem.setOnAction(event -> addCustomer());
		updateCustomerItem.setOnAction(event -> updateCustomer());
		deleteCustomerItem.setOnAction(event -> deleteCustomer());

		searchOrderItem.setOnAction(event -> searchOrder());
		listOrderItem.setOnAction(event -> listOrders());
		addOrderItem.setOnAction(event -> addOrder());
		updateOrderItem.setOnAction(event -> updateOrder());
		deleteOrderItem.setOnAction(event -> deleteOrder());

		// Create main layout
		VBox mainLayout = new VBox(menuBar);
		mainLayout.getChildren().addAll(searchCustomerField, customerListView, searchOrderField, orderListView,
				itemsComboBox, firstNameTextField, lastNameTextField, phoneNumberTextField, emailTextField);

		// Create and show the scene
		Scene scene = new Scene(mainLayout, 800, 600);
		primaryStage.setScene(scene);
		primaryStage.setTitle("CalPizzaApp");
		primaryStage.show();
	}

	public void clearCustomerFields() {
		// Clear the input fields for adding a customer
		searchCustomerField.clear();
		firstNameTextField.clear();
		lastNameTextField.clear();
		phoneNumberTextField.clear();
		emailTextField.clear();
	}

	public void clearOrderFields() {
		// Clear the relevant UI components for order input
		itemsComboBox.getSelectionModel().clearSelection();
	}

	public boolean showConfirmationDialog(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);

		ButtonType confirmButton = new ButtonType("Confirm");
		ButtonType cancelButton = new ButtonType("Cancel");
		alert.getButtonTypes().setAll(confirmButton, cancelButton);

		// Show the dialog and wait for user response
		alert.showAndWait();

		// Check the user's response
		return alert.getResult() == confirmButton;
	}

	private void showErrorMessage(String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	private void showInfoMessage(String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Information");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	public void searchCustomer() {
		String firstNameFilter = searchCustomerField.getText(); // Get the search filter from UI

		if (!firstNameFilter.isEmpty()) {
			Customer foundCustomer = customerDAO.searchCustomer(firstNameFilter);

			if (foundCustomer != null) {
				// Populate UI controls with customer data
				firstNameTextField.setText(foundCustomer.getFirstName());
				lastNameTextField.setText(foundCustomer.getLastName());
				phoneNumberTextField.setText(foundCustomer.getPhoneNumber());
				emailTextField.setText(foundCustomer.getEmail());

				// Provide feedback to the user
				showInfoMessage("Customer found.");
			} else {
				// No matching customer found
				showInfoMessage("No records found.");
			}
		} else {
			// Filter is empty, show a message or handle as needed
		}
	}

	public void addCustomer() {
		String firstName = firstNameTextField.getText();
		String lastName = lastNameTextField.getText();
		String phoneNumber = phoneNumberTextField.getText();
		String email = emailTextField.getText();

		if (!firstName.isEmpty() && !lastName.isEmpty() && !phoneNumber.isEmpty() && !email.isEmpty()) {
			Customer newCustomer = new Customer(firstName, lastName, phoneNumber, email);

			boolean added = customerDAO.addCustomer(newCustomer);

			if (added) {
				showInfoMessage("Customer successfully added.");
				clearCustomerFields(); // Clear the UI text fields after adding
			} else {
				showErrorMessage("Failed to add customer.");
			}
		} else {
			showErrorMessage("Please fill in all fields.");
		}
	}

	public void updateCustomer() {
		String filter = searchCustomerField.getText(); // Get the search filter from UI

		if (!filter.isEmpty()) {
			List<Customer> foundCustomers = customerDAO.searchCustomers(filter);

			if (!foundCustomers.isEmpty()) {
				// Display the first found customer's data
				Customer foundCustomer = foundCustomers.get(0);

				// Update customer information from UI
				foundCustomer.setFirstName(firstNameTextField.getText());
				foundCustomer.setLastName(lastNameTextField.getText());
				foundCustomer.setPhoneNumber(phoneNumberTextField.getText());
				foundCustomer.setEmail(emailTextField.getText());

				// Update the customer in the database
				customerDAO.updateCustomer(foundCustomer);

				// Provide feedback to the user
				showInfoMessage("Customer data successfully updated.");
			} else {
				// No matching customers found
				showInfoMessage("No records found.");
			}
		} else {
			// Filter is empty, show a message or handle as needed
		}
	}

	public void deleteCustomer() {
		String filter = searchCustomerField.getText(); // Get the search filter from UI

		if (!filter.isEmpty()) {
			List<Customer> foundCustomers = customerDAO.searchCustomers(filter);

			if (!foundCustomers.isEmpty()) {
				// Display the first found customer's data (you might adjust this logic)
				Customer foundCustomer = foundCustomers.get(0);

				// Prompt the user for confirmation
				boolean confirmed = showConfirmationDialog("Delete Customer",
						"Are you sure you want to delete this customer?");

				if (confirmed) {
					// Delete the customer from the database
					customerDAO.deleteCustomer(foundCustomer);

					// Provide feedback to the user
					showInfoMessage("Customer successfully deleted.");

					// Clear UI fields or perform other actions as needed
					clearCustomerFields();
				}
			} else {
				// No matching customers found
				showInfoMessage("No records found.");
			}
		} else {
			// Filter is empty, show a message or handle as needed
		}
	}

	public void searchOrder() {
		// Implement order search functionality
		String orderNumberFilterText = searchOrderField.getText(); // Get the search filter from UI

		if (!orderNumberFilterText.isEmpty()) {
			try {
				int orderNumberFilter = Integer.parseInt(orderNumberFilterText); // Convert to int

				Order foundOrder = orderDAO.getOrderById(orderNumberFilter);

				if (foundOrder != null) {
					// Populate UI controls with order data
					itemsComboBox.getSelectionModel().select(foundOrder.getItemName());

					// Provide feedback to the user
					showInfoMessage("Order found.");
				} else {
					// No matching order found
					showInfoMessage("No records found.");
				}
			} catch (NumberFormatException e) {
				// Handle if the input is not a valid integer
				showInfoMessage("Invalid order number.");
			}
		} else {
			// Filter is empty, show a message or handle as needed
		}
	}

	public void listOrders() {
		List<Order> allOrders = orderDAO.listOrders();

		if (!allOrders.isEmpty()) {
			orderListView.setItems(FXCollections.observableArrayList(allOrders));

			// Define a selection listener for the order list view
			orderListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue != null) {
					// Populate UI controls with selected order data
					itemsComboBox.getSelectionModel().select(newValue.getItemName());
				}
			});

			// Provide feedback to the user
			showInfoMessage("Orders listed.");
		} else {
			// No orders found
			showInfoMessage("No orders found.");
		}
	}

	public void addOrder() {
		// Implement order addition functionality
		Customer selectedCustomer = customerListView.getSelectionModel().getSelectedItem();

		if (selectedCustomer != null) {
			String selectedItem = itemsComboBox.getValue(); // Get the selected item from the combo box

			// Create a new order
			Order newOrder = new Order();
			newOrder.setCustomer(selectedCustomer);
			newOrder.setItemName(selectedItem);

			// Add the order to the database
			boolean added = orderDAO.addOrder(newOrder);

			if (added) {
				// Refresh the order list view
				listOrders();

				// Provide feedback to the user
				showInfoMessage("Order successfully included.");
			} else {
				showInfoMessage("Failed to add order."); // Handle failure case
			}

			// Clear UI controls
			clearOrderFields();
		}
	}

	public void updateOrder() {
		Order selectedOrder = orderListView.getSelectionModel().getSelectedItem();

		if (selectedOrder != null) {
			String updatedItem = itemsComboBox.getValue(); // Get the updated item from the combo box

			// Update the selected order
			selectedOrder.setItemName(updatedItem);

			// Update the order in the database
			boolean updated = orderDAO.updateOrder(selectedOrder);

			if (updated) {
				// Refresh the order list view
				listOrders();

				// Provide feedback to the user
				showInfoMessage("Order data successfully updated.");
			} else {
				showInfoMessage("Failed to update order."); // Handle failure case
			}

			// Clear UI controls
			clearOrderFields();
		} else {
			// No order selected, show a message or handle as needed
		}
	}

	public void deleteOrder() {
		Order selectedOrder = orderListView.getSelectionModel().getSelectedItem();

		if (selectedOrder != null) {
			boolean confirmed = showConfirmationDialog("Confirm Deletion",
					"Are you sure you want to delete the selected order?");

			if (confirmed) {
				// Delete the order from the database
				boolean deleted = orderDAO.deleteOrder(selectedOrder);

				if (deleted) {
					// Refresh the order list view
					listOrders();

					// Provide feedback to the user
					showInfoMessage("Order successfully deleted.");
				} else {
					showInfoMessage("Failed to delete order."); // Handle failure case
				}

				// Clear UI controls
				clearOrderFields();
			}
		} else {
			// No order selected, show a message or handle as needed
		}
	}

}
