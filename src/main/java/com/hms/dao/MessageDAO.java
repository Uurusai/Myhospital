package com.hms.dao;

import com.hms.model.Message;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.hms.dao.DatabaseConnection.getConnection;

public class MessageDAO{

    public static List<Message> getUnreadMessages(int userId) throws SQLException {

        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE is_read = false";
        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                messages.add(mapRowToMessage(rs));
            }
        }
        return messages ;
    }

    public int createMessage(Message message) throws SQLException {
        String sql = "INSERT INTO messages (recipient_id, sender_name, content, timestamp, is_read, attachment_name, attachment_data) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setInt(1, message.getRecipientId());
            statement.setString(2, message.getSenderName());
            statement.setString(3, message.getContent());
            statement.setTimestamp(4, Timestamp.valueOf(message.getTimestamp()));
            statement.setBoolean(5, message.isRead());

            if (message.getAttachmentName() != null) {
                statement.setString(6, message.getAttachmentName());
                statement.setBytes(7, message.getAttachmentData());
            } else {
                statement.setNull(6, Types.VARCHAR);
                statement.setNull(7, Types.BINARY);
            }

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("Creating message failed, no ID obtained.");
        }
    }

    public List<Message> getMessagesByRecipient(int recipientId) throws SQLException {
        String sql = "SELECT * FROM messages WHERE recipient_id = ? ORDER BY timestamp DESC";
        List<Message> messages = new ArrayList<>();

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setInt(1, recipientId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                messages.add(mapRowToMessage(rs));
            }
            return messages;
        }
    }
    

    // Mark message as read
    public static boolean markAsRead(int messageId) throws SQLException {
        String sql = "UPDATE messages SET is_read = TRUE WHERE id = ?";

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setInt(1, messageId);
            return statement.executeUpdate() > 0;
        }
    }

    // Delete message
    public boolean deleteMessage(int messageId) throws SQLException {
        String sql = "DELETE FROM messages WHERE id = ?";

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setInt(1, messageId);
            return statement.executeUpdate() > 0;
        }
    }

    private static Message mapRowToMessage(ResultSet rs) throws SQLException {
        Message message = new Message();
        message.setId(rs.getInt("id"));
        message.setRecipientId(rs.getInt("recipient_id"));
        message.setSenderName(rs.getString("sender_name"));
        message.setContent(rs.getString("content"));
        message.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
        message.setRead(rs.getBoolean("is_read"));
        message.setAttachmentName(rs.getString("attachment_name"));
        message.setAttachmentData(rs.getBytes("attachment_data"));
        return message;
    }
}