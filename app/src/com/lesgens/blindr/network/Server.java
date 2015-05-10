package com.lesgens.blindr.network;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import com.checkin.avatargenerator.AvatarGenerator;
import com.lesgens.blindr.controllers.Controller;
import com.lesgens.blindr.db.DatabaseHelper;
import com.lesgens.blindr.listeners.EventsListener;
import com.lesgens.blindr.listeners.FacebookProfileListener;
import com.lesgens.blindr.listeners.UserAuthenticatedListener;
import com.lesgens.blindr.models.City;
import com.lesgens.blindr.models.Event;
import com.lesgens.blindr.models.Match;
import com.lesgens.blindr.models.User;
import com.lesgens.blindr.network.HTTPRequest.RequestType;

public class Server {

	private static List<UserAuthenticatedListener> userAuthenticatedListeners = new ArrayList<UserAuthenticatedListener>();
	private static List<EventsListener> eventsListeners = new ArrayList<EventsListener>();
	private static List<FacebookProfileListener> profileListeners = new ArrayList<FacebookProfileListener>();
	private static String address = "https://blindr-backend.herokuapp.com/";
	private static String TAG = "Blindr_Server";

	public static void connect(String authenticationToken) {
		AsyncTask<String, Void, String> request = new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... arg0) {
				String finalAddress = address + "auth";
				Log.i(TAG, "auth address: " + finalAddress);
				List<NameValuePair> data = new ArrayList<NameValuePair>();
				data.add(new BasicNameValuePair("fb_token", arg0[0]));
				Log.i(TAG, "fb_token: " + arg0[0]);
				HTTPRequest request = new HTTPRequest(finalAddress, RequestType.POST, data);
				return request.getOutput();
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				String userId = "";
				String fakeName = "";
				Log.i(TAG, "Auth response's json: "+ result);
				try {
					String[] auth = readAuth(new StringReader(result));
					userId = auth[0];
					fakeName = auth[1];
					Log.i(TAG, "Auth response's userId: "+ userId + " fake name:" + fakeName);
					Controller.getInstance().setMyOwnUser(new User(fakeName, AvatarGenerator.generate(Controller.getInstance().getDimensionAvatar(), Controller.getInstance().getDimensionAvatar()), userId));
					for(UserAuthenticatedListener listener: userAuthenticatedListeners) {
						listener.onUserAuthenticated();
					}
				} catch(IOException ioe){
					ioe.printStackTrace();
					for(UserAuthenticatedListener listener: userAuthenticatedListeners) {
						listener.onUserServerErrorAuthentication();
					}
				} catch (Exception e) {
					Log.i(TAG, "Something went wrong while authenticating.");
					e.printStackTrace();
					for(UserAuthenticatedListener listener: userAuthenticatedListeners) {
						listener.onUserNetworkErrorAuthentication();
					}
				}
			}

		};
		request.execute(authenticationToken);
	}

	public static void getEvents() {

		AsyncTask<String, Void, String> request = new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... arg0) {
				String finalAddress = address + "events";
				Log.i(TAG, "Events address: " + finalAddress);
				Log.i("SERVER+INFOS", "Events city: " + arg0[1]);

				List<NameValuePair> data = new ArrayList<NameValuePair>();
				data.add(new BasicNameValuePair("city", arg0[1]));

				List<NameValuePair> headers = new ArrayList<NameValuePair>();
				headers.add(new BasicNameValuePair("X-User-Token", arg0[0]));

				HTTPRequest request = new HTTPRequest(finalAddress, RequestType.GET, data, headers);
				return request.getOutput();
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				List<Event> events = null;
				Log.i(TAG, "Events response's json: "+ result);
				try {
					events = readEvents(new StringReader(result));
					for(EventsListener listener:eventsListeners){
						listener.onEventsReceived(events);
					}
				} catch (IOException e) {
					Log.i(TAG, "Error while receiving events: ");
					e.printStackTrace();
				} catch (Exception e) {
					Log.i(TAG, "Something went wrong when fetching the events.");
					e.printStackTrace();
				}
			}

		};

		request.execute(Controller.getInstance().getMyself().getId(), Controller.getInstance().getCity().getName());
	}

	public static void sendPrivateMessage(User destination, String message) {
		AsyncTask<String, Void, String> request = new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... arg0) {
				String finalAddress = address + "events/message";
				Log.i(TAG, "Send private message address: " + finalAddress);

				List<NameValuePair> data = new ArrayList<NameValuePair>();
				data.add(new BasicNameValuePair("dst_user", arg0[1]));
				data.add(new BasicNameValuePair("message", arg0[2]));

				List<NameValuePair> headers = new ArrayList<NameValuePair>();
				headers.add(new BasicNameValuePair("X-User-Token", arg0[0]));

				HTTPRequest request = new HTTPRequest(finalAddress, RequestType.POST, data, headers);
				return request.getOutput();
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				Log.i(TAG, "Sent message's response: "+ result);
			}

		};
		request.execute(Controller.getInstance().getMyself().getId(), destination.getId(), message);
	}

	public static void sendPublicMessage(City destination, String message) {
		AsyncTask<String, Void, String> request = new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... arg0) {
				String finalAddress = address + "events/message";
				Log.i(TAG, "Send public message address: " + finalAddress);

				List<NameValuePair> data = new ArrayList<NameValuePair>();
				data.add(new BasicNameValuePair("dst_city", arg0[1]));
				data.add(new BasicNameValuePair("message", arg0[2]));

				List<NameValuePair> headers = new ArrayList<NameValuePair>();
				headers.add(new BasicNameValuePair("X-User-Token", arg0[0]));

				HTTPRequest request = new HTTPRequest(finalAddress, RequestType.POST, data, headers);
				return request.getOutput();
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				Log.i(TAG, "Sent message's response: "+ result);
			}

		};
		request.execute(Controller.getInstance().getMyself().getId(), destination.getId(), message);
	}

	public static void like(User user, String userFakeName) {
		AsyncTask<String, Void, String> request = new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... arg0) {
				String finalAddress = address + "events/like";
				Log.i(TAG, "Like address: " + finalAddress);

				List<NameValuePair> data = new ArrayList<NameValuePair>();
				data.add(new BasicNameValuePair("dst_user", arg0[1]));

				List<NameValuePair> headers = new ArrayList<NameValuePair>();
				headers.add(new BasicNameValuePair("X-User-Token", arg0[0]));

				HTTPRequest request = new HTTPRequest(finalAddress, RequestType.POST, data, headers);
				return request.getOutput();
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				Log.i(TAG, "Like's response: "+ result);
			}

		};
		request.execute(Controller.getInstance().getMyself().getId(), user.getId());
		for(EventsListener listener: eventsListeners) {
			listener.onUserLiked(user, userFakeName);
		}
	}

	public static void dislike(User user) {
		AsyncTask<String, Void, String> request = new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... arg0) {
				String finalAddress = address + "events/dislike";
				Log.i(TAG, "Like address: " + finalAddress);

				List<NameValuePair> data = new ArrayList<NameValuePair>();
				data.add(new BasicNameValuePair("dst_user", arg0[1]));

				List<NameValuePair> headers = new ArrayList<NameValuePair>();
				headers.add(new BasicNameValuePair("X-User-Token", arg0[0]));

				HTTPRequest request = new HTTPRequest(finalAddress, RequestType.POST, data, headers);
				return request.getOutput();
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				Log.i(TAG, "Dislike response: "+ result);
			}

		};
		request.execute(Controller.getInstance().getMyself().getId(), user.getId());
	}

	public static void getMatches() {
		// get tous les matchs déjà fait
		AsyncTask<String, Void, String> request = new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... arg0) {
				String finalAddress = address + "events/like";
				Log.i(TAG, "Like address: " + finalAddress);

				List<NameValuePair> headers = new ArrayList<NameValuePair>();
				headers.add(new BasicNameValuePair("X-User-Token", arg0[0]));

				HTTPRequest request = new HTTPRequest(finalAddress, RequestType.GET, null, headers);
				return request.getOutput();
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				try {
					Log.i(TAG, "Matches's response: "+ result);
					List<Match> matches = readMatches(new StringReader(result));
					for(EventsListener listener: eventsListeners) {
						listener.onOldMatchesReceives(matches);
					}
				} catch (IOException e) {
					Log.i(TAG, "Something went wrong when fetching the old matches.");
					e.printStackTrace();
				} catch (Exception e) {
					Log.i(TAG, "Something went wrong when fetching the old matches.");
					e.printStackTrace();
				}
			}
		};
		request.execute(Controller.getInstance().getMyself().getId());
	}

	public static void getUserEvents(User user) {
		// get les events avec un user en particulier
		AsyncTask<String, Void, String> request = new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... arg0) {
				String finalAddress = address + "events/" + arg0[1];
				Log.i(TAG, "Events avec user address: " + finalAddress);

				List<NameValuePair> headers = new ArrayList<NameValuePair>();
				headers.add(new BasicNameValuePair("X-User-Token", arg0[0]));

				List<NameValuePair> data = new ArrayList<NameValuePair>();
				long lastTime = DatabaseHelper.getInstance().getLastMessageFetched(arg0[1]);
				Log.i(TAG, "Last message fetched=" + lastTime);
				data.add(new BasicNameValuePair("since", String.valueOf(lastTime)));

				HTTPRequest request = new HTTPRequest(finalAddress, RequestType.GET, data, headers);
				return request.getOutput();
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				try {
					Log.i(TAG, "User events response: "+ result);
					List<Event> events = readEvents(new StringReader(result));
					for(EventsListener listener: eventsListeners) {
						listener.onUserHistoryReceived(events);
					}
				} catch (IOException e) {
					Log.i(TAG, "Something went wrong when fetching the old matches.");
					e.printStackTrace();
				} catch (Exception e) {
					Log.i(TAG, "Something went wrong when fetching the old matches.");
					e.printStackTrace();
				}
			}
		};
		request.execute(Controller.getInstance().getMyself().getId(), user.getId());
	}

	public static void getUserFacebookPictureSlideshow(User user) {
		// get les events avec un user en particulier
		AsyncTask<String, Void, String> request = new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... arg0) {
				String finalAddress = address + "pictures";
				Log.i(TAG, "Pictures avec user address: " + finalAddress);

				List<NameValuePair> headers = new ArrayList<NameValuePair>();
				headers.add(new BasicNameValuePair("X-User-Token", arg0[0]));

				List<NameValuePair> data = new ArrayList<NameValuePair>();
				data.add(new BasicNameValuePair("dst_id", arg0[1]));

				HTTPRequest request = new HTTPRequest(finalAddress, RequestType.GET, data, headers);
				return request.getOutput();
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				try {
					Log.i(TAG, "User pictures response: "+ result);
					List<String> pictures = new ArrayList<String>();
					StringReader in = new StringReader(result);
					JsonReader reader = new JsonReader(in);
					reader.beginArray();
					while(reader.hasNext()){
						for(String s :reader.nextString().split(",")){
							pictures.add(s);
						}
					}
					reader.endArray();
					reader.close();
					in.close();
					for(FacebookProfileListener listener: profileListeners) {
						listener.onSlideshowPicturesReceived(pictures);
					}
				} catch (IOException e) {
					Log.i(TAG, "Something went wrong when fetching the pictures.");
					e.printStackTrace();
				} catch (Exception e) {
					Log.i(TAG, "Something went wrong when fetching the pictures.");
					e.printStackTrace();
				}
			}
		};
		request.execute(Controller.getInstance().getMyself().getId(), user.getId());
	}

	public static void setFacebookUrls(String facebookUrls) {
		AsyncTask<String, Void, String> request = new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... arg0) {
				String finalAddress = address + "pictures";
				Log.i(TAG, "Set facebook urls address: " + finalAddress);

				List<NameValuePair> data = new ArrayList<NameValuePair>();
				data.add(new BasicNameValuePair("facebookUrls", arg0[1]));

				List<NameValuePair> headers = new ArrayList<NameValuePair>();
				headers.add(new BasicNameValuePair("X-User-Token", arg0[0]));

				HTTPRequest request = new HTTPRequest(finalAddress, RequestType.POST, data, headers);
				return request.getOutput();
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				Log.i(TAG, "Set facebook urls response: "+ result);
			}

		};
		request.execute(Controller.getInstance().getMyself().getId(), facebookUrls);
	}
	
	public static void setInterestedIn(String interests) {
		AsyncTask<String, Void, String> request = new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... arg0) {
				String finalAddress = address + "interests";
				Log.i(TAG, "Set interests address: " + finalAddress);

				List<NameValuePair> data = new ArrayList<NameValuePair>();
				data.add(new BasicNameValuePair("interested_in", arg0[1]));

				List<NameValuePair> headers = new ArrayList<NameValuePair>();
				headers.add(new BasicNameValuePair("X-User-Token", arg0[0]));

				HTTPRequest request = new HTTPRequest(finalAddress, RequestType.POST, data, headers);
				return request.getOutput();
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				Log.i(TAG, "Set interests response: "+ result);
			}

		};
		request.execute(Controller.getInstance().getMyself().getId(), interests);
	}

	public static void addUserAuthenticatedListener(UserAuthenticatedListener listener) {
		userAuthenticatedListeners.add(listener);
	}

	public static void removeUserAuthenticatedListener(UserAuthenticatedListener listener) {
		userAuthenticatedListeners.remove(listener);
	}

	public static void addEventsListener(EventsListener listener) {
		eventsListeners.add(listener);
	}

	public static void removeEventsListener(EventsListener listener) {
		eventsListeners.remove(listener);
	}

	public static void addProfileListener(FacebookProfileListener listener) {
		profileListeners.add(listener);
	}

	public static void removeProfileListener(FacebookProfileListener listener) {
		profileListeners.remove(listener);
	}

	private static String[] readAuth(Reader in) throws IOException {
		String[] auth = new String[2];
		JsonReader reader = new JsonReader(in);
		reader.beginObject();
		while(reader.hasNext()){
			String name = reader.nextName();
			if (name.equals("token")) {
				auth[0] = reader.nextString();
			} else if (name.equals("fake_name")) {
				auth[1] = reader.nextString();
			}
		}
		reader.endObject();
		reader.close();
		return auth;
	}

	private static ArrayList<Event> readEvents(Reader in) throws IOException{
		ArrayList<Event> events = new ArrayList<Event>();
		JsonReader reader = new JsonReader(in);
		reader.beginArray();
		while (reader.hasNext()) {
			events.add(readEvent(reader));
		}
		reader.endArray();
		reader.close();
		return events;
	}

	private static Event readEvent(JsonReader reader) throws IOException {
		UUID id = null;
		String type = null;
		String destination = null;
		Timestamp timestamp = null;
		String userId = null;
		String message = null;
		String gender = null;
		String fakeName = null;
		String realName = null;

		reader.beginObject();
		while(reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("event_id")) {
				id = UUID.fromString(reader.nextString());
			} else if (name.equals("type")) {
				type = reader.nextString();
			} else if (name.equals("dst")) {
				destination = reader.nextString();
			} else if (name.equals("sent_at")) {
				timestamp = new Timestamp((long)(reader.nextDouble()*1000));
			} else if (name.equals("src")) {
				userId = reader.nextString();
			} else if (name.equals("message")) {
				message = reader.nextString();
			} else if(name.equals("src_gender")) {
				gender = reader.nextString();
			} else if(name.equals("src_fake_name")) {
				fakeName = reader.nextString();
			} else if(name.equals("src_real_name")) {
				realName = reader.nextString();
			}else {
				reader.skipValue();
			}
		}
		reader.endObject();

		return EventBuilder.buildEvent(id, type, destination, timestamp, userId, message, gender, fakeName, realName);
	}

	private static List<Match> readMatches(Reader in) throws IOException {
		List<Match> matches = new ArrayList<Match>();
		JsonReader reader = new JsonReader(in);
		reader.beginArray();
		while(reader.hasNext()) {
			matches.add(readMatch(reader));
		}
		reader.endArray();
		reader.close();
		return matches;
	}

	private static Match readMatch(JsonReader reader) throws IOException {
		reader.beginObject();
		String userId = "";
		String fakeName = "";
		String realName = "";
		Boolean mutual = false;
		while(reader.hasNext()) {
			String name = reader.nextName();
			if(name.equals("other")) {
				userId = reader.nextString();
			}
			else if(name.equals("other_fake_name")) {
				fakeName = reader.nextString();
			}
			else if(name.equals("other_real_name")) {
				realName = reader.nextString();
			}
			else if(name.equals("mutual")) {
				mutual = reader.nextBoolean();
			}
			else {
				reader.skipValue();
			}
		}
		reader.endObject();
		User user = Controller.getInstance().getUser(userId);
		if(user != null) {
			return new Match(null, null, Controller.getInstance().getMyself(), user, mutual, realName, fakeName);
		}
		return null;
	}
}
