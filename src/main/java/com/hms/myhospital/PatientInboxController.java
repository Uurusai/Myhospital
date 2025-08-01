package com.hms.myhospital;

import com.hms.client.HMSClient;
import com.hms.client.NotificationClient;
import com.hms.dao.MessageDAO;
import com.hms.model.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.util.function.Consumer;

public class PatientInboxController {
    private int patientId;
    private NotificationClient notificationClient;
    private ObservableList<Message> messages = FXCollections.observableArrayList();

    @FXML
    private ListView<Message> messagesListView;
    private HMSClient client;

    public void initialize(int patientId,HMSClient client) {
        this.patientId = patientId;
        this.client= client ;
        setupListView();
        loadMessages();
        setupNotificationClient();
    }

    private void setupListView() {
        messagesListView.setItems(messages);
        messagesListView.setCellFactory(param -> new TextFieldListCell<>(new StringConverter<Message>() {
            @Override
            public String toString(Message message) {
                String status = message.isRead() ? "[Read]" : "[New]";
                return String.format("%s %s - %s: %s",
                        status,
                        message.getTimestamp().toString(),
                        message.getSenderName(),
                        message.getContent());
            }

            @Override
            public Message fromString(String string) {
                return null; // Not needed for display
            }
        }));

        // Mark message as read when selected
        messagesListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isRead()) {
                try {
                    MessageDAO.markAsRead(newVal.getId());
                    newVal.setRead(true);
                    messagesListView.refresh(); // Update the display
                } catch (SQLException e) {
                    e.printStackTrace();
                    // Handle error (show alert to user)
                }
            }
        });
    }

    private void loadMessages() {
        try {
            messages.setAll(client.getMessagesByRecipient(patientId));
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle error (show alert to user)
        }
    }

    private void setupNotificationClient() {
        Consumer<Message> messageHandler = message -> {
            // This runs on the notification client thread
            javafx.application.Platform.runLater(() -> {
                messages.add(0, message); // Add new message at the top
                // You might want to play a notification sound here
            });
        };

        notificationClient = new NotificationClient(patientId, messageHandler);
    }

    @FXML
    private void handleRefresh() {
        loadMessages();
    }

    @FXML
    private void handleDeleteSelected() {
        Message selected = messagesListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                if (client.deleteMessage(selected.getId())) {
                    messages.remove(selected);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle error (show alert to user)
            }
        }
    }
}

//UI CONFIGURATION

/*
* Based on the `PatientInboxController` and the existing FXML structure, I'll help you modify the inbox section of `patientDashboard.fxml` to properly work with the controller. Here's how to set up the UI:

1. First, update the controller reference in the FXML:
```xml
fx:controller="com.hms.controller.PatientInboxController"
```

2. Modify the `patientInbox` AnchorPane section to use a ListView instead of the current VBox structure:

```xml
<AnchorPane fx:id="patientInbox" layoutX="200.0" layoutY="210.0" prefHeight="390.0" prefWidth="700.0">
   <children>
      <Rectangle fill="#6b9ac4" height="75.0" stroke="#4059ad" strokeType="INSIDE" strokeWidth="3.0" width="700.0" />
      <Label layoutX="15.0" layoutY="20.0" text="Messages:" textFill="#4059ad">
         <font>
            <Font name="System Bold" size="24.0" />
         </font>
      </Label>

      <!-- Message Controls HBox -->
      <HBox layoutX="15.0" layoutY="40.0" spacing="10.0">
         <children>
            <Button fx:id="refreshBtn" mnemonicParsing="false" onAction="#handleRefresh" text="Refresh" />
            <Button fx:id="deleteBtn" mnemonicParsing="false" onAction="#handleDeleteSelected" text="Delete" />
         </children>
      </HBox>

      <ScrollPane hbarPolicy="NEVER" layoutY="75.0" prefHeight="315.0" prefWidth="700.0">
         <content>
            <ListView fx:id="messagesListView" prefHeight="315.0" prefWidth="700.0" />
         </content>
      </ScrollPane>
   </children>
</AnchorPane>
```

3. Remove the message composition controls (sentText, sentTo, sendMessageBtn) since they're not used in the current controller implementation.

4. Make sure to add these style classes to your CSS file (patientDashboard.css) for better message display:


5. Update your controller to include the new UI elements:

```java
// Add these annotations to your existing controller
@FXML
private ListView<Message> messagesListView;
@FXML
private Button refreshBtn;
@FXML
private Button deleteBtn;
```

The key changes are:

1. Replaced the VBox with a ListView that matches the controller's expectations
2. Added Refresh and Delete buttons that map to the controller methods
3. Removed the message composition UI since the controller currently only handles receiving and displaying messages
4. Maintained the same styling and layout structure as the rest of the dashboard

If you want to add message composition functionality later, you would need to:
1. Add the text fields and send button back to the UI
2. Add corresponding methods in the controller
3. Use MessageDAO to send new messages

The current setup will:
- Display messages in a scrollable list
- Show unread messages with bold text
- Allow marking messages as read by selecting them
- Provide refresh and delete functionality
- Maintain visual consistency with the rest of the dashboard*/