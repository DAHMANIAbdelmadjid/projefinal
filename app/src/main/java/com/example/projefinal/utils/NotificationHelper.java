package com.example.projefinal.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import com.example.projefinal.R;
import com.example.projefinal.models.Reservation;

import java.util.List;

public class NotificationHelper {
    private static final String CHANNEL_ID = "COSMETICS_STORE_CHANNEL";
    private static final String CHANNEL_NAME = "Reservations";
    private static final String CHANNEL_DESC = "Notifications for reservation updates";
    
    private Context context;
    private NotificationManagerCompat notificationManager;

    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = NotificationManagerCompat.from(context);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CHANNEL_DESC);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void showReservationConfirmation(Reservation reservation, String productName) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Reservation Confirmed")
                .setContentText("Your reservation for " + productName + " has been confirmed!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        notificationManager.notify(reservation.getId(), builder.build());
    }

    public void sendConfirmationEmail(String recipientEmail, Reservation reservation, String productName) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipientEmail});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Reservation Confirmation - Cosmetics Store");
        
        String emailBody = "Dear Customer,\n\n" +
                "Your reservation has been confirmed!\n\n" +
                "Reservation Details:\n" +
                "Product: " + productName + "\n" +
                "Reservation ID: " + reservation.getId() + "\n" +
                "Status: " + reservation.getStatus() + "\n\n" +
                "Thank you for choosing our store!\n\n" +
                "Best regards,\nCosmetics Store Team";
        
        intent.putExtra(Intent.EXTRA_TEXT, emailBody);

        // Verify that there is an email client available to handle the intent
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        
        if (activities.size() > 0) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Add this flag since we're starting from a non-activity context
            context.startActivity(intent);
        }
    }

    public void showReservationStatusUpdate(Reservation reservation, String productName, String newStatus) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Reservation Update")
                .setContentText("Reservation for " + productName + " is now " + newStatus)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        notificationManager.notify(reservation.getId(), builder.build());
    }

    public void cancelNotification(int reservationId) {
        notificationManager.cancel(reservationId);
    }
}
