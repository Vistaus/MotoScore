package de.markusfisch.android.motoscore.notification;

import de.markusfisch.android.motoscore.activity.MainActivity;
import de.markusfisch.android.motoscore.R;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class Notifications {
	public final Recording recording;

	private static final int NOTIFY_RECORDING = 1;
	private static final String CHANNEL_RECORDING = "Recording";

	private final NotificationManager notificationManager;

	public class Recording extends AbstractNotification {
		private Recording(Context context, int id) {
			super(id);
			Resources r = context.getResources();
			notification = createNotification(
					context,
					R.drawable.notify,
					r.getString(R.string.app_name),
					r.getString(R.string.recording),
					getDefaultIntent(context));
			notification.flags |= Notification.FLAG_ONGOING_EVENT;
		}
	}

	public Notifications(Context context) {
		notificationManager = (NotificationManager)
				context.getSystemService(Context.NOTIFICATION_SERVICE);
		recording = new Recording(context, NOTIFY_RECORDING);
	}

	private static Notification createNotification(
			Context context,
			int icon,
			String title,
			String text,
			Intent intent) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
			return new NotificationCompat.Builder(context)
					.setOngoing(true)
					.setSmallIcon(icon)
					.setContentTitle(title)
					.setContentText(text)
					.setContentIntent(PendingIntent.getActivity(
							context, 0, intent, 0))
					.build();
		} else {
			createChannel(context);
			return new Notification.Builder(context, CHANNEL_RECORDING)
					.setOngoing(true)
					.setSmallIcon(icon)
					.setContentTitle(title)
					.setContentText(text)
					.setContentIntent(PendingIntent.getActivity(
							context, 0, intent, 0))
					.build();
		}
	}

	private static Intent getDefaultIntent(Context context) {
		Intent intent = new Intent(context, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
				Intent.FLAG_ACTIVITY_SINGLE_TOP);
		return intent;
	}

	@TargetApi(Build.VERSION_CODES.O)
	private static void createChannel(Context context) {
		NotificationManager nm = (NotificationManager)
				context.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationChannel channel = nm.getNotificationChannel(
				CHANNEL_RECORDING);
		if (channel == null) {
			channel = new NotificationChannel(
					CHANNEL_RECORDING,
					context.getString(R.string.channel_name),
					NotificationManager.IMPORTANCE_HIGH);
			channel.setDescription(context.getString(
					R.string.channel_description));
			channel.enableLights(true);
			channel.setLightColor(Color.BLUE);
			channel.enableVibration(true);
			channel.setVibrationPattern(new long[]{100, 100, 100, 100});

			nm.createNotificationChannel(channel);
		}
	}

	private abstract class AbstractNotification {
		protected Notification notification;

		private final int id;

		public AbstractNotification(int id) {
			this.id = id;
		}

		public Notification getNotification() {
			return notification;
		}

		public void show() {
			notificationManager.notify(id, notification);
		}

		public void hide() {
			notificationManager.cancel(id);
		}
	}
}